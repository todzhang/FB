package ddb.util;

import ddb.dsz.annotations.DszLogo;
import ds.core.DSConstants;
import ds.jaxb.module.Module;
import ds.jaxb.startup.LoadMacro;
import ds.jaxb.startup.LoadPlugin;
import ds.jaxb.startup.ObjectFactory;
import ds.jaxb.startup.StartupConfig;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class StartupConfigParser {
   public static String LINUX = "Linux";
   public static String WINDOWS = "Windows";

   public static List<PluginInitInfo> parse(InputStream inputStream, String var1, String var2, String var3) throws XMLException {
      String osname = System.getProperty("os.name", "unknown").toLowerCase();
      if (osname.startsWith("windows")) {
         osname = "winnt";
      } else if (osname.startsWith("linux")) {
         osname = "linux";
      } else {
         System.out.println("Unrecognized platform: " + osname);
      }

      Vector vector = new Vector();
      StartupConfig startupConfig = null;

      try {
         JAXBContext var8 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var7 = var8.createUnmarshaller();
         startupConfig = (StartupConfig)var7.unmarshal(inputStream);
      } catch (JAXBException var22) {
         throw new XMLException(var22.getMessage() != null ? var22.getMessage() : "", var22.getCause());
      }

      Iterator var24 = startupConfig.getPluginOrMacro().iterator();

      while(true) {
         String var11;
         String var12;
         LoadPlugin var25;
         String var26;
         BigInteger var27;
         List var28;
         String var29;
         boolean var30;
         PluginInitInfo pluginInitInfo;
         label124:
         do {
            while(var24.hasNext()) {
               Object var9 = var24.next();
               int var18;
               String var19;
               if (var9 instanceof LoadPlugin) {
                  var25 = (LoadPlugin)var9;
                  var11 = var25.getPlatform();
                  var12 = var25.getPluginClass();
                  var26 = var25.getPluginName();
                  var27 = var25.getPluginId();
                  var28 = var25.getInitArgs();
                  var29 = var25.getAlign();
                  var30 = var25.isVisible();

                  for(var18 = 0; var18 < var28.size(); ++var18) {
                     var19 = (String)var28.get(var18);
                     var19 = replaceAllInString(var19, "@@RES_DIR@@", var2);
                     var19 = replaceAllInString(var19, "@@LOG_DIR@@", var1);
                     var19 = replaceAllInString(var19, "@@OS_DIR@@", DSConstants.getOsString());
                     var19 = replaceAllInString(var19, "@@BUILD_TYPE@@", var3);
                     var28.set(var18, var19);
                  }
                  continue label124;
               }

               if (var9 instanceof LoadMacro) {
                  LoadMacro var10 = (LoadMacro)var9;
                  var11 = var10.getPlatform();
                  if (var11 == null || osname.toLowerCase().startsWith(var11.toLowerCase())) {
                     var12 = String.format("Macros/%s", var10.getMacroFile());

                     try {
                        JAXBContext var13 = JaxbCache.getContext(ds.jaxb.module.ObjectFactory.class);
                        Unmarshaller var14 = var13.createUnmarshaller();
                        InputStream var15 = DSConstants.getClassLoader().getResourceAsStream(var12);
                        if (var15 == null) {
                           Logger.getLogger("ds.core").log(Level.SEVERE, String.format("Path not found: %s", var12));
                        } else {
                           var9 = var14.unmarshal(var15);
                           if (var9 instanceof JAXBElement) {
                              var9 = ((JAXBElement)JAXBElement.class.cast(var9)).getValue();
                           }

                           if (var9 instanceof Module) {
                              Module var16 = (Module)var9;
                              List var17 = var16.getInitArgs();

                              for(var18 = 0; var18 < var17.size(); ++var18) {
                                 var19 = (String)var17.get(var18);
                                 var19 = replaceAllInString(var19, "@@RES_DIR@@", var2);
                                 var19 = replaceAllInString(var19, "@@LOG_DIR@@", var1);
                                 var19 = replaceAllInString(var19, "@@OS_DIR@@", DSConstants.getOsString());
                                 var19 = replaceAllInString(var19, "@@BUILD_TYPE@@", var3);
                                 var17.set(var18, var19);
                              }

                              pluginInitInfo = new PluginInitInfo(var16.getClazz(), BigInteger.ZERO, var16.getName(), var17, var10.isVisible(), var10.getAlign());
                              pluginInitInfo.setIcon(var16.getLogo());
                              pluginInitInfo.setIdentifier(var16.getName());
                              vector.add(pluginInitInfo);
                           }
                        }
                     } catch (JAXBException var23) {
                        var23.printStackTrace();
                        throw new XMLException(var23.getMessage() != null ? var23.getMessage() : "", var23.getCause());
                     }
                  }
               }
            }

            return vector;
         } while(var11 != null && !osname.toLowerCase().startsWith(var11.toLowerCase()));

         pluginInitInfo = new PluginInitInfo(var12, var27, var26, var28, var30, var29);
         if (var25.getDetached() != null) {
            pluginInitInfo.setDetached(true);
            if (var25.getDetached().getHeight() != null && var25.getDetached().getWidth() != null) {
               pluginInitInfo.setFrameSize(new Dimension(var25.getDetached().getWidth().intValue(), var25.getDetached().getHeight().intValue()));
            }

            if (var25.getDetached().getPositionX() != null && var25.getDetached().getPositionY() != null) {
               pluginInitInfo.setFramePosition(new Point(var25.getDetached().getPositionX().intValue(), var25.getDetached().getPositionY().intValue()));
            }
         }

         if (var25.getIcon() != null && !var25.getIcon().equals("")) {
            pluginInitInfo.setIcon(var25.getIcon());
         } else {
            try {
               Class var32 = Class.forName(var12, false, StartupConfigParser.class.getClassLoader());
               DszLogo var20 = (DszLogo)var32.getAnnotation(DszLogo.class);
               if (var20 != null) {
                  pluginInitInfo.setIcon(var20.value());
               }
            } catch (Exception var21) {
            }
         }

         pluginInitInfo.setIdentifier(var12);
         vector.add(pluginInitInfo);
      }
   }

   private static String replaceAllInString(String var0, String var1, String var2) {
      if (var2 == null) {
         return var0;
      } else if (var2.contains(var1)) {
         return var0.replaceAll(var1, var2);
      } else {
         while(var0.contains(var1)) {
            var0 = var0.replace(var1, var2);
         }

         return var0;
      }
   }

   public static List<PluginInitInfo> parse(String var0, String var1, String var2, String var3) throws FileNotFoundException, XMLException {
      FileInputStream var4 = new FileInputStream(var0);
      return parse((InputStream)var4, var1, var2, var3);
   }
}
