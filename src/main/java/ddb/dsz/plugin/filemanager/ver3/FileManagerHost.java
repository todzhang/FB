package ddb.dsz.plugin.filemanager.ver3;

import ddb.dsz.core.contextmenu.CommandCallbackListener;
import ddb.dsz.core.contextmenu.ContextMenuAction;
import ddb.dsz.core.contextmenu.ContextMenuFactory;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.filemanager.ver3.custom.CustomDir;
import ddb.dsz.plugin.filemanager.ver3.custom.CustomGet;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.imagemanager.ImageManager;
import ddb.targetmodel.filemodel.Data;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemListener;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.util.UtilityConstants;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.apache.commons.collections.Closure;

public abstract class FileManagerHost extends SingleTargetImpl implements FileSystemListener, CommandCallbackListener, ContextMenuAction {
   public static final String HOST_NAME = "-host";
   public static final String INITIAL = "-initial";
   public static final String CONFIGPATH = "/FileBrowser/";
   public static final String FILECONTEXTMENU = "FileContextMenu.xml";
   public static final String DIRCONTEXTMENU = "DirContextMenu.xml";
   public static final String ROOTCONTEXTMENU = "RootContextMenu.xml";
   public static final Pattern INITIAL_PATTERN = Pattern.compile("-initial=(\\d+)");
   public static final Pattern SEARCH_PATTERN = Pattern.compile("-searchRoot=(\\d+)");
   protected static final Executor exec = Executors.newSingleThreadExecutor(UtilityConstants.createThreadFactory("FileManager"));
   protected FileSystemModel model;
   protected FileManager parent;

   public static Icon getIcon(String var0) {
      return ImageManager.getIcon(var0, ImageManager.SIZE16);
   }

   public static String makePath(FileObject var0) {
      return makePath(var0.getPath(), var0.getName());
   }

   public static String makePath(String var0, String var1) {
      if (var0 == null && var1.endsWith(":") && var1.length() == 2) {
         return String.format("%s/", var1);
      } else if (var0 == null) {
         return String.format("/%s/", var1);
      } else if (var0.length() == 2 && var0.charAt(1) == ':') {
         return String.format("%s/%s/", var0, var1);
      } else {
         return !var0.endsWith("/") && !var0.endsWith("\\") ? String.format("%s/%s/", var0, var1) : String.format("%s%s/", var0, var1);
      }
   }

   public static Collection<Map<String, String>> getMapsFor(Collection<FileObject> var0, String var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         FileObject var4 = (FileObject)var3.next();
         var2.add(getMapFor(var4, var1));
      }

