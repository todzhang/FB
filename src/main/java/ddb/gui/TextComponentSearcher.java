package ddb.gui;

import ddb.util.StringUtils;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public class TextComponentSearcher implements Searchable {
   public static final String classVersion = "3.0";
   protected JEditorPane editorPane;
   protected JTextComponent text;

   public TextComponentSearcher(JTextComponent c) {
      this.text = c;
      if (this.text instanceof JEditorPane) {
         this.editorPane = new JEditorPane(((JEditorPane)this.text).getContentType(), this.text.getText());
      }

   }

   public boolean find(String what, boolean forward, boolean searchFromBeginning, boolean matchCase, boolean matchWholeWords) {
      this.text.getCaret().setSelectionVisible(true);
      String textToSearch = null;
      if (!(this.text instanceof JEditorPane)) {
         textToSearch = this.text.getText();
      } else {
         this.editorPane.setText(this.text.getText());
         this.editorPane.selectAll();
         textToSearch = this.editorPane.getSelectedText();
      }

      String originalSearchString = what;
      if (!matchCase) {
         what = what.toUpperCase();
         textToSearch = textToSearch.toUpperCase();
      }

      int startPosition;
      if (searchFromBeginning) {
         startPosition = forward ? 0 : textToSearch.length() - 1;
      } else {
         startPosition = this.text.getCaretPosition();
         if (this.text.getSelectedText() != null) {
            if (forward) {
               startPosition = this.text.getSelectionStart() + 1;
            } else {
               startPosition = this.text.getSelectionStart();
               startPosition = startPosition >= 0 ? startPosition : 0;
            }
         }
      }

      int foundPosition;
      if (forward) {
         foundPosition = textToSearch.indexOf(what, startPosition);
      } else {
         String substring = textToSearch.substring(0, startPosition);
         foundPosition = substring.lastIndexOf(what);
      }

      if (foundPosition == -1) {
         this.text.setCaretPosition(this.text.getCaretPosition());
         this.text.getCaret().setSelectionVisible(true);
         return false;
      } else {
         if (matchWholeWords) {
            boolean matchFound = true;
            int prevCharPos = foundPosition - 1;
            if (prevCharPos != -1) {
               String s = textToSearch.substring(prevCharPos, prevCharPos + 1);
               if (StringUtils.containsOnlyAlphaNumerics(s)) {
                  matchFound = false;
               }
            }

            int nextCharPos = foundPosition + what.length();
            if (nextCharPos != textToSearch.length()) {
               String s = textToSearch.substring(nextCharPos, nextCharPos + 1);
               if (StringUtils.containsOnlyAlphaNumerics(s)) {
                  matchFound = false;
               }
            }

            if (!matchFound) {
               if (forward) {
                  if (nextCharPos == textToSearch.length()) {
                     return false;
                  }

                  this.text.setCaretPosition(foundPosition + 1);
               } else {
                  if (prevCharPos == -1) {
                     return false;
                  }

                  this.text.setCaretPosition(foundPosition - 1);
               }

               this.text.getCaret().setSelectionVisible(true);
               return this.find(originalSearchString, forward, false, matchCase, matchWholeWords);
            }
         }

         this.text.setSelectionStart(foundPosition);
         this.text.setSelectionEnd(foundPosition + what.length());
         this.text.getCaret().setSelectionVisible(true);

         try {
            this.text.scrollRectToVisible(this.text.modelToView(this.text.getSelectionStart()));
            return true;
         } catch (BadLocationException var14) {
            return true;
         }
      }
   }

   public void stopFind() {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
