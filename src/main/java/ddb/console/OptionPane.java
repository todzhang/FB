package ddb.console;

import ddb.detach.TabbableOption;
import ddb.imagemanager.ImageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class OptionPane extends TabbableOption {
   private static OptionPane instance = null;
   private static final String THEME_DIR = "\\Gui\\Config\\Console\\themes\\";
   private final Set<String> directories = new HashSet();
   private TextPanel panel = new TextPanel();

   public static final synchronized OptionPane getInstance() {
      if (instance == null) {
         instance = new OptionPane();
         instance.setName("Text");
         instance.setShortDescription("The current text settings");
      }

      return instance;
   }

   @Override
   public JComponent getDisplay() {
      return this.panel;
   }

   private OptionPane() {
   }

   public ColorTheme getSharedTheme() {
      return this.panel.currentTheme;
   }

   public void addDirectory(String var1) {
      this.addDirectories((Collection)Collections.singletonList(var1));
   }

   public void addDirectories(String[] var1) {
      if (var1 == null) {
         var1 = new String[0];
      }

      this.addDirectories((Collection)Arrays.asList(var1));
   }

   public void addDirectories(Collection<String> var1) {
      synchronized(var1) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.directories.add(var4);
         }

      }
   }

   public List<ColorTheme> getAllThemes() {
      Vector var1 = new Vector();
      var1.add(this.panel.currentTheme);
      ArrayList var2 = new ArrayList(this.directories.size());
      synchronized(this.directories) {
         var2.addAll(this.directories);
      }

      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();

         try {
            File var5 = new File(var4, "\\Gui\\Config\\Console\\themes\\");
            if (var5.exists()) {
               File[] var6 = var5.listFiles((f, name) -> name.toLowerCase().endsWith(".xml"));
               if (var6 != null) {
                  for(File var10: var6) {
                     try {
                        Properties var11 = new Properties();
                        var11.loadFromXML(new FileInputStream(var10));
                        ColorTheme var12 = new ColorTheme(var11);
                        var1.add(var12);
                     } catch (Exception var13) {
                        var13.printStackTrace();
                     }
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return var1;
   }

   public ColorTheme displayThemeSelectionDialog(ColorTheme var1) {
      List var2 = this.getAllThemes();
      Object var3 = JOptionPane.showInputDialog(this.getDisplay(), "Choose a theme", "Console Color Theme Selector", 3, ImageManager.getIcon(this.getLogo(), ImageManager.SIZE22), var2.toArray(new ColorTheme[var2.size()]), var1.getName());
      return (ColorTheme)var3;
   }
}
