package ddb.gui;

import ddb.util.StringUtils;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;

public class TextComponentSearchAndReplacer extends TextComponentSearcher implements SearchAndReplaceable {
   public static final String classVersion = "3.0";

   public TextComponentSearchAndReplacer(JTextComponent c) {
      super(c);
   }

   public String getSelectedText() {
      return this.text.getSelectedText();
   }

   public boolean isEditable() {
      return this.text.isEditable();
   }

   public boolean isSelectedTextWholeWord() {
      String s = this.text.getSelectedText();
      if (s == null) {
         return false;
      } else {
         String textToSearch = null;
         if (!(this.text instanceof JEditorPane)) {
            textToSearch = this.text.getText();
         } else {
            this.editorPane.setText(this.text.getText());
            this.editorPane.selectAll();
            textToSearch = this.editorPane.getSelectedText();
         }

         int prevCharPos = this.text.getSelectionStart() - 1;
         if (prevCharPos != -1) {
            s = textToSearch.substring(prevCharPos, prevCharPos + 1);
            if (StringUtils.containsOnlyAlphaNumerics(s)) {
               return false;
            }
         }

         int nextCharPos = this.text.getSelectionEnd();
         if (nextCharPos != textToSearch.length()) {
            s = textToSearch.substring(nextCharPos, nextCharPos + 1);
            if (StringUtils.containsOnlyAlphaNumerics(s)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean replaceSelectedText(String replacement) {
      if (this.text.getSelectedText() != null) {
         this.text.replaceSelection(replacement);
         return true;
      } else {
         return false;
      }
   }
}
