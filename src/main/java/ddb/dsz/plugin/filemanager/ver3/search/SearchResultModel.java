package ddb.dsz.plugin.filemanager.ver3.search;

import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.AbstractEnumeratedTableModel.FireTableDataChanged;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsInserted;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsUpdated;
import java.awt.EventQueue;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SearchResultModel extends AbstractEnumeratedTableModel<SearchResultColumns> {
   static final long DELAY = 1500L;
   static final TimeUnit UNIT;
   public static final Integer IS_DIR;
   List<Long> ids = new ArrayList();
   List<Reference<FileObject>> files = new ArrayList();
   int size = 0;
   FileSystemModel model;
   final ReadWriteLock LOCK = new ReentrantReadWriteLock();
   final Lock READ;
   final Lock WRITE;

   public SearchResultModel(FileSystemModel var1) {
      super(SearchResultColumns.class);
      this.READ = this.LOCK.readLock();
      this.WRITE = this.LOCK.writeLock();
      this.model = var1;
   }

   public int getRowCount() {
      this.READ.lock();

      int var1;
      try {
         var1 = this.size;
      } finally {
         this.READ.unlock();
      }

      return var1;
   }

   public Object getFileObjectAt(int var1) {
      this.READ.lock();

      Object var8;
      try {
         Reference var2 = (Reference)this.files.get(var1);
         if (var2 != null) {
            FileObject var3 = (FileObject)var2.get();
            if (var3 != null) {
               FileObject var4 = var3;
               return var4;
            }
         }

         var8 = this.ids.get(var1);
      } finally {
         this.READ.unlock();
      }

      return var8;
   }

   public Object getValueAt(final int i, SearchResultColumns e) {
      if (i < 0) {
         return null;
      } else {
         long var5 = -2L;
         FileObject var7 = null;
         this.READ.lock();

         List var4;
         try {
            Reference var8;
            if (i >= this.size) {
               var8 = null;
               return var8;
            }

            List var3 = this.ids;
            var4 = this.files;
            var5 = (Long)var3.get(i);
            var8 = (Reference)var4.get(i);
            if (var8 != null) {
               var7 = (FileObject)var8.get();
            }
         } finally {
            this.READ.unlock();
         }

         if (var7 == null) {
            var7 = new FileObject(this.model, var5);
            this.WRITE.lock();

            try {
               var4.set(i, new SoftReference(var7));
            } finally {
               this.WRITE.unlock();
            }

            this.model.retrieveFileInformation(var7, new Runnable() {
               public void run() {
                  SearchResultModel.this.READ.lock();

                  label33: {
                     try {
                        if (i < SearchResultModel.this.size) {
                           break label33;
                        }
                     } finally {
                        SearchResultModel.this.READ.unlock();
                     }

                     return;
                  }

                  EventQueue.invokeLater(new FireTableRowsUpdated(i, i));
               }
            });
            this.model.getPath(var7);
            return null;
         } else {
            try {
               switch(e) {
               case Accessed:
                  return var7.getDataElement(FileObjectFields.File_Accessed).getValue();
               case Created:
                  return var7.getDataElement(FileObjectFields.File_Created).getValue();
               case Modified:
                  return var7.getDataElement(FileObjectFields.File_Modified).getValue();
               case Name:
                  return var7;
               case Size:
                  if (var7.isDirectory()) {
                     return IS_DIR;
                  }

                  return var7.getDataElement(FileObjectFields.File_Size).getValue();
               case Path:
                  return var7.getPath();
               }
            } catch (Exception var18) {
            }

            return null;
         }
      }
   }

   @Override
   public Class<?> getColumnClass(SearchResultColumns e) {
      switch(e) {
      case Accessed:
      case Created:
      case Modified:
         return Calendar.class;
      case Name:
         return FileObject.class;
      case Size:
         return Integer.class;
      default:
         return Object.class;
      }
   }

   public void fileChanged(FileObject var1) {
      int var2 = -1;
      this.READ.lock();

      try {
         for(int var3 = 0; var3 < this.size; ++var3) {
            if (var1.getId().equals(this.ids.get(var3))) {
               this.files.set(var3, (Reference<FileObject>) null);
               var2 = var3;
               break;
            }
         }
      } finally {
         this.READ.unlock();
      }

      if (var2 != -1) {
         EventQueue.invokeLater(new FireTableRowsUpdated(var2, var2));
      }

   }

   public void addFile(FileObject var1) {
      boolean var2 = true;
      this.WRITE.lock();

      int var6;
      try {
         var6 = this.size;
         this.ids.add(var1.getId());
         this.files.add(new SoftReference(var1));
         ++this.size;
      } finally {
         this.WRITE.unlock();
      }

      if (var6 >= 0) {
         EventQueue.invokeLater(new FireTableRowsInserted( var6, var6));
      }

      this.model.getPath(var1);
   }

   public void clear() {
      this.WRITE.lock();

      try {
         this.ids = new ArrayList();
         this.files = new ArrayList();
         this.size = 0;
      } finally {
         this.WRITE.unlock();
      }

      EventQueue.invokeLater(new FireTableDataChanged());
   }

   static {
      UNIT = TimeUnit.MILLISECONDS;
      IS_DIR = -1;
   }
}
