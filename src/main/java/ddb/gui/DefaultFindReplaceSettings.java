package ddb.gui;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public class DefaultFindReplaceSettings implements FindReplaceSettings {
   public static final String classVersion = "4.2";
   protected boolean matchCase = false;
   protected boolean matchWholeWord = false;
   protected boolean confirmChanges = false;
   protected ComboBoxModel findModel = new DefaultComboBoxModel();
   protected ComboBoxModel replaceModel = new DefaultComboBoxModel();
   protected static FindReplaceSettings settings = new DefaultFindReplaceSettings();

   public boolean getConfirmChanges() {
      return this.confirmChanges;
   }

   public ComboBoxModel getFindModel() {
      return this.findModel;
   }

   public static FindReplaceSettings getInstance() {
      return settings;
   }

   public boolean getMatchCase() {
      return this.matchCase;
   }

   public boolean getMatchWholeWord() {
      return this.matchWholeWord;
   }

   public ComboBoxModel getReplaceModel() {
      return this.replaceModel;
   }

   public void setConfirmChanges(boolean value) {
      this.confirmChanges = value;
   }

   public void setFindModel(ComboBoxModel model) {
      this.findModel = model;
   }

   public void setMatchCase(boolean value) {
      this.matchCase = value;
   }

   public void setMatchWholeWord(boolean value) {
      this.matchWholeWord = value;
   }

   public void setReplaceModel(ComboBoxModel model) {
      this.replaceModel = model;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("FindReplaceDialogModel\n");
      sb.append("Version:  ");
      sb.append("4.2");
      sb.append("\n");
      sb.append("Match Case Property:  ");
      sb.append(this.getMatchCase());
      sb.append("\n");
      sb.append("Match Whole Word Property:  ");
      sb.append(this.getMatchWholeWord());
      sb.append("\n");
      sb.append("Confirm Changes Property:  ");
      sb.append(this.getConfirmChanges());
      sb.append("\n");
      sb.append("Search Strings:  ");

      int i;
      for(i = 0; i < this.getFindModel().getSize(); ++i) {
         if (i > 0) {
            sb.append("                 ");
         }

         sb.append((String)this.getFindModel().getElementAt(i));
         sb.append("\n");
      }

      for(i = 0; i < this.getReplaceModel().getSize(); ++i) {
         if (i > 0) {
            sb.append("                 ");
         }

         sb.append((String)this.getReplaceModel().getElementAt(i));
         sb.append("\n");
      }

      return sb.toString();
   }
}
