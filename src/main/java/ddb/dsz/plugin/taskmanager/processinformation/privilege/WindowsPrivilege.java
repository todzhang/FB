package ddb.dsz.plugin.taskmanager.processinformation.privilege;

import ddb.dsz.core.data.ObjectValue;

public class WindowsPrivilege extends Privilege {
   String name;
   boolean enabled;
   boolean enabledByDefault;
   boolean usedAccess;
   long mask;

   public WindowsPrivilege(ObjectValue privilege) {
      super(privilege);
      this.name = privilege.getString("name");
      ObjectValue attributes = privilege.getObject("attributes");
      if (attributes != null) {
         this.enabled = attributes.getBoolean("priv_enabled");
         this.enabledByDefault = attributes.getBoolean("priv_enabled_by_default");
         this.usedAccess = attributes.getBoolean("priv_used_access");
         this.mask = attributes.getInteger("mask");
      }

   }

   public WindowsPrivilege(String name, boolean enabled, boolean enabledByDefault, boolean usedAccess, long mask) {
      this.name = name;
      this.enabled = enabled;
      this.enabledByDefault = enabledByDefault;
      this.usedAccess = usedAccess;
      this.mask = mask;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isEnabledByDefault() {
      return this.enabledByDefault;
   }

   public Long getMask() {
      return this.mask;
   }

   public String getName() {
      return this.name;
   }

   public boolean isUsedAccess() {
      return this.usedAccess;
   }
}
