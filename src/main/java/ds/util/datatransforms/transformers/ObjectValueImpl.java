package ds.util.datatransforms.transformers;

import ddb.dsz.core.data.ObjectValue;
import ds.util.datatransforms.DataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.collections.map.TransformedMap;

public class ObjectValueImpl implements ObjectValue, MutableObjectValue {
   static final Transformer StringToLowerCase = new LowerCaseTransformer();
   List<Map<String, Object>> hashes = new ArrayList(DataType.values().length);
   private static final Pattern nameSplit = Pattern.compile("([^\\[]+)\\[([0-9]+)\\]");

   public static final void AddData(ObjectValue objectValue, DataType dataType, String var2, Object var3) {
      if (objectValue instanceof MutableObjectValue) {
         ((MutableObjectValue)MutableObjectValue.class.cast(objectValue)).addValue(dataType, var2, var3);
      }

   }

   public ObjectValueImpl() {
      for(DataType dt: DataType.values()) {
         this.hashes.add(TransformedMap.decorate(new MultiValueMap(), StringToLowerCase, dt.getTransformer()));
      }

   }

   @Override
   public void addValue(DataType dataType, String var2, Object var3) {
      this.getHash(dataType).put(var2, var3);
   }

   private static List<String> getStages(String stages) {
      return stages == null ? Collections.EMPTY_LIST : Arrays.asList(stages.toLowerCase().split("::"));
   }

   @Override
   public String getString(String var1) {
      return (String)String.class.cast(this.getValue(DataType.STRING, var1));
   }

   @Override
   public List<String> getStrings(String var1) {
      return this.getValues(DataType.STRING, var1);
   }

   @Override
   public Set<String> getStringNames() {
      return this.getKeys(DataType.STRING);
   }

   @Override
   public Boolean getBoolean(String var1) {
      return (Boolean)Boolean.class.cast(this.getValue(DataType.BOOLEAN, var1));
   }

   @Override
   public List<Boolean> getBooleans(String var1) {
      return this.getValues(DataType.BOOLEAN, var1);
   }

   @Override
   public Set<String> getBooleanNames() {
      return this.getKeys(DataType.BOOLEAN);
   }

   @Override
   public Long getInteger(String var1) {
      return (Long)Long.class.cast(this.getValue(DataType.INTEGER, var1));
   }

   @Override
   public List<Long> getIntegers(String var1) {
      return this.getValues(DataType.INTEGER, var1);
   }

   @Override
   public Set<String> getIntegerNames() {
      return this.getKeys(DataType.INTEGER);
   }

   @Override
   public ObjectValueImpl getObject(String var1) {
      return (ObjectValueImpl)ObjectValueImpl.class.cast(this.getValue(DataType.OBJECT, var1));
   }

   @Override
   public List<ObjectValue> getObjects(String var1) {
      return this.getValues(DataType.OBJECT, var1);
   }

   @Override
   public Set<String> getObjectNames() {
      return this.getKeys(DataType.OBJECT);
   }

   private Object getValue(DataType var1, String var2) {
      return this.getValue(var1, getStages(var2));
   }

   private Object getValue(DataType dataType, List<String> var2) {
      List var3 = this.getValues(dataType, var2);
      return var3.size() > 0 ? var3.get(0) : null;
   }

   private List getValues(DataType dataType, String stages) {
      return this.getValues(dataType, getStages(stages));
   }

   protected List<Object> getValues(DataType dataType, List<String> var2) {
      if (var2.size() == 0) {
         return Collections.emptyList();
      } else {
         String var3 = (String)var2.get(0);
         Matcher var4 = nameSplit.matcher(var3);
         String var5 = "";
         int var6 = -1;
         if (var4.matches()) {
            var5 = var4.group(1);
            var6 = Integer.parseInt(var4.group(2));
         } else {
            var5 = var3;
         }

         if (var2.size() > 1) {
            List var7 = this.getObjects(var5);
            if (var6 == -1) {
               Vector var8 = new Vector();
               Iterator var9 = var7.iterator();

               while(var9.hasNext()) {
                  ObjectValue var10 = (ObjectValue)var9.next();
                  if (var10 instanceof ObjectValueImpl) {
                     var8.addAll(((ObjectValueImpl)ObjectValueImpl.class.cast(var10)).getValues(dataType, var2.subList(1, var2.size())));
                  }
               }

               return var8;
            }

            if (var6 >= 0 && var6 < var7.size()) {
               return ((ObjectValueImpl)ObjectValueImpl.class.cast(var7.get(var6))).getValues(dataType, var2.subList(1, var2.size()));
            }
         } else {
            Object var11 = this.getHash(dataType).get(var5);
            if (var11 instanceof List) {
               List var12 = (List)List.class.cast(var11);
               if (var6 == -1) {
                  return var12;
               }

               if (var6 >= 0 && var6 < var12.size()) {
                  return Collections.singletonList(var12.get(var6));
               }
            }
         }

         return Collections.emptyList();
      }
   }

   private Set<String> getKeys(DataType var1) {
      return this.getHash(var1).keySet();
   }

   private Map<String, Object> getHash(DataType var1) {
      return (Map)this.hashes.get(var1.ordinal());
   }
}
