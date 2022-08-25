package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.task.TaskId;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import ddb.util.GeneralUtilities;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DirListener extends FileManagerDataListener {
   static final Map<String, FileObjectFields> attributeLookup = new HashMap();

   public DirListener(CoreController var1, FileSystemModel var2) {
      super(var1, var2);
   }

   protected void handleData(DataEvent var1) {
      switch(var1.getDataType()) {
      case DATA:
         if (var1.getData().getObject("diritem") != null) {
            List var2 = var1.getData().getObjects("DirItem");

            ObjectValue var4;
            String var5;
            for(Iterator var3 = var2.iterator(); var3.hasNext(); this.model.getNodeForPath(var5, true, CommandType.DIR, var1.getTaskId(), var1.getTimestamp(), true, new DirListener.FillinDirectory(this.model, var4, var1.getTaskId(), var1.getTimestamp()))) {
               var4 = (ObjectValue)var3.next();
               var5 = var4.getString("path");
               if (var5.length() == 3) {
                  System.out.printf("%s = %s, %d\n", var5, SimpleDateFormat.getTimeInstance().format(Calendar.getInstance().getTime()), Runtime.getRuntime().totalMemory());
               }
            }
         }
      default:
      }
   }

   static {
      attributeLookup.put("offline", FileObjectFields.Attr_Offline);
      attributeLookup.put("characterspecialfile", FileObjectFields.Attr_CharacterSpecialFile);
      attributeLookup.put("archive", FileObjectFields.Attr_Archive);
      attributeLookup.put("namedpipe", FileObjectFields.Attr_NamedPipe);
      attributeLookup.put("symboliclink", FileObjectFields.Attr_SymbolicLink);
      attributeLookup.put("compressed", FileObjectFields.Attr_Compressed);
      attributeLookup.put("encrypted", FileObjectFields.Attr_Encrypted);
      attributeLookup.put("blockspecialfile", FileObjectFields.Attr_BlockSpecialFile);
      attributeLookup.put("afunixfamilysocket", FileObjectFields.Attr_UnixFamilySocket);
      attributeLookup.put("hidden", FileObjectFields.Attr_Hidden);
      attributeLookup.put("temporary", FileObjectFields.Attr_Temporary);
      attributeLookup.put("reparsepoint", FileObjectFields.Attr_ReparsePoint);
      attributeLookup.put("system", FileObjectFields.Attr_System);
      attributeLookup.put("read-only", FileObjectFields.Attr_ReadOnly);
      attributeLookup.put("group", FileObjectFields.Attr_Group_Name);
      attributeLookup.put("owner", FileObjectFields.Attr_Owner_Name);
      attributeLookup.put("groupid", FileObjectFields.Attr_Group_Id);
      attributeLookup.put("ownerid", FileObjectFields.Attr_Owner_Id);
   }

   private class FillinDirectory extends FileManagerDataListener.DataClosure {
      ObjectValue directory;
      TaskId taskId;
      Calendar timestamp;

      public FillinDirectory(FileSystemModel var2, ObjectValue var3, TaskId var4, Calendar var5) {
         super(true, var2);
         this.directory = var3;
         this.taskId = var4;
         this.timestamp = var5;
      }

      public void executeChild(Object var1) {
         if (var1 != null && var1 instanceof FileObject) {
            FileObject var2 = (FileObject)var1;
            var2.setDirectory();
            String var3 = this.directory.getString("denied");
            if (var3 != null && var3.equals("true")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.Dir_AccessDenied, true);
            } else {
               var2.setDataElement((FileObjectFields)FileObjectFields.Dir_AccessDenied, false);
               List var4 = this.directory.getObjects("FileItem");
               Iterator var5 = var4.iterator();

               while(var5.hasNext()) {
                  ObjectValue var6 = (ObjectValue)var5.next();
                  String var7 = var6.getString("name");
                  if (var7 != null && !var7.equals(".") && !var7.equals("..")) {
                     this.model.getChildNode(var2, var7, true, CommandType.DIR, this.taskId, this.timestamp, true, DirListener.this.new FillinFileObject(this.model, var6, this.taskId, this.timestamp));
                  }
               }

            }
         }
      }
   }

   private class FillinFileObject extends FileManagerDataListener.DataClosure {
      ObjectValue file;
      TaskId taskId;
      Calendar timestamp;

      public FillinFileObject(FileSystemModel var2, ObjectValue var3, TaskId var4, Calendar var5) {
         super(false, var2);
         this.file = var3;
         this.taskId = var4;
         this.timestamp = var5;
      }

      public void executeChild(Object var1) {
         if (var1 != null && var1 instanceof FileObject) {
            FileObject var2 = (FileObject)var1;
            var2.setDataElement((FileObjectFields)FileObjectFields.File_AlternateName, this.file.getString("altName"));
            var2.setDataElement((FileObjectFields)FileObjectFields.File_Accessed, GeneralUtilities.stringToCalendar(this.file.getString("FileTimes::Accessed::time"), (Calendar)null));
            var2.setDataElement((FileObjectFields)FileObjectFields.File_Modified, GeneralUtilities.stringToCalendar(this.file.getString("FileTimes::Modified::time"), (Calendar)null));
            var2.setDataElement((FileObjectFields)FileObjectFields.File_Created, GeneralUtilities.stringToCalendar(this.file.getString("FileTimes::Created::time"), (Calendar)null));
            if (!this.file.getBoolean("Attributes::directory")) {
               var2.setDataElement((FileObjectFields)FileObjectFields.File_Size, this.file.getInteger("size"));
            } else {
               var2.setDirectory();
            }

            ObjectValue var3 = this.file.getObject("Attributes");
            Iterator var4 = var3.getBooleanNames().iterator();

            String var5;
            while(var4.hasNext()) {
               var5 = (String)var4.next();
               var2.setDataElement((FileObjectFields)((FileObjectFields)DirListener.attributeLookup.get(var5)), var3.getBoolean(var5));
            }

            var4 = var3.getStringNames().iterator();

            while(var4.hasNext()) {
               var5 = (String)var4.next();
               var2.setDataElement((FileObjectFields)((FileObjectFields)DirListener.attributeLookup.get(var5)), var3.getString(var5));
            }

            var3 = this.file.getObject("Unix-Specific");
            var4 = var3.getBooleanNames().iterator();

            while(var4.hasNext()) {
               var5 = (String)var4.next();
               var2.setDataElement((FileObjectFields)((FileObjectFields)DirListener.attributeLookup.get(var5)), var3.getBoolean(var5));
            }

            var4 = var3.getStringNames().iterator();

            while(var4.hasNext()) {
               var5 = (String)var4.next();
               var2.setDataElement((FileObjectFields)((FileObjectFields)DirListener.attributeLookup.get(var5)), var3.getString(var5));
            }

            List var13 = this.file.getObjects("hash");
            Iterator var14 = var13.iterator();

            while(var14.hasNext()) {
               ObjectValue var6 = (ObjectValue)var14.next();
               String var7 = var6.getString("type");
               String var8 = var6.getString("value");
               FileObjectFields[] var9 = FileObjectFields.HashFields;
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  FileObjectFields var12 = var9[var11];
                  if (var12.getName().equalsIgnoreCase(var7)) {
                     var2.setDataElement((FileObjectFields)var12, var8);
                  }
               }
            }

            var2.save();
            this.model.addHistoryItem(var2, this.taskId, ActionType.INFO, CommandType.DIR, this.timestamp);
         }
      }
   }
}
