package ddb.dsz.plugin.monitor;

import ddb.GuiConstants;
import ddb.dsz.core.task.TaskId;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public class OutputTableCellRenderer extends DszTableCellRenderer {
   private static final int MAX_OUTPUT_LENGTH = 124;

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!(var7 instanceof JLabel)) {
         return var7;
      } else {
         JLabel var8 = (JLabel)var7;
         MonitoredCommandOutput var9 = (MonitoredCommandOutput) value;
         TaskId var10 = var9.getTask().getId();
         String var11 = var9.getCommandOutput();
         String var12 = String.format("%s:  %s", var9.getTask().getCommandName(), var11);
         if (var12.length() > 124) {
            var12 = var12.substring(0, 123) + "...";
         }

         var8.setFont(GuiConstants.FIXED_WIDTH_FONT.Basic);
         var8.setText(var12);
         return var7;
      }
   }
}
