package ddb.dsz.plugin.about;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.about.jaxb.version.ObjectFactory;
import ddb.dsz.plugin.about.jaxb.version.VersionType;
import ddb.imagemanager.ImageManager;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.JaxbCache;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/cardgame.png")
@DszName("About")
@DszDescription("About Danderspritz")
public class About extends NoHostAbstractPlugin implements Plugin {
   public static final String ABOUT_ICON = "images/package_games_card.png";
   public static final String[] VERSION_DIRS = new String[]{"Version", "."};
   public static final String FILLED = "images/apply.png";
   public static final String UNFILLED = "images/gg_ignored.png";
   JTable table;
   About.VersionTableModel model;
   Icon requirementsFilled;
   Icon requirementsUnfilled;

   public About() {
      super.setName("About");
      super.setCareAboutLocalEvents(true);
   }

   @Override
   protected int init2() {
      this.model = new About.VersionTableModel();
      this.table = new JTable(this.model);
      super.setDisplay(new JScrollPane(this.table));
      this.requirementsFilled = ImageManager.getIcon("images/apply.png", this.core.getLabelImageSize());
      this.requirementsUnfilled = ImageManager.getIcon("images/gg_ignored.png", this.core.getLabelImageSize());
      final CoreController var1 = this.core;
      this.core.submit(new Runnable() {
         Unmarshaller unmarsh = null;

         {
            try {
               JAXBContext var3 = JaxbCache.getContext(ObjectFactory.class);
               this.unmarsh = var3.createUnmarshaller();
            } catch (JAXBException var4) {
               var4.printStackTrace();
               var1.logEvent(Level.WARNING, "Unable to determine version numbers", var4);
            }

         }

         private void handleProjectDirectory(File var1x) {
            String[] var2 = About.VERSION_DIRS;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               this.handleVersionDirectory(new File(String.format("%s/%s", var1x.getAbsolutePath(), var5)));
            }

         }

         private void handleVersionDirectory(File var1x) {
            if (var1x != null) {
               if (var1x.isDirectory()) {
                  File[] var2 = var1x.listFiles();
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     File var5 = var2[var4];
                     this.loadVersionFile(var5);
                  }

               }
            }
         }

         private void loadVersionFile(File var1x) {
            if (this.unmarsh != null) {
               try {
                  Object var2 = this.unmarsh.unmarshal(var1x);
                  if (var2 instanceof JAXBElement) {
                     var2 = ((JAXBElement)JAXBElement.class.cast(var2)).getValue();
                  }

                  if (var2 instanceof VersionType) {
                     About.this.model.addVersion((VersionType)VersionType.class.cast(var2));
                  }
               } catch (JAXBException var3) {
               }

            }
         }

         @Override
         public void run() {
            File var1x = new File(About.this.core.getResourceDirectory());
            if (var1x.exists() && var1x.isDirectory()) {
               try {
                  File[] var2 = var1x.listFiles();
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     File var5 = var2[var4];
                     this.handleProjectDirectory(var5);
                  }
               } catch (Throwable var6) {
                  var6.printStackTrace();
               }

               About.this.model.addVersion("Java Runtime", System.getProperty("java.version", "Unknown"));
            }
         }
      });
      return 0;
   }

   public static void main(String[] args) throws Throwable {
      Class var1 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var2 = var1.getMethod("main", args.getClass());
      var2.invoke((Object)null, args);
   }

   private final class VersionTableModel extends AbstractEnumeratedTableModel<About.VersionTableColumns> implements TableModel {
      List<About.VersionInformation> versions = new Vector();

      public VersionTableModel() {
         super(About.VersionTableColumns.class);
      }

      @Override
      public int getRowCount() {
         return this.versions.size();
      }

      @Override
      public String getColumnName(About.VersionTableColumns e) {
         switch(e) {
         case NAME:
            return "Product";
         case VERSION:
            return "Version";
         default:
            return "";
         }
      }

      @Override
      public Class<?> getColumnClass(About.VersionTableColumns e) {
         switch(e) {
         case NAME:
            return String.class;
         case VERSION:
            return String.class;
         default:
            return null;
         }
      }

      @Override
      public Object getValueAt(int i, About.VersionTableColumns e) {
         About.VersionInformation var3 = (About.VersionInformation)this.versions.get(i);
         if (var3 == null) {
            return null;
         } else {
            switch(e) {
            case NAME:
               return var3.name;
            case VERSION:
               return var3.version;
            default:
               return null;
            }
         }
      }

      public void addVersion(VersionType var1) {
         this.addVersion(About.this.new VersionInformation(var1));
      }

      public void addVersion(String var1, String var2) {
         this.addVersion(About.this.new VersionInformation(var1, var2));
      }

      private void addVersion(About.VersionInformation var1) {
         int var2;
         synchronized(this) {
            var2 = this.versions.size();
            this.versions.add(var1);
         }

         this.fireTableRowsInserted(var2, var2);
         this.fireTableRowsUpdated(0, var2);
      }
   }

   public class VersionInformation {
      public final String name;
      public final String version;

      public VersionInformation(VersionType var2) {
         this.name = var2.getValue();
         this.version = String.format("%d.%d.%d.%s", var2.getMajor(), var2.getMinor(), var2.getFix(), var2.getBuild());
      }

      public VersionInformation(String var2, String var3) {
         this.name = var2;
         this.version = var3;
      }
   }

   public enum VersionTableColumns {
      NAME,
      VERSION;
   }
}
