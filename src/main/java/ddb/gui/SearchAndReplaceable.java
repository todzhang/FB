package ddb.gui;

public interface SearchAndReplaceable extends Searchable {
   String classVersion = "2.3.1";

   String getSelectedText();

   boolean isEditable();

   boolean isSelectedTextWholeWord();

   boolean replaceSelectedText(String var1);
}
