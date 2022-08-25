package ddb.dsz.plugin.taskmanager.processinformation.generator;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.WindowsBasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.group.WindowsGroup;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.WindowsPrivilege;

public class WindowsGenerator extends Generator {
   public WindowsGenerator(CoreController core) {
      super(core);
   }

   public WindowsBasicInfo newBasicInfo(ObjectValue basicInfo) {
      return basicInfo == null ? null : new WindowsBasicInfo(basicInfo);
   }

   public WindowsGroup newGroup(ObjectValue group) {
      return group == null ? null : new WindowsGroup(group);
   }

   public WindowsPrivilege newPrivilege(ObjectValue privilege) {
      return privilege == null ? null : new WindowsPrivilege(privilege);
   }

   public Handle newHandle(ObjectValue handle) {
      if (handle == null) {
         return null;
      } else {
         Handle wh = new Handle(handle);
         return wh.getType() == null ? null : wh;
      }
   }
}
