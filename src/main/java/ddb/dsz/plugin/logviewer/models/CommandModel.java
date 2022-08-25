package ddb.dsz.plugin.logviewer.models;

import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.util.FrequentlyAppendedTableModel;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.table.TableModel;

public class CommandModel extends FrequentlyAppendedTableModel<CommandModelColumns, Task> implements TableModel {
   final List<Operation> operations = new Vector();

   public Task GetTask(int row) {
      return (Task)super.getRecord(row);
   }

   @Override
   public void addOrUpdateRecord(Task dataObject) {
      if (dataObject != null) {
         if (dataObject.getTypedCommand() != null) {
            synchronized(this.operations) {
               if (!this.operations.contains(dataObject.getId().getOperation())) {
                  this.operations.add(dataObject.getId().getOperation());
               }
            }

            super.addOrUpdateRecord(dataObject);
         }
      }
   }

   public CommandModel() {
      super(CommandModelColumns.class, 1L, TimeUnit.SECONDS);
      super.setComparator(Task.TaskComparator);
   }

   @Override
   public boolean isCellEditable(int i, CommandModelColumns e) {
      return false;
   }

   public Object getValueAt(int i, CommandModelColumns e) {
      Task task = (Task)super.getRecord(i);
      if (task == null) {
         return null;
      } else {
         switch(e) {
         case ID:
            return task.getId().getId();
         case OP:
            synchronized(this.operations) {
               return this.operations.indexOf(task.getId().getOperation());
            }
         case COMMAND:
            return task.getCommandName();
         case STATUS:
            return task.getState();
         case FULLCOMMAND:
            return task.getTypedCommand();
         case COMMENT:
            if (task.getTaskLogAccess() != null) {
               return "";
            } else if (task.getDataCount() > 0) {
               return "";
            } else {
               if (task.getTaskingAccess() != null) {
                  return "";
               }

               return "No Log File";
            }
         case CREATED:
            return task.getCreated();
         case TARGET:
            return task.getTargetId();
         case GUID:
            return task.getId().getOperation();
         case DISPLAY:
            return task.getDisplayTransform();
         case STORAGE:
            return task.getStorageTransform();
         case VALID:
            if (task.getTaskingAccess() != null) {
               return Boolean.TRUE;
            }

            return Boolean.FALSE;
         case TASK:
            return task;
         default:
            return null;
         }
      }
   }

   @Override
   public String getColumnName(CommandModelColumns e) {
      return e.getName();
   }

   @Override
   public Class<?> getColumnClass(CommandModelColumns e) {
      return e.getClazz();
   }
}
