package ddb.delegate;

import javax.swing.JLabel;

public class UpdateLabel implements Runnable {
   JLabel label;
   String text;

   public UpdateLabel(JLabel var1, String var2) {
      this.label = var1;
      this.text = var2;
   }

   public void run() {
      this.label.setText(this.text);
   }
}
