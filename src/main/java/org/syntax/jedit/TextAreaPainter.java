package org.syntax.jedit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import org.syntax.jedit.tokenmarker.Token;
import org.syntax.jedit.tokenmarker.TokenMarker;

public class TextAreaPainter extends JComponent implements TabExpander {
   int currentLineIndex;
   Token currentLineTokens;
   Segment currentLine;
   protected JEditTextArea textArea;
   protected SyntaxStyle[] styles;
   protected Color caretColor;
   protected Color selectionColor;
   protected Color lineHighlightColor;
   protected Color bracketHighlightColor;
   protected Color eolMarkerColor;
   protected boolean blockCaret;
   protected boolean lineHighlight;
   protected boolean bracketHighlight;
   protected boolean paintInvalid;
   protected boolean eolMarkers;
   protected int cols;
   protected int rows;
   protected int tabSize;
   protected FontMetrics fm;
   protected TextAreaPainter.Highlight highlights;

   public TextAreaPainter(JEditTextArea var1, TextAreaDefaults var2) {
      this.textArea = var1;
      this.setAutoscrolls(true);
      this.setDoubleBuffered(true);
      this.setOpaque(true);
      ToolTipManager.sharedInstance().registerComponent(this);
      this.currentLine = new Segment();
      this.currentLineIndex = -1;
      this.setCursor(Cursor.getPredefinedCursor(2));
      this.setFont(new Font("Monospaced", 0, 14));
      this.setForeground(Color.black);
      this.setBackground(Color.white);
      this.blockCaret = var2.blockCaret;
      this.styles = var2.styles;
      this.cols = var2.cols;
      this.rows = var2.rows;
      this.caretColor = var2.caretColor;
      this.selectionColor = var2.selectionColor;
      this.lineHighlightColor = var2.lineHighlightColor;
      this.lineHighlight = var2.lineHighlight;
      this.bracketHighlightColor = var2.bracketHighlightColor;
      this.bracketHighlight = var2.bracketHighlight;
      this.paintInvalid = var2.paintInvalid;
      this.eolMarkerColor = var2.eolMarkerColor;
      this.eolMarkers = var2.eolMarkers;
   }

   public final SyntaxStyle[] getStyles() {
      return this.styles;
   }

   public final void setStyles(SyntaxStyle[] var1) {
      this.styles = var1;
      this.repaint();
   }

   public final Color getCaretColor() {
      return this.caretColor;
   }

   public final void setCaretColor(Color var1) {
      this.caretColor = var1;
      this.invalidateSelectedLines();
   }

   public final Color getSelectionColor() {
      return this.selectionColor;
   }

   public final void setSelectionColor(Color var1) {
      this.selectionColor = var1;
      this.invalidateSelectedLines();
   }

   public final Color getLineHighlightColor() {
      return this.lineHighlightColor;
   }

   public final void setLineHighlightColor(Color var1) {
      this.lineHighlightColor = var1;
      this.invalidateSelectedLines();
   }

   public final boolean isLineHighlightEnabled() {
      return this.lineHighlight;
   }

   public final void setLineHighlightEnabled(boolean var1) {
      this.lineHighlight = var1;
      this.invalidateSelectedLines();
   }

   public final Color getBracketHighlightColor() {
      return this.bracketHighlightColor;
   }

   public final void setBracketHighlightColor(Color var1) {
      this.bracketHighlightColor = var1;
      this.invalidateLine(this.textArea.getBracketLine());
   }

   public final boolean isBracketHighlightEnabled() {
      return this.bracketHighlight;
   }

   public final void setBracketHighlightEnabled(boolean var1) {
      this.bracketHighlight = var1;
      this.invalidateLine(this.textArea.getBracketLine());
   }

   public final boolean isBlockCaretEnabled() {
      return this.blockCaret;
   }

   public final void setBlockCaretEnabled(boolean var1) {
      this.blockCaret = var1;
      this.invalidateSelectedLines();
   }

   public final Color getEOLMarkerColor() {
      return this.eolMarkerColor;
   }

   public final void setEOLMarkerColor(Color var1) {
      this.eolMarkerColor = var1;
      this.repaint();
   }

   public final boolean getEOLMarkersPainted() {
      return this.eolMarkers;
   }

   public final void setEOLMarkersPainted(boolean var1) {
      this.eolMarkers = var1;
      this.repaint();
   }

   public boolean getInvalidLinesPainted() {
      return this.paintInvalid;
   }

   public void setInvalidLinesPainted(boolean var1) {
      this.paintInvalid = var1;
   }

