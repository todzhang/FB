package ddb.targetmodel.filemodel.history;

public enum ActionType {
   UNKNOWN("Unknown"),
   DISCOVERED("Discovered"),
   DELETE("Deleted"),
   MOVESOURCE("Moved to %2$s"),
   MOVEDEST("Moved from %2$s"),
   COPYSOURCE("Copied to %2$s"),
   COPYDEST("Copied from %2$s"),
   INFO("Retrieved INFO in a %1$s"),
   RETRIEVAL("Retrieved the file"),
   CREATED("Created in a %1$s");

   String formatString;

   private ActionType(String var3) {
      this.formatString = var3;
   }

   public String getAction(CommandType var1, String var2) {
      return String.format(this.formatString, var1.toString(), var2);
   }
}
