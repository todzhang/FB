package ddb.dsz.plugin.taskmanager.renderers;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.TaskManagerOptions;
import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class FileStatusRenderer extends ProcessBasicRenderer {
   public FileStatusRenderer(CoreController core, TableModel model, TaskManagerOptions options) {
      super(core, model, options);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
      if (value instanceof FileStatus && c instanceof JLabel) {
         FileStatus fs = (FileStatus)FileStatus.class.cast(value);
         ((JLabel)JLabel.class.cast(c)).setIcon(fs.getIcon());
      }

      return c;
   }
}
