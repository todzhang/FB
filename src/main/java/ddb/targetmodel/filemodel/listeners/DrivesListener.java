package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.TaskId;
import ddb.targetmodel.filemodel.DriveType;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import java.util.Calendar;
import java.util.Iterator;

public class DrivesListener extends FileManagerDataListener {
   public DrivesListener(CoreController var1, FileSystemModel var2) {
      super(var1, var2);
   }

   protected void handleData(DataEvent var1) {
      if (DataEventType.DATA.equals(var1.getDataType())) {
         Iterator var2 = var1.getData().getObjects("driveitem").iterator();

         while(var2.hasNext()) {
            ObjectValue var3 = (ObjectValue)var2.next();
            this.model.getNodeForPath(var3.getString("drive"), true, CommandType.DRIVES, var1.getTaskId(), var1.getTimestamp(), true, new DrivesListener.FillinDrive(this.model, var3, var1.getTaskId(), var1.getTimestamp()));
         }
      }

   }

   private class FillinDrive extends FileManagerDataListener.DataClosure {
      ObjectValue driveObject;
      TaskId taskId;
      Calendar timestamp;

      public FillinDrive(FileSystemModel var2, ObjectValue var3, TaskId var4, Calendar var5) {
         super(false, var2);
         this.driveObject = var3;
         this.taskId = var4;
         this.timestamp = var5;
      }

      public void executeChild(Object var1) {
         if (var1 != null && var1 instanceof FileObject) {
            FileObject var2 = (FileObject)var1;
            var2.setDrive();
            var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Serial, this.driveObject.getString("serialnumber"));
            var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Options, this.driveObject.getString("options"));
            var2.setDataElement((FileObjectFields)FileObjectFields.Drive_FileSystem, this.driveObject.getString("filesystem"));
            var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Source, this.driveObject.getString("drivesource"));
            String var3 = this.driveObject.getString("type");
            if (var3 == null) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.DRIVE.ordinal());
            } else if (var3.equalsIgnoreCase("Fixed")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.DRIVE.ordinal());
            } else if (var3.equalsIgnoreCase("Removable")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.FLOPPYDISK.ordinal());
            } else if (var3.equalsIgnoreCase("Network")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.DRIVENETWORK.ordinal());
            } else if (var3.equalsIgnoreCase("Cdrom")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.CDDRIVE.ordinal());
            } else if (var3.equalsIgnoreCase("Ramdisk")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.RAMDISK.ordinal());
            } else if (var3.equalsIgnoreCase("Simulated")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.DRIVESIMULATED.ordinal());
            } else {
               var2.setDataElement((FileObjectFields)FileObjectFields.Drive_Type, DriveType.DRIVE.ordinal());
            }

            var2.save();
            this.model.addHistoryItem(var2, this.taskId, ActionType.INFO, CommandType.DRIVES, this.timestamp);
         }
      }
   }
}
