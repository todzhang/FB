package ddb.targetmodel.filemodel;

import java.util.Calendar;
import java.util.Comparator;

public final class FileObjectComparator implements Comparator<FileObject> {
   private final FileObjectFields key;

   public FileObjectComparator(FileObjectFields var1) {
      this.key = var1;
   }

   public final int compare(FileObject var1, FileObject var2) {
      if (var1 == null && var2 == null) {
         return 0;
      } else if (var1 == null && var2 != null) {
         return 1;
      } else if (var2 == null && var1 != null) {
         return -1;
      } else {
         int var3 = this.compareImpl(var1, var2, this.key);
         return var3 == 0 && this.key != FileObjectFields.File_Name ? this.compareImpl(var1, var2, FileObjectFields.File_Name) : var3;
      }
   }

   public final int compareImpl(FileObject var1, FileObject var2, FileObjectFields var3) {
      Data var4 = var1.getDataElement(var3);
      Data var5 = var2.getDataElement(var3);
      if (var4 == null && var5 == null) {
         return 0;
      } else if (var4 == null && var5 != null) {
         return 1;
      } else if (var5 == null && var4 != null) {
         return -1;
      } else if (var4.getValue() == null && var5.getValue() == null) {
         return 0;
      } else if (var4.getValue() == null && var5.getValue() != null) {
         return 1;
      } else if (var5.getValue() == null && var4.getValue() != null) {
         return -1;
      } else if (var4.getValue().getClass().isInstance(var5.getValue())) {
         if (var4.getValue() instanceof String) {
            String var10 = (String)var4.getValue();
            String var13 = (String)var5.getValue();
            return var10.compareToIgnoreCase(var13);
         } else if (var4.getValue() instanceof Calendar) {
            Calendar var9 = (Calendar)var4.getValue();
            Calendar var12 = (Calendar)var5.getValue();
            return var9.compareTo(var12);
         } else if (var4.getValue() instanceof Boolean) {
            Boolean var8 = (Boolean)var4.getValue();
            Boolean var11 = (Boolean)var5.getValue();
            return var8.compareTo(var11);
         } else if (var4.getValue() instanceof Long) {
            Long var6 = (Long)var4.getValue();
            Long var7 = (Long)var5.getValue();
            return var6.compareTo(var7);
         } else {
            System.err.println("Invalid data type: " + var4.getValue().getClass());
            return 0;
         }
      } else {
         return 0;
      }
   }
}
