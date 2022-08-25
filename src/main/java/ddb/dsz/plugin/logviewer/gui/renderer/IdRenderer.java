package ddb.dsz.plugin.logviewer.gui.renderer;

import java.awt.Component;
import javax.swing.JLabel;

public class IdRenderer extends CustomTableCellRenderer {
   protected Component modifyComponent(JLabel label, Object value) {
      label.setHorizontalAlignment(4);
      return label;
   }
}
