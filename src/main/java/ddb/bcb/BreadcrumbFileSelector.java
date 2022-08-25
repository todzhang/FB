package ddb.bcb;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

public class BreadcrumbFileSelector extends BreadcrumbBar {
   protected FileSystemView fsv;
   protected boolean useNativeIcons;

   public BreadcrumbFileSelector() {
      this(true);
   }

   public BreadcrumbFileSelector(boolean var1) {
      super((Icon)null, (String)":", (BreadcrumbBarCallBack)null);
      this.fsv = FileSystemView.getFileSystemView();
      this.useNativeIcons = var1;
      this.callback = new BreadcrumbFileSelector.DirCallback();
      File[] var2 = File.listRoots();
      BreadcrumbItem[] var3 = new BreadcrumbItem[var2.length];
      int var4 = 0;
      File[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         File var8 = var5[var7];
         String var9 = this.fsv.getSystemDisplayName(var8);
         if (var9.length() == 0) {
            var9 = var8.getAbsolutePath();
         }

         BreadcrumbItem var10 = new BreadcrumbItem(new String[]{var9, var8.getAbsolutePath()});
         Icon var11 = this.fsv.getSystemIcon(var8);
         if (var1) {
            var10.setIcon(var11);
         }

         var3[var4++] = var10;
      }

      this.pushChoices(new BreadcrumbItemChoices(var3));
   }

   public void setUseNativeIcons(boolean var1) {
      this.useNativeIcons = var1;
   }

   public FileSystemView getFileSystemView() {
      return this.fsv;
   }

   public class DirCallback implements BreadcrumbBarCallBack {
      protected BreadcrumbItemChoices getChoices(File var1) {
         if (!var1.exists()) {
            return new BreadcrumbItemChoices(new BreadcrumbItem[0]);
         } else if (!var1.isDirectory()) {
            return null;
         } else {
            LinkedList var2 = new LinkedList();
            File[] var3 = var1.listFiles();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               File var6 = var3[var5];
               if (var6.isDirectory() && !var6.isHidden()) {
                  BreadcrumbItem var7 = new BreadcrumbItem(new String[]{var6.getName(), var6.getAbsolutePath()});
                  if (BreadcrumbFileSelector.this.useNativeIcons) {
                     var7.setIcon(BreadcrumbFileSelector.this.fsv.getSystemIcon(var6));
                  }

                  var2.addLast(var7);
               }
            }

            BreadcrumbItem[] var8 = new BreadcrumbItem[var2.size()];
            var4 = 0;

            BreadcrumbItem var10;
            for(Iterator var9 = var2.iterator(); var9.hasNext(); var8[var4++] = var10) {
               var10 = (BreadcrumbItem)var9.next();
            }

            return new BreadcrumbItemChoices(var8);
         }
      }

      public BreadcrumbItemChoices getChoices(BreadcrumbItem[] var1) {
         if (var1 == null) {
            File[] var11 = File.listRoots();
            BreadcrumbItem[] var12 = new BreadcrumbItem[var11.length];
            int var4 = 0;
            File[] var5 = var11;
            int var6 = var11.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];
               String var9 = BreadcrumbFileSelector.this.fsv.getSystemDisplayName(var8);
               if (var9.length() == 0) {
                  var9 = var8.getAbsolutePath();
               }

               BreadcrumbItem var10 = new BreadcrumbItem(new String[]{var9, var8.getAbsolutePath()});
               if (BreadcrumbFileSelector.this.useNativeIcons) {
                  var10.setIcon(BreadcrumbFileSelector.this.fsv.getSystemIcon(var8));
               }

               var12[var4++] = var10;
            }

            return new BreadcrumbItemChoices(var12);
         } else if (var1.length == 0) {
            return null;
         } else {
            String var2 = var1[var1.length - 1].getValue()[1];
            File var3 = new File(var2);
            return this.getChoices(var3);
         }
      }
   }
}
