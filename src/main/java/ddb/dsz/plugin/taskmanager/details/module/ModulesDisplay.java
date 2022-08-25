package ddb.dsz.plugin.taskmanager.details.module;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.Generator;
import ddb.dsz.plugin.taskmanager.details.ListWithDetails;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.apache.commons.collections.Closure;

public class ModulesDisplay extends ListWithDetails {
   protected CoreController core;

   public ModulesDisplay(CoreController core, ProcessInformation procInfo) {
      this.core = core;
      procInfo.getModules(new Closure() {
         public void execute(Object o) {
            List<?> modules = (List)o;
            Iterator i$ = modules.iterator();

            while(i$.hasNext()) {
               Object obj = i$.next();
               if (obj instanceof Module) {
                  ModulesDisplay.this.model.addElement(Module.class.cast(obj));
               }
            }

         }
      });
   }

   protected ListCellRenderer getRenderer() {
      return new ModuleRenderer();
   }

   protected JPanel getDetailed(Object obj) {
      return Generator.makeModuleDisplay(this.core, (Module)obj);
   }
}
