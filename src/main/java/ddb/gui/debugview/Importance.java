package ddb.gui.debugview;

public enum Importance {
   NOT_SET("Not set"),
   DEBUG_VERY_VERBOSE("Very Verbose"),
   DEBUG_VERBOSE("Verbose"),
   DEBUG("Debug"),
   INFO("Info"),
   WARNING("Warning"),
   ERROR("Error"),
   CRITICAL("Critical"),
   OFF("Off");

   String desc;

   private Importance(String desc) {
      this.desc = desc;
   }

   public String toString() {
      return this.desc;
   }
}
