package ds.core.impl;

import ddb.dsz.core.operation.MutableOperation;
import ddb.dsz.core.operation.Operation;
import ddb.util.Guid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class OperationImpl implements Operation, MutableOperation {
   private static final List<OperationImpl> ALL_CREATED_OPERATIONS = new ArrayList();
   private final Guid guid;
   private final long startTime;
   private long lastTime = 0L;

   public static OperationImpl GenerateOperation(Guid var0) {
      return GenerateOperation(var0, Calendar.getInstance());
   }

   public static synchronized OperationImpl GenerateOperation(Guid var0, Calendar var1) {
      return GenerateOperation(var0, var1.getTimeInMillis());
   }

   public static synchronized OperationImpl GenerateOperation(Guid var0, long var1) {
      OperationImpl var3 = new OperationImpl(var0, var1);
      int var4 = Collections.binarySearch(ALL_CREATED_OPERATIONS, var3);
      if (var4 >= 0) {
         return (OperationImpl)ALL_CREATED_OPERATIONS.get(var4);
      } else {
         ++var4;
         var4 = 0 - var4;
         if (var4 < ALL_CREATED_OPERATIONS.size()) {
            ALL_CREATED_OPERATIONS.add(var4, var3);
         } else {
            ALL_CREATED_OPERATIONS.add(var3);
         }

         return var3;
      }
   }

   private OperationImpl(Guid var1, Calendar var2) {
      this.guid = var1;
      this.startTime = var2.getTimeInMillis();
   }

   private OperationImpl(Guid var1, long var2) {
      this.guid = var1;
      this.startTime = var2;
   }

   @Override
   public Guid getGuid() {
      return this.guid;
   }

   @Override
   public void setLastTime(Calendar lastTime) {
      long var2 = lastTime.getTimeInMillis();
      if (var2 > this.lastTime) {
         this.lastTime = var2;
      }

   }

   @Override
   public Calendar getStartTime() {
      Calendar var1 = Calendar.getInstance();
      var1.setTimeInMillis(this.startTime);
      return var1;
   }

   @Override
   public Calendar getLastTime() {
      if (this.lastTime == 0L) {
         return null;
      } else {
         Calendar var1 = Calendar.getInstance();
         var1.setTimeInMillis(this.lastTime);
         return var1;
      }
   }

   public int compareTo(Operation var1) {
      if (this == var1) {
         return 0;
      } else {
         return var1 == null ? 1 : this.guid.compareTo(var1.getGuid());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (var1 instanceof Operation) {
         Operation var2 = (Operation)var1;
         return this.guid.equals(var2.getGuid());
      } else {
         return false;
      }
   }

   public int hashCode() {
      byte var1 = 3;
      int var2 = 37 * var1 + (this.guid != null ? this.guid.hashCode() : 0);
      return var2;
   }

   public String toString() {
      return "Operation:  " + this.guid.toString();
   }
}
