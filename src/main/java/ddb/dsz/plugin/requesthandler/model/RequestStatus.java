package ddb.dsz.plugin.requesthandler.model;

public enum RequestStatus {
   PENDING("images/player_pause.png"),
   ALLOWED("images/player_play.png"),
   DENIED("images/tm_fatal.png"),
   CANCELLED("images/error.png"),
   EXECUTED("images/tm_completed.png");

   String img;

   private RequestStatus(String img) {
      this.img = img;
   }

   public String getImage() {
      return this.img;
   }
}
