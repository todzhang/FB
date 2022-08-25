package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;

public class PwdListener extends FileManagerDataListener {
   public PwdListener(CoreController var1, FileSystemModel var2) {
      super(var1, var2);
   }

   protected void handleData(DataEvent var1) {
      if (DataEventType.DATA.equals(var1.getDataType())) {
         String var2 = var1.getData().getString("currentdirectory::path");
         if (var2 == null) {
            return;
         }

         FileObject var3 = this.model.getNodeForPath(var2, true, CommandType.CD, var1.getTaskId(), var1.getTimestamp(), true, MAKE_DIRECTORY);
         FileSystemModel var10000 = this.model;
         TaskId var10002 = var1.getTaskId();
         CommandType var10004 = CommandType.CD;
         var10000.addHistoryItem(var3, var10002, ActionType.INFO, CommandType.PWD, var1.getTimestamp());
      }

   }
}
