package ddb.dsz.plugin.taskmanager.enumerated;

import ddb.imagemanager.ImageManager;
import javax.swing.ImageIcon;

public enum FileStatus {
   NONE("Unknown", (String)null),
   SAFE("Safe", "images/button_ok.png"),
   SECURITY_PRODUCT("Security Product", "images/important.png"),
   CORE_OS("Core OS", "images/gkrellm2.png"),
   MALICIOUS_SOFTWARE("Malicious/Dangerous Software", "images/error.png");

   String text;
   String icon;

   private FileStatus(String string, String icon) {
      this.text = string;
      this.icon = icon;
   }

   public String toString() {
      return this.text;
   }

   public ImageIcon getIcon() {
      return this.icon == null ? null : ImageManager.getIcon(this.icon, ImageManager.SIZE16);
   }
}
