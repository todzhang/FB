package ddb.dsz.library.console;

import ddb.history.History;
import ddb.util.FileExtensionFilter;
import ddb.util.XMLException;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class LoadSaveHistoryAction extends AbstractAction {
   private final Component parent;
   private History<String> history;
   private final LoadSaveHistoryAction.HistoryActionType mode;
   private final URL historyFile;
   private static final Object CHOOSER_LOCK = new Object();
   private static JFileChooser fileChooser = null;

   public LoadSaveHistoryAction(Console console, Component parent, History<String> history, LoadSaveHistoryAction.HistoryActionType mode) {
      this.parent = parent;
      this.history = history;
      this.mode = mode;
      this.historyFile = LoadSaveHistoryAction.class.getClassLoader().getResource(console.getHistory());
   }

   private static JFileChooser getFileChooser(URL url) {
      JFileChooser var1 = new JFileChooser();
      if (url != null) {
         String var2 = url.getPath();
         var1.setCurrentDirectory(new File(var2));
      }

      FileExtensionFilter var3 = new FileExtensionFilter("xml", true);
      var1.addChoosableFileFilter(var3);
      var1.setMultiSelectionEnabled(false);
      return var1;
   }

   public void actionPerformed(ActionEvent actionEvent) {
      synchronized(CHOOSER_LOCK) {
         if (fileChooser == null) {
            fileChooser = getFileChooser(this.historyFile);
         }

         if (fileChooser == null) {
            return;
         }
      }

      fileChooser.updateUI();
      int var2 = 2;
      switch(this.mode) {
      case LOAD:
         var2 = fileChooser.showOpenDialog(this.parent);
         break;
      case SAVE:
         var2 = fileChooser.showSaveDialog(this.parent);
      }

      switch(var2) {
      case -1:
      case 1:
         return;
      case 0:
         switch(this.mode) {
         case LOAD:
            this.loadHistoryFile(fileChooser.getSelectedFile());
            break;
         case SAVE:
            this.saveHistoryFile(fileChooser.getSelectedFile(), this.history.toList());
         }

         return;
      default:
      }
   }

   private void loadHistoryFile(File file) {
      List var2;
      try {
         var2 = HistoryFileParserWriter.parse(file.getPath());
      } catch (FileNotFoundException var5) {
         this.showErrorDialog(var5);
         return;
      } catch (XMLException var6) {
         this.showErrorDialog(var6);
         return;
      }

      Iterator var3 = var2.iterator();
      this.history.clear();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.history.addHistoryItem(var4);
      }

   }

   private void saveHistoryFile(File file, List<String> commands) {
      try {
         HistoryFileParserWriter.save(commands, file.getAbsolutePath());
      } catch (XMLException var4) {
         this.showErrorDialog(var4);
      } catch (IOException var5) {
         this.showErrorDialog(var5);
      }
   }

   public void showErrorDialog(Throwable throwable) {
      throwable.printStackTrace();
      JOptionPane.showMessageDialog(this.parent, "<pre>" + throwable.toString() + "</pre>", "Error", 0);
   }

   public enum HistoryActionType {
      LOAD,
      SAVE;
   }
}
