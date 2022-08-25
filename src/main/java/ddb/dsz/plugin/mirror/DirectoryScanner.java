package ddb.dsz.plugin.mirror;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import java.io.File;
import java.io.FilenameFilter;

public abstract class DirectoryScanner implements StoppableRunnable {
   public static final FilenameFilter filter = new FilenameFilter() {
      public boolean accept(File var1, String var2) {
         return !var2.toLowerCase().endsWith(".db");
      }
   };
   public static final FilenameFilter REPLAY_LOCK = new FilenameFilter() {
      public boolean accept(File var1, String var2) {
         return var2.toLowerCase().equals(".replay");
      }
   };
   protected boolean stop = false;
   protected CoreController core;

   protected abstract boolean handleFile(String var1, File var2);

   public DirectoryScanner(CoreController var1) {
      this.core = var1;
   }

   public void stop() {
      this.stop = true;
   }

   private boolean shouldSkip(File var1) {
      if (var1.isFile() && var1.getName().equals(".replay")) {
         return true;
      } else if (var1.isFile()) {
         return false;
      } else if (var1.getName().equals("Temp")) {
         return true;
      } else {
         File[] var2 = var1.listFiles(REPLAY_LOCK);
         return var2 != null && var2.length > 0;
      }
   }

   public boolean scanDirectory(String var1, String var2) {
      File var3 = new File(String.format("%s/%s", var1, var2));
      if (this.shouldSkip(var3)) {
         return true;
      } else if (!var3.exists()) {
         return true;
      } else {
         HostInfo var4 = this.core.getHostById("localhost");
         String[] var5 = new String[]{var4 != null ? var4.getId() : "__", "Tasking"};
         String[] var6 = var5;
         int var7 = var5.length;

         int var8;
         for(var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (!this.scanDirectory(var1, String.format("%s%s/", var2, var9))) {
               return false;
            }
         }

         if (var3 == null) {
            return true;
         } else {
            File[] var13 = var3.listFiles(filter);
            if (var13 == null) {
               return true;
            } else {
               File[] var14 = var13;
               var8 = var13.length;

               for(int var15 = 0; var15 < var8; ++var15) {
                  File var10 = var14[var15];
                  if (this.stop) {
                     return false;
                  }

                  boolean var11 = false;
                  String var12 = String.format("%s%s", var2, var10.getName());
                  if (var10.isDirectory()) {
                     var11 = this.scanDirectory(var1, var12 + "/");
                  } else {
                     if (!var10.canRead()) {
                        continue;
                     }

                     var11 = this.handleFile(var12, var10);
                  }

                  if (!var11) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }
}
