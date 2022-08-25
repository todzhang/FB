package ddb;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class CheckThreadViolationRepaintManager extends RepaintManager {
   boolean activeChecking = true;

   public void setEnableChecking(boolean var1) {
      this.activeChecking = var1;
   }

   public synchronized void addInvalidComponent(JComponent var1) {
      this.checkThreadViolations(var1);
      super.addInvalidComponent(var1);
   }

   public void addDirtyRegion(JComponent var1, int var2, int var3, int var4, int var5) {
      this.checkThreadViolations(var1);
      super.addDirtyRegion(var1, var2, var3, var4, var5);
   }

   private void checkThreadViolations(JComponent var1) {
      if (this.activeChecking) {
         if (!SwingUtilities.isEventDispatchThread() && var1.isShowing()) {
            Exception var2 = new Exception();
            boolean var3 = false;
            boolean var4 = false;
            StackTraceElement[] var5 = var2.getStackTrace();
            StackTraceElement[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               StackTraceElement var9 = var6[var8];
               if (var3 && var9.getClassName().startsWith("javax.swing.")) {
                  var4 = true;
               }

               if (var9.getClassName().startsWith("ddb.writequeue.WriteQueue")) {
                  return;
               }

               if ("repaint".equals(var9.getMethodName())) {
                  var3 = true;
               }

               if (var9.getClassName().equals("sun.awt.image.ImageFetcher") && var9.getMethodName().equals("fetchloop")) {
                  return;
               }
            }

            if (var3 && !var4) {
               return;
            }

            var2.printStackTrace();
         }

      }
   }
}
