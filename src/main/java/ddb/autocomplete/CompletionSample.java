package ddb.autocomplete;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CompletionSample implements Runnable {
   @Override
   public void run() {
      CompletionSample.NameService var1 = new CompletionSample.NameService();
      JTextField var2 = new JTextField(20);
      AutoCompleteDocument var3 = new AutoCompleteDocument(var1, var2);
      var2.setDocument(var3);
      JFrame var4 = new JFrame("Autocompletion sample");
      var4.setDefaultCloseOperation(3);
      JComponent var5 = (JComponent)var4.getContentPane();
      var5.setLayout(new BorderLayout());
      var5.add(var2, "South");
      var5.setBorder(new EmptyBorder(10, 10, 10, 10));
      var4.pack();
      var4.setVisible(true);
   }

   private static class NameService implements CompletionService<String> {
      private List<String> data = Arrays.asList("Apple", "Orange", "Banana", "Apricote");

      public NameService() {
      }

      @Override
      public String autoComplete(String var1) {
         String var2 = null;
         Iterator var3 = this.data.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4.startsWith(var1)) {
               if (var2 != null) {
                  var2 = null;
                  break;
               }

               var2 = var4;
            }
         }

         return var2;
      }
   }
}
