package ddb.dsz.core.contextmenu;

import ddb.Factory;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.JPopupMenu;

public abstract class ContextMenuFactory extends Factory {
   public static final String DEFAULT_IMPL = "ContextMenu.Impl";

   public static JPopupMenu createContextMenuURL(Collection<URL> var0, CoreController var1, CommandCallbackListener var2, HostInfo var3, Collection<Map<String, String>> var4, Object var5, ContextMenuAction var6) {
      return (JPopupMenu)Factory.newObject(System.getProperty("ContextMenu.Impl"), new Class[]{Collection.class, CoreController.class, CommandCallbackListener.class, HostInfo.class, Collection.class, Object.class, ContextMenuAction.class}, var0, var1, var2, var3, var4, var5, var6);
   }

   public static JPopupMenu createContextMenuString(String var0, CoreController var1, CommandCallbackListener var2, HostInfo var3, Collection<Map<String, String>> var4, Object var5, ContextMenuAction var6) {
      return createContextMenuString((Collection)Collections.singleton(var0), var1, var2, var3, var4, var5, var6);
   }

   public static JPopupMenu createContextMenuString(Collection<String> var0, CoreController var1, CommandCallbackListener var2, HostInfo var3, Collection<Map<String, String>> var4, Object var5, ContextMenuAction var6) {
      Vector var7 = new Vector();
      Iterator var8 = var0.iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         String[] var10 = var1.getResourcePackages();
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            String var13 = var10[var12];
            File var14 = new File(String.format("%s/%s/Gui/Config/%s", var1.getResourceDirectory(), var13, var9));
            if (var14.exists()) {
               try {
                  var7.add(var14.toURI().toURL());
               } catch (Exception var16) {
                  var16.printStackTrace();
               }
            }
         }
      }

      return createContextMenuURL((Collection)var7, var1, var2, var3, var4, var5, var6);
   }

   public static JPopupMenu createContextMenuURL(URL var0, CoreController var1, CommandCallbackListener var2, HostInfo var3, Collection<Map<String, String>> var4, Object var5, ContextMenuAction var6) {
      return createContextMenuURL((Collection)Collections.singleton(var0), var1, var2, var3, var4, var5, var6);
   }
}
