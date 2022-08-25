package org.syntax.jedit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.syntax.jedit.tokenmarker.Token;
import org.syntax.jedit.tokenmarker.TokenMarker;

public class JEditTextArea extends JComponent {
   public static String LEFT_OF_SCROLLBAR = "los";
   protected static String CENTER = "center";
   protected static String RIGHT = "right";
   protected static String BOTTOM = "bottom";
   protected static JEditTextArea focusedComponent;
   protected static Timer caretTimer = new Timer(500, new JEditTextArea.CaretBlinker());
   protected TextAreaPainter painter;
   protected JPopupMenu popup;
   protected EventListenerList listenerList;
   protected JEditTextArea.MutableCaretEvent caretEvent;
   protected boolean caretBlinks;
   protected boolean caretVisible;
   protected boolean blink;
   protected boolean editable;
   protected int firstLine;
   protected int visibleLines;
   protected int electricScroll;
   protected int horizontalOffset;
   protected JScrollBar vertical;
   protected JScrollBar horizontal;
   protected boolean scrollBarsInitialized;
   protected InputHandler inputHandler;
   protected SyntaxDocument document;
   protected JEditTextArea.DocumentHandler documentHandler;
   protected Segment lineSegment;
   protected int selectionStart;
   protected int selectionStartLine;
   protected int selectionEnd;
   protected int selectionEndLine;
   protected boolean biasLeft;
   protected int bracketPosition;
   protected int bracketLine;
   protected int magicCaret;
   protected boolean overwrite;
   protected boolean rectSelect;

   public JEditTextArea() {
      this(TextAreaDefaults.getDefaults());
   }

   public JEditTextArea(TextAreaDefaults var1) {
      this.enableEvents(8L);
      this.painter = new TextAreaPainter(this, var1);
      this.documentHandler = new JEditTextArea.DocumentHandler();
      this.listenerList = new EventListenerList();
      this.caretEvent = new JEditTextArea.MutableCaretEvent();
      this.lineSegment = new Segment();
      this.bracketLine = this.bracketPosition = -1;
      this.blink = true;
      this.setLayout(new JEditTextArea.ScrollLayout());
      this.add(CENTER, this.painter);
      this.add(RIGHT, this.vertical = new JScrollBar(1));
      this.add(BOTTOM, this.horizontal = new JScrollBar(0));
      this.vertical.addAdjustmentListener(new JEditTextArea.AdjustHandler());
      this.horizontal.addAdjustmentListener(new JEditTextArea.AdjustHandler());
      this.painter.addComponentListener(new JEditTextArea.ComponentHandler());
      this.painter.addMouseListener(new JEditTextArea.MouseHandler());
      this.painter.addMouseMotionListener(new JEditTextArea.DragHandler());
      this.addFocusListener(new JEditTextArea.FocusHandler());
      this.addMouseWheelListener(new MouseWheelListener() {
         public void mouseWheelMoved(MouseWheelEvent var1) {
            int var2 = JEditTextArea.this.getFirstLine() + var1.getWheelRotation();
            if (var2 < 0) {
               var2 = 0;
            }

            if (var2 > JEditTextArea.this.getLineCount()) {
               var2 = JEditTextArea.this.getLineCount();
            }

            JEditTextArea.this.setFirstLine(var2);
         }
      });
      this.setInputHandler(var1.inputHandler);
      this.setDocument(var1.document);
      this.editable = var1.editable;
      this.caretVisible = var1.caretVisible;
      this.caretBlinks = var1.caretBlinks;
      this.electricScroll = var1.electricScroll;
      this.popup = var1.popup;
      focusedComponent = this;
   }

   /** @deprecated */
   @Deprecated
   public final boolean isManagingFocus() {
      return true;
   }

   public final TextAreaPainter getPainter() {
      return this.painter;
   }

   public final InputHandler getInputHandler() {
      return this.inputHandler;
   }

   public void setInputHandler(InputHandler var1) {
      this.inputHandler = var1;
   }

   public final boolean isCaretBlinkEnabled() {
      return this.caretBlinks;
   }

   public void setCaretBlinkEnabled(boolean var1) {
      this.caretBlinks = var1;
      if (!var1) {
         this.blink = false;
      }

      this.painter.invalidateSelectedLines();
   }

   public final boolean isCaretVisible() {
      return (!this.caretBlinks || this.blink) && this.caretVisible;
   }

   public void setCaretVisible(boolean var1) {
      this.caretVisible = var1;
      this.blink = true;
      this.painter.invalidateSelectedLines();
   }

   public final void blinkCaret() {
      if (this.caretBlinks) {
         this.blink = !this.blink;
         this.painter.invalidateSelectedLines();
      } else {
         this.blink = true;
      }

   }

   public final int getElectricScroll() {
      return this.electricScroll;
   }

   public final void setElectricScroll(int var1) {
      this.electricScroll = var1;
   }

