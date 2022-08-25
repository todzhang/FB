package ddb.dsz.plugin.taskmanager.processinformation.group;

public class Attribute {
   String name;
   boolean enabled;

   public Attribute(String name, boolean enabled) {
      this.name = name;
      this.enabled = enabled;
   }

   public String getName() {
      return this.name;
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
