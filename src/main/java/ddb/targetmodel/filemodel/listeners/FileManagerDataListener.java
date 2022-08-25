package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.task.Task;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileSystemModel;
import org.apache.commons.collections.Closure;

public abstract class FileManagerDataListener implements Closure {
   protected static final Closure MAKE_DIRECTORY = new Closure() {
      public void execute(Object var1) {
         if (var1 instanceof FileObject) {
            ((FileObject)FileObject.class.cast(var1)).setDirectory();
         }

      }
   };
   protected final CoreController core;
   protected final FileSystemModel model;
   static final Object COUNT_LOCK = new Object();
   static int AvailableCount = 30;

   protected FileManagerDataListener(CoreController var1, FileSystemModel var2) {
      this.core = var1;
      this.model = var2;
   }

   public final void execute(Object var1) {
      if (var1 instanceof DataEvent) {
         DataEvent var2 = (DataEvent)DataEvent.class.cast(var1);
         Task var3 = this.core.getTaskById(var2.getTaskId());
         if (var3 != null) {
            this.handleData(var2);
         }
      }
   }

   protected abstract void handleData(DataEvent var1);

   protected abstract class DataClosure implements Closure {
      boolean limited;
      protected final FileSystemModel model;

      protected DataClosure(boolean var2, FileSystemModel var3) {
         this.limited = var2;
         this.model = var3;
         if (var2) {
            synchronized(FileManagerDataListener.COUNT_LOCK) {
               while(FileManagerDataListener.AvailableCount <= 0) {
                  try {
                     FileManagerDataListener.COUNT_LOCK.wait();
                  } catch (InterruptedException var7) {
                  }
               }

               --FileManagerDataListener.AvailableCount;
            }
         }

      }

      public final void execute(Object var1) {
         boolean var10 = false;

         try {
            var10 = true;
            this.executeChild(var1);
            var10 = false;
         } finally {
            if (var10) {
               if (this.limited) {
                  synchronized(FileManagerDataListener.COUNT_LOCK) {
                     ++FileManagerDataListener.AvailableCount;
                     if (FileManagerDataListener.AvailableCount > 0) {
                        FileManagerDataListener.COUNT_LOCK.notifyAll();
                     }
                  }
               }

            }
         }

         if (this.limited) {
            synchronized(FileManagerDataListener.COUNT_LOCK) {
               ++FileManagerDataListener.AvailableCount;
               if (FileManagerDataListener.AvailableCount > 0) {
                  FileManagerDataListener.COUNT_LOCK.notifyAll();
               }
            }
         }

      }

      protected abstract void executeChild(Object var1);
   }
}
