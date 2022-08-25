package ds.util.contextmenu;

import ddb.dsz.core.contextmenu.CommandCallbackListener;
import ddb.dsz.core.contextmenu.ContextMenuAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.imagemanager.ImageManager;
import ddb.util.JaxbCache;
import ds.jaxb.context.ContextMenuType;
import ds.jaxb.context.EntryType;
import ds.jaxb.context.MenuItemBase;
import ds.jaxb.context.ObjectFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ContextMenuImpl extends JPopupMenu {
   private static final Object LOCK = new Object();
   private static Unmarshaller unmarsh = null;

   private ContextMenuImpl(Collection<URL> var1, CoreController var2, CommandCallbackListener var3, HostInfo var4, Collection<Map<String, String>> var5, Object var6, ContextMenuAction var7) {
      this.handleContextFiles(var1, var2, var3, var4, var5, var6, var7);
   }

   private void handleContextFiles(Collection<URL> var1, CoreController var2, CommandCallbackListener var3, HostInfo var4, Collection<Map<String, String>> var5, Object var6, ContextMenuAction var7) {
      List var8 = var2.getUserAliases(var4);
      Iterator var9 = var1.iterator();

      while(var9.hasNext()) {
         URL var10 = (URL)var9.next();
         ContextMenuType var11 = getContextMenuType(var10, var2);
         if (var11 == null) {
            throw new NullPointerException(String.format("Could not find %s", var10.toString()));
         }

         List var12 = handleMenu(var11, var2, var3, var4, var5, var6, var7, (String)null);

         for(int var13 = 0; var13 < var12.size(); ++var13) {
            if (var12.get(var13) == null) {
               this.addSeparator();
            } else {
               this.add((JMenuItem)var12.get(var13));
            }
         }
      }

      JMenu var18 = new JMenu("As User");
      this.add(var18);
      Iterator var19 = var8.iterator();

      while(var19.hasNext()) {
         String var20 = (String)var19.next();
         JMenu var21 = new JMenu(var20);
         var18.add(var21);
         Iterator var22 = var1.iterator();

         while(var22.hasNext()) {
            URL var14 = (URL)var22.next();
            ContextMenuType var15 = getContextMenuType(var14, var2);
            if (var15 == null) {
               throw new NullPointerException(String.format("Could not find %s", var14.toString()));
            }

            List var16 = handleMenu(var15, var2, var3, var4, var5, var6, var7, var20);

            for(int var17 = 0; var17 < var16.size(); ++var17) {
               if (var16.get(var17) == null) {
                  var21.addSeparator();
               } else {
                  var21.add((JMenuItem)var16.get(var17));
               }
            }
         }
      }

      if (var8.size() == 0) {
         var18.setEnabled(false);
      }

   }

   private static List<JMenuItem> handleMenu(ContextMenuType var0, CoreController var1, CommandCallbackListener var2, HostInfo var3, Collection<Map<String, String>> var4, Object var5, ContextMenuAction var6, String var7) {
      Vector var8 = new Vector();
      Iterator var9 = var0.getEntryOrSeperatorOrSubmenu().iterator();

      while(true) {
         while(true) {
            MenuItemBase var10;
            Iterator var28;
            while(true) {
               if (!var9.hasNext()) {
                  return var8;
               }

               var10 = (MenuItemBase)var9.next();
               if (!(var10 instanceof MenuItemBase)) {
                  break;
               }

               MenuItemBase var11 = (MenuItemBase)var10;
               if (var11.isLive() && var1.isLiveOperation() || var11.isReplay() && !var1.isLiveOperation()) {
                  boolean var12 = true;
                  Iterator var13 = var11.getRequired().iterator();

                  while(var13.hasNext()) {
                     String var14 = (String)var13.next();
                     if (var4.size() == 0) {
                        var12 = false;
                     }

                     if (!var12) {
                        break;
                     }

                     Iterator var15 = var4.iterator();

                     while(var15.hasNext()) {
                        Map var16 = (Map)var15.next();
                        if (var16.get(var14) == null) {
                           var12 = false;
                           break;
                        }
                     }
                  }

                  boolean var23 = true;
                  var28 = var11.getForbidden().iterator();

                  while(var28.hasNext()) {
                     String var31 = (String)var28.next();
                     Iterator var33 = var4.iterator();

                     while(var33.hasNext()) {
                        Map var17 = (Map)var33.next();
                        if (var17.get(var31) != null) {
                           var23 = false;
                        }
                     }
                  }

                  if (var12 && var23) {
                     break;
                  }
               }
            }

            if (var10 instanceof EntryType) {
               EntryType var22 = (EntryType)EntryType.class.cast(var10);
               JMenuItem var25;
               if (var4.size() == 1) {
                  Map var27 = (Map)var4.iterator().next();
                  var25 = new JMenuItem(substitute(var27, var22.getLabel()));
                  if (var22.getIcon() != null) {
                     var25.setIcon(ImageManager.getIcon(substitute(var27, var22.getIcon()), var1.getLabelImageSize()));
                  }
               } else {
                  var25 = new JMenuItem(var22.getLabel());
                  if (var22.getIcon() != null) {
                     var25.setIcon(ImageManager.getIcon(var22.getIcon(), var1.getLabelImageSize()));
                  }
               }

               ArrayList var29 = new ArrayList();

               ItemActionListener.IndividualCommand var34;
               for(var28 = var4.iterator(); var28.hasNext(); var29.add(var34)) {
                  Map var32 = (Map)var28.next();
                  var34 = new ItemActionListener.IndividualCommand();
                  var34.command = substitute(var32, var22.getCommand());
                  var34.parameters = new Vector();
                  if (var22.getAction() != null) {
                     Iterator var35 = var22.getAction().getLine().iterator();

                     while(var35.hasNext()) {
                        String var18 = (String)var35.next();

                        try {
                           var34.parameters.add(substitute(var32, var18));
                        } catch (Exception var20) {
                           System.err.println(var34.parameters);
                           System.err.println("replace = " + var32);
                           System.err.println("s = " + var18);
                           var20.printStackTrace();
                        }
                     }
                  }

                  if (var7 != null) {
                     var34.parameters.add("useralias=" + var7);
                  }
               }

               var25.addActionListener(new ItemActionListener(var1, var29, var25.getText(), new RegisterIdCallback(var2, var25.getText()), var3, var6, var22.isResult(), var22.isVerify(), var22.getAction().getLine(), var5));
               var8.add(var25);
            } /*else if (var10 instanceof String) {
               var8.add(null);
            } */else if (var10 instanceof ContextMenuType) {
               ContextMenuType var21 = (ContextMenuType)ContextMenuType.class.cast(var10);
               List var24 = handleMenu(var21, var1, var2, var3, var4, var5, var6, var7);
               JMenu var26 = new JMenu(var21.getName());

               for(int var30 = 0; var30 < var24.size(); ++var30) {
                  if (var24.get(var30) == null) {
                     var26.addSeparator();
                  } else {
                     var26.add((JMenuItem)var24.get(var30));
                  }
               }

               var8.add(var26);
            }
         }
      }
   }

   private static Unmarshaller getUnmarshaller() throws JAXBException {
      synchronized(LOCK) {
         if (unmarsh == null) {
            JAXBContext var1 = JaxbCache.getContext(ObjectFactory.class);
            unmarsh = var1.createUnmarshaller();
         }
      }

      return unmarsh;
   }

   private static ContextMenuType getContextMenuType(URL var0, CoreController var1) {
      try {
         Unmarshaller var2 = getUnmarshaller();
         if (var2 == null) {
            return null;
         }

         Object var3 = var2.unmarshal(var0);
         if (var3 instanceof JAXBElement) {
            var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
         }

         if (var3 instanceof ContextMenuType) {
            return (ContextMenuType)ContextMenuType.class.cast(var3);
         }

         var1.logEvent(Level.CONFIG, "Unable to load " + var0);
      } catch (JAXBException var4) {
         var1.logEvent(Level.CONFIG, var4.getMessage(), var4);
      }

      return null;
   }

   public static String substitute(Map<String, String> var0, String var1) {
      if (var1 == null) {
         return null;
      } else {
         if (var0 != null) {
            Iterator var2 = var0.keySet().iterator();

            while(var2.hasNext()) {
               String var3 = (String)var2.next();
               if (var0.get(var3) != null) {
                  var1 = var1.replaceAll("\\{" + var3 + "\\}", Matcher.quoteReplacement((String)var0.get(var3)));
               } else {
                  var1 = var1.replaceAll("\\{" + var3 + "\\}", "");
               }
            }
         }

         return var1;
      }
   }
}
