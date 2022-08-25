package ds.core;

import com.birosoft.liquid.LiquidLookAndFeel;
import ddb.GuiConstants;
import ddb.detach.TabNavigationListener;
import ddb.imagemanager.ImageManager;
import ddb.util.JaxbCache;
import ds.core.commandevents.CommandEventDemultiplexorImpl;
import ds.jaxb.mimemap.MimeMap;
import ds.jaxb.mimemap.MimeMapList;
import ds.jaxb.mimemap.ObjectFactory;
import ds.util.contextmenu.ContextMenuImpl;
import ds.util.datatransforms.CommandMetaDataClosure;
import ds.util.datatransforms.DataTransformerImpl2;
import ds.util.datatransforms.DisplayTaskClosure;
import ds.util.datatransforms.ReadXmlClosure;
import ds.util.datatransforms.VariableTaskClosure;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class DSConstants {
   public static final String HIDE_PLUGIN_ICON = "images/folder_sent_mail.png";
   public static final String OPTIONS_ICON = "images/utilities.png";
   public static final String NEW_INSTANCE_ICON = "images/edit_add.png";
   public static final String APP_TITLE = "DanderSpritz";
   public static final String REPLAY_TITLE = "DanderSpritz Replay";
   public static final String VERSION = "1.2";
   public static final String LP_CONFIG_FILE = "config.xml";
   public static final String IMAGE_REGISTER_FILE = "resources/icons.xml";
   public static final String IPC_COMMS_SCHEMA = "xml/schema/ipcComms.xsd";
   public static final int IPC_COMMS_END_CODE = 1;
   public static final String DS_ICON = "images/Rupert.png";
   public static final String SPLASHSCREEN_MAIN_ICON = "images/earth.png";
   public static final String KEYBINDINGS = "keybindings.xml";
   public static final String DEFAULT_RESOURCE_DIRECTORY = "Dsz";
   public static final String MIME_MAP_LOCATION = "images/mime-types/mime-map.xml";
   private static Map<String, String> suffixToIcon = null;
   public static int FRAME_WIDTH = 500;
   public static int FRAME_HEIGHT = 500;
   public static int WINDOW_WIDTH = 500;
   public static int WINDOW_HEIGHT = 500;
   public static int MAIN_TAB_ALIGNMENT = 1;
   public static int SUB_TAB_ALIGNMENT = 2;
   private static ClassLoader loader = null;
   public static final Color DEFAULT_BG_COLOR;
   public static final Color DEFAULT_FG_COLOR;
   public static final Color SECONDARY_FG_COLOR;
   public static final Color SECONDARY_BG_COLOR;
   public static final Color PROMPT_MODE_BG_COLOR;
   public static final Color PROMPT_MODE_FG_COLOR;
   public static final Color ERROR_COLOR;
   public static final Color WARNING_COLOR;
   public static final Color NOTICE_COLOR;
   public static final int ITALIC = -1;
   public static final int BOLD = -2;
   public static final int UNDERLINE = -3;
   public static final int DEFAULT = 0;
   public static final int EMERGENCY = 1;
   public static final int ALERT = 2;
   public static final int CRITICAL = 3;
   public static final int ERROR = 4;
   public static final int WARNING = 5;
   public static final int NOTICE = 6;
   public static final int INFO = 7;
   public static final int DEBUG = 8;
   public static final String LOGGER_NAME = "ds.core";
   public static final int SYNC_CORE = 100;
   public static final int SYNC_CORE_SUB = 1000;
   public static final int SYNC_PLUGIN = 10000;
   public static final int SYNC_PLUGIN_SUB = 100000;
   public static final String DSZ_KEYWORD = "DSZ_KEYWORD";
   public static Map<String, String> GENERAL_SETTINGS;
   private static Dimension LABEL_SIZE;
   private static Dimension TAB_SIZE;

   public static void setLoader(ClassLoader var0) {
      loader = var0;
      JaxbCache.setClassLoader(var0);
   }

   public static ClassLoader getClassLoader() {
      ClassLoader var0 = loader;
      if (var0 == null) {
         var0 = Thread.currentThread().getContextClassLoader();
      }

      if (var0 == null) {
         var0 = ClassLoader.getSystemClassLoader();
      }

      return var0;
   }

   public static final List<File> getStartupConfigurationFiles(File config, String filename) {
      String var2 = System.getProperty("DSZ_KEYWORD");
      if (config == null) {
         return Collections.emptyList();
      } else {
         String var3 = "Gui/Config/";
         String var4 = var2 == null ? String.format("%s.xml", filename) : String.format("%s_%s.xml", filename, var2);
         Vector var5 = new Vector();
         var5.add(".");
         File[] var6 = config.listFiles();
         File var10;
         if (var6 != null) {
            File[] var7 = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               var10 = var7[var9];
               if (var10.isDirectory()) {
                  var5.add(var10.getName());
               }
            }
         }

         Vector var11 = new Vector();
         Iterator var12 = var5.iterator();

         while(var12.hasNext()) {
            String var13 = (String)var12.next();
            var10 = new File(config, String.format("%s/%s/%s", var13, var3, var4));
            if (var10.exists()) {
               var11.add(var10);
            }
         }

         return var11;
      }
   }

   public static String getOsString() {
      String var0 = System.getProperty("os.name");
      return var0.regionMatches(true, 0, "Windows", 0, 6) ? System.getProperty("windows.tool.chain", "i386-winnt-vc6") : System.getProperty("linux.tool.chain", "i386-linux_se-gcc");
   }

   public static void addLibraryPath(String libPath) {
      String var1 = null;

      try {
         var1 = (new File(libPath)).getCanonicalPath();
      } catch (IOException var8) {
         var8.printStackTrace();
         return;
      }

      try {
         Field var2 = ClassLoader.class.getDeclaredField("usr_paths");
         var2.setAccessible(true);
         String[] var3 = (String[])((String[])var2.get((Object)null));
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var7.equals(var1)) {
               return;
            }
         }

         var4 = new String[var3.length + 1];
         System.arraycopy(var3, 0, var4, 0, var3.length);
         var4[var3.length] = var1;
         var2.set((Object)null, var4);
      } catch (Throwable var9) {
         var9.printStackTrace();
      }

   }

   public static ThreadFactory namedFactory(final String var0) {
      return new ThreadFactory() {
         int i = 0;

         @Override
         public synchronized Thread newThread(Runnable var1) {
            Thread var2 = new Thread(var1);
            var2.setName(String.format("%s %d", var0, ++this.i));
            var2.setDaemon(true);
            var2.setPriority(1);
            return var2;
         }
      };
   }

   public static String getIcon(String icon) {
      String var1 = "images/mime-types/mime-map.xml";
      synchronized("images/mime-types/mime-map.xml") {
         if (suffixToIcon == null) {
            suffixToIcon = new HashMap();
            JAXBContext var2 = JaxbCache.getContext(ObjectFactory.class);

            try {
               Unmarshaller var3 = var2.createUnmarshaller();
               Object var4 = var3.unmarshal(getClassLoader().getResource("images/mime-types/mime-map.xml"));
               if (var4 instanceof JAXBElement) {
                  var4 = ((JAXBElement)JAXBElement.class.cast(var4)).getValue();
               }

               if (var4 instanceof MimeMapList) {
                  Iterator var5 = ((MimeMapList)MimeMapList.class.cast(var4)).getMap().iterator();

                  while(var5.hasNext()) {
                     MimeMap var6 = (MimeMap)var5.next();
                     suffixToIcon.put(var6.getSuffix(), "images/mime-types/" + var6.getIcon());
                  }
               }
            } catch (Exception var8) {
               return null;
            }
         }
      }

      return icon == null ? null : (String)suffixToIcon.get(icon.toLowerCase());
   }

   public static XMLStreamReader createXMLStreamReader(InputStream inputStream) {
      try {
         return XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
      } catch (XMLStreamException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static boolean jumpRight(JComponent var0) {
      return TabNavigationListener.jumpRight(var0);
   }

   public static boolean jumpLeft(JComponent var0) {
      return TabNavigationListener.jumpLeft(var0);
   }

   public static void InstallDefaults() {
      final Class var0 = LiquidLookAndFeel.class;
      System.setProperty("swing.defaultlaf", var0.getCanonicalName());

      try {
         Runnable var1 = new Runnable() {
            @Override
            public void run() {
               try {
                  UIManager.setLookAndFeel(var0.getCanonicalName());
               } catch (Exception var2) {
                  var2.printStackTrace();
               }

            }
         };
         if (EventQueue.isDispatchThread()) {
            var1.run();
         } else {
            EventQueue.invokeAndWait(var1);
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      UIManager.put("BreadcrumbBar.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Button.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("CheckBox.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("CheckBoxMenuItem.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ColorChooser.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ComboBox.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("EditorPane.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Label.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("List.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Menu.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("MenuBar.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("MenuItem.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("PopupMenu.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("OptionPane.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Panel.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("PasswordField.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ProgressBar.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("RadioButton.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("RadioButtonMenuItem.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ScrollPane.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TabbedPane.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Table.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TableHeader.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TextArea.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TextField.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TextPane.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("TitledBorder.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ToggleButton.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ToolBar.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("ToolTip.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Tree.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      UIManager.put("Viewport.font", GuiConstants.FIXED_WIDTH_FONT.Basic);
      registerDefault("CommandEventDemultiplexor.impl", CommandEventDemultiplexorImpl.class);
      registerDefault("DataTranformer.impl", DataTransformerImpl2.class);
      registerDefault("CommandMetaData.impl", CommandMetaDataClosure.class);
      registerDefault("VariableClosure.impl", VariableTaskClosure.class);
      registerDefault("DisplayClosure.impl", DisplayTaskClosure.class);
      registerDefault("XmlStringClosure.impl", ReadXmlClosure.class);
      registerDefault("ContextMenu.Impl", ContextMenuImpl.class);
   }

   private static void registerDefault(String key, Class<?> val) {
      System.setProperty(key, val.getCanonicalName());
   }

   static void setLabelImageSize(Dimension var0) {
      LABEL_SIZE = var0;
   }

   static void setTabImageSize(Dimension var0) {
      TAB_SIZE = var0;
   }

   public static Dimension getLabelImageSize() {
      return LABEL_SIZE;
   }

   public static Dimension getTabImageSize() {
      return TAB_SIZE;
   }

   private DSConstants() {
   }

   static {
      DEFAULT_BG_COLOR = Color.BLACK;
      DEFAULT_FG_COLOR = Color.LIGHT_GRAY;
      SECONDARY_FG_COLOR = Color.BLUE;
      SECONDARY_BG_COLOR = Color.LIGHT_GRAY;
      PROMPT_MODE_BG_COLOR = Color.RED;
      PROMPT_MODE_FG_COLOR = Color.YELLOW;
      ERROR_COLOR = Color.RED;
      WARNING_COLOR = Color.YELLOW;
      NOTICE_COLOR = Color.GREEN;
      GENERAL_SETTINGS = new HashMap();
      LABEL_SIZE = ImageManager.SIZE16;
      TAB_SIZE = ImageManager.SIZE32;
   }

   public enum StageIcon {
      INTERNAL_DATA("images/coreStageInternalData.png"),
      STARTUP_CONFIG("images/coreStageStartupConfig.png"),
      PLUGIN("images/coreStagePlugins.png"),
      SOCKET("images/coreStageWaiting.png"),
      WAITING("images/coreStageSocket.png");

      String path;

      private StageIcon(String var3) {
         this.path = var3;
      }

      public String getPath() {
         return this.path;
      }
   }

   public enum Icon {
      ABOUT("images/about.png"),
      ERROR("images/fatal.png"),
      INFO("images/info.png"),
      WARNING("images/warning.png"),
      QUIT("images/quit.png"),
      DEBUG("images/bug-buddy.png"),
      LOCAL("images/yast_accessx.png");

      String path;

      private Icon(String path) {
         this.path = path;
      }

      public String getPath() {
         return this.path;
      }
   }
}