   public void addCustomHighlight(TextAreaPainter.Highlight var1) {
      var1.init(this.textArea, this.highlights);
      this.highlights = var1;
   }

   public String getToolTipText(MouseEvent var1) {
      return this.highlights != null ? this.highlights.getToolTipText(var1) : null;
   }

   public FontMetrics getFontMetrics() {
      return this.fm;
   }

   public void setFont(Font var1) {
      super.setFont(var1);
      this.fm = Toolkit.getDefaultToolkit().getFontMetrics(var1);
      this.textArea.recalculateVisibleLines();
   }

   public void paint(Graphics var1) {
      this.tabSize = this.fm.charWidth(' ') * (Integer)this.textArea.getDocument().getProperty("tabSize");
      Rectangle var2 = var1.getClipBounds();
      var1.setColor(this.getBackground());
      var1.fillRect(var2.x, var2.y, var2.width, var2.height);
      int var3 = this.fm.getHeight();
      int var4 = this.textArea.getFirstLine();
      int var5 = var4 + var2.y / var3;
      int var6 = var4 + (var2.y + var2.height - 1) / var3;

      try {
         TokenMarker var7 = this.textArea.getDocument().getTokenMarker();
         int var8 = this.textArea.getHorizontalOffset();

         int var9;
         for(var9 = var5; var9 <= var6; ++var9) {
            this.paintLine(var1, var7, var9, var8);
         }

         if (var7 != null && var7.isNextLineRequested()) {
            var9 = var2.y + var2.height;
            this.repaint(0, var9, this.getWidth(), this.getHeight() - var9);
         }
      } catch (Exception var10) {
         System.err.println("Error repainting line range {" + var5 + "," + var6 + "}:");
         var10.printStackTrace();
      }

   }

   public final void invalidateLine(int var1) {
      this.repaint(0, this.textArea.lineToY(var1) + this.fm.getMaxDescent() + this.fm.getLeading(), this.getWidth(), this.fm.getHeight());
   }

   public final void invalidateLineRange(int var1, int var2) {
      this.repaint(0, this.textArea.lineToY(var1) + this.fm.getMaxDescent() + this.fm.getLeading(), this.getWidth(), (var2 - var1 + 1) * this.fm.getHeight());
   }

   public final void invalidateSelectedLines() {
      this.invalidateLineRange(this.textArea.getSelectionStartLine(), this.textArea.getSelectionEndLine());
   }

   public float nextTabStop(float var1, int var2) {
      int var3 = this.textArea.getHorizontalOffset();
      int var4 = ((int)var1 - var3) / this.tabSize;
      return (float)((var4 + 1) * this.tabSize + var3);
   }

