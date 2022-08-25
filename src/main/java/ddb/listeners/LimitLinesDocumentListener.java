package ddb.listeners;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class LimitLinesDocumentListener implements DocumentListener {
   private int maximumLines;
   private boolean isRemoveFromStart;

   public LimitLinesDocumentListener(int var1) {
      this(var1, true);
   }

   public LimitLinesDocumentListener(int var1, boolean var2) {
      this.maximumLines = var1;
      this.isRemoveFromStart = var2;
   }

   public int getLimitLines() {
      return this.maximumLines;
   }

   public void setLimitLiens(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("Maximum lines must be greater than 0");
      } else {
         this.maximumLines = var1;
      }
   }

   public void insertUpdate(final DocumentEvent var1) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            LimitLinesDocumentListener.this.removeLines(var1);
         }
      });
   }

   public void removeUpdate(DocumentEvent var1) {
   }

   public void changedUpdate(DocumentEvent var1) {
   }

   private void removeLines(DocumentEvent var1) {
      Document var2 = var1.getDocument();
      Element var3 = var2.getDefaultRootElement();

      while(var3.getElementCount() > this.maximumLines) {
         int var4 = var3.getElementCount() - this.maximumLines + (int)((double)this.maximumLines * 0.3D);
         if (this.isRemoveFromStart) {
            this.removeFromStart(var2, var3, var4);
         } else {
            this.removeFromEnd(var2, var3, var4);
         }
      }

   }

   private void removeFromStart(Document var1, Element var2, int var3) {
      Element var4 = var2.getElement(var3);
      int var5 = var4.getEndOffset();

      try {
         var1.remove(0, var5);
      } catch (BadLocationException var7) {
         var7.printStackTrace();
      }

   }

   private void removeFromEnd(Document var1, Element var2, int var3) {
      Element var4 = var2.getElement(var2.getElementCount() - 1 - var3);
      int var5 = var4.getStartOffset();
      int var6 = var4.getEndOffset();

      try {
         var1.remove(var5 - 1, var6 - var5);
      } catch (BadLocationException var8) {
         var8.printStackTrace();
      }

   }
}
