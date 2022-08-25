package ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity;

import ddb.dsz.core.data.ObjectValue;

public class Identity {
   String attributes;
   String type;
   String name;

   public Identity(ObjectValue identity) {
      this.attributes = identity.getString("attributes");
      this.type = identity.getString("type");
      this.name = identity.getString("name");
   }

   public String getAttributes() {
      return this.attributes;
   }

   public String getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }
}
