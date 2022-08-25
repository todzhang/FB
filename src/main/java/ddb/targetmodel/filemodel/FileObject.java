package ddb.targetmodel.filemodel;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileObject {
   private static final Data<?> NULL = new StringData("null");
   private static final String PENDING_FILE;
   private static final String PENDING_DIR;
   private static final String PENDING_DRIVE;
   private FileSystemModel fileSystem;
   private long fileId = -1L;
   private String path = null;
   private boolean isDir = false;
   private boolean isDrive = false;
   private boolean isDiscard = false;
   private boolean pendingRequest = false;
   Data<?>[] dataFields = new Data[FileObjectFields.values().length];

   public static List<FileObject> getFiles(FileSystemModel var0, ResultSet var1, String var2) {
      return getFiles(var0, var1, var2, Integer.MAX_VALUE);
   }

   public static List<FileObject> getFiles(FileSystemModel var0, ResultSet var1, String var2, int var3) {
      ArrayList var4 = new ArrayList();

      try {
         try {
            ResultSetMetaData var5 = var1.getMetaData();

            while(var1.next() && var3-- > 0) {
               FileObject var6 = new FileObject(var0);
               var6.fileId = (long)var1.getInt("FileId");
               var6.setPath(var2);
               var6.retrieve(var1, var5);
               var4.add(var6);
            }
         } finally {
            var1.close();
         }
      } catch (SQLException var11) {
         Logger.getLogger("dsz").log(Level.SEVERE, (String)null, var11);
      }

      return var4;
   }

   public final void retrieve(ResultSet var1) throws SQLException {
      this.retrieve(var1, var1.getMetaData());
   }

   public final void retrieve(ResultSet var1, ResultSetMetaData var2) throws SQLException {
      Data var4;
      for(int var3 = 1; var3 <= var2.getColumnCount(); ++var3) {
         var4 = this.getDataElement(var2.getColumnName(var3), true);
         if (var4 != null && var4 != NULL) {
            var4.retreive(var1, var3);
         }
      }

      Data var5 = this.getDataElement(FileObjectFields.Dir_Id);
      var4 = this.getDataElement(FileObjectFields.Drive_Id);
      if (var5 != NULL) {
         if (var5.hasValue() && (Long)((LongData)LongData.class.cast(var5)).getValue() == this.fileId) {
            this.isDir = true;
         } else {
            var5.setValue((Object)null);
         }
      }

      if (var4 != NULL) {
         if (var4.hasValue() && (Long)((LongData)LongData.class.cast(var4)).getValue() == this.fileId) {
            this.isDrive = true;
         } else {
            var4.setValue((Object)null);
         }
      }

   }

   public final String toString() {
      Data var1 = this.getDataElement(FileObjectFields.File_Name);
      return var1 != null && var1 != NULL ? var1.getValue().toString() : "?";
   }

   public final String getIcon() {
      if (this.isPendingRequest()) {
         if (this.isDrive()) {
            return PENDING_DRIVE;
         } else {
            return this.isDirectory() ? PENDING_DIR : PENDING_FILE;
         }
      } else {
         Data var1;
         if (!this.isDirectory()) {
            var1 = this.getDataElement(FileObjectFields.File_Name);
            return var1 != NULL && var1.hasValue() ? DriveType.DOCUMENT.getIcon() : DriveType.QUESTION.getIcon();
         } else if (!this.isDrive()) {
            var1 = this.getDataElement(FileObjectFields.Dir_AccessDenied);
            Data var2 = this.getDataElement(FileObjectFields.File_Children);
            if (var1 != NULL && var1.hasValue() && Boolean.TRUE.equals(var1.getValue())) {
               return DriveType.ACCESS_DENIED_FOLDER.getIcon();
            } else {
               return var2 != NULL && var2.getValue() != null && (Long)((LongData)LongData.class.cast(var2)).getValue() != 0L ? DriveType.FOLDER.getIcon() : DriveType.EMPTYFOLDER.getIcon();
            }
         } else {
            var1 = this.getDataElement(FileObjectFields.Drive_Type);
            return var1 != NULL && var1.hasValue() ? DriveType.values()[((Long)((LongData)LongData.class.cast(var1)).getValue()).intValue()].getIcon() : DriveType.DRIVE.getIcon();
         }
      }
   }

   public FileObject(FileSystemModel var1) {
      this.fileSystem = var1;
   }

   public FileObject(FileSystemModel var1, long var2) {
      this.fileSystem = var1;
      this.fileId = var2;
   }

   public final void setDataElement(String var1, Object var2) {
      if (var1 != null) {
         Data var3 = this.getDataElement(var1, true);
         if (var3 != null && var3 != NULL) {
            var3.setValue(var2);
         }
      }
   }

   public final void setDataElement(FileObjectFields var1, Object var2) {
      if (var1 != null) {
         Data var3 = this.getDataElement(var1, true);
         if (var3 != null && var3 != NULL) {
            var3.setValue(var2);
         }
      }
   }

   public final Data<?> getDataElement(String var1) {
      return this.getDataElement(var1, false);
   }

   public final Data<?> getDataElement(String var1, boolean var2) {
      return var1 == null ? NULL : this.getDataElement(FileObjectFields.getField(var1), var2);
   }

   public final Data<?> getDataElement(FileObjectFields var1) {
      return this.getDataElement(var1, false);
   }

   public final Data<?> getDataElement(FileObjectFields var1, boolean var2) {
      if (var1 == null) {
         return NULL;
      } else {
         Data var3 = this.dataFields[var1.ordinal()];
         if (var3 == null && var2) {
            try {
               var3 = (Data)var1.getClazz().getConstructor(String.class).newInstance(var1.getName());
               this.dataFields[var1.ordinal()] = var3;
            } catch (InstantiationException var5) {
               Logger.getLogger(FileObject.class.getName()).log(Level.SEVERE, (String)null, var5);
            } catch (IllegalAccessException var6) {
               Logger.getLogger(FileObject.class.getName()).log(Level.SEVERE, (String)null, var6);
            } catch (IllegalArgumentException var7) {
               Logger.getLogger(FileObject.class.getName()).log(Level.SEVERE, (String)null, var7);
            } catch (InvocationTargetException var8) {
               Logger.getLogger(FileObject.class.getName()).log(Level.SEVERE, (String)null, var8);
            } catch (NoSuchMethodException var9) {
               var9.printStackTrace();
            }
         }

         return var3 == null ? NULL : var3;
      }
   }

   public final boolean isDirectory() {
      return this.isDir;
   }

   public final boolean isDrive() {
      return this.isDrive;
   }

   public final void forceDirectory() {
      if (!this.isDirectory()) {
         this.isDir = true;
         this.setDataElement((FileObjectFields)FileObjectFields.Dir_Id, this.fileId);
      }
   }

   public final void setDirectory() {
      LongData var1 = (LongData)this.getDataElement(FileObjectFields.Dir_Id, true);
      if (var1 != null && var1 != NULL) {
         if (!var1.hasValue() || (Long)var1.getValue() != this.fileId) {
            this.isDir = true;
            if (this.fileId != -1L) {
               this.fileSystem.makeDirectory(this.fileId);
               var1.setValue(this.fileId, false);
               this.fileSystem.fireFileChanged(this);
            }

         }
      }
   }

   public final void setDrive() {
      LongData var1 = (LongData)this.getDataElement(FileObjectFields.Drive_Id, true);
      if (var1 != null && var1 != NULL) {
         if (!var1.hasValue() || (Long)var1.getValue() != this.fileId) {
            this.setDirectory();
            this.isDrive = true;
            if (this.fileId != -1L) {
            }

            this.isDir = true;
            if (this.fileId != -1L) {
               this.fileSystem.makeDrive(this.fileId);
               var1.setValue(this.fileId, false);
               this.fileSystem.fireFileChanged(this);
            }

         }
      }
   }

   public final void save() {
      this.fileSystem.save(this);
   }

   public final boolean isModified() {
      Data[] var1 = this.dataFields;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Data var4 = var1[var3];
         if (var4 != null && var4 != NULL && var4.isModified()) {
            return true;
         }
      }

      return false;
   }

   public final Long getId() {
      return this.fileId;
   }

   public final Long getParent() {
      Data var1 = this.getDataElement(FileObjectFields.File_Parent);
      if (var1 != null && var1 != NULL) {
         return var1.hasValue() ? (Long)((LongData)LongData.class.cast(var1)).getValue() : -1L;
      } else {
         return -1L;
      }
   }

   public final String getName() {
      StringData var1 = (StringData)this.getDataElement(FileObjectFields.File_Name);
      return var1 == null ? null : (String)var1.getValue();
   }

   public final void setParent(long var1) {
      LongData var3 = (LongData)this.getDataElement(FileObjectFields.File_Parent, true);
      if (var3 != null && var3 != NULL) {
         var3.setValue(var1);
      }
   }

   public final void setName(String var1) {
      StringData var2 = (StringData)this.getDataElement(FileObjectFields.File_Name, true);
      if (var2 != null && var2 != NULL) {
         var2.setValue(var1);
      }
   }

   public final void setPath(String var1) {
      this.path = var1;
   }

   public final String getPath() {
      return this.path;
   }

   public final void setId(int var1) {
      if (this.fileId == -1L) {
         this.fileId = (long)var1;
         LongData var2;
         if (this.isDir) {
            var2 = (LongData)this.getDataElement(FileObjectFields.Dir_Id, true);
            if (var2 != null && var2 != NULL) {
               var2.setValue(var1, false);
            }
         }

         if (this.isDrive) {
            var2 = (LongData)this.getDataElement(FileObjectFields.Drive_Id, true);
            if (var2 != null && var2 != NULL) {
               var2.setValue(var1, false);
            }
         }

      }
   }

   public final void setPendingRequest(boolean var1) {
      this.pendingRequest = var1;
   }

   public final boolean isPendingRequest() {
      return this.pendingRequest;
   }

   public final void discard() {
      this.isDiscard = true;
   }

   public final boolean isDiscard() {
      return this.isDiscard;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         FileObject var2 = (FileObject)var1;
         if (this.fileSystem == var2.fileSystem || this.fileSystem != null && this.fileSystem.equals(var2.fileSystem)) {
            if (this.fileId != var2.fileId) {
               return false;
            } else {
               label39: {
                  if (this.path == null) {
                     if (var2.path == null) {
                        break label39;
                     }
                  } else if (this.path.equals(var2.path)) {
                     break label39;
                  }

                  return false;
               }

               if (this.getName() == null) {
                  if (var2.getName() != null) {
                     return false;
                  }
               } else if (!this.getName().equals(var2.getName())) {
                  return false;
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      byte var1 = 3;
      int var2 = 47 * var1 + (this.fileSystem != null ? this.fileSystem.hashCode() : 0);
      var2 = 47 * var2 + (int)(this.fileId ^ this.fileId >>> 32);
      var2 = 47 * var2 + (this.path != null ? this.path.hashCode() : 0);
      var2 = 47 * var2 + (this.getName() != null ? this.getName().hashCode() : 0);
      return var2;
   }

   static {
      PENDING_FILE = DriveType.PENDING_FILE.getIcon();
      PENDING_DIR = DriveType.PENDING_FOLDER.getIcon();
      PENDING_DRIVE = PENDING_DIR;
   }
}
