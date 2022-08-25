package org.syntax.jedit;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

public abstract class InputHandler extends KeyAdapter {
   public static final String SMART_HOME_END_PROPERTY = "InputHandler.homeEnd";
   public static final ActionListener BACKSPACE = new InputHandler.backspace();
   public static final ActionListener BACKSPACE_WORD = new InputHandler.backspace_word();
   public static final ActionListener DELETE = new InputHandler.delete();
   public static final ActionListener DELETE_WORD = new InputHandler.delete_word();
   public static final ActionListener END = new InputHandler.end(false);
   public static final ActionListener DOCUMENT_END = new InputHandler.document_end(false);
   public static final ActionListener SELECT_ALL = new InputHandler.select_all();
   public static final ActionListener SELECT_END = new InputHandler.end(true);
   public static final ActionListener SELECT_DOC_END = new InputHandler.document_end(true);
   public static final ActionListener INSERT_BREAK = new InputHandler.insert_break();
   public static final ActionListener INSERT_TAB = new InputHandler.insert_tab();
   public static final ActionListener UNSERT_TAB = new InputHandler.unsert_tab();
   public static final ActionListener HOME = new InputHandler.home(false);
   public static final ActionListener DOCUMENT_HOME = new InputHandler.document_home(false);
   public static final ActionListener SELECT_HOME = new InputHandler.home(true);
   public static final ActionListener SELECT_DOC_HOME = new InputHandler.document_home(true);
   public static final ActionListener NEXT_CHAR = new InputHandler.next_char(false);
   public static final ActionListener NEXT_LINE = new InputHandler.next_line(false);
   public static final ActionListener NEXT_PAGE = new InputHandler.next_page(false);
   public static final ActionListener NEXT_WORD = new InputHandler.next_word(false);
   public static final ActionListener SELECT_NEXT_CHAR = new InputHandler.next_char(true);
   public static final ActionListener SELECT_NEXT_LINE = new InputHandler.next_line(true);
   public static final ActionListener SELECT_NEXT_PAGE = new InputHandler.next_page(true);
   public static final ActionListener SELECT_NEXT_WORD = new InputHandler.next_word(true);
   public static final ActionListener OVERWRITE = new InputHandler.overwrite();
   public static final ActionListener PREV_CHAR = new InputHandler.prev_char(false);
   public static final ActionListener PREV_LINE = new InputHandler.prev_line(false);
   public static final ActionListener PREV_PAGE = new InputHandler.prev_page(false);
   public static final ActionListener PREV_WORD = new InputHandler.prev_word(false);
   public static final ActionListener SELECT_PREV_CHAR = new InputHandler.prev_char(true);
   public static final ActionListener SELECT_PREV_LINE = new InputHandler.prev_line(true);
   public static final ActionListener SELECT_PREV_PAGE = new InputHandler.prev_page(true);
   public static final ActionListener SELECT_PREV_WORD = new InputHandler.prev_word(true);
   public static final ActionListener REPEAT = new InputHandler.repeat();
   public static final ActionListener TOGGLE_RECT = new InputHandler.toggle_rect();
   public static final ActionListener CLIP_COPY = new InputHandler.clip_copy();
   public static final ActionListener CLIP_PASTE = new InputHandler.clip_paste();
   public static final ActionListener CLIP_CUT = new InputHandler.clip_cut();
   public static final ActionListener INSERT_CHAR = new InputHandler.insert_char();
   private static Hashtable<String, ActionListener> actions = new Hashtable();
   protected ActionListener grabAction;
   protected boolean repeat;
   protected int repeatCount;
   protected InputHandler.MacroRecorder recorder;

   public static ActionListener getAction(String var0) {
      return (ActionListener)actions.get(var0);
   }

   public static String getActionName(ActionListener var0) {
      Enumeration var1 = getActions();

      String var2;
      ActionListener var3;
      do {
         if (!var1.hasMoreElements()) {
            return null;
         }

         var2 = (String)var1.nextElement();
         var3 = getAction(var2);
      } while(var3 != var0);

      return var2;
   }

   public static Enumeration<String> getActions() {
      return actions.keys();
   }

   public abstract void addDefaultKeyBindings();

