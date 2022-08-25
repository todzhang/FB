package ddb.dsz.plugin.taskmanager.processinformation.generator;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.BasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;

public class Generator {
   protected CoreController core;

   public Generator(CoreController core) {
      this.core = core;
   }

   public BasicInfo newBasicInfo(ObjectValue basicInfo) {
      return basicInfo == null ? null : new BasicInfo(basicInfo);
   }

   public Group newGroup(ObjectValue group) {
      return group == null ? null : new Group(group);
   }

   public Module newModule(ObjectValue module) {
      return module == null ? null : new Module(module, this.core);
   }

   public Privilege newPrivilege(ObjectValue privilege) {
      return privilege == null ? null : null;
   }

   public Handle newHandle(ObjectValue handle) {
      return handle == null ? null : null;
   }
}
