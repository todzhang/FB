package ddb.dsz.plugin.scripteditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

public class LineFocusMouseListener extends MouseAdapter {
   private final ScriptEditor _editor;
   private final JTable output;

   public LineFocusMouseListener(ScriptEditor var1, JTable var2) {
      this._editor = var1;
      this.output = var2;
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 2) {
         int var2 = var1.getY() / this.output.getRowHeight();
         ErrorEntry var3 = this._editor.getModel().getError(var2);
         if (var3 != null) {
            this._editor.focusOn(var3);
         }
      }

   }
}
