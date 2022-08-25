package ddb.dsz.plugin.logviewer.gui.screenlog;

import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.AbstractEnumeratedTableModel.FireTableCellUpdated;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsInserted;
import java.awt.EventQueue;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenLogModel extends AbstractEnumeratedTableModel<ScreenLogColumns> {
   List<ScreenLogModel.Record> data = new Vector();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock READ;
   private final Lock WRITE;
   final Pattern timestampPattern;

   public ScreenLogModel() {
      super(ScreenLogColumns.class);
      this.READ = this.lock.readLock();
      this.WRITE = this.lock.writeLock();
      this.timestampPattern = Pattern.compile(".*_([0-9]{4})_([0-9]{2})_([0-9]{2})_([0-9]{2})h([0-9]{2})m([0-9]{2})s.([0-9]{3}).dsz");
   }

   @Override
   public Class<?> getColumnClass(ScreenLogColumns e) {
      return e.getType();
   }

   @Override
   public String getColumnName(ScreenLogColumns e) {
      return e.getName();
   }

   public Object getValueAt(int i, ScreenLogColumns e) {
      if (i < 0) {
         return null;
      } else {
         this.READ.lock();

         try {
            ScreenLogModel.Record r;
            if (i >= this.data.size()) {
               r = null;
               return r;
            } else {
               r = (ScreenLogModel.Record)this.data.get(i);
               Long var4;
               if (r == null) {
                  var4 = null;
                  return var4;
               } else {
                  switch(e) {
                  case FILENAME:
                     File var9 = r.file;
                     return var9;
                  case TIMESTAMP:
                     Calendar var8 = this.getTimestamp(r.file);
                     return var8;
                  case SIZE:
                     var4 = r.lastSize;
                     return var4;
                  default:
                     var4 = null;
                     return var4;
                  }
               }
            }
         } finally {
            this.READ.unlock();
         }
      }
   }

   private Calendar getTimestamp(File file) {
      Matcher m = this.timestampPattern.matcher(file.getName());

      try {
         if (m.matches()) {
            Calendar cal = Calendar.getInstance();
            cal.set(1, Integer.parseInt(m.group(1)));
            cal.set(2, Integer.parseInt(m.group(2)) - 1);
            cal.set(5, Integer.parseInt(m.group(3)));
            cal.set(11, Integer.parseInt(m.group(4)));
            cal.set(12, Integer.parseInt(m.group(5)));
            cal.set(13, Integer.parseInt(m.group(6)));
            cal.set(14, Integer.parseInt(m.group(7)));
            return cal;
         }
      } catch (NumberFormatException var4) {
      }

      return null;
   }

   public int getRowCount() {
      this.READ.lock();

      int var1;
      try {
         var1 = this.data.size();
      } finally {
         this.READ.unlock();
      }

      return var1;
   }

   public void updateFile(File f) {
      if (f.length() != 0L) {
         int update = -1;
         int add = -1;
         this.WRITE.lock();

         try {
            for(int i = 0; i < this.data.size(); ++i) {
               ScreenLogModel.Record r = (ScreenLogModel.Record)this.data.get(i);
               if (r.file.equals(f)) {
                  update = i;
                  r.lastSize = f.length();
               }
            }

            if (update == -1) {
               ScreenLogModel.Record newRecord = new ScreenLogModel.Record();
               newRecord.file = f;
               newRecord.lastSize = f.length();

               for(int i = 0; i < this.data.size(); ++i) {
                  ScreenLogModel.Record oldRecord = (ScreenLogModel.Record)this.data.get(i);
                  if (oldRecord.file.compareTo(newRecord.file) > 0) {
                     add = i;
                     this.data.add(i, newRecord);
                     break;
                  }
               }

               if (add == -1) {
                  add = this.data.size();
                  this.data.add(newRecord);
               }
            }
         } finally {
            this.WRITE.unlock();
         }

         if (update != -1) {
            EventQueue.invokeLater(new FireTableCellUpdated( update, ScreenLogColumns.SIZE));
         } else if (add != -1) {
            EventQueue.invokeLater(new FireTableRowsInserted( add, add));
         }

      }
   }

   private class Record {
      File file;
      long lastSize;

      private Record() {
      }

      // $FF: synthetic method
      Record(Object x1) {
         this();
      }
   }
}
