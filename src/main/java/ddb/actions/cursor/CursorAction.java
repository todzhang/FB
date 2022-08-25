package ddb.actions.cursor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

public class CursorAction extends AbstractAction {
   private CursorScope direction;
   private JTextComponent component;

   public CursorAction(JTextComponent var1, CursorScope var2) {
      this.component = var1;
      this.direction = var2;
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      try {
         switch(this.direction) {
         case END:
            this.component.setCaretPosition(this.component.getText().length());
            break;
         case START:
            this.component.setCaretPosition(0);
         }
      } catch (Exception var3) {
      }

   }
}
