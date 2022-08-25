package ddb.dsz.plugin.transfermonitor.model;

import ddb.dsz.plugin.transfermonitor.TransferMonitorColumns;
import ddb.util.TableSorter;
import java.util.Calendar;
import java.util.Comparator;
import javax.swing.table.TableModel;

public class TransferMonitorTableSorter extends TableSorter {
   private static final Comparator<Calendar> compareCalendars = new Comparator<Calendar>() {
      public int compare(Calendar var1, Calendar var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 == null) {
            return 1;
         } else {
            return var2 == null ? -1 : var1.compareTo(var2);
         }
      }
   };

   public TransferMonitorTableSorter(TableModel var1) {
      super(var1);
   }

   @Override
   public int compareRowsByColumn(int rowIndex1, int rowIndex2, int columnIndex) {
      if (!(this.model instanceof TransferMonitorModel)) {
         return 0;
      } else if (columnIndex == -1) {
         return 0;
      } else {
         TransferMonitorModel var4 = (TransferMonitorModel)this.model;
         TransferRecord var5 = var4.getRecord(rowIndex1);
         TransferRecord var6 = var4.getRecord(rowIndex2);
         if (var5 == null && var6 == null) {
            return 0;
         } else if (var5 == null) {
            return -1;
         } else if (var6 == null) {
            return 1;
         } else {
            switch(TransferMonitorColumns.values()[columnIndex]) {
            case ID:
               if (var5.getId() == var6.getId()) {
                  return 0;
               } else {
                  if (var5.getId() < var6.getId()) {
                     return -1;
                  }

                  return 1;
               }
            case LOCAL:
               return var5.getLocal().compareTo(var6.getLocal());
            case REMOTE:
               return var5.getRemote().compareTo(var6.getRemote());
            case SIZE:
               int var7 = TransferState.COMPARATOR.compare(var5.getState(), var6.getState());
               if (var7 != 0) {
                  return var7;
               } else if (var5.getSize().compareTo(0L) != 0 && var6.getSize().compareTo(0L) != 0) {
                  if (var5.getSize().compareTo(var5.getTransfered()) <= 0 && var6.getSize().compareTo(var6.getTransfered()) <= 0) {
                     if (var5.getSize().compareTo(var6.getSize()) == 0) {
                        return 0;
                     } else {
                        if (var5.getSize().compareTo(var6.getSize()) < 0) {
                           return -1;
                        }

                        return 1;
                     }
                  } else if (var5.getSize().compareTo(var5.getTransfered()) <= 0) {
                     return 1;
                  } else if (var6.getSize().compareTo(var6.getTransfered()) <= 0) {
                     return -1;
                  } else if (var5.getTransfered().compareTo(var6.getTransfered()) > 0) {
                     return 1;
                  } else {
                     if (var5.getTransfered().compareTo(var6.getTransfered()) < 0) {
                        return -1;
                     }

                     return 0;
                  }
               } else if (var5.getTransfered() == var6.getTransfered()) {
                  return 0;
               } else {
                  if (var5.getTransfered().compareTo(var6.getTransfered()) < 0) {
                     return -1;
                  }

                  return 1;
               }
            case STATE:
               return TransferState.COMPARATOR.compare(var5.getState(), var6.getState());
            case TIME_ACCESSED:
               return compareCalendars.compare(var5.getAccessed(), var6.getAccessed());
            case TIME_CREATED:
               return compareCalendars.compare(var5.getCreated(), var6.getCreated());
            case TIME_MODIFIED:
               return compareCalendars.compare(var5.getModified(), var6.getModified());
            default:
               return 0;
            }
         }
      }
   }
}
