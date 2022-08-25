package ddb.dsz.plugin.scripteditor;

import ddb.util.AbstractEnumeratedTableModel;
import java.util.List;
import java.util.Vector;

public final class ErrorTableModel extends AbstractEnumeratedTableModel<ErrorTableColumns> {
   List<ErrorEntry> data = new Vector();
   ErrorTableModel.EvaluationState state;

   ErrorTableModel() {
      super(ErrorTableColumns.class);
      this.state = ErrorTableModel.EvaluationState.NONE;
   }

   public void clear() {
      synchronized(this) {
         this.data.clear();
      }

      this.fireTableDataChanged();
   }

   public void addError(ErrorEntry var1) {
      boolean var2 = true;
      int var6;
      synchronized(this) {
         var6 = this.data.size();
         this.data.add(var1);
      }

      this.fireTableRowsInserted(var6, var6);
   }

   public void setState(ErrorTableModel.EvaluationState var1) {
      int var2 = this.data.size();
      this.state = var1;
      this.fireTableRowsUpdated(var2, var2);
   }

   public ErrorEntry getError(int var1) {
      synchronized(this) {
         return this.data.size() <= var1 ? null : (ErrorEntry)this.data.get(var1);
      }
   }

   public Object getValueAt(int i, ErrorTableColumns e) {
      ErrorEntry var3 = null;
      synchronized(this) {
         if (i >= 0 && i < this.data.size()) {
            var3 = (ErrorEntry)this.data.get(i);
         } else if (i == this.data.size()) {
            var3 = this.state.ee;
         }
      }

      if (var3 == null) {
         return i == 0 && e.equals(ErrorTableColumns.MESSAGE) ? "Compiling ....." : null;
      } else {
         switch(e) {
         case LINE:
            return var3.getLine();
         case MESSAGE:
            return var3.getText();
         case FILE:
            return var3.getFile();
         default:
            return null;
         }
      }
   }

   public int getRowCount() {
      synchronized(this) {
         return this.data.size() + 1;
      }
   }

   public static enum EvaluationState {
      NONE(""),
      RUNNING("Compiling"),
      SUCCESS("Sucessfully compiled"),
      FAILURE("Compile failed");

      ErrorEntry ee = null;

      private EvaluationState(String var3) {
         this.ee = new ErrorEntry();
         this.ee.setText(var3);
      }
   }
}
