package ddb.listeners;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JTextField;

public class CursorControlAction extends AbstractAction {
   private CursorControlAction.Destination dest;
   private JTextField textField;

   public CursorControlAction(JTextField var1, CursorControlAction.Destination var2) {
      this.dest = var2;
      this.textField = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      switch(this.dest) {
      case START:
         this.textField.setCaretPosition(0);
         break;
      case END:
         this.textField.setCaretPosition(this.textField.getText().length());
      }

   }

   public static enum Destination {
      START,
      END;
   }
}
