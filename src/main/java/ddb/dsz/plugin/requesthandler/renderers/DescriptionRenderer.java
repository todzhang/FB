package ddb.dsz.plugin.requesthandler.renderers;

import ddb.dsz.plugin.requesthandler.model.RequestTableColumns;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.DisplayTransformer;
import ddb.dsz.plugin.requesthandler.tranformers.ScopeTransformer;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;

public class DescriptionRenderer extends DszTableCellRenderer {
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof RequestedOperation) {
         RequestedOperation operation = (RequestedOperation) value;
         if (column == RequestTableColumns.SCOPE.ordinal()) {
            value = ScopeTransformer.getInstance().transform(value);
         } else {
            value = DisplayTransformer.getInstance().transform(value);
         }
      }

      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }
}
