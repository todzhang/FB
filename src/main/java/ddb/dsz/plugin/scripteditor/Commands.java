package ddb.dsz.plugin.scripteditor;

import javax.swing.KeyStroke;

public enum Commands {
   New("New", 'N', "images/filenew.png", "Create a new document", KeyStroke.getKeyStroke(78, 2)),
   Open("Open ...", 'O', "images/fileopen.png", "Open an existing document", KeyStroke.getKeyStroke(79, 2)),
   Save("Save", 'S', "images/filesave.png", "Save the current document", KeyStroke.getKeyStroke(83, 2)),
   SaveAll("Save All", 'L', (String)null, "Save all documents", KeyStroke.getKeyStroke(83, 3)),
   SaveAs("Save As ...", 'A', "images/filesaveas.png", "Save the current document to a new file", (KeyStroke)null),
   Close("Close", 'C', "images/fileclose.png", "Close the current document", KeyStroke.getKeyStroke(87, 2)),
   CloseAll("Close All", 'E', (String)null, "Close all documents", KeyStroke.getKeyStroke(87, 3)),
   RecentFiles("Recent Files", 'F', (String)null, "", (KeyStroke)null, true),
   Copy("Copy", 'C', "images/editcopy.png", "Copy the currently selected text", KeyStroke.getKeyStroke(67, 2)),
   Cut("Cut", 'T', "images/editcut.png", "Cut the currently selected text", KeyStroke.getKeyStroke(88, 2)),
   Paste("Paste", 'P', "images/editpaste.png", "Paste the contents of the clipboard", KeyStroke.getKeyStroke(86, 2)),
   Undo("Undo", 'U', "images/undo.png", "Undo the last action taken", KeyStroke.getKeyStroke(90, 2)),
   Redo("Redo", 'R', "images/redo.png", "Redo the last action undone", KeyStroke.getKeyStroke(89, 2)),
   Reload("Redo", 'E', "images/reload.png", "Reload an unmodified version of this file", (KeyStroke)null),
   Compile("Compile", 'C', "images/kguitar.png", "Save and test the script", KeyStroke.getKeyStroke(118, 0)),
   NextTab("Next Tab", 'N', (String)null, (String)null, KeyStroke.getKeyStroke(33, 2)),
   PreviousTab("Previous Tab", 'N', (String)null, (String)null, KeyStroke.getKeyStroke(34, 2));

   String description;
   String icon;
   char mnemonic;
   String text;
   KeyStroke stroke;
   boolean submenu;

   private Commands(String var3, char var4, String var5, String var6, KeyStroke var7) {
      this(var3, var4, var5, var6, var7, false);
   }

   private Commands(String var3, char var4, String var5, String var6, KeyStroke var7, boolean var8) {
      this.text = var3;
      this.mnemonic = var4;
      this.icon = var5;
      this.description = var6;
      this.stroke = var7;
      this.submenu = var8;
   }
}
