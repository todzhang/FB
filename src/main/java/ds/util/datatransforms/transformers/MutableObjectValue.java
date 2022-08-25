package ds.util.datatransforms.transformers;

import ddb.dsz.core.data.ObjectValue;
import ds.util.datatransforms.DataType;

public interface MutableObjectValue extends ObjectValue {
   void addValue(DataType var1, String var2, Object var3);
}
