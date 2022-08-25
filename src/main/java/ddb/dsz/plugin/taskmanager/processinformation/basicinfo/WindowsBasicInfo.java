package ddb.dsz.plugin.taskmanager.processinformation.basicinfo;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.WindowsIdentity;

public class WindowsBasicInfo extends BasicInfo {
   public WindowsBasicInfo(ObjectValue basicInfo) {
      super(basicInfo);
   }

   protected WindowsIdentity newIdentity(ObjectValue identity) {
      return new WindowsIdentity(identity);
   }
}
