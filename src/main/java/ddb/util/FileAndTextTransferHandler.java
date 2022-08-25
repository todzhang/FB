package ddb.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

public class FileAndTextTransferHandler extends TransferHandler {
   private DataFlavor fileFlavor;
   private DataFlavor stringFlavor;
   private JTextComponent source;
   private boolean shouldRemove;
   Position p0 = null;
   Position p1 = null;

   public FileAndTextTransferHandler() {
      this.fileFlavor = DataFlavor.javaFileListFlavor;
      this.stringFlavor = DataFlavor.stringFlavor;
   }

   @Override
   public boolean importData(TransferSupport transInfo) {
      if (!(transInfo.getComponent() instanceof JTextComponent)) {
         return false;
      } else {
         JTextComponent tc = (JTextComponent)JTextComponent.class.cast(transInfo.getComponent());
         if (!this.canImport(transInfo)) {
            return false;
         } else {
            try {
               StringBuilder insert = new StringBuilder();
               String insertText;
               if (this.hasFileFlavor(transInfo.getDataFlavors())) {
                  insertText = null;
                  List<File> files = (List)transInfo.getTransferable().getTransferData(this.fileFlavor);

                  File f;
                  for(Iterator i$ = files.iterator(); i$.hasNext(); insert.append(f.getAbsolutePath())) {
                     f = (File)i$.next();
                     if (insert.length() > 0) {
                        insert.append(" ");
                     }
                  }
               }

               if (this.hasStringFlavor(transInfo.getDataFlavors())) {
                  if (insert.length() > 0) {
                     insert.append(" ");
                  }

                  insertText = (String)String.class.cast(transInfo.getTransferable().getTransferData(this.stringFlavor));
                  insert.append(insertText);
               }

               if (tc.equals(this.source) && tc.getCaretPosition() >= this.p0.getOffset() && tc.getCaretPosition() <= this.p1.getOffset()) {
                  this.shouldRemove = false;
                  return true;
               } else {
                  insertText = insert.toString();
                  if (insertText.contains(" ") && transInfo.isDrop()) {
                     int caretPos = tc.getCaretPosition();
                     String quotePairsEx = "\".*\"";
                     Pattern p = Pattern.compile(quotePairsEx);
                     Matcher match = p.matcher(tc.getText());
                     boolean isPreQuoted = false;

                     while(match.find()) {
                        if (caretPos > match.start() && caretPos < match.end()) {
                           isPreQuoted = true;
                           break;
                        }
                     }

                     if (!isPreQuoted) {
                        insertText = String.format("\"%s\"", insertText);
                     }
                  }

                  tc.replaceSelection(insertText);
                  return true;
               }
            } catch (IOException var10) {
               return false;
            } catch (UnsupportedFlavorException var11) {
               return false;
            }
         }
      }
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      this.source = (JTextComponent)c;
      int start = this.source.getSelectionStart();
      int end = this.source.getSelectionEnd();
      Document doc = this.source.getDocument();
      if (start == end) {
         return null;
      } else {
         try {
            this.p0 = doc.createPosition(start);
            this.p1 = doc.createPosition(end);
         } catch (BadLocationException var6) {
            var6.printStackTrace();
         }

         this.shouldRemove = true;
         String data = this.source.getSelectedText();
         return new StringSelection(data);
      }
   }

   @Override
   public int getSourceActions(JComponent c) {
      return 3;
   }

   @Override
   protected void exportDone(JComponent c, Transferable data, int action) {
      if (this.shouldRemove && action == 2 && this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset()) {
         try {
            JTextComponent tc = (JTextComponent)c;
            tc.getDocument().remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
         } catch (BadLocationException var5) {
            var5.printStackTrace();
         }
      }

      this.source = null;
   }

   @Override
   public boolean canImport(JComponent c, DataFlavor[] flavors) {
      if (this.hasFileFlavor(flavors)) {
         return true;
      } else {
         return this.hasStringFlavor(flavors);
      }
   }

   private boolean hasFileFlavor(DataFlavor[] flavors) {
      return this.hasFlavor(this.fileFlavor, flavors);
   }

   private boolean hasStringFlavor(DataFlavor[] flavors) {
      return this.hasFlavor(this.stringFlavor, flavors);
   }

   private boolean hasFlavor(DataFlavor flav, DataFlavor[] flavors) {
      DataFlavor[] arr$ = flavors;
      int len$ = flavors.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DataFlavor df = arr$[i$];
         if (flav.equals(df)) {
            return true;
         }
      }

      return false;
   }
}
