package ddb.dsz.plugin.taskmanager.details.handle;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.util.AbstractEnumeratedTableModel;
import java.util.List;
import java.util.Vector;
import org.apache.commons.collections.Closure;

public class HandleInfoModel extends AbstractEnumeratedTableModel<HandleInfoColumns> {
   List<Handle> handles = new Vector();

   public HandleInfoModel(CoreController core, ProcessInformation procInfo) {
      super(HandleInfoColumns.class);
      procInfo.getHandles(new Closure() {
         public void execute(Object o) {
            List<Handle> temp = (List)o;
            HandleInfoModel.this.handles.addAll(temp);
            HandleInfoModel.this.fireTableDataChanged();
         }
      });
   }

   public Object getValueAt(int i, HandleInfoColumns e) {
      if (i >= 0 && i <= this.handles.size()) {
         Handle h = (Handle)this.handles.get(i);
         if (h == null) {
            return null;
         } else {
            switch(e) {
            case ID:
               return h.getId();
            case METADATA:
               return h.getMetaData();
            case TYPE:
               return h.getType();
            default:
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public int getRowCount() {
      return this.handles.size();
   }

   @Override
   public Class<?> getColumnClass(HandleInfoColumns e) {
      return e.getClazz();
   }
}
