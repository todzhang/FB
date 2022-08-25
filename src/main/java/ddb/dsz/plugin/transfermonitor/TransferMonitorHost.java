package ddb.dsz.plugin.transfermonitor;

import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.dsz.plugin.transfermonitor.listeners.GetClosure;
import ddb.dsz.plugin.transfermonitor.listeners.PapercutClosure;
import ddb.dsz.plugin.transfermonitor.listeners.PutClosure;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.tabs.TransferDetails;
import ddb.dsz.plugin.transfermonitor.tabs.TransferMainTab;
import ddb.util.FileManips;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.map.ReferenceMap;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/download_manager.png")
@DszName("Transfer Monitor")
@DszDescription("Lists all 'GET' requests")
public class TransferMonitorHost extends SingleTargetImpl {
   private static final Collection<String> INTERESTING_COMMANDS;
   public static final String MAGICFILE = "TransferMonitor/magic.xml";
   Map<TransferRecord, TransferDetails> recordDetails = new ReferenceMap();
   public static final String TM_LOGO = "images/tm_logo.png";
   public static final String ERROR_ICON = "images/tm_fatal.png";
   public static final String RUNNING_ICON = "images/exec.png";
   public static final String COMPLETED_ICON = "images/tm_completed.png";
   String transform;
   private DataTransformer dataTranslator = DataTransformer.newInstance();
   final Object WORKBENCH_LOCK = new Object();
   TransferWorkbench transferWorkbench;
   TransferMainTab mainTab;
   JFileChooser chooser = null;

