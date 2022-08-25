package ddb.dsz.plugin.taskmanager.processinformation.basicinfo;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.Identity;

public class BasicInfo {
   Identity user;
   Identity owner;
   Identity primaryGroup;

   public BasicInfo(ObjectValue basicInfo) {
      this.user = this.newIdentity(basicInfo.getObject("user"));
      this.owner = this.newIdentity(basicInfo.getObject("owner"));
      this.primaryGroup = this.newIdentity(basicInfo.getObject("primarygroup"));
   }

   protected Identity newIdentity(ObjectValue identity) {
      return new Identity(identity);
   }

   public Identity getOwner() {
      return this.owner;
   }

   public Identity getPrimaryGroup() {
      return this.primaryGroup;
   }

   public Identity getUser() {
      return this.user;
   }
}