   public void updateScrollBars() {
      if (this.vertical != null && this.visibleLines != 0) {
         this.vertical.setValues(this.firstLine, this.visibleLines, 0, this.getLineCount());
         this.vertical.setUnitIncrement(2);
         this.vertical.setBlockIncrement(this.visibleLines);
      }

      int var1 = this.painter.getWidth();
      if (this.horizontal != null && var1 != 0) {
         this.horizontal.setValues(-this.horizontalOffset, var1, 0, var1 * 5);
         this.horizontal.setUnitIncrement(this.painter.getFontMetrics().charWidth('w'));
         this.horizontal.setBlockIncrement(var1 / 2);
      }

   }

   public final int getFirstLine() {
      return this.firstLine;
   }

   public void setFirstLine(int var1) {
      if (var1 != this.firstLine) {
         int var2 = this.firstLine;
         this.firstLine = var1;
         if (var1 != this.vertical.getValue()) {
            this.updateScrollBars();
         }

         this.painter.repaint();
      }
   }

   public final int getVisibleLines() {
      return this.visibleLines;
   }

   public final void recalculateVisibleLines() {
      if (this.painter != null) {
         int var1 = this.painter.getHeight();
         int var2 = this.painter.getFontMetrics().getHeight();
         int var3 = this.visibleLines;
         this.visibleLines = var1 / var2;
         this.updateScrollBars();
      }
   }

   public final int getHorizontalOffset() {
      return this.horizontalOffset;
   }

   public void setHorizontalOffset(int var1) {
      if (var1 != this.horizontalOffset) {
         this.horizontalOffset = var1;
         if (var1 != this.horizontal.getValue()) {
            this.updateScrollBars();
         }

         this.painter.repaint();
      }
   }

   public boolean setOrigin(int var1, int var2) {
      boolean var3 = false;
      int var4 = this.firstLine;
      if (var2 != this.horizontalOffset) {
         this.horizontalOffset = var2;
         var3 = true;
      }

      if (var1 != this.firstLine) {
         this.firstLine = var1;
         var3 = true;
      }

      if (var3) {
         this.updateScrollBars();
         this.painter.repaint();
      }

      return var3;
   }

   public boolean scrollToCaret() {
      int var1 = this.getCaretLine();
      int var2 = this.getLineStartOffset(var1);
      int var3 = Math.max(0, Math.min(this.getLineLength(var1) - 1, this.getCaretPosition() - var2));
      return this.scrollTo(var1, var3);
   }

   public boolean scrollTo(int var1, int var2) {
      if (this.visibleLines == 0) {
         this.setFirstLine(Math.max(0, var1 - this.electricScroll));
         return true;
      } else {
         int var3 = this.firstLine;
         int var4 = this.horizontalOffset;
         if (var1 < this.firstLine + this.electricScroll) {
            var3 = Math.max(0, var1 - this.electricScroll);
         } else if (var1 + this.electricScroll >= this.firstLine + this.visibleLines) {
            var3 = var1 - this.visibleLines + this.electricScroll + 1;
            if (var3 + this.visibleLines >= this.getLineCount()) {
               var3 = this.getLineCount() - this.visibleLines;
            }

            if (var3 < 0) {
               var3 = 0;
            }
         }

         int var5 = this._offsetToX(var1, var2);
         int var6 = this.painter.getFontMetrics().charWidth('w');
         if (var5 < 0) {
            var4 = Math.min(0, this.horizontalOffset - var5 + var6 + 5);
         } else if (var5 + var6 >= this.painter.getWidth()) {
            var4 = this.horizontalOffset + (this.painter.getWidth() - var5) - var6 - 5;
         }

         return this.setOrigin(var3, var4);
      }
   }

   public int lineToY(int var1) {
      FontMetrics var2 = this.painter.getFontMetrics();
      return (var1 - this.firstLine) * var2.getHeight() - (var2.getLeading() + var2.getMaxDescent());
   }

   public int yToLine(int var1) {
      FontMetrics var2 = this.painter.getFontMetrics();
      int var3 = var2.getHeight();
      return Math.max(0, Math.min(this.getLineCount() - 1, var1 / var3 + this.firstLine));
   }

   public final int offsetToX(int var1, int var2) {
      this.painter.currentLineTokens = null;
      return this._offsetToX(var1, var2);
   }

   public int _offsetToX(int var1, int var2) {
      TokenMarker var3 = this.getTokenMarker();
      FontMetrics var4 = this.painter.getFontMetrics();
      this.getLineText(var1, this.lineSegment);
      int var5 = this.lineSegment.offset;
      int var6 = this.horizontalOffset;
      if (var3 == null) {
         this.lineSegment.count = var2;
         return var6 + Utilities.getTabbedTextWidth(this.lineSegment, var4, var6, this.painter, 0);
      } else {
         Token var7;
         if (this.painter.currentLineIndex == var1 && this.painter.currentLineTokens != null) {
            var7 = this.painter.currentLineTokens;
         } else {
            this.painter.currentLineIndex = var1;
            var7 = this.painter.currentLineTokens = var3.markTokens(this.lineSegment, var1);
         }

         Toolkit var8 = this.painter.getToolkit();
         Font var9 = this.painter.getFont();
         SyntaxStyle[] var10 = this.painter.getStyles();

         while(true) {
            byte var11 = var7.id;
            if (var11 == 127) {
               return var6;
            }

            if (var11 == 0) {
               var4 = this.painter.getFontMetrics();
            } else {
               var4 = var10[var11].getFontMetrics(var9);
            }

            int var12 = var7.length;
            if (var2 + var5 < this.lineSegment.offset + var12) {
               this.lineSegment.count = var2 - (this.lineSegment.offset - var5);
               return var6 + Utilities.getTabbedTextWidth(this.lineSegment, var4, var6, this.painter, 0);
            }

            this.lineSegment.count = var12;
            var6 += Utilities.getTabbedTextWidth(this.lineSegment, var4, var6, this.painter, 0);
            Segment var10000 = this.lineSegment;
            var10000.offset += var12;
            var7 = var7.next;
         }
      }
   }

