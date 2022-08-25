package ddb.dsz.plugin.taskmanager.details.privilege;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.Generator;
import ddb.dsz.plugin.taskmanager.details.ListWithDetails;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.apache.commons.collections.Closure;

public class PrivilegesDisplay extends ListWithDetails {
   protected CoreController core;

   public PrivilegesDisplay(CoreController core, ProcessInformation procInfo) {
      this.core = core;
      procInfo.getPrivileges(new Closure() {
         public void execute(Object o) {
            List<?> modules = (List)o;
            Iterator i$ = modules.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               if (obj instanceof Privilege) {
                  PrivilegesDisplay.this.model.addElement(Privilege.class.cast(obj));
               }
            }

         }
      });
   }

   protected ListCellRenderer getRenderer() {
      return new PrivilegeRenderer();
   }

   protected JPanel getDetailed(Object obj) {
      return Generator.makePrivilegeDisplay(this.core, (Privilege)obj);
   }
}
