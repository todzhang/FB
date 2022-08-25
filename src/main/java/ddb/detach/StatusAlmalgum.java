package ddb.detach;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;

public class StatusAlmalgum extends Observable implements TabbableStatus, Observer {
   private static final TabbableStatus DEFAULT_STATUS = new TabbableStatusImpl((Tabbable)null);
   TabbableStatus del;
   ReadWriteLock rwl = new ReentrantReadWriteLock();
   Lock readLock;
   Lock writeLock;

   public StatusAlmalgum(TabbableStatus var1, Tabbable var2) {
      this.readLock = this.rwl.readLock();
      this.writeLock = this.rwl.writeLock();
      this.setDelegate(var1, var2);
   }

   public synchronized void setDelegate(TabbableStatus var1, Tabbable var2) {
      this.writeLock.lock();

      try {
         if (this.del != null) {
            this.del.deleteObserver(this);
         }

         if (var1 == null) {
            this.del = new TabbableStatusImpl((Tabbable)null);
         } else {
            this.del = var1;
         }

         this.del.addObserver(this);
      } finally {
         this.writeLock.unlock();
      }

      this.setChanged();
      this.notifyObservers(var2);
   }

   public void update(Observable var1, Object var2) {
      this.setChanged();
      this.notifyObservers(var2);
   }

   public TabbableStatus.State getDetails() {
      this.readLock.lock();

      TabbableStatus.State var1;
      try {
         if (this.del == null) {
            return DEFAULT_STATUS.getDetails();
         }

         var1 = this.del.getDetails();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public TabbableStatus.State getHost() {
      this.readLock.lock();

      TabbableStatus.State var1;
      try {
         if (this.del == null) {
            return DEFAULT_STATUS.getHost();
         }

         var1 = this.del.getHost();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public boolean isIndeterminate() {
      this.readLock.lock();

      try {
         if (this.del != null) {
            boolean var1 = this.del.isIndeterminate();
            return var1;
         }
      } finally {
         this.readLock.unlock();
      }

      return DEFAULT_STATUS.isIndeterminate();
   }

   public BoundedRangeModel getProgressModel() {
      this.readLock.lock();

      BoundedRangeModel var1;
      try {
         if (this.del == null) {
            return DEFAULT_STATUS.getProgressModel();
         }

         var1 = this.del.getProgressModel();
      } finally {
         this.readLock.unlock();
      }

      return var1;
   }

   public Icon getStatusIcon() {
      this.readLock.lock();

      try {
         if (this.del != null) {
            Icon var1 = this.del.getStatusIcon();
            return var1;
         }
      } finally {
         this.readLock.unlock();
      }

      return DEFAULT_STATUS.getStatusIcon();
   }

   public void fini() {
      this.writeLock.lock();

      try {
         if (this.del != null) {
            this.del.deleteObserver(this);
         }

         this.del = null;
      } finally {
         this.writeLock.unlock();
      }

   }
}