      return var2;
   }

   public static Map<String, String> getMapFor(FileObject var0, String var1) {
      HashMap var2 = new HashMap();
      var2.put("host", var1);
      if (var0 != null) {
         if (var0.getPath() != null && !var0.getPath().equals("")) {
            var2.put("path", String.format("%s/", var0.getPath()));
            var2.put("fullpath", makePath(var0));
         } else {
            var2.put("path", "");
            var2.put("fullpath", String.format("%s/", var0.getName()));
         }

         var2.put("name", var0.getName());
         var2.put("id", var0.getId().toString());
         long var3 = var0.getParent();
         if (var3 != -1L) {
            var2.put("pid", String.format("%d", var3));
         }

         Data var5;
         if (var0.isDirectory()) {
            var5 = var0.getDataElement(FileObjectFields.File_Children);
            if (var5.hasValue()) {
               var2.put("children", var5.getValue().toString());
            } else {
               var2.put("children", "??");
            }

            var2.put("bytes", "??");
         } else {
            var5 = var0.getDataElement(FileObjectFields.File_Size);
            if (var5.hasValue()) {
               var2.put("bytes", var5.getValue().toString());
            } else {
               var2.put("bytes", "??");
            }
         }
      }

      return var2;
   }

   public FileManagerHost(HostInfo var1, CoreController var2, FileManager var3) {
      super(var1, var2);
      this.parent = var3;
      super.setName("FileManager");
      super.setShowButtons(false);
   }

   protected void setFileSystemModel(FileSystemModel var1) {
      if (this.model != null) {
         this.model.removeFileSystemListener(this);
      }

      this.model = var1;
      if (this.model != null) {
         this.model.addFileSystemListener(this);
      }

      this.modelChanged();
   }

   public void fileChanged(FileObject var1) {
      this.fileChanged(var1.getId());
   }

   public void fileChanged(long var1) {
   }

   protected abstract void modelChanged();

   public void showPopup(MouseEvent var1, FileObject var2) {
      if (var1 != null) {
         ArrayList var3 = new ArrayList();
         String var4 = null;
         if (var2 != null && var2.getId() != FileSystemModel.ROOT) {
            System.out.printf("%s\t%s\n", var2.getPath(), var2.getName());
            if (var2.isDirectory()) {
               var4 = "DirContextMenu.xml";
            } else {
               var4 = "FileContextMenu.xml";
            }
         } else {
            var4 = "RootContextMenu.xml";
         }

         var3.add("/FileBrowser/" + var4);
         var3.addAll(this.getAdditionalOptions());
         JPopupMenu var5 = ContextMenuFactory.createContextMenuString(var3, this.core, this, this.target, Collections.singleton(getMapFor(var2, this.target.getId())), (Object)null, this);
         var5.show(var1.getComponent(), var1.getX(), var1.getY());
      }
   }

   public void showPopup(MouseEvent var1, Collection<FileObject> var2, Collection<Integer> var3) {
      if (var1 != null) {
         if (var2.isEmpty() && var3.isEmpty()) {
            this.showPopup(var1, (FileObject)null);
         } else {
            ArrayList var4 = new ArrayList();
            ArrayList var5;
            if (var3.isEmpty()) {
               var5 = new ArrayList();
               boolean var6 = true;
               String var7 = "DirContextMenu.xml";
               Iterator var8 = var2.iterator();

               FileObject var9;
               while(var8.hasNext()) {
                  var9 = (FileObject)var8.next();
                  if (!var9.isDirectory()) {
                     var7 = "FileContextMenu.xml";
                     var6 = false;
                     break;
                  }
               }

               if (!var3.isEmpty() && var6) {
                  var7 = "FileContextMenu.xml";
                  var6 = false;
               }

               var8 = var2.iterator();

               while(var8.hasNext()) {
                  var9 = (FileObject)var8.next();
                  if (var9 == null) {
                     System.out.println("File is null");
                  } else if (var6 == var9.isDirectory()) {
                     var5.add(var9);
                  }
               }

               var4.add("/FileBrowser/" + var7);
            } else {
               var5 = new ArrayList();
               var5.addAll(var2);
               var4.add("/FileBrowser/FileContextMenu.xml");
               var4.add("/FileBrowser/DirContextMenu.xml");
            }

            var4.addAll(this.getAdditionalOptions());
            JPopupMenu var10 = ContextMenuFactory.createContextMenuString(var4, this.core, this, this.target, getMapsFor(var5, this.target.getId()), var3.isEmpty() ? null : var3, this);
            var10.show(var1.getComponent(), var1.getX(), var1.getY());
         }
      }
   }

   public List<String> getAdditionalOptions() {
      ArrayList var1 = new ArrayList();
      return var1;
   }

   @Override
   public void registerCommand(String var1, TaskId taskId) {
   }

   @Override
   public void action(List<String> var1) {
      if (var1.size() != 0) {
         if (((String)var1.get(0)).equalsIgnoreCase("properties")) {
            for(int var2 = 1; var2 < var1.size(); ++var2) {
            }
         } else {
            Iterator var4;
            String var5;
            Matcher var6;
            long var11;
            if (((String)var1.get(0)).equalsIgnoreCase("search")) {
               var11 = FileSystemModel.ROOT;
               var4 = var1.iterator();

               while(var4.hasNext()) {
                  var5 = (String)var4.next();
                  System.out.println(var5);
                  var6 = SEARCH_PATTERN.matcher(var5);
                  if (var6.matches() && var6.groupCount() == 1) {
                     try {
                        var11 = (long)Integer.parseInt(var6.group(1));
                     } catch (NumberFormatException var10) {
                     }
                  }
               }

               this.parent.newSearch(this.target, var11);
            } else if (((String)var1.get(0)).equalsIgnoreCase("browse")) {
               var11 = FileSystemModel.ROOT;
               var4 = var1.iterator();

               while(var4.hasNext()) {
                  var5 = (String)var4.next();
                  var6 = INITIAL_PATTERN.matcher(var5);
                  if (var6.matches() && var6.groupCount() == 1) {
                     try {
                        var11 = (long)Integer.parseInt(var6.group(1));
                     } catch (NumberFormatException var9) {
                     }
                  }
               }

               this.parent.newBrowser(this.target, var11);
            } else if (((String)var1.get(0)).equalsIgnoreCase("expand")) {
               var1.set(0, "request");
               var1.add(String.format("host=%s", this.target.getId()));
               if (((String)var1.get(1)).equalsIgnoreCase("dir")) {
                  CustomDir var12 = new CustomDir(this.getFrame(), true, this.core, var1);
                  var12.setVisible(true);
               } else if (((String)var1.get(1)).equalsIgnoreCase("get")) {
                  CustomGet var13 = new CustomGet(this.getFrame(), true, this.core, var1);
                  var13.setVisible(true);
               }
            } else if (((String)var1.get(0)).equals("dump")) {
               Long var14 = Long.parseLong((String)var1.get(1));
               this.model.DumpHistory(var14, System.out);
            } else {
               var1.add(String.format("host=%s", this.target.getId()));
               InternalCommandCallback var16 = null;
               Iterator var3 = var1.iterator();

               while(var3.hasNext()) {
                  String var15 = (String)var3.next();
                  if (var15.startsWith("id=")) {
                     var15 = var15.substring(3);

                     try {
                        final int var17 = Integer.parseInt(var15);
                        var16 = new InternalCommandCallback() {
                           @Override
                           public void taskingRecieved(List<String> var1, Object var2) {
                              FileManagerHost.this.model.taskingStarted((long)var17);
                           }

                           @Override
                           public void taskingExecuted(Object var1, Object var2) {
                              FileManagerHost.this.model.taskingDone((long)var17);
                           }

                           @Override
                           public void taskingRejected(Object var1, Object var2) {
                              FileManagerHost.this.model.taskingDone((long)var17);
                           }
                        };
                     } catch (NumberFormatException var8) {
                     }
                  }
               }

               if (this.core.internalCommand(var16, var1)) {
               }
            }
         }

      }
   }

   @Override
   public void action(List<String> var1, Object var2) {
      if (var2 instanceof List) {
         List var3 = (List)var2;
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            int var5 = (Integer)var4.next();
            this.model.getFullFile((long)var5, (String)null, new FileManagerHost.GetPathAndDoAction(var1, this.target.getId()));
         }
      }

   }

   public void fini2() {
   }

   @Override
   public void close() {
      this.parent.remove(this);
      super.close();
   }

   @Override
   public boolean isClosable() {
      return true;
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   private class DoAction implements Closure {
      List<String> parameters;
      String hostId;

      public DoAction(List<String> var2, String var3) {
         this.parameters = var2;
         this.hostId = var3;
      }

      public void execute(Object var1) {
         if (var1 instanceof FileObject) {
            ArrayList var2 = new ArrayList();
            Map var3 = FileManagerHost.getMapFor((FileObject)FileObject.class.cast(var1), this.hostId);
            Iterator var4 = this.parameters.iterator();

            while(true) {
               String var5;
               do {
                  if (!var4.hasNext()) {
                     FileManagerHost.this.action(var2);
                     return;
                  }

                  var5 = (String)var4.next();
               } while(var5 == null);

               Iterator var6 = var3.keySet().iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  if (var3.get(var7) == null) {
                     var5 = var5.replaceAll("\\{" + var7 + "\\}", (String)var3.get(var7));
                  } else {
                     var5 = var5.replaceAll("\\{" + var7 + "\\}", "");
                  }
               }

               var2.add(var5);
            }
         }
      }
   }

   private class GetPathAndDoAction implements Closure {
      List<String> parameters;
      String hostId;

      public GetPathAndDoAction(List<String> var2, String var3) {
         this.parameters = var2;
         this.hostId = var3;
      }

      public void execute(Object var1) {
         if (var1 instanceof FileObject) {
            FileManagerHost.this.model.getPath((FileObject)FileObject.class.cast(var1), FileManagerHost.this.new DoAction(this.parameters, this.hostId));
         }

      }
   }
}
