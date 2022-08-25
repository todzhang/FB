package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.dsz.plugin.logviewer.gui.renderer.CustomRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeBase implements ActionListener {
   int base;
   String prefix;
   CustomRenderer renderer;

   public ChangeBase(int newBase, String prefix, CustomRenderer renderer) {
      this.base = newBase;
      this.prefix = prefix;
      this.renderer = renderer;
   }

   public void actionPerformed(ActionEvent arg0) {
      this.renderer.setBase(this.base, this.prefix);
   }
}
