package ddb.dsz.plugin.logviewer.gui.renderer;

import ddb.util.Guid;
import java.awt.Component;
import javax.swing.JLabel;

public class GuidRenderer extends CustomTableCellRenderer {
   Guid guid;

   public GuidRenderer() {
      this.guid = Guid.NULL;
   }

   public void setGuid(Guid guid) {
      this.guid = guid;
   }

   protected Component modifyComponent(JLabel label, Object value) {
      if (this.guid.equals(value)) {
         label.setText("Live: " + label.getText());
      }

      return label;
   }
}
