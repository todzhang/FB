package ddb.dsz.plugin.requesthandler.renderers;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.requesthandler.model.RequestStatus;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public class StatusRenderer extends DszTableCellRenderer {
   CoreController core;

   public StatusRenderer(CoreController core) {
      this.core = core;
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (var7 instanceof JLabel && value instanceof RequestStatus) {
         JLabel var8 = (JLabel)JLabel.class.cast(var7);
         RequestStatus var9 = (RequestStatus)RequestStatus.class.cast(value);
         var8.setHorizontalAlignment(0);
         var8.setText("");
         var8.setIcon(ImageManager.getIcon(var9.getImage(), this.core.getLabelImageSize()));
      }

      return var7;
   }
}
