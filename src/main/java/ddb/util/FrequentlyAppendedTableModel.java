package ddb.util;

import java.awt.EventQueue;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

public abstract class FrequentlyAppendedTableModel<Columns extends Enum<?>, DataObject> extends AbstractEnumeratedTableModel<Columns> {
   private final ScheduledExecutorService exec;
   private static final int MAXIMUM = Integer.MAX_VALUE;
   private List<DataObject> data;
   private List<DataObject> pendingAdds;
   private List<DataObject> pendingDeletes;
   private int maximum;
   private long wait;
   private TimeUnit units;
   private FrequentlyAppendedTableModel.RecordState state;
   private boolean stop;
   private Comparator<DataObject> Compare;
   private boolean bSort;
   private final ReadWriteLock dataLock;
   private final Object PENDING_LOCK;
   Runnable update;

   public void setComparator(Comparator<DataObject> var1) {
      this.writeLock();

      try {
         this.Compare = var1;
         if (this.Compare != null) {
            this.bSort = true;
         }
      } finally {
         this.writeUnlock();
      }

      if (var1 != null) {
         synchronized(this.PENDING_LOCK) {
            Collections.sort(this.pendingAdds, var1);
         }
      }

   }

   private int findIndex(List<DataObject> var1, DataObject var2, Comparator<DataObject> var3) {
      if (var3 != null) {
         return Collections.binarySearch(var1, var2, var3);
      } /*else if (var2 instanceof Comparable) {
         return Collections.binarySearch(var1, var2);
      } */else {
         int var4 = var1.indexOf(var2);
         return var4 < 0 ? -var1.size() - 1 : var4;
      }
   }

   public void readLock() {
      this.dataLock.readLock().lock();
   }

   public void readUnlock() {
      this.dataLock.readLock().unlock();
   }

   public void writeLock() {
      this.dataLock.writeLock().lock();
   }

   public void writeUnlock() {
      this.dataLock.writeLock().unlock();
   }

   public FrequentlyAppendedTableModel(Class<Columns> var1) {
      this(var1, 250L, TimeUnit.MILLISECONDS);
   }

   public FrequentlyAppendedTableModel(Class<Columns> var1, long var2, TimeUnit var4) {
      this(var1, var2, var4, -1);
   }

