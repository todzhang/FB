package ddb.targetmodel.filemodel;

public enum DriveType {
   FLOPPYDISK("images/devices/usbpendrive_unmount.png"),
   CDDRIVE("images/devices/cdrom_unmount.png"),
   RAMDISK("images/devices/smartmedia_unmount.png"),
   DRIVE("images/devices/hdd_unmount.png"),
   DRIVENETWORK("images/devices/network_local.png"),
   DRIVESIMULATED("images/devices/pipe.png"),
   DOCUMENT("images/mime-types/doc.png"),
   EDITDELETE("images/editdelete.png"),
   FBCOMPLETE("images/fb-complete.png"),
   FILEMANAGER("images/file-manager.png"),
   FILESEARCH("images/filesearch.png"),
   FOLDER("images/folder_blue.png"),
   ACCESS_DENIED_FOLDER("images/folder_locked.png"),
   EMPTYFOLDER("images/folder_grey.png"),
   QUESTION("images/question.png"),
   REFRESH("images/refresh.png"),
   RESET("images/reset.png"),
   SEARCH("images/search.png"),
   TEMP(""),
   PENDING_FILE("images/services.png"),
   PENDING_FOLDER("images/folder_favorites.png");

   String icon;

   private DriveType(String var3) {
      this.icon = var3;
   }

   public String getIcon() {
      return this.icon;
   }
}
