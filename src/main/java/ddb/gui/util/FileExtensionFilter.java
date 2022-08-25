package ddb.gui.util;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class FileExtensionFilter extends FileFilter implements FilenameFilter, java.io.FileFilter {
   private String suffix;
   private boolean acceptDirectories;

   public FileExtensionFilter(String var1, boolean var2) {
      this.suffix = var1;
      this.acceptDirectories = var2;
   }

   public boolean accept(File var1, String var2) {
      if (var2.endsWith(this.suffix)) {
         return true;
      } else {
         return this.acceptDirectories && var1.getName().equals(var2);
      }
   }

   public boolean accept(File var1) {
      if (var1.getName().endsWith(this.suffix)) {
         return true;
      } else {
         return this.acceptDirectories && var1.isDirectory();
      }
   }

   public String getDescription() {
      return this.suffix + " files";
   }
}
