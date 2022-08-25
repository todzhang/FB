package ddb.util;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class FileExtensionFilter extends FileFilter implements FilenameFilter, java.io.FileFilter {
   private String suffix;
   private boolean acceptDirectories;

   public FileExtensionFilter(String suffix, boolean acceptDirectories) {
      this.suffix = suffix;
      this.acceptDirectories = acceptDirectories;
   }

   @Override
   public boolean accept(File dir, String name) {
      if (name.endsWith(this.suffix)) {
         return true;
      } else {
         return this.acceptDirectories && dir.getName().equals(name);
      }
   }

   @Override
   public boolean accept(File pathname) {
      if (pathname.getName().endsWith(this.suffix)) {
         return true;
      } else {
         return this.acceptDirectories && pathname.isDirectory();
      }
   }

   @Override
   public String getDescription() {
      return this.suffix + " files";
   }
}
