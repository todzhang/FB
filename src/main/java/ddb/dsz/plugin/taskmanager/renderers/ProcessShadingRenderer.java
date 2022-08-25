package ddb.dsz.plugin.taskmanager.renderers;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.TaskManagerOptions;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ProcessShadingRenderer extends ProcessBasicRenderer {
   public ProcessShadingRenderer(CoreController core, TableModel model, TaskManagerOptions options) {
      super(core, model, options);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      ProcessInformation procInfo = this.getProcessInformation(table, row);
      if (c instanceof JLabel && procInfo != null) {
         JLabel label = (JLabel)JLabel.class.cast(c);
         Color col = Color.BLACK;
         switch(procInfo.getType()) {
         case CORE_OS:
            col = this.options.coreOsColor;
            break;
         case MALICIOUS_SOFTWARE:
            col = this.options.maliceColor;
            break;
         case SAFE:
            col = this.options.safeColor;
            break;
         case SECURITY_PRODUCT:
            col = this.options.secProdColor;
            break;
         case NONE:
            col = this.options.unknownColor;
         }

         label.setForeground(col);
      }

      return c;
   }
}
