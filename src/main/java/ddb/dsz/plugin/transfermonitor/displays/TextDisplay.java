package ddb.dsz.plugin.transfermonitor.displays;

import ddb.dsz.plugin.transfermonitor.TransferTabbable;
import ddb.util.FileManips;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class TextDisplay extends TransferTabbable {
   public TextDisplay(final File var1) throws IOException {
      super("Text");
      final JTextArea var2 = new JTextArea(20, 60);
      var2.setText("File Loading...");
      var2.setEditable(false);
      var2.setEnabled(false);
      super.display.add(new JScrollPane(var2));
      SwingWorker var3 = new SwingWorker<String, String>() {
         protected String doInBackground() throws Exception {
            if (var1.length() == 0L) {
               return "* Empty File *";
            } else {
               InputStreamReader var1x = FileManips.createFileReader(var1);
               boolean var2x = false;
               char[] var3 = new char[128];
               StringBuffer var4 = new StringBuffer();

               int var5;
               while(-1 != (var5 = var1x.read(var3))) {
                  var4.append(var3, 0, var5);
               }

               return var4.toString();
            }
         }

         protected void done() {
            try {
               var2.setText((String)this.get());
            } catch (Exception var2x) {
               var2.setText("Unable to retrieve file data: " + var2x.getMessage());
            }

            var2.setEnabled(true);
            var2.setCaretPosition(0);
         }
      };
      var3.execute();
   }

   @Override
   public boolean isClosable() {
      return false;
   }
}
