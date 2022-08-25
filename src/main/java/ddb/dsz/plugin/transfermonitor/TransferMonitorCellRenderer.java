package ddb.dsz.plugin.transfermonitor;

import ddb.GuiConstants;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

public class TransferMonitorCellRenderer extends DszTableCellRenderer {
   ImageIcon errorIcon;
   ImageIcon runningIcon;
   ImageIcon completedIcon;

   public TransferMonitorCellRenderer(CoreController var1) {
      this.errorIcon = ImageManager.getIcon("images/tm_fatal.png", var1.getLabelImageSize());
      this.runningIcon = ImageManager.getIcon("images/exec.png", var1.getLabelImageSize());
      this.completedIcon = ImageManager.getIcon("images/tm_completed.png", var1.getLabelImageSize());
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JLabel var7 = null;
      JLabel var8;
      if (TransferMonitorColumns.values()[column].equals(TransferMonitorColumns.STATE)) {
         var8 = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         var8.setIcon(this.runningIcon);
         var8.setText("");
         var8.setAlignmentX(0.5F);
         switch((TransferState)TransferState.class.cast(value)) {
         case FAILURE:
            var8.setIcon(this.errorIcon);
            break;
         case SUCCESS:
            var8.setIcon(this.completedIcon);
            break;
         case STARTED:
            var8.setIcon(this.runningIcon);
         }

         var7 = var8;
      } else {
         var8 = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         var8.setIcon((Icon)null);
         var7 = var8;
         if (TransferMonitorColumns.values()[column].equals(TransferMonitorColumns.ID)) {
            var8.setAlignmentX(1.0F);
            var8.setHorizontalAlignment(4);
         }
      }

      var7.setFont(GuiConstants.FIXED_WIDTH_FONT.Basic);
      return var7;
   }
}
