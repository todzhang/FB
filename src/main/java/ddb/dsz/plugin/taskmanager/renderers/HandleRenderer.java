package ddb.dsz.plugin.taskmanager.renderers;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.TaskManagerOptions;
import ddb.dsz.plugin.taskmanager.enumerated.HandlesStatus;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class HandleRenderer extends ProcessBasicRenderer {
   public HandleRenderer(CoreController core, TableModel model, TaskManagerOptions options) {
      super(core, model, options);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
      if (c instanceof JLabel) {
         if (value == HandlesStatus.HasHandles) {
            ((JLabel)JLabel.class.cast(c)).setIcon(ImageManager.getIcon("images/gnome-system-tools.png", ImageManager.SIZE16));
         } else {
            ((JLabel)JLabel.class.cast(c)).setIcon((Icon)null);
         }
      }

      return c;
   }
}
