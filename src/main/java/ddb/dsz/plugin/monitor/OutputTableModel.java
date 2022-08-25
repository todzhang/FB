package ddb.dsz.plugin.monitor;

import ddb.dsz.core.task.Task;
import ddb.util.FrequentlyAppendedTableModel;
import java.util.List;
import java.util.Vector;

public class OutputTableModel extends FrequentlyAppendedTableModel<OutputTableColumns, MonitoredCommandOutput> {
   public OutputTableModel(int var1) {
      super(OutputTableColumns.class);
      super.setMaximum(var1);
   }

   public Object getValueAt(int i, OutputTableColumns e) {
      MonitoredCommandOutput var3 = (MonitoredCommandOutput)super.getRecord(i);
      if (var3 == null) {
         return null;
      } else {
         switch(e) {
         case COMMAND:
            return var3.getTask();
         case WHEN:
            return var3.getWhen();
         case OUTPUT:
            return var3.getCommandOutput();
         default:
            return null;
         }
      }
   }

   @Override
   public Class<?> getColumnClass(OutputTableColumns e) {
      switch(e) {
      case COMMAND:
         return Task.class;
      case WHEN:
         return Long.class;
      case OUTPUT:
         return String.class;
      default:
         return Object.class;
      }
   }

   @Override
   public String getColumnName(OutputTableColumns e) {
      switch(e) {
      case COMMAND:
         return "Command";
      case WHEN:
         return "Time";
      case OUTPUT:
         return "Output";
      default:
         return "";
      }
   }

   public List<MonitoredCommandOutput> getOutputsFor(Task var1) {
      Vector var2 = new Vector();
      if (var1 == null) {
         return var2;
      } else {
         super.readLock();

         try {
            for(int var3 = 0; var3 < this.getRowCount(); ++var3) {
               MonitoredCommandOutput var4 = (MonitoredCommandOutput)this.getRecord(var3);
               if (var1.equals(var4.getTask())) {
                  var2.add(var4);
               }
            }
         } finally {
            super.readUnlock();
         }

         return var2;
      }
   }
}