   public abstract void addKeyBinding(String var1, ActionListener var2);

   public abstract void addKeyBinding(KeyStroke var1, ActionListener var2);

   public abstract void removeKeyBinding(String var1);

   public abstract void removeAllKeyBindings();

   public void grabNextKeyStroke(ActionListener var1) {
      this.grabAction = var1;
   }

   public boolean isRepeatEnabled() {
      return this.repeat;
   }

   public void setRepeatEnabled(boolean var1) {
      this.repeat = var1;
   }

   public int getRepeatCount() {
      return this.repeat ? Math.max(1, this.repeatCount) : 1;
   }

   public void setRepeatCount(int var1) {
      this.repeatCount = var1;
   }

   public InputHandler.MacroRecorder getMacroRecorder() {
      return this.recorder;
   }

   public void setMacroRecorder(InputHandler.MacroRecorder var1) {
      this.recorder = var1;
   }

   public abstract InputHandler copy();

   public void executeAction(ActionListener var1, Object var2, String var3) {
      ActionEvent var4 = new ActionEvent(var2, 1001, var3);
      if (var1 instanceof InputHandler.Wrapper) {
         var1.actionPerformed(var4);
      } else {
         boolean var5 = this.repeat;
         int var6 = this.getRepeatCount();
         if (var1 instanceof InputHandler.NonRepeatable) {
            var1.actionPerformed(var4);
         } else {
            for(int var7 = 0; var7 < Math.max(1, this.repeatCount); ++var7) {
               var1.actionPerformed(var4);
            }
         }

         if (this.grabAction == null) {
            if (this.recorder != null && !(var1 instanceof InputHandler.NonRecordable)) {
               if (var6 != 1) {
                  this.recorder.actionPerformed(REPEAT, String.valueOf(var6));
               }

               this.recorder.actionPerformed(var1, var3);
            }

            if (var5) {
               this.repeat = false;
               this.repeatCount = 0;
            }
         }

      }
   }

   public static JEditTextArea getTextArea(EventObject var0) {
      if (var0 != null) {
         Object var1 = var0.getSource();
         if (var1 instanceof Component) {
            Object var2 = (Component)var1;

            while(true) {
               if (var2 instanceof JEditTextArea) {
                  return (JEditTextArea)var2;
               }

               if (var2 == null) {
                  break;
               }

               if (var2 instanceof JPopupMenu) {
                  var2 = ((JPopupMenu)var2).getInvoker();
               } else {
                  var2 = ((Component)var2).getParent();
               }
            }
         }
      }

      System.err.println("BUG: getTextArea() returning null");
      System.err.println("Report this to Slava Pestov <sp@gjt.org>");
      return null;
   }

   protected void handleGrabAction(KeyEvent var1) {
      ActionListener var2 = this.grabAction;
      this.grabAction = null;
      this.executeAction(var2, var1.getSource(), String.valueOf(var1.getKeyChar()));
   }

   static {
      actions.put("backspace", BACKSPACE);
      actions.put("backspace-word", BACKSPACE_WORD);
      actions.put("delete", DELETE);
      actions.put("delete-word", DELETE_WORD);
      actions.put("end", END);
      actions.put("select-all", SELECT_ALL);
      actions.put("select-end", SELECT_END);
      actions.put("document-end", DOCUMENT_END);
      actions.put("select-doc-end", SELECT_DOC_END);
      actions.put("insert-break", INSERT_BREAK);
      actions.put("insert-tab", INSERT_TAB);
      actions.put("home", HOME);
      actions.put("select-home", SELECT_HOME);
      actions.put("document-home", DOCUMENT_HOME);
      actions.put("select-doc-home", SELECT_DOC_HOME);
      actions.put("next-char", NEXT_CHAR);
      actions.put("next-line", NEXT_LINE);
      actions.put("next-page", NEXT_PAGE);
      actions.put("next-word", NEXT_WORD);
      actions.put("select-next-char", SELECT_NEXT_CHAR);
      actions.put("select-next-line", SELECT_NEXT_LINE);
      actions.put("select-next-page", SELECT_NEXT_PAGE);
      actions.put("select-next-word", SELECT_NEXT_WORD);
      actions.put("overwrite", OVERWRITE);
      actions.put("prev-char", PREV_CHAR);
      actions.put("prev-line", PREV_LINE);
      actions.put("prev-page", PREV_PAGE);
      actions.put("prev-word", PREV_WORD);
      actions.put("select-prev-char", SELECT_PREV_CHAR);
      actions.put("select-prev-line", SELECT_PREV_LINE);
      actions.put("select-prev-page", SELECT_PREV_PAGE);
      actions.put("select-prev-word", SELECT_PREV_WORD);
      actions.put("repeat", REPEAT);
      actions.put("toggle-rect", TOGGLE_RECT);
      actions.put("insert-char", INSERT_CHAR);
      actions.put("clipboard-copy", CLIP_COPY);
      actions.put("clipboard-paste", CLIP_PASTE);
      actions.put("clipboard-cut", CLIP_CUT);
   }

