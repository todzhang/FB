package ds.core.commanddispatcher;

import ddb.dsz.core.controller.CoreController;
import ds.plugin.replay.ReplayTableModel;
import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.TimeUnit;

public class DirectoryMonitoringCommandDispatcher extends AbstractCommandDispatcher implements CommandDispatcher, Runnable {
   File targetDir;
   MultipleCommandDispatcherClient mcdc;

   public DirectoryMonitoringCommandDispatcher(EventPublisher var1, CoreController var2, File var3, MultipleCommandDispatcherClient var4) {
      super(var1, var2);
      this.targetDir = var3;
      this.mcdc = var4;
      this.core.schedule(this, 10L, TimeUnit.SECONDS);
   }

   public void run() {
      try {
         File[] var1 = this.targetDir.listFiles(new FileFilter() {
            public boolean accept(File var1) {
               return var1.isDirectory();
            }
         });
         if (var1 != null) {
            File[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               File var5 = var2[var4];
               if (var5.isDirectory()) {
                  File var6 = new File(var5, "OpLogs");
                  if (var6.exists()) {
                     File[] var7 = var6.listFiles();
                     if (var7 != null) {
                        File[] var8 = var7;
                        int var9 = var7.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                           File var11 = var8[var10];
                           ReplayTableModel.getReplayModel().addRecord(var11);
                        }
                     }
                  }
               }
            }

            return;
         }
      } finally {
         this.core.schedule(this, 10L, TimeUnit.SECONDS);
      }

   }
}
