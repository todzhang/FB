package ddb.dsz.plugin.filemanager.ver3.browser;

import ddb.targetmodel.filemodel.FileObjectComparator;
import ddb.targetmodel.filemodel.FileObjectFields;

public enum DirectoryListingColumns {
   Name("Name", true, FileObjectFields.File_Name),
   AltName("Alt Name", true, FileObjectFields.File_AlternateName),
   Size("Size", true, FileObjectFields.File_Size),
   Created("Created", true, FileObjectFields.File_Created),
   Modified("Modified", true, FileObjectFields.File_Modified),
   Accessed("Accessed", true, FileObjectFields.File_Accessed);

   String text;
   boolean show;
   FileObjectFields field;
   FileObjectComparator comparator;

   private DirectoryListingColumns(String var3, boolean var4, FileObjectFields var5) {
      this.text = var3;
      this.show = var4;
      this.field = var5;
      this.comparator = new FileObjectComparator(var5);
   }

   public String toString() {
      return this.text;
   }

   public boolean isShow() {
      return this.show;
   }

   public FileObjectComparator getComparator() {
      return this.comparator;
   }

   public FileObjectFields getField() {
      return this.field;
   }
}