   public static class clip_cut implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.cut();
      }
   }

   public static class clip_paste implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.paste();
      }
   }

   public static class clip_copy implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.copy();
      }
   }

   public static class insert_char implements ActionListener, InputHandler.NonRepeatable {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         String var3 = var1.getActionCommand();
         int var4 = var2.getInputHandler().getRepeatCount();
         if (var2.isEditable()) {
            StringBuffer var5 = new StringBuffer();

            for(int var6 = 0; var6 < var4; ++var6) {
               var5.append(var3);
            }

            var2.overwriteSetSelectedText(var5.toString());
         } else {
            var2.getToolkit().beep();
         }

      }
   }

   public static class toggle_rect implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.setSelectionRectangular(!var2.isSelectionRectangular());
      }
   }

   public static class repeat implements ActionListener, InputHandler.NonRecordable {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.getInputHandler().setRepeatEnabled(true);
         String var3 = var1.getActionCommand();
         if (var3 != null) {
            var2.getInputHandler().setRepeatCount(Integer.parseInt(var3));
         }

      }
   }

   public static class prev_word implements ActionListener {
      private boolean select;

      public prev_word(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getCaretLine();
         int var5 = var2.getLineStartOffset(var4);
         var3 -= var5;
         String var6 = var2.getLineText(var2.getCaretLine());
         if (var3 == 0) {
            if (var5 == 0) {
               var2.getToolkit().beep();
               return;
            }

            --var3;
         } else {
            String var7 = (String)var2.getDocument().getProperty("noWordSep");
            var3 = TextUtilities.findWordStart(var6, var3, var7);
         }

         if (this.select) {
            var2.select(var2.getMarkPosition(), var5 + var3);
         } else {
            var2.setCaretPosition(var5 + var3);
         }

      }
   }

   public static class prev_page implements ActionListener {
      private boolean select;

      public prev_page(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getFirstLine();
         int var4 = var2.getVisibleLines();
         int var5 = var2.getCaretLine();
         if (var3 < var4) {
            var3 = var4;
         }

         var2.setFirstLine(var3 - var4);
         int var6 = var2.getLineStartOffset(Math.max(0, var5 - var4));
         if (this.select) {
            var2.select(var2.getMarkPosition(), var6);
         } else {
            var2.setCaretPosition(var6);
         }

      }
   }

   public static class prev_line implements ActionListener {
      private boolean select;

      public prev_line(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getCaretLine();
         if (var4 == 0) {
            var2.getToolkit().beep();
         } else {
            int var5 = var2.getMagicCaretPosition();
            if (var5 == -1) {
               var5 = var2.offsetToX(var4, var3 - var2.getLineStartOffset(var4));
            }

            var3 = var2.getLineStartOffset(var4 - 1) + var2.xToOffset(var4 - 1, var5);
            if (this.select) {
               var2.select(var2.getMarkPosition(), var3);
            } else {
               var2.setCaretPosition(var3);
            }

            var2.setMagicCaretPosition(var5);
         }
      }
   }

   public static class prev_char implements ActionListener {
      private boolean select;

      public prev_char(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         if (var3 == 0) {
            var2.getToolkit().beep();
         } else {
            if (this.select) {
               var2.select(var2.getMarkPosition(), var3 - 1);
            } else {
               var2.setCaretPosition(var3 - 1);
            }

         }
      }
   }

   public static class overwrite implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.setOverwriteEnabled(!var2.isOverwriteEnabled());
      }
   }

   public static class next_word implements ActionListener {
      private boolean select;

      public next_word(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getCaretLine();
         int var5 = var2.getLineStartOffset(var4);
         var3 -= var5;
         String var6 = var2.getLineText(var2.getCaretLine());
         if (var3 == var6.length()) {
            if (var5 + var3 == var2.getDocumentLength()) {
               var2.getToolkit().beep();
               return;
            }

            ++var3;
         } else {
            String var7 = (String)var2.getDocument().getProperty("noWordSep");
            var3 = TextUtilities.findWordEnd(var6, var3, var7);
         }

         if (this.select) {
            var2.select(var2.getMarkPosition(), var5 + var3);
         } else {
            var2.setCaretPosition(var5 + var3);
         }

      }
   }

   public static class next_page implements ActionListener {
      private boolean select;

      public next_page(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getLineCount();
         int var4 = var2.getFirstLine();
         int var5 = var2.getVisibleLines();
         int var6 = var2.getCaretLine();
         var4 += var5;
         if (var4 + var5 >= var3 - 1) {
            var4 = var3 - var5;
         }

         var2.setFirstLine(var4);
         int var7 = var2.getLineStartOffset(Math.min(var2.getLineCount() - 1, var6 + var5));
         if (this.select) {
            var2.select(var2.getMarkPosition(), var7);
         } else {
            var2.setCaretPosition(var7);
         }

      }
   }

   public static class next_line implements ActionListener {
      private boolean select;

      public next_line(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getCaretLine();
         if (var4 == var2.getLineCount() - 1) {
            var2.getToolkit().beep();
         } else {
            int var5 = var2.getMagicCaretPosition();
            if (var5 == -1) {
               var5 = var2.offsetToX(var4, var3 - var2.getLineStartOffset(var4));
            }

            var3 = var2.getLineStartOffset(var4 + 1) + var2.xToOffset(var4 + 1, var5);
            if (this.select) {
               var2.select(var2.getMarkPosition(), var3);
            } else {
               var2.setCaretPosition(var3);
            }

            var2.setMagicCaretPosition(var5);
         }
      }
   }

   public static class next_char implements ActionListener {
      private boolean select;

      public next_char(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         if (var3 == var2.getDocumentLength()) {
            var2.getToolkit().beep();
         } else {
            if (this.select) {
               var2.select(var2.getMarkPosition(), var3 + 1);
            } else {
               var2.setCaretPosition(var3 + 1);
            }

         }
      }
   }

   public static class unsert_tab implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (!var2.isEditable()) {
            var2.getToolkit().beep();
         } else {
            var2.getDocument().beginCompoundEdit();
            var2.getDocument().endCompoundEdit();
         }
      }
   }

   public static class insert_tab implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (!var2.isEditable()) {
            var2.getToolkit().beep();
         } else {
            var2.getDocument().beginCompoundEdit();

            try {
               if (var2.getSelectedText() != null && var2.getSelectedText().length() != 0) {
                  int var3 = var2.getSelectionStart();
                  int var4 = var2.getSelectionEnd() - var3;
                  byte var5 = 1;
                  var2.setSelectionStart(var2.getLineStartOffset(var2.getSelectionStartLine()));
                  if (var3 == var2.getSelectionStart()) {
                     var5 = 0;
                  }

                  String var6 = var2.getSelectedText().replaceAll("\r\n", "\n");
                  StringBuilder var7 = new StringBuilder();
                  int var8 = 0;

                  label118:
                  while(true) {
                     while(true) {
                        if (var8 >= var6.length()) {
                           break label118;
                        }

                        if (var6.charAt(var8) == '\n') {
                           var7.append("\n");
                           ++var8;
                        } else {
                           int var9 = var6.indexOf("\n", var8);
                           if (var9 == -1) {
                              if (var5 == 0 || var7.length() > 0) {
                                 ++var4;
                              }

                              var7.append(String.format("\t%s", var6.substring(var8)));
                              break label118;
                           }

                           if (var5 == 0 || var7.length() > 0) {
                              ++var4;
                           }

                           var7.append(String.format("\t%s", var6.substring(var8, var9)));
                           var8 = var9;
                        }
                     }
                  }

                  var2.overwriteSetSelectedText(var7.toString());
                  var2.setSelectionStart(var3 + var5);
                  var2.setSelectionEnd(var3 + var5 + var4);
               } else {
                  var2.overwriteSetSelectedText("\t");
               }
            } finally {
               var2.getDocument().endCompoundEdit();
            }

         }
      }
   }

   public static class insert_break implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (!var2.isEditable()) {
            var2.getToolkit().beep();
         } else {
            var2.getDocument().beginCompoundEdit();

            try {
               String var3 = "";
               int var4 = var2.getSelectionStartLine();

               while(true) {
                  if (var4 >= 0) {
                     String var5 = var2.getLineText(var4);
                     if (var5.trim().length() == 0) {
                        --var4;
                        continue;
                     }

                     int var6 = 0;

                     StringBuilder var7;
                     for(var7 = new StringBuilder(); var6 < var5.length() && (var5.charAt(var6) == ' ' || var5.charAt(var6) == '\t'); ++var6) {
                        var7.append(var5.charAt(var6));
                     }

                     var3 = var7.toString();
                  }

                  var4 = 0;
                  int var11 = 0;

                  while(var11 < var3.length()) {
                     if (var3.charAt(var11) == '\t') {
                        if (var11 == var4) {
                           ++var4;
                           ++var11;
                        } else if (var11 - var4 <= 0) {
                           ++var11;
                        } else {
                           var3 = String.format("%s%s%s", var3.substring(0, var4), "\t", var3.substring(var11 + 1));
                           var4 = 0;
                           var11 = 0;
                        }
                     } else if (var11 - var4 == 3) {
                        var3 = String.format("%s%s%s", var3.substring(0, var4), "\t", var3.substring(var11 + 1));
                        var4 = 0;
                        var11 = 0;
                     } else {
                        ++var11;
                     }
                  }

                  var2.overwriteSetSelectedText(String.format("%s%s", "\n", var3));
                  return;
               }
            } finally {
               var2.getDocument().endCompoundEdit();
            }
         }
      }
   }

   public static class document_home implements ActionListener {
      private boolean select;

      public document_home(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (this.select) {
            var2.select(var2.getMarkPosition(), 0);
         } else {
            var2.setCaretPosition(0);
         }

      }
   }

   public static class home implements ActionListener {
      private boolean select;

      public home(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getFirstLine();
         int var5 = var2.getLineStartOffset(var2.getCaretLine());

         int var6;
         for(var6 = var5; var2.getText(var6, 1).equals(" ") || var2.getText(var6, 1).equals("\t"); ++var6) {
         }

         if (var2.getText(var6, 1).equals("\r") || var2.getText(var6, 1).equals("\n")) {
            var6 = var5;
         }

         int var7 = var4 == 0 ? 0 : var4 + var2.getElectricScroll();
         int var8 = var2.getLineStartOffset(var7);
         if (var3 == 0) {
            var2.getToolkit().beep();
         } else {
            if (!Boolean.TRUE.equals(var2.getClientProperty("InputHandler.homeEnd"))) {
               if (var3 == var6) {
                  var3 = var5;
               } else {
                  var3 = var6;
               }
            } else if (var3 == var8) {
               var3 = 0;
            } else if (var3 == var5) {
               var3 = var8;
            } else {
               var3 = var5;
            }

            if (this.select) {
               var2.select(var2.getMarkPosition(), var3);
            } else {
               var2.setCaretPosition(var3);
            }

         }
      }
   }

   public static class document_end implements ActionListener {
      private boolean select;

      public document_end(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (this.select) {
            var2.select(var2.getMarkPosition(), var2.getDocumentLength());
         } else {
            var2.setCaretPosition(var2.getDocumentLength());
         }

      }
   }

   public static class select_all implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         var2.selectAll();
      }
   }

   public static class end implements ActionListener {
      private boolean select;

      public end(boolean var1) {
         this.select = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getCaretPosition();
         int var4 = var2.getLineEndOffset(var2.getCaretLine()) - 1;
         int var5 = var2.getFirstLine() + var2.getVisibleLines();
         if (var5 >= var2.getLineCount()) {
            var5 = Math.min(var2.getLineCount() - 1, var5);
         } else {
            var5 -= var2.getElectricScroll() + 1;
         }

         int var6 = var2.getLineEndOffset(var5) - 1;
         int var7 = var2.getDocumentLength();
         if (var3 == var7) {
            var2.getToolkit().beep();
         } else {
            if (!Boolean.TRUE.equals(var2.getClientProperty("InputHandler.homeEnd"))) {
               var3 = var4;
            } else if (var3 == var6) {
               var3 = var7;
            } else if (var3 == var4) {
               var3 = var6;
            } else {
               var3 = var4;
            }

            if (this.select) {
               var2.select(var2.getMarkPosition(), var3);
            } else {
               var2.setCaretPosition(var3);
            }

         }
      }
   }

   public static class delete_word implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getSelectionStart();
         if (var3 != var2.getSelectionEnd()) {
            var2.setSelectedText("");
         }

         int var4 = var2.getCaretLine();
         int var5 = var2.getLineStartOffset(var4);
         int var6 = var3 - var5;
         String var7 = var2.getLineText(var2.getCaretLine());
         if (var6 == var7.length()) {
            if (var5 + var6 == var2.getDocumentLength()) {
               var2.getToolkit().beep();
               return;
            }

            ++var6;
         } else {
            String var8 = (String)var2.getDocument().getProperty("noWordSep");
            var6 = TextUtilities.findWordEnd(var7, var6, var8);
         }

         try {
            var2.getDocument().remove(var3, var6 + var5 - var3);
         } catch (BadLocationException var9) {
            var9.printStackTrace();
         }

      }
   }

   public static class delete implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (!var2.isEditable()) {
            var2.getToolkit().beep();
         } else {
            if (var2.getSelectionStart() != var2.getSelectionEnd()) {
               var2.setSelectedText("");
            } else {
               int var3 = var2.getCaretPosition();
               if (var3 == var2.getDocumentLength()) {
                  var2.getToolkit().beep();
                  return;
               }

               try {
                  var2.getDocument().remove(var3, 1);
               } catch (BadLocationException var5) {
                  var5.printStackTrace();
               }
            }

         }
      }
   }

   public static class backspace_word implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         int var3 = var2.getSelectionStart();
         if (var3 != var2.getSelectionEnd()) {
            var2.setSelectedText("");
         }

         int var4 = var2.getCaretLine();
         int var5 = var2.getLineStartOffset(var4);
         int var6 = var3 - var5;
         String var7 = var2.getLineText(var2.getCaretLine());
         if (var6 == 0) {
            if (var5 == 0) {
               var2.getToolkit().beep();
               return;
            }

            --var6;
         } else {
            String var8 = (String)var2.getDocument().getProperty("noWordSep");
            var6 = TextUtilities.findWordStart(var7, var6, var8);
         }

         try {
            var2.getDocument().remove(var6 + var5, var3 - (var6 + var5));
         } catch (BadLocationException var9) {
            var9.printStackTrace();
         }

      }
   }

   public static class backspace implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         JEditTextArea var2 = InputHandler.getTextArea(var1);
         if (!var2.isEditable()) {
            var2.getToolkit().beep();
         } else {
            if (var2.getSelectionStart() != var2.getSelectionEnd()) {
               var2.setSelectedText("");
            } else {
               int var3 = var2.getCaretPosition();
               if (var3 == 0) {
                  var2.getToolkit().beep();
                  return;
               }

               try {
                  var2.getDocument().remove(var3 - 1, 1);
               } catch (BadLocationException var5) {
                  var5.printStackTrace();
               }
            }

         }
      }
   }

   public interface MacroRecorder {
      void actionPerformed(ActionListener var1, String var2);
   }

   public interface Wrapper {
   }

   public interface NonRecordable {
   }

   public interface NonRepeatable {
   }
}
