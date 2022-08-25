package ddb.dsz.plugin.taskmanager.models;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import ddb.dsz.plugin.taskmanager.enumerated.HandlesStatus;
import ddb.dsz.plugin.taskmanager.enumerated.ProcessInfoStatus;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.util.AbstractEnumeratedTableModel;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProcessTableModel extends AbstractEnumeratedTableModel<ProcessTableColumns> {
   List<ProcessInformation> processes = new Vector(40);
   int updateNumber = 0;
   CoreController core;
   ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   Lock read;
   Lock write;

   public ProcessTableModel(CoreController cc) {
      super(ProcessTableColumns.class);
      this.read = this.lock.readLock();
      this.write = this.lock.writeLock();
      this.core = cc;
   }

   @Override
   public Class<?> getColumnClass(ProcessTableColumns e) {
      return e.getClazz();
   }

   @Override
   public String getColumnName(ProcessTableColumns e) {
      return e.getColumnName();
   }

   public int getRowCount() {
      this.read.lock();

      int var1;
      try {
         var1 = this.processes.size();
      } finally {
         this.read.unlock();
      }

      return var1;
   }

   @Override
   public int getColumnCount() {
      return ProcessTableColumns.values().length;
   }

   public ProcessInformation getProcessAtRow(int row) {
      this.read.lock();

      ProcessInformation var2;
      try {
         if (row < 0 || row >= this.processes.size()) {
            var2 = null;
            return var2;
         }

         var2 = (ProcessInformation)this.processes.get(row);
      } finally {
         this.read.unlock();
      }

      return var2;
   }

   public Object getValueAt(int i, ProcessTableColumns e) {
      ProcessInformation p = null;
      this.read.lock();

      try {
         if (i >= 0 && i < this.processes.size()) {
            p = (ProcessInformation)this.processes.get(i);
         }
      } finally {
         this.read.unlock();
      }

      if (p == null) {
         return null;
      } else {
         switch(e) {
         case PROCESSID:
            return p.getId();
         case PROCESSNAME:
            return p.getProcName();
         case PROCESSPATH:
            return p.getProcPath();
         case USERNAME:
            return p.getUserName();
         case CPUTIME:
            int time = p.getCpuTime().intValue();
            int seconds = time % 60;
            time /= 60;
            int minutes = time % 60;
            time /= 60;
            int hours = time % 24;
            time /= 24;
            if (time > 0) {
               return String.format("%d.%02d:%02d:%02d", time, hours, minutes, seconds);
            }

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
         case ARCH_TYPE:
            if (p.is64Bit()) {
               return "64-Bit";
            }

            return "";
         case CREATIONTIME:
            if (p.getCreateTime() == null) {
               return "--";
            }

            return String.format("%04d-%02d-%02d %02d:%02d:%02d", p.getCreateTime().get(1), p.getCreateTime().get(3), p.getCreateTime().get(5), p.getCreateTime().get(11), p.getCreateTime().get(12), p.getCreateTime().get(13));
         case DISPLAY:
            return p.getDisplay();
         case PARENTID:
            return p.getParent();
         case HIGHLIGHT:
            return p.isHighlight();
         case TYPE:
            return p.getType();
         case PROCESS:
            return p;
         case EXPLANATION:
            return p.getComment();
         case HANDLEINFO:
            return p.hasHandleInfo() ? HandlesStatus.HasHandles : HandlesStatus.NoHandles;
         case PROCESSINFO:
            return p.hasProcessInfo() ? ProcessInfoStatus.HasProcessInfo : ProcessInfoStatus.NoProcessInfo;
         default:
            return "oops";
         }
      }
   }

   public void addProcess(final ProcessInformation p) {
      int index = 0;
      boolean inserted = false;
      this.write.lock();

//      int index;
      try {
         if (p.getId() == null) {
            System.out.println("Error!");
         }

         index = this.processes.indexOf(p);
         if (index != -1) {
            ((ProcessInformation)this.processes.get(index)).setAs(p);
         } else {
            p.addObserver(new Observer() {
               public void update(Observable o, Object arg) {
                  ProcessTableModel.this.read.lock();

                  int newIndex;
                  label33: {
                     try {
                        newIndex = ProcessTableModel.this.processes.indexOf(p);
                        if (newIndex != -1) {
                           break label33;
                        }
                     } finally {
                        ProcessTableModel.this.read.unlock();
                     }

                     return;
                  }

                  ProcessTableModel.this.fireTableRowsUpdated(newIndex, newIndex);
               }
            });
            index = this.processes.size();
            this.processes.add(p);
            inserted = true;
         }
      } finally {
         this.write.unlock();
      }

      if (inserted) {
         this.fireTableRowsInserted(index, index);
      } else {
         this.fireTableRowsUpdated(index, index);
      }

   }

   public void removeProcess(ProcessInformation p) {
      int index = 0;
      this.write.lock();

//      int index;
      try {
         index = this.processes.indexOf(p);
         if (index == -1) {
            return;
         }

         this.processes.remove(index);
      } finally {
         this.write.unlock();
      }

      this.fireTableRowsDeleted(index, index);
   }

   public void updateProcess(ProcessInformation p) {
      int index = 0;
      this.write.lock();

//      int index;
      try {
         index = this.processes.indexOf(p);
         if (index == -1) {
            return;
         }
      } finally {
         this.write.unlock();
      }

      this.fireTableRowsUpdated(index, index);
   }

   public void purge() {
      this.write.lock();

      int size;
      try {
         size = this.processes.size() - 1;
         this.processes.clear();
      } finally {
         this.write.unlock();
      }

      if (size >= 0) {
         this.fireTableRowsDeleted(0, size);
      }

   }

   public ProcessInformation getProcessById(Long integerValue) {
      if (integerValue == null) {
         return null;
      } else {
         this.read.lock();

         ProcessInformation var4;
         try {
            Iterator i$ = this.processes.iterator();

            ProcessInformation p;
            do {
               if (!i$.hasNext()) {
                  i$ = null;
                  return null;
               }

               p = (ProcessInformation)i$.next();
            } while(!p.getId().equals(integerValue));

            var4 = p;
         } finally {
            this.read.unlock();
         }

         return var4;
      }
   }

   public FileStatus getType(int row) {
      this.read.lock();

      FileStatus var2;
      try {
         var2 = ((ProcessInformation)this.processes.get(row)).getType();
      } finally {
         this.read.unlock();
      }

      return var2;
   }

   public List<ProcessInformation> getProcessesWithModule(String file) {
      List<ProcessInformation> retVal = new Vector();
      this.read.lock();

      try {
         Iterator i$ = this.processes.iterator();

         while(i$.hasNext()) {
            ProcessInformation p = (ProcessInformation)i$.next();
            if (p.hasModuleNamed(file)) {
               retVal.add(p);
            }
         }
      } finally {
         this.read.unlock();
      }

      return retVal;
   }

   public List<ProcessInformation> getAllProcesses() {
      List<ProcessInformation> retVal = new Vector();
      this.read.lock();

      try {
         retVal.addAll(this.processes);
      } finally {
         this.read.unlock();
      }

      return retVal;
   }
}