   public Dimension getPreferredSize() {
      Dimension var1 = new Dimension();
      var1.width = this.fm.charWidth('w') * this.cols;
      var1.height = this.fm.getHeight() * this.rows;
      return var1;
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   protected void paintLine(Graphics var1, TokenMarker var2, int var3, int var4) {
      Font var5 = this.getFont();
      Color var6 = this.getForeground();
      this.currentLineIndex = var3;
      int var7 = this.textArea.lineToY(var3);
      if (var3 >= 0 && var3 < this.textArea.getLineCount()) {
         if (var2 == null) {
            this.paintPlainLine(var1, var3, var5, var6, var4, var7);
         } else {
            this.paintSyntaxLine(var1, var2, var3, var5, var6, var4, var7);
         }
      } else if (this.paintInvalid) {
         this.paintHighlight(var1, var3, var7);
         this.styles[10].setGraphicsFlags(var1, var5);
         var1.drawString("~", 0, var7 + this.fm.getHeight());
      }

   }

   protected void paintPlainLine(Graphics var1, int var2, Font var3, Color var4, int var5, int var6) {
      this.paintHighlight(var1, var2, var6);
      this.textArea.getLineText(var2, this.currentLine);
      var1.setFont(var3);
      var1.setColor(var4);
      var6 += this.fm.getHeight();
      var5 = Utilities.drawTabbedText(this.currentLine, var5, var6, var1, this, 0);
      if (this.eolMarkers) {
         var1.setColor(this.eolMarkerColor);
         var1.drawString(".", var5, var6);
      }

   }

   protected void paintSyntaxLine(Graphics var1, TokenMarker var2, int var3, Font var4, Color var5, int var6, int var7) {
      this.textArea.getLineText(this.currentLineIndex, this.currentLine);
      this.currentLineTokens = var2.markTokens(this.currentLine, this.currentLineIndex);
      this.paintHighlight(var1, var3, var7);
      var1.setFont(var4);
      var1.setColor(var5);
      var7 += this.fm.getHeight();
      var6 = SyntaxUtilities.paintSyntaxLine(this.currentLine, this.currentLineTokens, this.styles, this, var1, var6, var7);
      if (this.eolMarkers) {
         var1.setColor(this.eolMarkerColor);
         var1.drawString(".", var6, var7);
      }

   }

   protected void paintHighlight(Graphics var1, int var2, int var3) {
      if (var2 >= this.textArea.getSelectionStartLine() && var2 <= this.textArea.getSelectionEndLine()) {
         this.paintLineHighlight(var1, var2, var3);
      }

      if (this.highlights != null) {
         this.highlights.paintHighlight(var1, var2, var3);
      }

      if (this.bracketHighlight && var2 == this.textArea.getBracketLine()) {
         this.paintBracketHighlight(var1, var2, var3);
      }

      if (var2 == this.textArea.getCaretLine()) {
         this.paintCaret(var1, var2, var3);
      }

   }

   protected void paintLineHighlight(Graphics var1, int var2, int var3) {
      int var4 = this.fm.getHeight();
      var3 += this.fm.getLeading() + this.fm.getMaxDescent();
      int var5 = this.textArea.getSelectionStart();
      int var6 = this.textArea.getSelectionEnd();
      if (var5 == var6) {
         if (this.lineHighlight) {
            var1.setColor(this.lineHighlightColor);
            var1.fillRect(0, var3, this.getWidth(), var4);
         }
      } else {
         var1.setColor(this.selectionColor);
         int var7 = this.textArea.getSelectionStartLine();
         int var8 = this.textArea.getSelectionEndLine();
         int var9 = this.textArea.getLineStartOffset(var2);
         int var10;
         int var11;
         if (this.textArea.isSelectionRectangular()) {
            int var12 = this.textArea.getLineLength(var2);
            var10 = this.textArea._offsetToX(var2, Math.min(var12, var5 - this.textArea.getLineStartOffset(var7)));
            var11 = this.textArea._offsetToX(var2, Math.min(var12, var6 - this.textArea.getLineStartOffset(var8)));
            if (var10 == var11) {
               ++var11;
            }
         } else if (var7 == var8) {
            var10 = this.textArea._offsetToX(var2, var5 - var9);
            var11 = this.textArea._offsetToX(var2, var6 - var9);
         } else if (var2 == var7) {
            var10 = this.textArea._offsetToX(var2, var5 - var9);
            var11 = this.getWidth();
         } else if (var2 == var8) {
            var10 = 0;
            var11 = this.textArea._offsetToX(var2, var6 - var9);
         } else {
            var10 = 0;
            var11 = this.getWidth();
         }

         var1.fillRect(var10 > var11 ? var11 : var10, var3, var10 > var11 ? var10 - var11 : var11 - var10, var4);
      }

   }

   protected void paintBracketHighlight(Graphics var1, int var2, int var3) {
      int var4 = this.textArea.getBracketPosition();
      if (var4 != -1) {
         var3 += this.fm.getLeading() + this.fm.getMaxDescent();
         int var5 = this.textArea._offsetToX(var2, var4);
         var1.setColor(this.bracketHighlightColor);
         var1.drawRect(var5, var3, this.fm.charWidth('(') - 1, this.fm.getHeight() - 1);
      }
   }

   protected void paintCaret(Graphics var1, int var2, int var3) {
      if (this.textArea.isCaretVisible()) {
         int var4 = this.textArea.getCaretPosition() - this.textArea.getLineStartOffset(var2);
         int var5 = this.textArea._offsetToX(var2, var4);
         int var6 = !this.blockCaret && !this.textArea.isOverwriteEnabled() ? 1 : this.fm.charWidth('w');
         var3 += this.fm.getLeading() + this.fm.getMaxDescent();
         int var7 = this.fm.getHeight();
         var1.setColor(this.caretColor);
         if (this.textArea.isOverwriteEnabled()) {
            var1.fillRect(var5, var3 + var7 - 1, var6, 1);
         } else {
            var1.drawRect(var5, var3, var6, var7 - 1);
         }
      }

   }

   public interface Highlight {
      void init(JEditTextArea var1, TextAreaPainter.Highlight var2);

      void paintHighlight(Graphics var1, int var2, int var3);

      String getToolTipText(MouseEvent var1);
   }
}
