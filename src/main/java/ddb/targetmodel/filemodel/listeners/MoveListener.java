package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import org.apache.commons.collections.ClosureUtils;

public class MoveListener extends FileManagerDataListener {
   public MoveListener(CoreController var1, FileSystemModel var2) {
      super(var1, var2);
   }

   protected void handleData(DataEvent var1) {
      if (DataEventType.DATA.equals(var1.getDataType())) {
         FileObject var2 = this.model.getNodeForPath(var1.getData().getString("moveresults::destination"), true, CommandType.MOVE, var1.getTaskId(), var1.getTimestamp(), true, ClosureUtils.nopClosure());
         FileObject var3 = this.model.getNodeForPath(var1.getData().getString("moveresults::source"), true, CommandType.MOVE, var1.getTaskId(), var1.getTimestamp(), true, ClosureUtils.nopClosure());
         this.model.addHistoryItem(var2, var1.getTaskId(), ActionType.MOVEDEST, CommandType.MOVE, var1.getTimestamp());
         this.model.addHistoryItem(var3, var1.getTaskId(), ActionType.MOVESOURCE, CommandType.MOVE, var1.getTimestamp());
      }

   }
}
