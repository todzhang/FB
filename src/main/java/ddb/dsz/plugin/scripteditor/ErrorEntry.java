package ddb.dsz.plugin.scripteditor;

import java.io.File;

public class ErrorEntry {
   private String text;
   private Long line;
   private File file;
   static String[] directories = new String[]{"Scripts", "TestScripts", "Scripts/Include", "TestScripts/Include"};

   public ErrorEntry() {
      this((String)null, (Long)null, (File)null);
   }

   public ErrorEntry(String var1) {
      this(var1, (Long)null, (File)null);
   }

   public ErrorEntry(String var1, Long var2, File var3) {
      this.text = var1;
      this.line = var2;
      this.file = var3;
   }

   public File getFile() {
      return this.file;
   }

   public void setFile(String var1, File var2, File var3) {
      File var4 = new File(String.format("%s/%s", var3.getParent(), var1));
      if (var4.exists()) {
         this.file = var4;
      } else {
         String[] var5 = directories;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            var4 = new File(String.format("%s/%s/%s", var2, var8, var1));
            if (var4.exists()) {
               this.file = var4;
               return;
            }
         }

         this.file = new File(var1);
      }
   }

   public Long getLine() {
      return this.line;
   }

   public void setLine(Long var1) {
      this.line = var1;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      this.text = var1;
   }
}