   public int xToOffset(int var1, int var2) {
      TokenMarker var3 = this.getTokenMarker();
      FontMetrics var4 = this.painter.getFontMetrics();
      this.getLineText(var1, this.lineSegment);
      char[] var5 = this.lineSegment.array;
      int var6 = this.lineSegment.offset;
      int var7 = this.lineSegment.count;
      int var8 = this.horizontalOffset;
      if (var3 == null) {
         for(int var19 = 0; var19 < var7; ++var19) {
            char var20 = var5[var19 + var6];
            int var21;
            if (var20 == '\t') {
               var21 = (int)this.painter.nextTabStop((float)var8, var19) - var8;
            } else {
               var21 = var4.charWidth(var20);
            }

            if (this.painter.isBlockCaretEnabled()) {
               if (var2 - var21 <= var8) {
                  return var19;
               }
            } else if (var2 - var21 / 2 <= var8) {
               return var19;
            }

            var8 += var21;
         }

         return var7;
      } else {
         Token var9;
         if (this.painter.currentLineIndex == var1 && this.painter.currentLineTokens != null) {
            var9 = this.painter.currentLineTokens;
         } else {
            this.painter.currentLineIndex = var1;
            var9 = this.painter.currentLineTokens = var3.markTokens(this.lineSegment, var1);
         }

         int var10 = 0;
         Toolkit var11 = this.painter.getToolkit();
         Font var12 = this.painter.getFont();
         SyntaxStyle[] var13 = this.painter.getStyles();

         while(true) {
            byte var14 = var9.id;
            if (var14 == 127) {
               return var10;
            }

            if (var14 == 0) {
               var4 = this.painter.getFontMetrics();
            } else {
               var4 = var13[var14].getFontMetrics(var12);
            }

            int var15 = var9.length;

            for(int var16 = 0; var16 < var15; ++var16) {
               char var17 = var5[var6 + var10 + var16];
               int var18;
               if (var17 == '\t') {
                  var18 = (int)this.painter.nextTabStop((float)var8, var10 + var16) - var8;
               } else {
                  var18 = var4.charWidth(var17);
               }

               if (this.painter.isBlockCaretEnabled()) {
                  if (var2 - var18 <= var8) {
                     return var10 + var16;
                  }
               } else if (var2 - var18 / 2 <= var8) {
                  return var10 + var16;
               }

               var8 += var18;
            }

            var10 += var15;
            var9 = var9.next;
         }
      }
   }

   public int xyToOffset(int var1, int var2) {
      int var3 = this.yToLine(var2);
      int var4 = this.getLineStartOffset(var3);
      return var4 + this.xToOffset(var3, var1);
   }

   public final SyntaxDocument getDocument() {
      return this.document;
   }

   public void setDocument(SyntaxDocument var1) {
      if (this.document != var1) {
         if (this.document != null) {
            this.document.removeDocumentListener(this.documentHandler);
         }

         this.document = var1;
         var1.addDocumentListener(this.documentHandler);
         this.select(0, 0);
         this.updateScrollBars();
         this.painter.repaint();
      }
   }

   public final TokenMarker getTokenMarker() {
      return this.document.getTokenMarker();
   }

   public final void setTokenMarker(TokenMarker var1) {
      this.document.setTokenMarker(var1);
   }

   public final int getDocumentLength() {
      return this.document.getLength();
   }

   public final int getLineCount() {
      return this.document.getDefaultRootElement().getElementCount();
   }

   public final int getLineOfOffset(int var1) {
      return this.document.getDefaultRootElement().getElementIndex(var1);
   }

   public int getLineStartOffset(int var1) {
      Element var2 = this.document.getDefaultRootElement().getElement(var1);
      return var2 == null ? -1 : var2.getStartOffset();
   }

   public int getLineEndOffset(int var1) {
      Element var2 = this.document.getDefaultRootElement().getElement(var1);
      return var2 == null ? -1 : var2.getEndOffset();
   }

   public int getLineLength(int var1) {
      Element var2 = this.document.getDefaultRootElement().getElement(var1);
      return var2 == null ? -1 : var2.getEndOffset() - var2.getStartOffset() - 1;
   }

