package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class LogListPane extends LogViewerDetachable {
   File monitoredDirectory;
   CoreController core;
   boolean stop = false;
   Runnable scan = new Runnable() {
      public void run() {
         if (!LogListPane.this.stop) {
            if (LogListPane.this.monitoredDirectory != null) {
               try {
                  File[] arr$ = LogListPane.this.monitoredDirectory.listFiles();
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     File f = arr$[i$];
                     if (!f.isDirectory()) {
                        LogListPane.this.model.updateFile(f);
                     }
                  }
               } finally {
                  LogListPane.this.core.schedule(this, 5L, TimeUnit.SECONDS);
               }

            }
         }
      }
   };
   ScreenLogModel model = new ScreenLogModel();
   JTable tableList;
   JScrollPane tableScroll;
   ScreenLogPane parent;

   public LogListPane(CoreController core, ScreenLogPane parentPane, File monitorDirectory) {
      this.tableList = new JTable(this.model);
      this.tableScroll = new JScrollPane(this.tableList);
      this.parent = parentPane;
      super.setName("Screen Logs");
      super.setAlignment(Alignment.LEFT);
      super.setShowButtons(true);
      this.monitoredDirectory = monitorDirectory;
      this.core = core;
      core.submit(this.scan);
      this.tableList.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
               int row = e.getY() / LogListPane.this.tableList.getRowHeight();
               File f = (File)LogListPane.this.model.getValueAt(row, ScreenLogColumns.FILENAME);
               if (f != null) {
                  LogListPane.this.parent.loadFile(f);
               }
            }
         }
      });
      LogRenderer renderer = new LogRenderer();
      this.tableList.setDefaultRenderer(Calendar.class, renderer);
      this.tableList.setDefaultRenderer(Long.class, renderer);
      this.tableList.setDefaultRenderer(File.class, renderer);
   }

   @Override
   public JComponent getDisplay() {
      return this.tableScroll;
   }

   public void stop() {
      this.stop = true;
   }

   @Override
   public boolean isClosable() {
      return false;
   }
}
