package ddb.imagemanager;

import java.awt.Dimension;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.ReferenceMap;

public class ImageManager {
   Map<String, ImageIcon> nameToIcon = LazyMap.decorate(new ReferenceMap(), new LoadImageOnDemand(this));
   Map<Dimension, Map<String, ImageIcon>> sizeToImageMap = LazyMap.decorate(new ReferenceMap(), new CreateSizeMapOnDemand(this));
   public static final Dimension SIZE128 = new Dimension(128, 128);
   public static final Dimension SIZE256 = new Dimension(256, 256);
   public static final Dimension SIZE64 = new Dimension(64, 64);
   public static final Dimension SIZE48 = new Dimension(48, 48);
   public static final Dimension SIZE32 = new Dimension(32, 32);
   public static final Dimension SIZE22 = new Dimension(22, 22);
   public static final Dimension SIZE16 = new Dimension(16, 16);
   public static final Dimension BANNER_SIZE;
   public static final Dimension NOTICE_SIZE;
   private static final String BROKEN_FILE = "images/file_broken.png";
   private static ImageManager manager;

   private static synchronized ImageManager getManager() {
      if (manager == null) {
         manager = new ImageManager();
      }

      return manager;
   }

   private ImageManager() {
   }

   public static ImageIcon getIcon(String logo, Dimension dimension) {
      try {
         if (dimension == null) {
            return null;
         } else {
            return logo == null ? getIcon("images/file_broken.png", dimension) : (ImageIcon)((Map)getManager().sizeToImageMap.get(dimension)).get(logo);
         }
      } catch (Exception var3) {
         return null;
      }
   }

   ImageIcon getBrokenFile(String var1, Dimension var2) {
      return !var1.equals("images/file_broken.png") ? getIcon("images/file_broken.png", var2) : null;
   }

   ImageIcon getFullIcon(String var1) {
      ImageIcon var2 = (ImageIcon)this.nameToIcon.get(var1);
      if (var2 == null) {
         System.out.println("Unable to get icon for '" + var1 + "'.");
      }

      return var2;
   }

   static {
      BANNER_SIZE = SIZE64;
      NOTICE_SIZE = SIZE128;
   }
}
