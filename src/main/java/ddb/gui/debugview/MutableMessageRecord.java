package ddb.gui.debugview;

import java.util.Calendar;

public interface MutableMessageRecord extends MessageRecord {
   void setMessage(String var1);

   void setPriority(Importance var1);

   void setSection(String var1);

   void setThread(int var1);

   void setTime(Calendar var1);

   void append(String var1);
}
