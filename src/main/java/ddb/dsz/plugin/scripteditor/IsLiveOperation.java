package ddb.dsz.plugin.scripteditor;

import ddb.dsz.core.controller.CoreController;

public class IsLiveOperation implements ValidOption {
   CoreController cc;

   IsLiveOperation(CoreController var1) {
      this.cc = var1;
   }

   public boolean isValid() {
      return this.cc.isLiveOperation();
   }
}
