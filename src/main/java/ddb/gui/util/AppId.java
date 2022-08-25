package ddb.gui.util;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;

public class AppId {
   private AppId() {
   }

   public static void setCurrentProcessExplicitAppUserModelID(String var0) {
      if (System.getProperty("os.name").equals("Windows 7")) {
         SetCurrentProcessExplicitAppUserModelID(new WString(var0));
      }

   }

   private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString var0);

   static {
      Native.register("shell32");
   }
}
