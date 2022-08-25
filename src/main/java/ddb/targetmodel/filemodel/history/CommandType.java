package ddb.targetmodel.filemodel.history;

public enum CommandType {
   UNKNOWN("UNKNOWN"),
   CD("CD"),
   CHECKSUM("CHECKSUM"),
   COPY("COPY"),
   DELETE("DELETE"),
   DIR("DIR"),
   DRIVES("DRIVES"),
   GET("GET"),
   MKDIR("MKDIR"),
   MOVE("MOVE"),
   PUT("PUT"),
   PWD("PWD"),
   RECURSIVE_DIR("DIR"),
   REMOVE_DIR("RMDIR"),
   STRINGS("STRINGS"),
   PROCESSINFO("PROCESSINFO"),
   DISKSPACE("Diskspace"),
   DRIVERS("DRIVERS");

   String description;

   private CommandType(String var3) {
      this.description = var3;
   }

   public String toString() {
      return this.description;
   }
}
