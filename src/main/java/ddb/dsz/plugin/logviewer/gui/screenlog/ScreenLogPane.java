package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.detach.Alignment;
import ddb.detach.TabbableStatus;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JComponent;

public class ScreenLogPane extends LogViewerDetachable {
   private final Map<File, LogViewPane> fileToPane = new HashMap();
   private final ScreenLogWorkspace workspace;
   LogListPane logList;
   CoreController core;
   private boolean stop = false;
   private Runnable update = new Runnable() {
      public void run() {
         if (!ScreenLogPane.this.stop) {
            try {
               synchronized(ScreenLogPane.this.fileToPane) {
                  Iterator i$ = ScreenLogPane.this.fileToPane.values().iterator();

                  while(i$.hasNext()) {
                     LogViewPane pane = (LogViewPane)i$.next();
                     pane.update();
                  }

               }
            } finally {
               ScreenLogPane.this.core.schedule(this, 5L, TimeUnit.SECONDS);
            }
         }
      }
   };

   public ScreenLogPane(CoreController core) {
      this.core = core;
      super.setName("Screen Logs");
      super.setAlignment(Alignment.LEFT);
      super.setShowButtons(true);
      super.setLogo("images/gkrellm2.png", ImageManager.SIZE16);
      this.workspace = new ScreenLogWorkspace(core.getSystemLogger(), this, 1);
      this.logList = new LogListPane(core, this, new File(core.getLogDirectory(), "Logs"));
      this.workspace.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{this.logList});
      core.submit(this.update);
   }

   @Override
   public void close() {
      this.logList.stop();
      this.stop = true;
   }

   @Override
   public JComponent getDisplay() {
      return this.workspace;
   }

   @Override
   public boolean isClosable() {
      return false;
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   @Override
   public boolean isHideable() {
      return true;
   }

   public void release(LogViewerDetachable lvd) {
      this.workspace.enqueAction(WorkbenchAction.REMOVETAB, new Object[]{lvd});
   }

   @Override
   public void fini() {
      synchronized(this.fileToPane) {
         Iterator i$ = this.fileToPane.values().iterator();

         while(i$.hasNext()) {
            LogViewPane log = (LogViewPane)i$.next();
            log.close();
         }

         this.fileToPane.clear();
      }
   }

   @Override
   public TabbableStatus getStatus() {
      return this.workspace.getStatus();
   }

   public void destroy(LogViewerDetachable lvd) {
      synchronized(this.fileToPane) {
         Iterator i$ = this.fileToPane.keySet().iterator();

         File f;
         do {
            if (!i$.hasNext()) {
               return;
            }

            f = (File)i$.next();
         } while(lvd != this.fileToPane.get(f));

         this.fileToPane.remove(f);
         lvd.close();
      }
   }

   public void loadFile(File file) {
      if (file != null && file.exists() && !file.isDirectory()) {
         boolean add = false;
         LogViewPane pane;
         synchronized(this.fileToPane) {
            pane = (LogViewPane)this.fileToPane.get(file);
            if (pane == null) {
               add = true;

               try {
                  pane = new LogViewPane(this.core, file);
               } catch (Throwable var11) {
                  this.core.logEvent(Level.SEVERE, "Unable to load " + file.getAbsolutePath() + " in detail", var11);
                  return;
               }

               this.fileToPane.put(file, pane);
            }
         }

         if (add) {
            this.workspace.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{pane});
            this.core.logEvent(Level.FINER, "Loading task " + file.getAbsolutePath() + " in a detail pane");
         } else {
            synchronized(this.workspace) {
               boolean found = false;
               Component[] arr$ = this.workspace.getComponents();
               int len$ = arr$.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  Component c = arr$[i$];
                  if (c.equals(pane.getDisplay())) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  if (pane.getFrame() != null && pane.getFrame().isVisible()) {
                     pane.getFrame().toFront();
                     return;
                  }

                  this.workspace.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{pane});
               }
            }
         }

         this.workspace.enqueAction(WorkbenchAction.SETSELECTEDTAB, new Object[]{pane});
      }
   }
}
