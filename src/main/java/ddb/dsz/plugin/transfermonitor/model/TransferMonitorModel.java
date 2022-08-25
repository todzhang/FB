package ddb.dsz.plugin.transfermonitor.model;

import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.transfermonitor.TransferMonitorColumns;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsInserted;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsUpdated;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.table.TableModel;

public class TransferMonitorModel extends AbstractEnumeratedTableModel<TransferMonitorColumns> implements TableModel {
   List<TransferRecord> recordList;
   final ReadWriteLock LOCK = new ReentrantReadWriteLock();
   final Lock WRITE;
   final Lock READ;

   public TransferMonitorModel() {
      super(TransferMonitorColumns.class);
      this.WRITE = this.LOCK.writeLock();
      this.READ = this.LOCK.readLock();
      this.recordList = new Vector();
   }

   @Override
   public String getColumnName(TransferMonitorColumns e) {
      return e.getName();
   }

   public int getRowCount() {
      this.READ.lock();

      int var1;
      try {
         var1 = this.recordList.size();
      } finally {
         this.READ.unlock();
      }

      return var1;
   }

   @Override
   public Class<?> getColumnClass(TransferMonitorColumns e) {
      return e.getClazz();
   }

   public Object getValueAt(int i, TransferMonitorColumns e) {
      TransferRecord var3 = null;
      this.READ.lock();

      try {
         if (i >= 0 && i < this.recordList.size()) {
            var3 = (TransferRecord)this.recordList.get(i);
         }
      } finally {
         this.READ.unlock();
      }

      if (var3 == null) {
         return null;
      } else {
         switch(e) {
         case ID:
            return new Integer(var3.getId());
         case STATE:
            return var3.getState();
         case REMOTE:
            return var3.getRemote();
         case LOCAL:
            return var3.getLocal();
         case SIZE:
            return var3;
         case TYPE:
            return var3.getDescription();
         case TIME_ACCESSED:
            return var3.getAccessed();
         case TIME_CREATED:
            return var3.getCreated();
         case TIME_MODIFIED:
            return var3.getModified();
         default:
            return null;
         }
      }
   }

   public void addRecord(TransferRecord var1) {
      boolean var2 = true;
      this.WRITE.lock();

      int var6;
      try {
         this.recordList.add(var1);
         var6 = this.recordList.size() - 1;
      } finally {
         this.WRITE.unlock();
      }

      EventQueue.invokeLater(new FireTableRowsInserted( var6, var6));
   }

   public TransferRecord getRecord(int var1) {
      this.READ.lock();

      TransferRecord var2;
      try {
         if (var1 >= 0 && var1 < this.recordList.size()) {
            var2 = (TransferRecord)this.recordList.get(var1);
            return var2;
         }

         var2 = null;
      } finally {
         this.READ.unlock();
      }

      return var2;
   }

   public void recordChanged(TransferRecord var1) {
      boolean var2 = true;
      this.READ.lock();

      int var6;
      try {
         var6 = this.recordList.indexOf(var1);
      } finally {
         this.READ.unlock();
      }

      if (var6 != -1) {
         EventQueue.invokeLater(new FireTableRowsUpdated( var6, var6));
      }

   }

   public int getNext() {
      this.READ.lock();

      int var1;
      try {
         var1 = this.recordList.size();
      } finally {
         this.READ.unlock();
      }

      return var1;
   }

   public void commandEnded(Task var1) {
      this.READ.lock();

      try {
         Iterator var2 = this.recordList.iterator();

         while(var2.hasNext()) {
            TransferRecord var3 = (TransferRecord)var2.next();
            if (var3.getTaskId().equals(var1.getId())) {
               var3.setState(TransferState.DONE);
            }
         }
      } finally {
         this.READ.unlock();
      }

   }
}
