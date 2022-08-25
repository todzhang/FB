package ddb.dsz.plugin.taskmanager.details.group;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.Generator;
import ddb.dsz.plugin.taskmanager.details.ListWithDetails;
import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class GroupsDisplay extends ListWithDetails {
   protected CoreController core;

   public GroupsDisplay(CoreController core, List<Group> groups) {
      this.core = core;
      Iterator i$ = groups.iterator();

      while(i$.hasNext()) {
         Group group = (Group)i$.next();
         this.model.addElement(group);
      }

   }

   protected ListCellRenderer getRenderer() {
      return new GroupRenderer();
   }

   protected JPanel getDetailed(Object obj) {
      return Generator.makeGroupDisplay(this.core, (Group)obj);
   }
}
