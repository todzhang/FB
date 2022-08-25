package ddb.dsz.plugin.verifier;

import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

public class StateRenderer extends DszTableCellRenderer {
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (var7 instanceof JLabel && value instanceof VerifierState) {
         VerifierState var8 = (VerifierState)VerifierState.class.cast(value);
         JLabel var9 = (JLabel)JLabel.class.cast(var7);
         switch(var8) {
         case NotVerified:
            var9.setForeground(Color.BLACK);
            break;
         case Verifying:
            var9.setForeground(Color.YELLOW);
            break;
         case VerifyFailure:
         case NoLog:
            var9.setForeground(Color.RED);
            break;
         case VerifySuccess:
            var9.setForeground(Color.GREEN);
         }
      }

      return var7;
   }
}
