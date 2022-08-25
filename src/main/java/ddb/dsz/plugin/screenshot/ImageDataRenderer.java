package ddb.dsz.plugin.screenshot;

import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.Calendar;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ImageDataRenderer extends DefaultListCellRenderer {
   public static final ImageDataRenderer Instance = new ImageDataRenderer();
   Calendar cal = Calendar.getInstance();

   public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
      if (var2 instanceof File) {
         var2 = ((File)File.class.cast(var2)).getName();
      }

      boolean var6 = false;
      if (var2 instanceof ImageData) {
         ImageData var7 = (ImageData)ImageData.class.cast(var2);
         this.cal.setTimeInMillis(var7.time);
         var2 = String.format("%02d:%02d:%02d", this.cal.get(11), this.cal.get(12), this.cal.get(13));
         if (!var7.isViewed()) {
            var6 = true;
         }
      }

      Component var9 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
      Font var8 = var9.getFont();
      if (var6) {
         var8 = var8.deriveFont(1);
      } else {
         var8 = var8.deriveFont(0);
      }

      var9.setFont(var8);
      return var9;
   }
}