   public FrequentlyAppendedTableModel(Class<Columns> var1, long var2, TimeUnit var4, int var5) {
      super(var1);
      this.exec = UtilityConstants.createScheduledExecutorService("FrequentlyAppendedTableModel");
      this.state = FrequentlyAppendedTableModel.RecordState.SHOW;
      this.stop = false;
      this.Compare = null;
      this.bSort = false;
      this.dataLock = UtilityConstants.createReadWriteLock();
      this.PENDING_LOCK = new Object();
      this.update = new Runnable() {
         private final List<DataObject> getTempAddList() {
            Vector var1 = new Vector();
            synchronized(FrequentlyAppendedTableModel.this.PENDING_LOCK) {
               if (FrequentlyAppendedTableModel.this.pendingAdds.size() == 0 && FrequentlyAppendedTableModel.this.pendingDeletes.size() == 0) {
                  try {
                     FrequentlyAppendedTableModel.this.PENDING_LOCK.wait();
                  } catch (Exception var6) {
                  }
               }

               switch(FrequentlyAppendedTableModel.this.state) {
               case DISCARD:
                  FrequentlyAppendedTableModel.this.pendingAdds.clear();
                  FrequentlyAppendedTableModel.this.pendingDeletes.clear();
                  return var1;
               case HOLD:
                  try {
                     FrequentlyAppendedTableModel.this.PENDING_LOCK.wait();
                  } catch (Exception var5) {
                  }
               default:
                  List var3 = FrequentlyAppendedTableModel.this.pendingAdds.subList(0, Math.min(FrequentlyAppendedTableModel.this.pendingAdds.size(), Integer.MAX_VALUE));
                  var1.addAll(var3);
                  var3.clear();
                  return var1;
               }
            }
         }

         @Override
         public final void run() {
            try {
               List var1 = this.getTempAddList();
               Vector var2 = new Vector();
               synchronized(FrequentlyAppendedTableModel.this.PENDING_LOCK) {
                  var2.addAll(FrequentlyAppendedTableModel.this.pendingDeletes);
                  FrequentlyAppendedTableModel.this.pendingDeletes.clear();
               }

               if (var1.size() != 0 || var2.size() != 0) {
                  Vector var3 = new Vector();
                  List<DataObject> var4 = new Vector<DataObject>();
                  boolean var8 = false;
                  FrequentlyAppendedTableModel.this.readLock();

                  List var5;
                  Comparator var6;
                  FrequentlyAppendedTableModel.RecordState var7;
                  try {
                     var7 = FrequentlyAppendedTableModel.this.state;
                     var4.addAll(FrequentlyAppendedTableModel.this.data);
                     var5 = FrequentlyAppendedTableModel.this.data;
                     var6 = FrequentlyAppendedTableModel.this.Compare;
                  } finally {
                     FrequentlyAppendedTableModel.this.readUnlock();
                  }

                  int var40;
                  Iterator var9;
                  DataObject var10;
                  int var11;
                  if (FrequentlyAppendedTableModel.this.bSort && var6 != null) {
                     FrequentlyAppendedTableModel.this.bSort = false;
                     FrequentlyAppendedTableModel.this.insertObservable.setChanged();
                     FrequentlyAppendedTableModel.this.updateObservable.setChanged();
                     var4.addAll(var1);
                     Collections.sort(var4, var6);
                     if (FrequentlyAppendedTableModel.this.maximum != -1 && var4.size() > FrequentlyAppendedTableModel.this.maximum) {
                        var40 = var4.size() - FrequentlyAppendedTableModel.this.maximum + FrequentlyAppendedTableModel.this.maximum * 4 / 10;
                        var4.subList(0, var40).clear();
                     }

                     var3.add(FrequentlyAppendedTableModel.this.new FireTableDataChanged());
                     var8 = true;
                  } else {
                     var9 = var1.iterator();

                     while(var9.hasNext()) {
                        var10 = (DataObject) var9.next();
                        var11 = FrequentlyAppendedTableModel.this.findIndex(var4, (DataObject) var10, var6);
                        if (var11 < 0) {
                           ++var11;
                           var11 = -var11;
                           var4.add(var11, (DataObject) var10);
                           var3.add(FrequentlyAppendedTableModel.this.new FireTableRowsInserted(var11, var11));
                           FrequentlyAppendedTableModel.this.insertObservable.setChanged();
                        } else {
                           var3.add(FrequentlyAppendedTableModel.this.new FireTableRowsUpdated(var11, var11));
                           FrequentlyAppendedTableModel.this.updateObservable.setChanged();
                        }
                     }

                     if (var3.size() * 10 >= var4.size()) {
                        var3.clear();
                        var3.add(FrequentlyAppendedTableModel.this.new FireTableDataChanged());
                        var8 = true;
                     }

                     for(var40 = 0; var40 < var3.size() - 1; ++var40) {
                        if (((AbstractEnumeratedTableModel.FireTableModification)var3.get(var40)).getType() == AbstractEnumeratedTableModel.TableModificationType.Updated && ((AbstractEnumeratedTableModel.FireTableModification)var3.get(var40 + 1)).getType() == AbstractEnumeratedTableModel.TableModificationType.Updated) {
                           AbstractEnumeratedTableModel.FireTableRowsUpdated var41 = (AbstractEnumeratedTableModel.FireTableRowsUpdated)var3.get(var40);
                           AbstractEnumeratedTableModel.FireTableRowsUpdated var44 = (AbstractEnumeratedTableModel.FireTableRowsUpdated)var3.get(var40 + 1);
                           if (var41.lastRow + 1 == var44.firstRow) {
                              var41.lastRow = var44.lastRow;
                              var3.remove(var40 + 1);
                              --var40;
                              continue;
                           }

                           if (var41.lastRow > var44.firstRow) {
                              var41.lastRow = Math.max(var41.lastRow, var44.lastRow);
                           }
                        }

                        if (((AbstractEnumeratedTableModel.FireTableModification)var3.get(var40)).getType() == AbstractEnumeratedTableModel.TableModificationType.Inserted && ((AbstractEnumeratedTableModel.FireTableModification)var3.get(var40 + 1)).getType() == AbstractEnumeratedTableModel.TableModificationType.Inserted) {
                           AbstractEnumeratedTableModel.FireTableRowsInserted var42 = (AbstractEnumeratedTableModel.FireTableRowsInserted)var3.get(var40);
                           AbstractEnumeratedTableModel.FireTableRowsInserted var45 = (AbstractEnumeratedTableModel.FireTableRowsInserted)var3.get(var40 + 1);
                           if (var42.lastRow + 1 == var45.firstRow) {
                              var42.lastRow = var45.lastRow;
                              var3.remove(var40 + 1);
                              --var40;
                           } else if (var42.lastRow > var45.firstRow) {
                              var42.lastRow = Math.max(var42.lastRow, var45.lastRow);
                           }
                        }
                     }

                     FrequentlyAppendedTableModel.this.writeLock();

                     try {
                        if (var7 == FrequentlyAppendedTableModel.RecordState.SHOW && FrequentlyAppendedTableModel.this.maximum != -1 && var4.size() > FrequentlyAppendedTableModel.this.maximum) {
                           var40 = var4.size() - FrequentlyAppendedTableModel.this.maximum + FrequentlyAppendedTableModel.this.maximum * 4 / 10;
                           var4.subList(0, var40).clear();
                           var3.clear();
                           var3.add(FrequentlyAppendedTableModel.this.new FireTableDataChanged());
                           var8 = true;
                        }
                     } finally {
                        FrequentlyAppendedTableModel.this.writeUnlock();
                     }
                  }

                  var9 = var2.iterator();

                  while(var9.hasNext()) {
                     var10 = (DataObject) var9.next();
                     var11 = FrequentlyAppendedTableModel.this.findIndex(var4, var10, var6);
                     if (var11 >= 0) {
                        var4.remove(var11);
                        if (!var8) {
                           var3.add(FrequentlyAppendedTableModel.this.new FireTableRowsDeleted(var11, var11));
                        }
                     }
                  }

                  FrequentlyAppendedTableModel.this.writeLock();

                  try {
                     if (var5 != FrequentlyAppendedTableModel.this.data) {
                        return;
                     }

                     FrequentlyAppendedTableModel.this.data = var4;
                     var9 = var3.iterator();

                     while(var9.hasNext()) {
                        AbstractEnumeratedTableModel.FireTableModification var43 = (AbstractEnumeratedTableModel.FireTableModification)var9.next();
                        EventQueue.invokeLater(var43);
                     }
                  } finally {
                     FrequentlyAppendedTableModel.this.writeUnlock();
                  }

                  FrequentlyAppendedTableModel.this.updateObservable.notifyObservers();
                  FrequentlyAppendedTableModel.this.insertObservable.notifyObservers();
                  return;
               }
            } finally {
               if (!FrequentlyAppendedTableModel.this.stop) {
                  FrequentlyAppendedTableModel.this.exec.schedule(this, FrequentlyAppendedTableModel.this.wait, FrequentlyAppendedTableModel.this.units);
               }

            }

         }
      };
      this.wait = var2;
      this.units = var4;
      this.maximum = var5;
      this.data = new Vector();
      this.pendingAdds = new Vector();
      this.pendingDeletes = new Vector();
      this.exec.submit(this.update);
   }

