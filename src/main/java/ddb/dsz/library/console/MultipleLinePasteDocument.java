package ddb.dsz.library.console;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MultipleLinePasteDocument extends PlainDocument {
   ExcessLineListener excess;

   public MultipleLinePasteDocument(ExcessLineListener excess) {
      this.excess = excess;
   }

   @Override
   public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      int var4 = str.indexOf(10);
      int var5 = str.indexOf(13);
      if (var4 == -1 || var5 != -1 && var5 < var4) {
         var4 = var5;
      }

      if (var4 != -1) {
         if (var4 == 0 && str.length() > 1) {
            this.insertString(offs, str.substring(1), a);
         } else if (var4 != 0) {
            super.insertString(offs, str.substring(0, var4), a);
            this.excess.handleExcessData(str.substring(var4));
         }
      } else {
         super.insertString(offs, str, a);
      }

   }
}
