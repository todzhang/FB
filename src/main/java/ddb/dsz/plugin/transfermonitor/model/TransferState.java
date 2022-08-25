package ddb.dsz.plugin.transfermonitor.model;

import java.util.Comparator;

public enum TransferState {
   STARTED,
   SUCCESS,
   FAILURE,
   DONE;

   public static Comparator<TransferState> COMPARATOR = new Comparator<TransferState>() {
      public int compare(TransferState var1, TransferState var2) {
         if (var1 == var2) {
            return 0;
         } else if (var1 == null) {
            return -1;
         } else if (var2 == null) {
            return 1;
         } else if (var1.equals(TransferState.STARTED) && !var1.equals(TransferState.STARTED)) {
            return -1;
         } else {
            return var2.equals(TransferState.STARTED) ? 1 : 0;
         }
      }
   };
}