   public TransferMonitorHost(HostInfo var1, CoreController var2) {
      super(var1, var2);
      super.setName(var1.getId());
      var2.logEvent(Level.FINE, "Initializing TransferMonitor");
      this.transferWorkbench = new TransferWorkbench();
      this.mainTab = new TransferMainTab(this, var2);
      this.transferWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{this.mainTab});
      super.setDisplay(this.transferWorkbench);
      if (this.dataTranslator != null) {
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "get", "Dsz", this.wrapClosure(new GetClosure(this, var2))));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "papercut", "Dsz", this.wrapClosure(new PapercutClosure(this, var2))));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(var2, "put", "Dsz", this.wrapClosure(new PutClosure(this, var2))));
      }

   }

   private Closure wrapClosure(final Closure var1) {
      final Predicate var2 = super.getDataPredicate();
      return new Closure() {
         public void execute(Object var1x) {
            if (var2.evaluate(var1x)) {
               var1.execute(var1x);
            }

         }
      };
   }

   public int getNext() {
      return this.mainTab.getNext();
   }

   public void addRecord(TransferRecord var1) {
      this.mainTab.addRecord(var1);
   }

   public void recordChanged(TransferRecord var1) {
      this.mainTab.recordChanged(var1);
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      Task var2 = this.core.getTaskById(commandEvent.getId());
      if (var2 != null) {
         this.addCommand(var2);
         switch(var2.getState()) {
         case FAILED:
         case KILLED:
         case SUCCEEDED:
            this.commandEnded(var2);
         default:
         }
      }
   }

   private void commandEnded(Task var1) {
      this.mainTab.commandEnded(var1);
   }

   private void addCommand(Task var1) {
      if (var1.getCommandName() != null) {
         if (INTERESTING_COMMANDS.contains(var1.getCommandName().toLowerCase()) && this.dataTranslator != null) {
            this.dataTranslator.addTask(var1);
         }

      }
   }

   public synchronized void saveAll(final TransferRecord[] var1) {
      if (var1.length != 0) {
         if (this.chooser == null) {
            this.chooser = new JFileChooser();
         }

         this.chooser.setFileSelectionMode(1);
         int var2 = this.chooser.showSaveDialog(this.transferWorkbench);
         if (var2 != 1) {
            final File var3 = this.chooser.getSelectedFile();
            if (var3.isDirectory()) {
               this.core.submit(new Runnable() {
                  private void save(File var1x, File var2) {
                     try {
                        FileManips.CopyFile(var1x, var2);
                     } catch (IOException var4) {
                        TransferMonitorHost.this.core.logEvent(Level.SEVERE, var4.getMessage(), var4);
                     }

                  }

                  public void run() {
                     ArrayList var1x = new ArrayList();
                     TransferRecord[] var2 = var1;
                     int var3x = var2.length;

                     int var4;
                     for(var4 = 0; var4 < var3x; ++var4) {
                        TransferRecord var5 = var2[var4];
                        File var6 = TransferMonitorHost.this.getFileFromRecord(var5);
                        boolean var7 = false;

                        for(int var8 = 0; var8 < 1000; ++var8) {
                           File var9 = new File(var3, String.format("%s.%03d", TransferMonitorHost.this.getRemoteName(var5), var8));
                           if (!var9.exists()) {
                              try {
                                 FileManips.CopyFile(var6, var9);
                                 var7 = true;
                                 break;
                              } catch (Exception var11) {
                              }
                           }
                        }

                        if (!var7) {
                           var1x.add(var5);
                        }
                     }

                     if (var1x.size() > 0) {
                        StringBuilder var12 = new StringBuilder();

                        for(var4 = 0; var4 < var1x.size() && var4 < 10; ++var4) {
                           var12.append(String.format("   %s\n", TransferMonitorHost.this.getRemoteName((TransferRecord)var1x.get(var4))));
                        }

                        if (var1x.size() > 10) {
                           var12.append(String.format("   and %d others\n", var1x.size() - 10));
                        }

                        JOptionPane.showMessageDialog(TransferMonitorHost.this.transferWorkbench, String.format("Unable to save:\n%s", var12.toString()), "Error Saving File", 0);
                     }

                  }
               });
            }
         }
      }
   }

   public synchronized boolean save(TransferRecord var1) {
      if (this.chooser == null) {
         this.chooser = new JFileChooser();
      }

      if (var1.getLocal().length() == 0) {
         return true;
      } else {
         final File var2 = this.getFileFromRecord(var1);
         if (var2 != null && var2.exists() && !var2.isDirectory()) {
            this.chooser.setFileSelectionMode(0);
            this.chooser.setSelectedFile(new File(String.format("%s.000", this.getRemoteName(var1))));

            int var3;
            File var4;
            do {
               do {
                  var3 = this.chooser.showSaveDialog(this.transferWorkbench);
                  if (var3 == 1) {
                     return false;
                  }
               } while(var3 != 0);

               var4 = this.chooser.getSelectedFile();
               if (!var4.exists()) {
                  break;
               }

               var3 = JOptionPane.showConfirmDialog(this.transferWorkbench, "File already exists.  Are you sure you with to overwrite it?", "File Alrady Exists", 1, 3);
               if (var3 == 1) {
                  return false;
               }
            } while(var3 == 1);

            File finalVar = var4;
            this.core.submit(new Runnable() {
               public void run() {
                  try {
                     FileManips.CopyFile(var2, finalVar);
                  } catch (IOException var2x) {
                     TransferMonitorHost.this.core.logEvent(Level.SEVERE, var2x.getMessage(), var2x);
                     JOptionPane.showMessageDialog(TransferMonitorHost.this.transferWorkbench, String.format("Unable to save %s:  %s", finalVar.getAbsolutePath(), var2x.getMessage()), "Error Saving File", 0);
                  }

               }
            });
            return true;
         } else {
            return true;
         }
      }
   }

   private String getRemoteName(TransferRecord var1) {
      return var1.getRemote().replaceAll(".*[\\\\\\/]", "");
   }

   public void open(TransferRecord var1) {
      TransferDetails var2 = null;
      File var3 = this.getFileFromRecord(var1);
      if (var3 != null) {
         var2 = (TransferDetails)this.recordDetails.get(var1);
         if (var2 == null) {
            try {
               var2 = new TransferDetails(var1, var3);
            } catch (IOException var11) {
               return;
            }

            this.recordDetails.put(var1, var2);
            this.transferWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var2});
         } else {
            synchronized(this.WORKBENCH_LOCK) {
               boolean var5 = false;
               Component[] var6 = this.transferWorkbench.getComponents();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Component var9 = var6[var8];
                  if (var9.equals(var2.getDisplay())) {
                     var5 = true;
                     break;
                  }
               }

               if (!var5) {
                  if (var2.getFrame() != null && var2.getFrame().isVisible()) {
                     var2.getFrame().toFront();
                     return;
                  }

                  this.transferWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var2});
               }
            }
         }

         this.transferWorkbench.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{var2});
      }
   }

   public File getFileFromRecord(TransferRecord var1) {
      return new File(String.format("%s/%s/%s", this.core.getLogDirectory(), var1.getSubDir(), var1.getLocal()));
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("get");
      var0.add("put");
      var0.add("papercut");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(var0);
   }
}