   public String getText() {
      try {
         return this.document.getText(0, this.document.getLength());
      } catch (BadLocationException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public void setText(String var1) {
      try {
         this.document.beginCompoundEdit();
         this.document.remove(0, this.document.getLength());
         this.document.insertString(0, var1, (AttributeSet)null);
      } catch (BadLocationException var6) {
         var6.printStackTrace();
      } finally {
         this.document.endCompoundEdit();
      }

   }

   public final String getText(int var1, int var2) {
      try {
         return this.document.getText(var1, var2);
      } catch (BadLocationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public final void getText(int var1, int var2, Segment var3) {
      try {
         this.document.getText(var1, var2, var3);
      } catch (BadLocationException var5) {
         var5.printStackTrace();
         var3.offset = var3.count = 0;
      }

   }

   public final String getLineText(int var1) {
      int var2 = this.getLineStartOffset(var1);
      return this.getText(var2, this.getLineEndOffset(var1) - var2 - 1);
   }

   public final void getLineText(int var1, Segment var2) {
      int var3 = this.getLineStartOffset(var1);
      this.getText(var3, this.getLineEndOffset(var1) - var3 - 1, var2);
   }

   public final int getSelectionStart() {
      return this.selectionStart;
   }

   public int getSelectionStart(int var1) {
      if (var1 == this.selectionStartLine) {
         return this.selectionStart;
      } else if (this.rectSelect) {
         Element var2 = this.document.getDefaultRootElement();
         int var3 = this.selectionStart - var2.getElement(this.selectionStartLine).getStartOffset();
         Element var4 = var2.getElement(var1);
         int var5 = var4.getStartOffset();
         int var6 = var4.getEndOffset() - 1;
         return Math.min(var6, var5 + var3);
      } else {
         return this.getLineStartOffset(var1);
      }
   }

   public final int getSelectionStartLine() {
      return this.selectionStartLine;
   }

   public final void setSelectionStart(int var1) {
      this.select(var1, this.selectionEnd);
   }

   public final int getSelectionEnd() {
      return this.selectionEnd;
   }

   public int getSelectionEnd(int var1) {
      if (var1 == this.selectionEndLine) {
         return this.selectionEnd;
      } else if (this.rectSelect) {
         Element var2 = this.document.getDefaultRootElement();
         int var3 = this.selectionEnd - var2.getElement(this.selectionEndLine).getStartOffset();
         Element var4 = var2.getElement(var1);
         int var5 = var4.getStartOffset();
         int var6 = var4.getEndOffset() - 1;
         return Math.min(var6, var5 + var3);
      } else {
         return this.getLineEndOffset(var1) - 1;
      }
   }

   public final int getSelectionEndLine() {
      return this.selectionEndLine;
   }

   public final void setSelectionEnd(int var1) {
      this.select(this.selectionStart, var1);
   }

   public final int getCaretPosition() {
      return this.biasLeft ? this.selectionStart : this.selectionEnd;
   }

   public final int getCaretLine() {
      return this.biasLeft ? this.selectionStartLine : this.selectionEndLine;
   }

   public final int getMarkPosition() {
      return this.biasLeft ? this.selectionEnd : this.selectionStart;
   }

   public final int getMarkLine() {
      return this.biasLeft ? this.selectionEndLine : this.selectionStartLine;
   }

   public final void setCaretPosition(int var1) {
      this.select(var1, var1);
   }

   public final void selectAll() {
      this.select(0, this.getDocumentLength());
   }

   public final void selectNone() {
      this.select(this.getCaretPosition(), this.getCaretPosition());
   }

   public void select(int var1, int var2) {
      int var3;
      int var4;
      boolean var5;
      if (var1 <= var2) {
         var3 = var1;
         var4 = var2;
         var5 = false;
      } else {
         var3 = var2;
         var4 = var1;
         var5 = true;
      }

      if (var3 >= 0 && var4 <= this.getDocumentLength()) {
         if (var3 != this.selectionStart || var4 != this.selectionEnd || var5 != this.biasLeft) {
            int var6 = this.getLineOfOffset(var3);
            int var7 = this.getLineOfOffset(var4);
            if (this.painter.isBracketHighlightEnabled()) {
               if (this.bracketLine != -1) {
                  this.painter.invalidateLine(this.bracketLine);
               }

               this.updateBracketHighlight(var2);
               if (this.bracketLine != -1) {
                  this.painter.invalidateLine(this.bracketLine);
               }
            }

            this.painter.invalidateLineRange(this.selectionStartLine, this.selectionEndLine);
            this.painter.invalidateLineRange(var6, var7);
            this.document.addUndoableEdit(new JEditTextArea.CaretUndo(this.selectionStart, this.selectionEnd));
            this.selectionStart = var3;
            this.selectionEnd = var4;
            this.selectionStartLine = var6;
            this.selectionEndLine = var7;
            this.biasLeft = var5;
            this.fireCaretEvent();
         }

         this.blink = true;
         caretTimer.restart();
         if (this.selectionStart == this.selectionEnd) {
            this.rectSelect = false;
         }

         this.magicCaret = -1;
         this.scrollToCaret();
      } else {
         throw new IllegalArgumentException("Bounds out of range: " + var3 + "," + var4);
      }
   }

   public final String getSelectedText() {
      if (this.selectionStart == this.selectionEnd) {
         return null;
      } else if (this.rectSelect) {
         Element var1 = this.document.getDefaultRootElement();
         int var2 = this.selectionStart - var1.getElement(this.selectionStartLine).getStartOffset();
         int var3 = this.selectionEnd - var1.getElement(this.selectionEndLine).getStartOffset();
         if (var3 < var2) {
            int var4 = var3;
            var3 = var2;
            var2 = var4;
         }

         StringBuffer var11 = new StringBuffer();
         Segment var5 = new Segment();

         for(int var6 = this.selectionStartLine; var6 <= this.selectionEndLine; ++var6) {
            Element var7 = var1.getElement(var6);
            int var8 = var7.getStartOffset();
            int var9 = var7.getEndOffset() - 1;
            int var10000 = var9 - var8;
            var8 = Math.min(var8 + var2, var9);
            int var10 = Math.min(var3 - var2, var9 - var8);
            this.getText(var8, var10, var5);
            var11.append(var5.array, var5.offset, var5.count);
            if (var6 != this.selectionEndLine) {
               var11.append('\n');
            }
         }

         return var11.toString();
      } else {
         return this.getText(this.selectionStart, this.selectionEnd - this.selectionStart);
      }
   }

   public void setSelectedText(String var1) {
      if (!this.editable) {
         throw new InternalError("Text component read only");
      } else {
         this.document.beginCompoundEdit();

         try {
            if (this.rectSelect) {
               Element var2 = this.document.getDefaultRootElement();
               int var3 = this.selectionStart - var2.getElement(this.selectionStartLine).getStartOffset();
               int var4 = this.selectionEnd - var2.getElement(this.selectionEndLine).getStartOffset();
               int var5;
               if (var4 < var3) {
                  var5 = var4;
                  var4 = var3;
                  var3 = var5;
               }

               var5 = 0;
               int var6 = 0;

               int var7;
               for(var7 = this.selectionStartLine; var7 <= this.selectionEndLine; ++var7) {
                  Element var8 = var2.getElement(var7);
                  int var9 = var8.getStartOffset();
                  int var10 = var8.getEndOffset() - 1;
                  int var11 = Math.min(var10, var9 + var3);
                  this.document.remove(var11, Math.min(var10 - var11, var4 - var3));
                  if (var1 != null) {
                     var6 = var1.indexOf(10, var5);
                     if (var6 == -1) {
                        var6 = var1.length();
                     }

                     this.document.insertString(var11, var1.substring(var5, var6), (AttributeSet)null);
                     var5 = Math.min(var1.length(), var6 + 1);
                  }
               }

               if (var1 != null && var6 != var1.length()) {
                  var7 = var2.getElement(this.selectionEndLine).getEndOffset() - 1;
                  this.document.insertString(var7, "\n", (AttributeSet)null);
                  this.document.insertString(var7 + 1, var1.substring(var6 + 1), (AttributeSet)null);
               }
            } else {
               this.document.remove(this.selectionStart, this.selectionEnd - this.selectionStart);
               if (var1 != null) {
                  this.document.insertString(this.selectionStart, var1, (AttributeSet)null);
               }
            }
         } catch (BadLocationException var15) {
            var15.printStackTrace();
            throw new InternalError("Cannot replace selection");
         } finally {
            this.document.endCompoundEdit();
         }

         this.setCaretPosition(this.selectionEnd);
      }
   }

   public final boolean isEditable() {
      return this.editable;
   }

   public final void setEditable(boolean var1) {
      this.editable = var1;
   }

   public final JPopupMenu getRightClickPopup() {
      return this.popup;
   }

   public final void setRightClickPopup(JPopupMenu var1) {
      this.popup = var1;
   }

   public final int getMagicCaretPosition() {
      return this.magicCaret;
   }

   public final void setMagicCaretPosition(int var1) {
      this.magicCaret = var1;
   }

   public void overwriteSetSelectedText(String var1) {
      if (this.overwrite && this.selectionStart == this.selectionEnd) {
         int var2 = this.getCaretPosition();
         int var3 = this.getLineEndOffset(this.getCaretLine());
         if (var3 - var2 <= var1.length()) {
            this.setSelectedText(var1);
         } else {
            this.document.beginCompoundEdit();

            try {
               this.document.remove(var2, var1.length());
               this.document.insertString(var2, var1, (AttributeSet)null);
            } catch (BadLocationException var8) {
               var8.printStackTrace();
            } finally {
               this.document.endCompoundEdit();
            }

         }
      } else {
         this.setSelectedText(var1);
      }
   }

   public final boolean isOverwriteEnabled() {
      return this.overwrite;
   }

   public final void setOverwriteEnabled(boolean var1) {
      this.overwrite = var1;
      this.painter.invalidateSelectedLines();
   }

   public final boolean isSelectionRectangular() {
      return this.rectSelect;
   }

   public final void setSelectionRectangular(boolean var1) {
      this.rectSelect = var1;
      this.painter.invalidateSelectedLines();
   }

   public final int getBracketPosition() {
      return this.bracketPosition;
   }

   public final int getBracketLine() {
      return this.bracketLine;
   }

   public final void addCaretListener(CaretListener var1) {
      this.listenerList.add(CaretListener.class, var1);
   }

   public final void removeCaretListener(CaretListener var1) {
      this.listenerList.remove(CaretListener.class, var1);
   }

   public void cut() {
      if (this.editable) {
         this.copy();
         this.setSelectedText("");
      }

   }

   public void copy() {
      if (this.selectionStart != this.selectionEnd) {
         Clipboard var1 = this.getToolkit().getSystemClipboard();
         String var2 = this.getSelectedText();
         int var3 = this.inputHandler.getRepeatCount();
         StringBuffer var4 = new StringBuffer();

         for(int var5 = 0; var5 < var3; ++var5) {
            var4.append(var2);
         }

         var1.setContents(new StringSelection(var4.toString()), (ClipboardOwner)null);
      }

   }

   public void paste() {
      if (this.editable) {
         Clipboard var1 = this.getToolkit().getSystemClipboard();

         try {
            String var2 = ((String)var1.getContents(this).getTransferData(DataFlavor.stringFlavor)).replaceAll("\r\n", "\n").replace('\r', '\n');
            int var3 = this.inputHandler.getRepeatCount();
            StringBuffer var4 = new StringBuffer();

            for(int var5 = 0; var5 < var3; ++var5) {
               var4.append(var2);
            }

            var2 = var4.toString();
            this.setSelectedText(var2);
         } catch (Exception var6) {
            this.getToolkit().beep();
            System.err.println("Clipboard does not contain a string");
         }
      }

   }

   public void removeNotify() {
      super.removeNotify();
      if (focusedComponent == this) {
         focusedComponent = null;
      }

   }

   public void processKeyEvent(KeyEvent var1) {
      if (this.inputHandler != null) {
         switch(var1.getID()) {
         case 400:
            this.inputHandler.keyTyped(var1);
            break;
         case 401:
            this.inputHandler.keyPressed(var1);
            break;
         case 402:
            this.inputHandler.keyReleased(var1);
         }

      }
   }

   public SyntaxStyle[] getStyles() {
      return this.painter.getStyles();
   }

   public void setStyles(SyntaxStyle[] var1) {
      this.painter.setStyles(var1);
   }

   protected void fireCaretEvent() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; --var2) {
         if (var1[var2] == CaretListener.class) {
            ((CaretListener)var1[var2 + 1]).caretUpdate(this.caretEvent);
         }
      }

   }

   protected void updateBracketHighlight(int var1) {
      if (var1 == 0) {
         this.bracketPosition = this.bracketLine = -1;
      } else {
         try {
            int var2 = TextUtilities.findMatchingBracket(this.document, var1 - 1);
            if (var2 != -1) {
               this.bracketLine = this.getLineOfOffset(var2);
               this.bracketPosition = var2 - this.getLineStartOffset(this.bracketLine);
               return;
            }
         } catch (BadLocationException var3) {
            var3.printStackTrace();
         }

         this.bracketLine = this.bracketPosition = -1;
      }
   }

   protected void documentChanged(DocumentEvent var1) {
      ElementChange var2 = var1.getChange(this.document.getDefaultRootElement());
      int var3;
      if (var2 == null) {
         var3 = 0;
      } else {
         var3 = var2.getChildrenAdded().length - var2.getChildrenRemoved().length;
      }

      int var4 = this.getLineOfOffset(var1.getOffset());
      if (var3 == 0) {
         this.painter.invalidateLine(var4);
      } else if (var4 < this.firstLine) {
         this.setFirstLine(this.firstLine + var3);
      } else {
         this.painter.invalidateLineRange(var4, this.firstLine + this.visibleLines);
         this.updateScrollBars();
      }

   }

   static {
      caretTimer.setInitialDelay(500);
      caretTimer.start();
   }

   class CaretUndo extends AbstractUndoableEdit {
      private int start;
      private int end;

      CaretUndo(int var2, int var3) {
         this.start = var2;
         this.end = var3;
      }

      public boolean isSignificant() {
         return false;
      }

      public String getPresentationName() {
         return "caret move";
      }

      public void undo() throws CannotUndoException {
         super.undo();
         JEditTextArea.this.select(this.start, this.end);
      }

      public void redo() throws CannotRedoException {
         super.redo();
         JEditTextArea.this.select(this.start, this.end);
      }

      public boolean addEdit(UndoableEdit var1) {
         if (var1 instanceof JEditTextArea.CaretUndo) {
            JEditTextArea.CaretUndo var2 = (JEditTextArea.CaretUndo)var1;
            this.start = var2.start;
            this.end = var2.end;
            var2.die();
            return true;
         } else {
            return false;
         }
      }
   }

   class MouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent var1) {
         JEditTextArea.this.requestFocus();
         JEditTextArea.this.setCaretVisible(true);
         JEditTextArea.focusedComponent = JEditTextArea.this;
         if ((var1.getModifiers() & 4) != 0 && JEditTextArea.this.popup != null) {
            JEditTextArea.this.popup.show(JEditTextArea.this.painter, var1.getX(), var1.getY());
         } else {
            int var2 = JEditTextArea.this.yToLine(var1.getY());
            int var3 = JEditTextArea.this.xToOffset(var2, var1.getX());
            int var4 = JEditTextArea.this.getLineStartOffset(var2) + var3;
            switch(var1.getClickCount()) {
            case 1:
               this.doSingleClick(var1, var2, var3, var4);
               break;
            case 2:
               try {
                  this.doDoubleClick(var1, var2, var3, var4);
               } catch (BadLocationException var6) {
                  var6.printStackTrace();
               }
               break;
            case 3:
               this.doTripleClick(var1, var2, var3, var4);
            }

         }
      }

      private void doSingleClick(MouseEvent var1, int var2, int var3, int var4) {
         if ((var1.getModifiers() & 1) != 0) {
            JEditTextArea.this.rectSelect = (var1.getModifiers() & 2) != 0;
            JEditTextArea.this.select(JEditTextArea.this.getMarkPosition(), var4);
         } else {
            JEditTextArea.this.setCaretPosition(var4);
         }

      }

      private void doDoubleClick(MouseEvent var1, int var2, int var3, int var4) throws BadLocationException {
         if (JEditTextArea.this.getLineLength(var2) != 0) {
            try {
               int var5 = TextUtilities.findMatchingBracket(JEditTextArea.this.document, Math.max(0, var4 - 1));
               if (var5 != -1) {
                  int var14 = JEditTextArea.this.getMarkPosition();
                  if (var5 > var14) {
                     ++var5;
                     --var14;
                  }

                  JEditTextArea.this.select(var14, var5);
                  return;
               }
            } catch (BadLocationException var12) {
               var12.printStackTrace();
            }

            String var13 = JEditTextArea.this.getLineText(var2);
            char var6 = var13.charAt(Math.max(0, var3 - 1));
            String var7 = (String)JEditTextArea.this.document.getProperty("noWordSep");
            if (var7 == null) {
               var7 = "";
            }

            boolean var8 = !Character.isLetterOrDigit(var6) && var7.indexOf(var6) == -1;
            int var9 = 0;

            int var10;
            for(var10 = var3 - 1; var10 >= 0; --var10) {
               var6 = var13.charAt(var10);
               if (var8 ^ (!Character.isLetterOrDigit(var6) && var7.indexOf(var6) == -1)) {
                  var9 = var10 + 1;
                  break;
               }
            }

            var10 = var13.length();

            int var11;
            for(var11 = var3; var11 < var13.length(); ++var11) {
               var6 = var13.charAt(var11);
               if (var8 ^ (!Character.isLetterOrDigit(var6) && var7.indexOf(var6) == -1)) {
                  var10 = var11;
                  break;
               }
            }

            var11 = JEditTextArea.this.getLineStartOffset(var2);
            JEditTextArea.this.select(var11 + var9, var11 + var10);
         }
      }

      private void doTripleClick(MouseEvent var1, int var2, int var3, int var4) {
         JEditTextArea.this.select(JEditTextArea.this.getLineStartOffset(var2), JEditTextArea.this.getLineEndOffset(var2) - 1);
      }
   }

