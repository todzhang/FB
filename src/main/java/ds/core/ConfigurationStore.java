package ds.core;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.Plugin;
import ddb.util.JaxbCache;
import ds.jaxb.configuration.ColorType;
import ds.jaxb.configuration.ConfigurationType;
import ds.jaxb.configuration.ObjectFactory;
import ds.jaxb.configuration.ObjectType;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ConfigurationStore {
   private File root;
   private CoreController core;
   private Map<Class<? extends Plugin>, Map<String, Object>> classToOptions;

   public ConfigurationStore(String file, CoreController core) {
      this(new File(file), core);
   }

   public ConfigurationStore(File file, CoreController core) {
      this.root = null;
      this.core = core;
      this.classToOptions = new Hashtable();
      if (file != null) {
         if (!file.exists()) {
            if (file.mkdir()) {
               this.root = file;
            }

         } else if (file.isDirectory()) {
            this.root = file;

            Unmarshaller var4;
            try {
               JAXBContext var3 = JaxbCache.getContext(ObjectFactory.class);
               var4 = var3.createUnmarshaller();
            } catch (JAXBException var17) {
               this.core.logEvent(Level.WARNING, "Unable to load user configuration", var17);
               return;
            }

            File[] var5 = this.root.listFiles(new ConfigurationStore.XmlFileFilter());
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];

               try {
                  Object var9 = var4.unmarshal(var8);
                  ConfigurationType var10 = null;
                  if (var9 instanceof JAXBElement) {
                     JAXBElement var11 = (JAXBElement)JAXBElement.class.cast(var9);
                     var9 = var11.getValue();
                  }

                  if (var9 instanceof ConfigurationType) {
                     var10 = (ConfigurationType)ConfigurationType.class.cast(var9);
                  }

                  if (var10 != null) {
                     Class var19 = Class.forName(var10.getClazz(), false, DSConstants.getClassLoader());
                     Class var12 = var19.asSubclass(Plugin.class);
                     Object var13 = (Map)this.classToOptions.get(var12);
                     if (var13 == null) {
                        var13 = new Hashtable();
                        this.classToOptions.put((Class<? extends Plugin>) var12, (Map<String, Object>) var13);
                     }

                     Iterator var14 = var10.getObject().iterator();

                     while(var14.hasNext()) {
                        ObjectType var15 = (ObjectType)var14.next();
                        if (var15.getString() != null) {
                           ((Map)var13).put(var15.getName(), var15.getString());
                        } else if (var15.getColor() != null) {
                           Color var16 = new Color(var15.getColor().getRed().intValue(), var15.getColor().getGreen().intValue(), var15.getColor().getBlue().intValue());
                           ((Map)var13).put(var15.getName(), var16);
                        } else if (var15.getData() != null) {
                           ((Map)var13).put(var15.getName(), var15.getData());
                        } else {
                           ((Map)var13).put(var15.getName(), this.extractList(var15));
                        }
                     }
                  }
               } catch (Throwable var18) {
               }
            }

         }
      }
   }

   private Collection<Object> extractList(ObjectType objectType) {
      Vector var2 = new Vector();
      Iterator var3 = objectType.getObject().iterator();

      while(var3.hasNext()) {
         ObjectType var4 = (ObjectType)var3.next();
         if (var4.getString() != null) {
            var2.add(var4.getString());
         } else if (var4.getColor() != null) {
            Color var5 = new Color(var4.getColor().getRed().intValue(), var4.getColor().getGreen().intValue(), var4.getColor().getBlue().intValue());
            var2.add(var5);
         } else if (var4.getData() != null) {
            var2.add(var4.getData());
         } else {
            var2.add(this.extractList(var4));
         }
      }

      return var2;
   }

   public void commitSettings() {
      synchronized(this) {
         Marshaller var3;
         ObjectFactory var4;
         try {
            var4 = new ObjectFactory();
            JAXBContext var2 = JaxbCache.getContext(ObjectFactory.class);
            var3 = var2.createMarshaller();
         } catch (JAXBException var17) {
            this.core.logEvent(Level.WARNING, "Unable to load user configuration", var17);
            return;
         }

         Iterator var5 = this.classToOptions.keySet().iterator();

         while(var5.hasNext()) {
            Class var6 = (Class)var5.next();
            ConfigurationType var7 = new ConfigurationType();
            var7.setClazz(var6.getCanonicalName());
            Map var8 = (Map)this.classToOptions.get(var6);

            ObjectType var11;
            for(Iterator var9 = var8.keySet().iterator(); var9.hasNext(); var7.getObject().add(var11)) {
               String var10 = (String)var9.next();
               var11 = new ObjectType();
               var11.setName(var10);
               Object var12 = var8.get(var10);
               if (var12 instanceof Collection) {
                  this.addListToObject((Collection)Collection.class.cast(var12), var11.getObject());
               } else if (var12 instanceof Color) {
                  ColorType var13 = new ColorType();
                  Color var14 = (Color)Color.class.cast(var12);
                  var13.setRed(BigInteger.valueOf((long)var14.getRed()));
                  var13.setGreen(BigInteger.valueOf((long)var14.getGreen()));
                  var13.setBlue(BigInteger.valueOf((long)var14.getBlue()));
                  var11.setColor(var13);
               } else if (var12 instanceof byte[]) {
                  var11.setData((byte[])byte[].class.cast(var12));
               } else {
                  var11.setString(var12.toString());
               }
            }

            try {
               var3.marshal(var4.createConfiguration(var7), new FileOutputStream(this.root.getAbsolutePath() + "/" + var6.getSimpleName() + ".xml"));
            } catch (Throwable var16) {
               this.core.logEvent(Level.WARNING, "Unable to save configuration", var16);
            }
         }

      }
   }

   private void addListToObject(Collection<?> src, Collection<ObjectType> dst) {
      ObjectType var5;
      for(Iterator var3 = src.iterator(); var3.hasNext(); dst.add(var5)) {
         Object var4 = var3.next();
         var5 = new ObjectType();
         if (var4 instanceof Collection) {
            Collection var6 = (Collection)Collection.class.cast(var4);
            this.addListToObject(var6, var5.getObject());
         } else if (var4 instanceof Color) {
            ColorType var8 = new ColorType();
            Color var7 = (Color)Color.class.cast(var4);
            var8.setRed(BigInteger.valueOf((long)var7.getRed()));
            var8.setGreen(BigInteger.valueOf((long)var7.getGreen()));
            var8.setBlue(BigInteger.valueOf((long)var7.getBlue()));
            var5.setColor(var8);
         }

         if (var4 instanceof byte[]) {
            var5.setData((byte[])byte[].class.cast(var4));
         } else {
            var5.setString(var4.toString());
         }
      }

   }

   public void setOption(Class<? extends Plugin> plugin, String key, Object val) {
      synchronized(this) {
         Map optionMap = (Map)this.classToOptions.get(plugin);
         if (optionMap == null) {
            optionMap = new Hashtable();
            this.classToOptions.put(plugin, optionMap);
         }

         ((Map)optionMap).put(key, val);
      }
   }

   public Object getOption(Class<? extends Plugin> plugin, String key) {
      synchronized(this) {
         Map var4 = (Map)this.classToOptions.get(plugin);
         return var4 == null ? null : var4.get(key);
      }
   }

   public void setOption(Plugin plugin, String key, Object val) {
      synchronized(this) {
         this.setOption(plugin.getClass(), key, val);
      }
   }

   public Object getOption(Plugin plugin, String key) {
      synchronized(this) {
         return this.getOption(plugin.getClass(), key);
      }
   }

   private class XmlFileFilter implements FilenameFilter {
      private XmlFileFilter() {
      }

      @Override
      public boolean accept(File dir, String name) {
         return name.matches(".*\\.xml");
      }

      // $FF: synthetic method
      XmlFileFilter(Object var2) {
         this();
      }
   }
}
