package ddb.dsz.plugin.taskmanager.details;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ProcessDetails extends JPanel {
   JTabbedPane tabs = new JTabbedPane();
   CoreController core;

   public ProcessDetails(CoreController core) {
      super(new BorderLayout());
      this.core = core;
      super.add(this.tabs, "Center");
   }

   public boolean setProcessInfo(ProcessInformation procInfo) {
      if (procInfo == null) {
         return false;
      } else {
         this.tabs.removeAll();
         if (procInfo.hasProcessInfo()) {
            if (procInfo.getBasicInfo() != null) {
               this.tabs.add("Basic Info", Generator.makeBasicInfoDisplay(this.core, procInfo.getBasicInfo()));
            }

            if (procInfo.getGroups().size() != 0) {
               this.tabs.add("Groups", Generator.makeGroupsDisplay(this.core, procInfo.getGroups()));
            }

            this.tabs.add("Privileges", Generator.makePrivilegesDisplay(this.core, procInfo));
            this.tabs.add("Modules", Generator.makeModulesDisplay(this.core, procInfo));
         }

         if (procInfo.hasHandleInfo()) {
            this.tabs.add("Handles", Generator.makeHandleInfoDisplay(this.core, procInfo));
         }

         return this.tabs.getTabCount() > 0;
      }
   }
}