   class FocusHandler implements FocusListener {
      public void focusGained(FocusEvent var1) {
         JEditTextArea.this.setCaretVisible(true);
         JEditTextArea.focusedComponent = JEditTextArea.this;
      }

      public void focusLost(FocusEvent var1) {
         JEditTextArea.this.setCaretVisible(false);
         JEditTextArea.focusedComponent = null;
      }
   }

   class DragHandler implements MouseMotionListener {
      public void mouseDragged(MouseEvent var1) {
         if (JEditTextArea.this.popup == null || !JEditTextArea.this.popup.isVisible()) {
            JEditTextArea.this.setSelectionRectangular((var1.getModifiers() & 2) != 0);
            JEditTextArea.this.select(JEditTextArea.this.getMarkPosition(), JEditTextArea.this.xyToOffset(var1.getX(), var1.getY()));
         }
      }

      public void mouseMoved(MouseEvent var1) {
      }
   }

   class DocumentHandler implements DocumentListener {
      public void insertUpdate(DocumentEvent var1) {
         JEditTextArea.this.documentChanged(var1);
         int var2 = var1.getOffset();
         int var3 = var1.getLength();
         int var4;
         if (JEditTextArea.this.selectionStart <= var2 && (JEditTextArea.this.selectionStart != JEditTextArea.this.selectionEnd || JEditTextArea.this.selectionStart != var2)) {
            var4 = JEditTextArea.this.selectionStart;
         } else {
            var4 = JEditTextArea.this.selectionStart + var3;
         }

         int var5;
         if (JEditTextArea.this.selectionEnd >= var2) {
            var5 = JEditTextArea.this.selectionEnd + var3;
         } else {
            var5 = JEditTextArea.this.selectionEnd;
         }

         JEditTextArea.this.select(var4, var5);
      }

