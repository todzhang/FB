package ddb.gui;

import javax.swing.ComboBoxModel;

public interface FindReplaceSettings {
   String classVersion = "4.2";

   boolean getConfirmChanges();

   ComboBoxModel getFindModel();

   boolean getMatchCase();

   boolean getMatchWholeWord();

   ComboBoxModel getReplaceModel();

   void setConfirmChanges(boolean var1);

   void setFindModel(ComboBoxModel var1);

   void setMatchCase(boolean var1);

   void setMatchWholeWord(boolean var1);

   void setReplaceModel(ComboBoxModel var1);
}
