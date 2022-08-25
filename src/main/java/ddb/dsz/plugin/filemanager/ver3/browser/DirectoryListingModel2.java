package ddb.dsz.plugin.filemanager.ver3.browser;

import ddb.detach.MutableTabbableStatus;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.util.AbstractEnumeratedTableModel;
import ddb.util.InsertionOrderedSet;
import ddb.util.UtilityConstants;
import ddb.util.AbstractEnumeratedTableModel.FireTableDataChanged;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsDeleted;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsInserted;
import ddb.util.AbstractEnumeratedTableModel.FireTableRowsUpdated;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.collections.Closure;

public class DirectoryListingModel2 extends AbstractEnumeratedTableModel<DirectoryListingColumns> {
   static ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, UtilityConstants.createThreadFactory("DirectoryListingModel"));
   static final long DELAY = 50L;
   static final TimeUnit UNIT;
   public static final Integer IS_DIR;
   FileObject parentFile = null;
   List<FileObject> files = new ArrayList();
   DirectoryListingColumns currentSort;
   final MutableTabbableStatus status;
   int size;
   private boolean invert;
   final Comparator<FileObject> INVERTABLE_COMPARATOR;
   final Comparator<FileObject> COLUMN_COMPARATOR;
   final Comparator<FileObject> PRIME_COMPARATOR;
   private FileSystemModel model;
   private Long parentId;
   private String path;
   private final ReadWriteLock LOCK;
   private final Lock READ;
   private final Lock WRITE;
   private Collection<Long> updated;
   private boolean parentChanged;
   private int pendingCount;
   private boolean directoryRequested;
   private String details;
   final Set<FileObject> pendingFiles;
   private Runnable considerChangedParent;
   private Runnable considerUpdatedFiles;

   public DirectoryListingModel2(FileSystemModel var1, MutableTabbableStatus var2) {
      super(DirectoryListingColumns.class);
      this.currentSort = DirectoryListingColumns.Name;
      this.size = 0;
      this.invert = false;
      this.INVERTABLE_COMPARATOR = new Comparator<FileObject>() {
         public int compare(FileObject var1, FileObject var2) {
            int var3 = DirectoryListingModel2.this.COLUMN_COMPARATOR.compare(var1, var2);
            return DirectoryListingModel2.this.invert ? -var3 : var3;
         }
      };
      this.COLUMN_COMPARATOR = new Comparator<FileObject>() {
         public int compare(FileObject var1, FileObject var2) {
            if (var1 != null && var2 != null) {
               if (var1.isDirectory() && !var2.isDirectory()) {
                  return -1;
               }

               if (!var1.isDirectory() && var2.isDirectory()) {
                  return 1;
               }
            }

            return DirectoryListingModel2.this.currentSort.getComparator().compare(var1, var2);
         }
      };
      this.PRIME_COMPARATOR = this.INVERTABLE_COMPARATOR;
      this.parentId = FileSystemModel.ROOT;
      this.path = null;
      this.LOCK = new ReentrantReadWriteLock();
      this.READ = this.LOCK.readLock();
      this.WRITE = this.LOCK.writeLock();
      this.updated = new InsertionOrderedSet(HashSet.class, LinkedList.class);
      this.parentChanged = false;
      this.pendingCount = 0;
      this.directoryRequested = false;
      this.details = "";
      this.pendingFiles = new HashSet();
      this.considerChangedParent = new Runnable() {
         public void run() {
            DirectoryListingModel2.this.READ.lock();

            label123: {
               try {
                  if (DirectoryListingModel2.this.parentChanged) {
                     break label123;
                  }
               } finally {
                  DirectoryListingModel2.this.READ.unlock();
               }

               return;
            }

            DirectoryListingModel2.this.WRITE.lock();

            try {
               Iterator var1 = DirectoryListingModel2.this.files.iterator();

               while(true) {
                  FileObject var2;
                  if (!var1.hasNext()) {
                     var1 = DirectoryListingModel2.this.pendingFiles.iterator();

                     while(var1.hasNext()) {
                        var2 = (FileObject)var1.next();
                        var2.discard();
                     }

                     DirectoryListingModel2.this.pendingFiles.clear();
                     DirectoryListingModel2.this.files.clear();
                     DirectoryListingModel2.this.updated.clear();
                     DirectoryListingModel2.this.size = 0;
                     DirectoryListingModel2.this.parentChanged = false;
                     DirectoryListingModel2.this.directoryRequested = true;
                     break;
                  }

                  var2 = (FileObject)var1.next();
                  var2.discard();
               }
            } finally {
               DirectoryListingModel2.this.WRITE.unlock();
            }

            DirectoryListingModel2.this.setBusyMode();
            EventQueue.invokeLater(new FireTableDataChanged());
            DirectoryListingModel2.this.model.getChildren(DirectoryListingModel2.this.parentId, DirectoryListingModel2.this.path, DirectoryListingModel2.this.new GotChildren(DirectoryListingModel2.this.parentId));
         }
      };
      this.considerUpdatedFiles = new Runnable() {
         public void run() {
            DirectoryListingModel2.this.considerChangedParent.run();
            boolean var1 = false;
            DirectoryListingModel2.this.READ.lock();

            try {
               if (!DirectoryListingModel2.this.updated.isEmpty()) {
                  var1 = true;
               }
            } finally {
               DirectoryListingModel2.this.READ.unlock();
            }

            DirectoryListingModel2.this.setBusyMode();
            if (var1) {
               DirectoryListingModel2.this.WRITE.lock();

               try {
                  Collection var2 = DirectoryListingModel2.this.updated;
                  DirectoryListingModel2.this.updated = new Vector();
                  Iterator var3 = var2.iterator();

                  while(var3.hasNext()) {
                     Long var4 = (Long)var3.next();
                     final FileObject var5 = new FileObject(DirectoryListingModel2.this.model, var4);
                     DirectoryListingModel2.this.pendingFiles.add(var5);
                     DirectoryListingModel2.this.model.retrieveFileInformation(var5, new Runnable() {
                        public void run() {
                           DirectoryListingModel2.this.addFile(var5);
                        }
                     });
                  }
               } finally {
                  DirectoryListingModel2.this.WRITE.unlock();
               }

               DirectoryListingModel2.this.setBusyMode();
            }
         }
      };
      this.model = var1;
      this.status = var2;
      this.parentChanged = true;
      exec.scheduleWithFixedDelay(this.considerChangedParent, 1000L, 50L, TimeUnit.MILLISECONDS);
      exec.scheduleWithFixedDelay(this.considerUpdatedFiles, 1L, 1L, TimeUnit.SECONDS);
   }

   public void setSorting(DirectoryListingColumns var1, boolean var2) {
      this.WRITE.lock();

      try {
         this.currentSort = var1;
         this.invert = var2;
         Collections.sort(this.files, this.PRIME_COMPARATOR);
      } finally {
         this.WRITE.unlock();
      }

      EventQueue.invokeLater(new FireTableDataChanged());
   }

   public int getRowCount() {
      return this.size;
   }

   public Object getFileObjectAt(int var1) {
      this.READ.lock();

      Object var2;
      try {
         if (var1 >= 0 && var1 <= this.files.size()) {
            var2 = this.files.get(var1);
            return var2;
         }

         var2 = null;
      } finally {
         this.READ.unlock();
      }

      return var2;
   }

   public Object getValueAt(int i, DirectoryListingColumns e) {
      if (i >= 0 && i <= this.size) {
         FileObject var4 = null;
         this.READ.lock();

         label86: {
            Object var5;
            try {
               List var3 = this.files;
               if (i < var3.size()) {
                  var4 = (FileObject)var3.get(i);
                  break label86;
               }

               var5 = null;
            } finally {
               this.READ.unlock();
            }

            return var5;
         }

         try {
            switch(e) {
            case Name:
               return var4;
            case Size:
               if (var4.isDrive()) {
                  return "<DRIVE>";
               } else if (var4.isDirectory()) {
                  return "<DIR>";
               }
            default:
               return var4.getDataElement(e.getField()).getValue();
            }
         } catch (Exception var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   public void setModel(FileSystemModel var1) {
      this.WRITE.lock();

      label39: {
         try {
            if (this.model != var1) {
               this.model = var1;
               this.parentChanged = true;
               this.parentId = FileSystemModel.ROOT;
               this.path = null;
               this.details = "";
               break label39;
            }
         } finally {
            this.WRITE.unlock();
         }

         return;
      }

      this.fireTableDataChanged();
   }

   public void addFile(FileObject var1) {
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      this.WRITE.lock();

      label104: {
         try {
            this.pendingFiles.remove(var1);
            if (var1.getParent().intValue() == this.parentId.intValue()) {
               var1.setPath(this.path);

               int var5;
               for(var5 = 0; var5 < this.files.size(); ++var5) {
                  if (((FileObject)this.files.get(var5)).getId().equals(var1.getId())) {
                     this.files.remove(var5);
                     var4 = var5;
                     --this.size;
                     break;
                  }
               }

               if (var3 == -1) {
                  var5 = Collections.binarySearch(this.files, var1, this.PRIME_COMPARATOR);
                  if (var5 >= 0) {
                     var3 = var5;
                     this.files.set(var5, var1);
                  } else {
                     int var6 = -var5 - 1;
                     this.files.add(var6, var1);
                     var2 = var6;
                     ++this.size;
                  }
               }
               break label104;
            }
         } finally {
            this.WRITE.unlock();
         }

         return;
      }

      if (var4 >= 0) {
         EventQueue.invokeLater(new FireTableRowsDeleted( var4, var4));
      }

      if (var2 >= 0) {
         EventQueue.invokeLater(new FireTableRowsInserted(var2, var2));
      }

      if (var3 >= 0) {
         EventQueue.invokeLater(new FireTableRowsUpdated( var3, var3));
      }

   }

   @Override
   public Class<?> getColumnClass(DirectoryListingColumns e) {
      switch(e) {
      case Name:
         return FileObject.class;
      case Size:
         return Integer.class;
      case Accessed:
      case Modified:
      case Created:
         return Calendar.class;
      default:
         return Object.class;
      }
   }

   public void setParent(FileObject var1) {
      this.setParent(var1.getId(), var1.getPath(), var1);
   }

   public void setParent(long var1, String var3, FileObject var4) {
      this.WRITE.lock();

      try {
         if (this.parentId.equals(var1)) {
            return;
         }

         this.parentId = var1;
         this.parentFile = var4;
         if (this.parentFile == null) {
            this.parentFile = new FileObject(this.model, var1);
            if (var3 != null) {
               int var5 = var3.lastIndexOf(47);
               if (var5 == var3.length() - 1) {
                  var3 = var3.substring(0, var3.length() - 1);
                  var5 = var3.lastIndexOf(47);
               }

               var3 = var3 + "/";
               if (var5 != -1) {
                  this.parentFile.setPath(var3.substring(0, var5));
                  this.parentFile.setDataElement(FileObjectFields.File_Name, var3.substring(var5 + 1));
                  this.parentFile.forceDirectory();
               } else {
                  this.parentFile.setDataElement(FileObjectFields.File_Name, var3.substring(var5 + 1));
                  this.parentFile.forceDirectory();
               }

               this.model.retrieveFileInformation(this.parentFile, (Runnable)null);
            }
         }

         this.path = var3;
         this.parentChanged = true;
         this.details = "";
      } finally {
         this.WRITE.unlock();
      }

      this.setBusyMode();
   }

   public FileObject getParentFile() {
      return this.parentFile;
   }

   public void fileChanged(FileObject var1) {
      long var2 = var1.getParent();
      this.READ.lock();

      try {
         if (var2 != this.parentId) {
            return;
         }
      } finally {
         this.READ.unlock();
      }

      this.WRITE.lock();

      try {
         long var4 = var1.getId();
         this.updated.add(var4);
      } finally {
         this.WRITE.unlock();
      }

      this.setBusyMode();
   }

   void setBusyMode() {
      boolean var1 = false;
      this.READ.lock();

      try {
         if (this.pendingCount > 0 || this.updated.size() > 0 || this.directoryRequested) {
            var1 = true;
         }

         if (this.status.isIndeterminate() == var1) {
            return;
         }
      } finally {
         this.READ.unlock();
      }

      this.status.setIndeterminate(var1);
      this.status.notifyObservers();
   }

   public String getDetails() {
      return this.details;
   }

   static {
      UNIT = TimeUnit.MILLISECONDS;
      IS_DIR = -1;
   }

   private class GotChildren implements Closure {
      Long parent;

      public GotChildren(Long var2) {
         this.parent = var2;
      }

      public void execute(Object var1) {
         Collection var2 = (Collection)var1;
         DirectoryListingModel2.this.WRITE.lock();

         try {
            if (DirectoryListingModel2.this.parentId == this.parent) {
               DirectoryListingModel2.this.updated.addAll(var2);
               DirectoryListingModel2.this.directoryRequested = false;
               return;
            }
         } finally {
            DirectoryListingModel2.this.WRITE.unlock();
            DirectoryListingModel2.this.setBusyMode();
         }

      }
   }
}
