package ddb.dsz.plugin.filemanager.ver3.search;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public enum DateSearchType {
   Modified("Modified"),
   Accessed("Last Accessed"),
   Created("Created");

   String text;

   private DateSearchType(String var3) {
      this.text = var3;
   }

   public String toString() {
      return this.text;
   }

   public static ComboBoxModel getModel() {
      DefaultComboBoxModel var0 = new DefaultComboBoxModel();
      var0.addElement(Modified);
      var0.addElement(Created);
      var0.addElement(Accessed);
      return var0;
   }
}
