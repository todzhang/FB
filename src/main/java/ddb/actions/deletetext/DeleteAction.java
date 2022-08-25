package ddb.actions.deletetext;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

public class DeleteAction extends AbstractAction {
   private DeleteScope direction;
   JTextComponent component;

   public DeleteAction(JTextComponent var1, DeleteScope var2) {
      this.component = var1;
      this.direction = var2;
   }

   @Override
   public void actionPerformed(ActionEvent var1) {
      try {
         switch(this.direction) {
         case CURSOR_TO_END:
            int var2 = this.component.getCaretPosition();
            int var3 = this.component.getText().length();
            this.component.select(var2, var3);
            this.component.cut();
            break;
         case WHOLE_LINE:
            this.component.setText("");
         }
      } catch (Exception var4) {
      }

   }
}
