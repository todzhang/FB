package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GetListener extends FileManagerDataListener {
   Set<GetListener.GetRecord> Pending = new HashSet();

   public GetListener(CoreController var1, FileSystemModel var2) {
      super(var1, var2);
   }

   protected void handleData(DataEvent var1) {
      GetListener.GetRecord var2 = null;
      if (DataEventType.DATA.equals(var1.getDataType())) {
         synchronized(this) {
            ObjectValue var4 = var1.getData().getObject("filestart");
            if (var4 != null) {
               String var5 = var4.getString("filename");
               long var6 = this.getId(var4);
               if (var6 != -1L) {
                  var2 = this.searchForRecord(new GetListener.GetRecord(var5, var1.getTaskId(), var6, var4.getInteger("size")), true);
               }
            }

            ObjectValue var11 = var1.getData().getObject("filestop");
            if (var11 != null) {
               String var12 = var11.getString("filename");
               long var7 = this.getId(var11);
               if (var7 != -1L) {
                  var2 = this.searchForRecord(new GetListener.GetRecord(var12, var1.getTaskId(), var7, -1L), false);
               }
            }
         }
      }

      if (var2 != null) {
         FileObject var3 = this.model.getNodeForPath(var2.path, true, CommandType.GET, var1.getTaskId(), var1.getTimestamp(), true, new GetListener.SetSize(var2.size, this.model));
         this.model.addHistoryItem(var3, var1.getTaskId(), ActionType.INFO, CommandType.GET, var1.getTimestamp());
      }

   }

   private long getId(ObjectValue var1) {
      try {
         return var1.getInteger("id");
      } catch (NullPointerException var5) {
         try {
            return Long.parseLong(var1.getString("id"));
         } catch (Exception var4) {
            var4.printStackTrace();
            return -1L;
         }
      }
   }

   private GetListener.GetRecord searchForRecord(GetListener.GetRecord var1, boolean var2) {
      Iterator var3 = this.Pending.iterator();

      GetListener.GetRecord var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (GetListener.GetRecord)var3.next();
      } while(!var4.equals(var1));

      this.Pending.remove(var4);
      return var2 ? var1 : var4;
   }

   private class SetSize extends FileManagerDataListener.DataClosure {
      Long size;

      public SetSize(Long var2, FileSystemModel var3) {
         super(false, var3);
         this.size = var2;
      }

      protected void executeChild(Object var1) {
         if (var1 instanceof FileObject && this.size != null) {
            FileObject var2 = (FileObject)var1;
            var2.setDataElement((FileObjectFields)FileObjectFields.File_Size, this.size);
         }

      }
   }

   private class GetRecord {
      final String path;
      final TaskId taskId;
      final long fileId;
      final long size;

      public GetRecord(String var2, TaskId var3, long var4, long var6) {
         this.path = var2;
         this.taskId = var3;
         this.fileId = var4;
         this.size = var6;
      }

      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (this.getClass() != var1.getClass()) {
            return false;
         } else {
            GetListener.GetRecord var2 = (GetListener.GetRecord)var1;
            if (this.taskId != var2.taskId && (this.taskId == null || !this.taskId.equals(var2.taskId))) {
               return false;
            } else {
               return this.fileId == var2.fileId;
            }
         }
      }

      public int hashCode() {
         byte var1 = 7;
         int var2 = 67 * var1 + (this.taskId != null ? this.taskId.hashCode() : 0);
         var2 = 67 * var2 + (int)this.fileId;
         return var2;
      }
   }
}
