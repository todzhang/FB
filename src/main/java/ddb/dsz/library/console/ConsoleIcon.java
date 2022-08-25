package ddb.dsz.library.console;

public enum ConsoleIcon {
   CONSOLE("images/terminal.png"),
   BUSY("images/exec.png"),
   IDLE("images/forward.png"),
   LOAD("images/load.png"),
   SAVE("images/save.png"),
   PLUS("images/blue-plus.png"),
   MINUS("images/blue-minus.png"),
   CUT("images/editcut.png"),
   COPY("images/editcopy.png"),
   PASTE("images/editpaste.png");

   String icon;

   private ConsoleIcon(String icon) {
      this.icon = icon;
   }

   public String getIcon() {
      return this.icon;
   }
}
