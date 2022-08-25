package ddb.dsz.plugin.taskmanager.renderers;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.TaskManagerOptions;
import ddb.dsz.plugin.taskmanager.models.ProcessTableColumns;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ProcessBasicRenderer extends DszTableCellRenderer {
   protected final TableModel model;
   protected final TaskManagerOptions options;
   protected final CoreController core;
   protected final Color basicForeground;
   private final JLabel highlight = new JLabel("");

   public ProcessBasicRenderer(CoreController core, TableModel model, TaskManagerOptions options) {
      this.core = core;
      this.model = model;
      this.options = options;
      this.basicForeground = this.getForeground();
      this.highlight.setBackground(Color.BLUE);
      this.highlight.setForeground(Color.WHITE);
      this.highlight.setOpaque(true);
   }

   protected ProcessInformation getProcessInformation(JTable table, int row) {
      return (ProcessInformation)table.getModel().getValueAt(row, ProcessTableColumns.PROCESS.ordinal());
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      ((Component)c).setForeground(this.basicForeground);
      ProcessInformation procInfo = this.getProcessInformation(table, row);
      JLabel label;
      if (procInfo != null && procInfo.isHighlight() && c instanceof JLabel) {
         label = (JLabel)JLabel.class.cast(c);
         this.highlight.setText(label.getText());
         this.highlight.setBorder(label.getBorder());
         c = this.highlight;
      }

      if (c instanceof JLabel) {
         label = (JLabel)JLabel.class.cast(c);
         label.setIcon((Icon)null);
         if (value instanceof Number) {
            label.setHorizontalAlignment(4);
         } else {
            label.setHorizontalAlignment(2);
         }
      }

      return (Component)c;
   }
}