      public void removeUpdate(DocumentEvent var1) {
         JEditTextArea.this.documentChanged(var1);
         int var2 = var1.getOffset();
         int var3 = var1.getLength();
         int var4;
         if (JEditTextArea.this.selectionStart > var2) {
            if (JEditTextArea.this.selectionStart > var2 + var3) {
               var4 = JEditTextArea.this.selectionStart - var3;
            } else {
               var4 = var2;
            }
         } else {
            var4 = JEditTextArea.this.selectionStart;
         }

         int var5;
         if (JEditTextArea.this.selectionEnd > var2) {
            if (JEditTextArea.this.selectionEnd > var2 + var3) {
               var5 = JEditTextArea.this.selectionEnd - var3;
            } else {
               var5 = var2;
            }
         } else {
            var5 = JEditTextArea.this.selectionEnd;
         }

         JEditTextArea.this.select(var4, var5);
      }

      public void changedUpdate(DocumentEvent var1) {
      }
   }

   class ComponentHandler extends ComponentAdapter {
      public void componentResized(ComponentEvent var1) {
         JEditTextArea.this.recalculateVisibleLines();
         JEditTextArea.this.scrollBarsInitialized = true;
      }
   }

   class AdjustHandler implements AdjustmentListener {
      public void adjustmentValueChanged(final AdjustmentEvent var1) {
         if (JEditTextArea.this.scrollBarsInitialized) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  if (var1.getAdjustable() == JEditTextArea.this.vertical) {
                     JEditTextArea.this.setFirstLine(JEditTextArea.this.vertical.getValue());
                  } else {
                     JEditTextArea.this.setHorizontalOffset(-JEditTextArea.this.horizontal.getValue());
                  }

               }
            });
         }
      }
   }

   class MutableCaretEvent extends CaretEvent {
      MutableCaretEvent() {
         super(JEditTextArea.this);
      }

      public int getDot() {
         return JEditTextArea.this.getCaretPosition();
      }

      public int getMark() {
         return JEditTextArea.this.getMarkPosition();
      }
   }

   static class CaretBlinker implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         if (JEditTextArea.focusedComponent != null && JEditTextArea.focusedComponent.hasFocus()) {
            JEditTextArea.focusedComponent.blinkCaret();
         }

      }
   }

   class ScrollLayout implements LayoutManager {
      private Component center;
      private Component right;
      private Component bottom;
      private Vector<Component> leftOfScrollBar = new Vector();

      public void addLayoutComponent(String var1, Component var2) {
         if (var1.equals(JEditTextArea.CENTER)) {
            this.center = var2;
         } else if (var1.equals(JEditTextArea.RIGHT)) {
            this.right = var2;
         } else if (var1.equals(JEditTextArea.BOTTOM)) {
            this.bottom = var2;
         } else if (var1.equals(JEditTextArea.LEFT_OF_SCROLLBAR)) {
            this.leftOfScrollBar.addElement(var2);
         }

      }

      public void removeLayoutComponent(Component var1) {
         if (this.center == var1) {
            this.center = null;
         }

         if (this.right == var1) {
            this.right = null;
         }

         if (this.bottom == var1) {
            this.bottom = null;
         } else {
            this.leftOfScrollBar.removeElement(var1);
         }

      }

      public Dimension preferredLayoutSize(Container var1) {
         Dimension var2 = new Dimension();
         Insets var3 = JEditTextArea.this.getInsets();
         var2.width = var3.left + var3.right;
         var2.height = var3.top + var3.bottom;
         Dimension var4 = this.center.getPreferredSize();
         var2.width += var4.width;
         var2.height += var4.height;
         Dimension var5 = this.right.getPreferredSize();
         var2.width += var5.width;
         Dimension var6 = this.bottom.getPreferredSize();
         var2.height += var6.height;
         return var2;
      }

      public Dimension minimumLayoutSize(Container var1) {
         Dimension var2 = new Dimension();
         Insets var3 = JEditTextArea.this.getInsets();
         var2.width = var3.left + var3.right;
         var2.height = var3.top + var3.bottom;
         Dimension var4 = this.center.getMinimumSize();
         var2.width += var4.width;
         var2.height += var4.height;
         Dimension var5 = this.right.getMinimumSize();
         var2.width += var5.width;
         Dimension var6 = this.bottom.getMinimumSize();
         var2.height += var6.height;
         return var2;
      }

      public void layoutContainer(Container var1) {
         Dimension var2 = var1.getSize();
         Insets var3 = var1.getInsets();
         int var4 = var3.top;
         int var5 = var3.left;
         int var6 = var3.bottom;
         int var7 = var3.right;
         int var8 = this.right.getPreferredSize().width;
         int var9 = this.bottom.getPreferredSize().height;
         int var10 = var2.width - var8 - var5 - var7;
         int var11 = var2.height - var9 - var4 - var6;
         this.center.setBounds(var5, var4, var10, var11);
         this.right.setBounds(var5 + var10, var4, var8, var11);

         Dimension var14;
         for(Enumeration var12 = this.leftOfScrollBar.elements(); var12.hasMoreElements(); var5 += var14.width) {
            Component var13 = (Component)var12.nextElement();
            var14 = var13.getPreferredSize();
            var13.setBounds(var5, var4 + var11, var14.width, var9);
         }

         this.bottom.setBounds(var5, var4 + var11, var2.width - var8 - var5 - var7, var9);
      }
   }
}
