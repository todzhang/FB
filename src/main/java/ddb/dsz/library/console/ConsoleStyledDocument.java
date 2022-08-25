package ddb.dsz.library.console;

import ddb.GuiConstants;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.collections.map.LazyMap;

public class ConsoleStyledDocument extends DefaultStyledDocument {
   Map<MultiKey, Font> fontMap = LazyMap.decorate(new LRUMap(5), new Transformer() {
      public Object transform(Object var1) {
         Font var2 = GuiConstants.FIXED_WIDTH_FONT.Basic;
         if (var1 instanceof MultiKey) {
            MultiKey var3 = (MultiKey)var1;
            var2 = var2.deriveFont(Float.valueOf(String.format("%d", var3.getKey(0)))).deriveFont((Integer)var3.getKey(1));
         }

         return var2;
      }
   });
   private int MaximumCharacters = 0;
   private int CurrentCharacters = 0;
   private Runnable g_Clear = null;

   public void setMaximumCharacters(int maximumCharacters) {
      this.MaximumCharacters = maximumCharacters;
   }

   public void appendString(String var1, final AttributeSet var2) throws BadLocationException {
      if (this.MaximumCharacters > 0 && var1.length() > this.MaximumCharacters) {
         var1 = var1.substring(var1.length() - this.MaximumCharacters);
      }

      if (!EventQueue.isDispatchThread()) {
         String finalVar = var1;
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               try {
                  ConsoleStyledDocument.this.appendString(finalVar, var2);
               } catch (BadLocationException var2x) {
                  var2x.printStackTrace();
               }

            }
         });
      } else {
         this.insertString(this.CurrentCharacters, var1, var2);
      }
   }

   @Override
   public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  ConsoleStyledDocument.this.insertString(offs, str, a);
               } catch (BadLocationException var2x) {
                  var2x.printStackTrace();
               }

            }
         });
      } else {
         this.readLock();

         label70: {
            try {
               if (this.g_Clear == null) {
                  break label70;
               }
            } finally {
               this.readUnlock();
            }

            return;
         }

         int var4 = offs;
         String var5 = str;
         if (this.MaximumCharacters > 0 && str.length() > this.MaximumCharacters) {
            var5 = str.substring(str.length() - this.MaximumCharacters);
         }

         if (this.MaximumCharacters > 0) {
            if (var5.length() == this.MaximumCharacters) {
               this.remove(0, this.CurrentCharacters);
               var4 = 0;
            } else if (var5.length() + this.CurrentCharacters > this.MaximumCharacters) {
               int var6 = this.CurrentCharacters - (this.MaximumCharacters - var5.length());
               this.remove(0, var6);
               var4 = offs - var6;
            }
         }

         super.insertString(var4, var5, a);
         this.CurrentCharacters += var5.length();
      }
   }

   @Override
   public void remove(final int offs, final int len) throws BadLocationException {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  ConsoleStyledDocument.this.remove(offs, len);
               } catch (BadLocationException var2x) {
                  var2x.printStackTrace();
               }

            }
         });
      } else {
         this.readLock();

         try {
            if (this.g_Clear != null) {
               return;
            }
         } finally {
            this.readUnlock();
         }

         try {
            super.remove(offs, len);
            this.CurrentCharacters -= len;
         } catch (BadLocationException var6) {
            System.err.printf("Invalid remove of %d bytes at %d\n", len, offs);
         }

      }
   }

   public void clear() {
      if (!EventQueue.isDispatchThread()) {
         this.writeLock();

         try {
            this.g_Clear = new Runnable() {
               public void run() {
                  ConsoleStyledDocument.this.readLock();

                  try {
                     if (this != ConsoleStyledDocument.this.g_Clear) {
                        return;
                     }
                  } finally {
                     ConsoleStyledDocument.this.readUnlock();
                  }

                  ConsoleStyledDocument.this.writeLock();

                  try {
                     ConsoleStyledDocument.this.g_Clear = null;
                  } finally {
                     ConsoleStyledDocument.this.writeUnlock();
                  }

                  ConsoleStyledDocument.this.clear();
               }
            };
            EventQueue.invokeLater(this.g_Clear);
         } finally {
            this.writeUnlock();
         }

      } else {
         try {
            this.remove(0, this.CurrentCharacters);
         } catch (BadLocationException var5) {
         }

      }
   }

   @Override
   public Font getFont(AttributeSet attr) {
      Font var2 = super.getFont(attr);
      return this.fontMap.get(new MultiKey(super.getFont(attr).getSize(), var2.getStyle()));
   }
}