   public void stop() {
      this.stop = true;
   }

   protected DataObject getRecord(int var1) {
      this.readLock();

      DataObject var2;
      try {
         if (var1 >= 0 && var1 < this.data.size()) {
            var2 = this.data.get(var1);
            return (DataObject) var2;
         }

         var2 = null;
      } finally {
         this.readUnlock();
      }

      return var2;
   }

   public final void addRecord(DataObject dataObject) {
      this.addOrUpdateRecord(dataObject);
   }

   public void addOrUpdateRecord(DataObject dataObject) {
      synchronized(this.PENDING_LOCK) {
         if (this.state != FrequentlyAppendedTableModel.RecordState.DISCARD) {
            int var3 = this.findIndex(this.pendingAdds, dataObject, this.Compare);
            if (var3 < 0) {
               ++var3;
               var3 = -var3;
               this.pendingAdds.add(var3, dataObject);
               this.PENDING_LOCK.notify();
            }
         }
      }
   }

   public final void updateRecord(DataObject var1) {
      this.addOrUpdateRecord(var1);
   }

   public final void deleteRecord(DataObject var1) {
      synchronized(this.PENDING_LOCK) {
         if (this.state != FrequentlyAppendedTableModel.RecordState.DISCARD) {
            this.pendingDeletes.add(var1);
         }
      }
   }

   public void setRecordState(FrequentlyAppendedTableModel.RecordState var1) {
      this.writeLock();

      try {
         this.state = var1;
      } finally {
         this.writeUnlock();
      }

      synchronized(this.PENDING_LOCK) {
         try {
            this.PENDING_LOCK.notify();
         } catch (Exception var8) {
         }

      }
   }

   @Override
   public int getRowCount() {
      this.readLock();

      int var1;
      try {
         var1 = this.data.size();
      } finally {
         this.readUnlock();
      }

      return var1;
   }

   public void setMaximum(int var1) {
      this.maximum = var1;
   }

   public void clear() {
      this.writeLock();

      try {
         synchronized(this.PENDING_LOCK) {
            this.pendingAdds = new Vector();
         }

         this.data = new Vector();
         EventQueue.invokeLater(new AbstractEnumeratedTableModel.FireTableDataChanged());
      } finally {
         this.writeUnlock();
      }

   }

   protected void updated(DataObject var1) {
   }

   protected void removeObject(DataObject var1) {
      boolean var2 = true;
      this.writeLock();

      try {
         synchronized(this.PENDING_LOCK) {
            this.pendingAdds.remove(var1);
         }

         int var10 = this.data.indexOf(var1);
         if (var10 > 0) {
            this.data.remove(var10);
            EventQueue.invokeLater(new AbstractEnumeratedTableModel.FireTableRowsDeleted(var10, var10));
         }
      } finally {
         this.writeUnlock();
      }

   }

   public enum RecordState {
      SHOW,
      HOLD,
      DISCARD,
      KEEP;
   }
}
