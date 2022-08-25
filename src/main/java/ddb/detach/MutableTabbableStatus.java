package ddb.detach;

import java.awt.Color;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;

public interface MutableTabbableStatus extends TabbableStatus {
   void setDetails(String var1);

   void setDetails(String var1, Color var2, Color var3);

   void setHost(String var1);

   void setHost(String var1, Color var2, Color var3);

   void setIndeterminate(boolean var1);

   void setProgressModel(BoundedRangeModel var1);

   void setStatusIcon(Icon var1);

   void setProgressModelChanged();
}
