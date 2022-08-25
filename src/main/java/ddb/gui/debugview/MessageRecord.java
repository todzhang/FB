package ddb.gui.debugview;

import java.util.Calendar;

public interface MessageRecord {
   String getMessage();

   Importance getPriority();

   String getSection();

   int getThread();

   Calendar getTime();
}
