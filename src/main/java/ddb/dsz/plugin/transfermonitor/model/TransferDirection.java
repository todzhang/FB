package ddb.dsz.plugin.transfermonitor.model;

public enum TransferDirection {
   GET("Received"),
   PUT("Sent");

   String text;

   private TransferDirection(String var3) {
      this.text = var3;
   }

   public String getText() {
      return this.text;
   }
}
