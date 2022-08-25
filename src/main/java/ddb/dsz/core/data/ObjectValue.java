package ddb.dsz.core.data;

import java.util.List;
import java.util.Set;

public interface ObjectValue {
   String getString(String var1);

   List<String> getStrings(String var1);

   Set<String> getStringNames();

   Boolean getBoolean(String var1);

   List<Boolean> getBooleans(String var1);

   Set<String> getBooleanNames();

   Long getInteger(String var1);

   List<Long> getIntegers(String var1);

   Set<String> getIntegerNames();

   ObjectValue getObject(String var1);

   List<ObjectValue> getObjects(String var1);

   Set<String> getObjectNames();
}
