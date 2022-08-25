package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.library.console.ConsoleOutputPane;
import ddb.dsz.library.console.ConsoleStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;

public class LogViewerOutputPane extends ConsoleOutputPane {
   public LogViewerOutputPane(CoreController core) {
      super(core);
   }

   public void setDocument(ConsoleStyledDocument doc) {
      this.outputDoc = doc;
      if (doc != null) {
         this.textPane.setDocument(doc);
      }

   }

   public Style getBoldStyle() {
      return this.boldStyle;
   }

   public Style getDefaultStyle() {
      return this.defaultStyle;
   }

   public Style getDocProvidedStyle() {
      return this.docProvidedStyle;
   }

   public Style getErrorStyle() {
      return this.errorStyle;
   }

   public Style getNoticeStyle() {
      return this.noticeStyle;
   }

   public Style getWarningStyle() {
      return this.warningStyle;
   }

   public JTextComponent getTextComponent() {
      return this.textPane;
   }
}
