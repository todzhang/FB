package ddb.dsz.plugin.scripteditor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;
import org.syntax.jedit.SyntaxDocument;

public class DssDocument extends SyntaxDocument {
   CompoundEdit ce = null;

   public void beginCompoundEdit() {
      super.beginCompoundEdit();
      synchronized(this) {
         this.ce = new CompoundEdit();
      }
   }

   public void endCompoundEdit() {
      super.endCompoundEdit();
      CompoundEdit var1;
      synchronized(this) {
         var1 = this.ce;
         this.ce = null;
      }

      if (var1 != null) {
         var1.end();
         this.fireUndoableEditUpdate(new UndoableEditEvent(this, var1));
      }

   }

   protected void fireUndoableEditUpdate(UndoableEditEvent var1) {
      synchronized(this) {
         if (this.equals(var1.getSource()) && this.ce != null) {
            this.ce.addEdit(var1.getEdit());
            return;
         }
      }

      super.fireUndoableEditUpdate(var1);
   }
}
