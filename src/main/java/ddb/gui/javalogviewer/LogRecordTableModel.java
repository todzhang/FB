package ddb.gui.javalogviewer;

import ddb.util.FrequentlyAppendedTableModel;
import java.util.Calendar;
import java.util.logging.LogRecord;

public class LogRecordTableModel extends FrequentlyAppendedTableModel<LogRecordTableColumns, LogRecord> {
   LogRecordTableModel() {
      super(LogRecordTableColumns.class);
   }

   public Object getValueAt(int i, LogRecordTableColumns e) {
      LogRecord var3 = (LogRecord)super.getRecord(i);
      if (var3 == null) {
         return null;
      } else {
         switch(e) {
         case TIME:
            Calendar var4 = Calendar.getInstance();
            var4.clear();
            var4.setTimeInMillis(var3.getMillis());
            return var4;
         case LEVEL:
            return var3;
         case MESSAGE:
            return var3.getMessage();
         case SOURCECLASS:
            return var3.getSourceClassName();
         case SOURCEMETHOD:
            return var3.getSourceMethodName();
         case LOGGER:
            return var3.getLoggerName();
         default:
            return null;
         }
      }
   }

   @Override
   public String getColumnName(LogRecordTableColumns e) {
      return e.getName();
   }

   @Override
   public Class<?> getColumnClass(LogRecordTableColumns e) {
      return e.getClazz();
   }
}
