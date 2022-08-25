package ddb.dsz.plugin.verifier;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.TaskState;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;

public class TaskStateRenderer extends DszTableCellRenderer {
   CoreController core;

   public TaskStateRenderer(CoreController var1) {
      this.core = var1;
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (var7 instanceof JLabel && value instanceof TaskState) {
         TaskState var8 = (TaskState)TaskState.class.cast(value);
         JLabel var9 = (JLabel)JLabel.class.cast(var7);
         var9.setText("");
         switch(var8) {
         case FAILED:
         case KILLED:
         case SUCCEEDED:
            var9.setIcon((Icon)null);
            break;
         default:
            var9.setIcon(ImageManager.getIcon("images/verifier_running.png", this.core.getLabelImageSize()));
         }
      }

      return var7;
   }
}
