package ddb.dsz.plugin.monitor;

import ddb.dsz.core.task.Task;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class CommandListCellRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
      Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
      if (!(var6 instanceof JLabel)) {
         return var6;
      } else {
         JLabel var7 = (JLabel)var6;
         MonitoredCommand var8 = (MonitoredCommand)var2;
         Task var9 = var8.getTask();
         if (var9 == null) {
            var7.setText("Error accessing task");
            return var7;
         } else {
            if (var8.isChangedSinceLastViewing()) {
               var7.setFont(var7.getFont().deriveFont(1));
            } else {
               var7.setFont(var7.getFont().deriveFont(0));
            }

            var7.setText(var8.getTask().getId() + ": " + var9.getCommandName());
            return var7;
         }
      }
   }
}
