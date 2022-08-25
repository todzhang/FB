package ddb.autocomplete;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class AutoCompleteDocument extends PlainDocument {
   private CompletionService<?> completionService;
   private JTextComponent documentOwner;

   public AutoCompleteDocument(CompletionService<?> var1, JTextComponent var2) {
      this.completionService = var1;
      this.documentOwner = var2;
   }

   protected String complete(String var1) {
      Object var2 = this.completionService.autoComplete(var1);
      return var2 == null ? null : var2.toString();
   }

   @Override
   public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
      if (var2 != null && var2.length() != 0) {
         String var4 = this.getText(0, var1);
         String var5 = this.complete(var4 + var2);
         int var6 = var1 + var2.length();
         if (var5 != null && var4.length() > 0) {
            var2 = var5.substring(Math.max(0, var6 - 1));
            super.insertString(var1, var2, var3);
            this.documentOwner.select(var6, this.getLength());
         } else {
            super.insertString(var1, var2, var3);
         }

      }
   }
}
