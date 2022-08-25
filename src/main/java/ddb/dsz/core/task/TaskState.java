package ddb.dsz.core.task;

public enum TaskState {
   INITIALIZED("Initialized", "images/player_end.png"),
   KILLED("Killed", "images/error.png"),
   SUCCEEDED("Succeeded", "images/button_ok.png"),
   FAILED("Failed", "images/error.png"),
   TASKED("Tasked", "images/player_end.png"),
   RUNNING("Running", "images/player_play.png"),
   PAUSED("Paused", "player_pause.png");

   private final String text;
   private final String icon;

   TaskState(String text, String icon) {
      this.text = text;
      this.icon = icon;
   }

   public final String getText() {
      return this.text;
   }

   public final String getIcon() {
      return this.icon;
   }

   public static final TaskState parseResult(String result) {
      if (result.equalsIgnoreCase("0x00000000")) {
         return SUCCEEDED;
      } else {
         return result.equalsIgnoreCase("0x10000001") ? KILLED : FAILED;
      }
   }
}
