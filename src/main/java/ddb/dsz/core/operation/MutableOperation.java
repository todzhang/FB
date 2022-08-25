package ddb.dsz.core.operation;

import java.util.Calendar;

public interface MutableOperation extends Operation {
   void setLastTime(Calendar lastTime);
}
