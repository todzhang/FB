package ddb.dsz.plugin.transfermonitor.renderer;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public class StateRenderer extends DszTableCellRenderer {
   CoreController core;

   public StateRenderer(CoreController var1) {
      this.core = var1;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (value instanceof TransferState && var7 instanceof JLabel) {
         JLabel var8 = (JLabel)JLabel.class.cast(var7);
         var8.setText("");
         switch((TransferState)TransferState.class.cast(value)) {
         case FAILURE:
            this.setIcon(ImageManager.getIcon("images/tm_fatal.png", this.core.getLabelImageSize()));
            break;
         case STARTED:
            var8.setIcon(ImageManager.getIcon("images/exec.png", this.core.getLabelImageSize()));
            break;
         case SUCCESS:
            var8.setIcon(ImageManager.getIcon("images/tm_completed.png", this.core.getLabelImageSize()));
         }
      }

      return var7;
   }
}
