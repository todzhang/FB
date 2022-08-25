package ds.plugin.replay;

import ddb.util.AbstractEnumeratedTableModel;
import ds.core.commanddispatcher.ObserveOngoingOperationCommandDispatcher;
import ds.core.commanddispatcher.ReplayCommandDispatcher;
import ds.core.controller.MutableCoreController;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public class ReplayTableModel extends AbstractEnumeratedTableModel<ReplayTableModelColumns> {
   private static final ReplayTableModel model = new ReplayTableModel();
   private Predicate alreadyLookedAt = PredicateUtils.notPredicate(PredicateUtils.uniquePredicate());
   final List<ReplayTableModel.OperationMetaData> replayOptions = new Vector();
   MutableCoreController mcc = null;
   boolean autoLoad;
   boolean stop = false;

   public static ReplayTableModel getReplayModel() {
      return model;
   }

   private ReplayTableModel() {
      super(ReplayTableModelColumns.class);
   }

   public int getRowCount() {
      return this.replayOptions.size();
   }

   public Object getValueAt(int i, ReplayTableModelColumns e) {
      if (i >= 0 && i <= this.replayOptions.size()) {
         ReplayTableModel.OperationMetaData var3 = (ReplayTableModel.OperationMetaData)this.replayOptions.get(i);
         switch(e) {
         case FILE:
            return var3.file;
         case GUID:
            if (var3.dispatcher == null) {
               return null;
            }

            return var3.dispatcher.getOperation().getGuid();
         case START:
            if (var3.dispatcher == null) {
               return null;
            }

            return var3.dispatcher.getOperation().getStartTime();
         case LOADED:
            return var3.loaded;
         default:
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public String getColumnName(ReplayTableModelColumns e) {
      return e.getName();
   }

   @Override
   public Class<?> getColumnClass(ReplayTableModelColumns e) {
      return e.getClazz();
   }

   @Override
   public boolean isCellEditable(int i, ReplayTableModelColumns e) {
      if (i >= 0 && i <= this.replayOptions.size()) {
         ReplayTableModel.OperationMetaData var3 = (ReplayTableModel.OperationMetaData)this.replayOptions.get(i);
         switch(e) {
         case LOADED:
            return !var3.loaded;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void setValueAt(Object o, int i, ReplayTableModelColumns e) {
      if (i >= 0 && i <= this.replayOptions.size() && ReplayTableModelColumns.LOADED.equals(e)) {
         ReplayTableModel.OperationMetaData var4 = (ReplayTableModel.OperationMetaData)this.replayOptions.get(i);
         if (!var4.loaded) {
            if (o.equals(Boolean.TRUE)) {
               var4.loaded = Boolean.TRUE;
               if (this.mcc != null) {
                  this.mcc.newThread(new ReplayTableModel.OperationHandler(var4.dispatcher)).start();
               } else {
                  Thread var5 = new Thread(new ReplayTableModel.OperationHandler(var4.dispatcher));
                  var5.setPriority(1);
                  var5.setDaemon(true);
                  var5.start();
               }
            }

         }
      }
   }

   public void setCoreController(MutableCoreController var1) {
      this.mcc = var1;
   }

   public void addRecord(File var1) {
      if (var1 != null) {
         boolean var2 = true;
         int var7;
         synchronized(this) {
            if (this.alreadyLookedAt.evaluate(var1)) {
               return;
            }

            ReplayTableModel.OperationMetaData var4 = new ReplayTableModel.OperationMetaData();
            var4.file = var1;
            var4.loaded = false;
            if (this.mcc.isLiveOperation()) {
               var4.dispatcher = new ReplayCommandDispatcher(this.mcc.getDispatcherClient(), this.mcc, var1);
            } else {
               var4.dispatcher = new ObserveOngoingOperationCommandDispatcher(this.mcc.getDispatcherClient(), this.mcc, var1);
            }

            if (var4.dispatcher.getOperation() == null) {
               return;
            }

            if (!this.mcc.getDispatcherClient().addReplayCommandDispatcher(var4.dispatcher)) {
               return;
            }

            var7 = this.replayOptions.size();
            this.replayOptions.add(var7, var4);
         }

         this.fireTableRowsInserted(var7, var7);
         if (this.isAutoLoad()) {
            this.setValueAt(Boolean.TRUE, var7, ReplayTableModelColumns.LOADED.ordinal());
         }

      }
   }

   public boolean isAutoLoad() {
      return this.autoLoad;
   }

   public void setAutoLoad(boolean var1) {
      this.autoLoad = var1;
   }

   public void setStop() {
      this.stop = true;
      synchronized(this.replayOptions) {
         Iterator var2 = this.replayOptions.iterator();

         while(var2.hasNext()) {
            ReplayTableModel.OperationMetaData var3 = (ReplayTableModel.OperationMetaData)var2.next();
            var3.dispatcher.stop();
         }

      }
   }

   private class OperationMetaData {
      public File file;
      public Boolean loaded;
      public ReplayCommandDispatcher dispatcher;

      private OperationMetaData() {
      }

      // $FF: synthetic method
      OperationMetaData(Object var2) {
         this();
      }
   }

   private class OperationHandler implements Runnable {
      ReplayCommandDispatcher dispatcher;
      long lastSize = 0L;

      public OperationHandler(ReplayCommandDispatcher var2) {
         this.dispatcher = var2;
      }

      public void run() {
         try {
            if (this.dispatcher == null) {
               return;
            }

            while(!this.dispatcher.isFinished()) {
               long var1 = this.dispatcher.getFileSize();
               if (var1 > this.lastSize) {
                  try {
                     this.dispatcher.parseFile();
                     this.lastSize = var1;
                  } catch (Throwable var9) {
                  }
               }

               try {
                  TimeUnit.SECONDS.sleep(1L);
               } catch (Exception var8) {
               }
            }
         } finally {
            System.out.println("finished dispatcher");
         }

      }
   }
}
