package org.syntax.jedit;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.undo.UndoableEdit;
import org.syntax.jedit.tokenmarker.TokenMarker;

public class SyntaxDocument extends PlainDocument {
   protected TokenMarker tokenMarker;

   public TokenMarker getTokenMarker() {
      return this.tokenMarker;
   }

   public void setTokenMarker(TokenMarker var1) {
      this.tokenMarker = var1;
      if (var1 != null) {
         this.tokenMarker.insertLines(0, this.getDefaultRootElement().getElementCount());
         this.tokenizeLines();
      }
   }

   public void tokenizeLines() {
      this.tokenizeLines(0, this.getDefaultRootElement().getElementCount());
   }

   public void tokenizeLines(int var1, int var2) {
      if (this.tokenMarker != null && this.tokenMarker.supportsMultilineTokens()) {
         Segment var3 = new Segment();
         Element var4 = this.getDefaultRootElement();
         var2 += var1;

         try {
            for(int var5 = var1; var5 < var2; ++var5) {
               Element var6 = var4.getElement(var5);
               int var7 = var6.getStartOffset();
               this.getText(var7, var6.getEndOffset() - var7 - 1, var3);
               this.tokenMarker.markTokens(var3, var5);
            }
         } catch (BadLocationException var8) {
            var8.printStackTrace();
         }

      }
   }

   public void beginCompoundEdit() {
   }

   public void endCompoundEdit() {
   }

   public void addUndoableEdit(UndoableEdit var1) {
   }

   protected void fireInsertUpdate(DocumentEvent var1) {
      if (this.tokenMarker != null) {
         ElementChange var2 = var1.getChange(this.getDefaultRootElement());
         if (var2 != null) {
            this.tokenMarker.insertLines(var2.getIndex() + 1, var2.getChildrenAdded().length - var2.getChildrenRemoved().length);
         }
      }

      super.fireInsertUpdate(var1);
   }

   protected void fireRemoveUpdate(DocumentEvent var1) {
      if (this.tokenMarker != null) {
         ElementChange var2 = var1.getChange(this.getDefaultRootElement());
         if (var2 != null) {
            this.tokenMarker.deleteLines(var2.getIndex() + 1, var2.getChildrenRemoved().length - var2.getChildrenAdded().length);
         }
      }

      super.fireRemoveUpdate(var1);
   }
}
