package ddb.targetmodel.filemodel;

import ddb.detach.MutableTabbableStatus;
import ddb.detach.Tabbable;
import ddb.detach.TabbableStatus;
import ddb.detach.TabbableStatusImpl;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.targetmodel.TargetDetail;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import ddb.targetmodel.filemodel.listeners.CdListener;
import ddb.targetmodel.filemodel.listeners.CopyListener;
import ddb.targetmodel.filemodel.listeners.DirCrawler;
import ddb.targetmodel.filemodel.listeners.DrivesListener;
import ddb.targetmodel.filemodel.listeners.GetListener;
import ddb.targetmodel.filemodel.listeners.MoveListener;
import ddb.targetmodel.filemodel.listeners.PwdListener;
import ddb.util.Guid;
import ddb.util.UtilityConstants;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.event.EventListenerList;
import org.apache.commons.collections.Closure;

public class FileSystemModel implements TargetDetail {
   public static final int TRANSLATE_PRIORITY = Integer.MAX_VALUE;
   public static final int RETRIEVE_PRIORITY = Integer.MIN_VALUE;
   static final List<FileObject> NULL_LIST = Collections.unmodifiableList(new ArrayList());
   static Data<?>[] NO_DATA = new Data[0];
   static final Set<String> INTERESTING_TASKS = new HashSet();
   static final ExecutorService onFinishExec;
   DataTransformer transformer = DataTransformer.newInstance("FileSystemModel");
   boolean finished = false;
   private static final String FILE_TABLE = "create Table File (\tFILEID INTEGER PRIMARY KEY ON CONFLICT IGNORE AUTOINCREMENT,PARENT INTEGER DEFAULT -1,\tNAME TEXT NOT NULL DEFAULT '.' COLLATE NOCASE,\tTRANSLATED_NAME TEXT COLLATE NOCASE,\tALT_NAME TEXT COLLATE NOCASE,\tSIZE INTEGER,\tCREATED TEXT,\tMODIFIED TEXT,\tACCESSED TEXT, UNIQUE (Parent, Name) ON CONFLICT IGNORE, CONSTRAINT Fk_Parent FOREIGN KEY (Parent) REFERENCES File(FileId));";
   private static final String DIRECTORY_TABLE = "create Table Directory (\tDIRID INTEGER PRIMARY KEY ON CONFLICT IGNORE,\tLAST_PARTIAL TEXT,\tLAST_FULL TEXT,\tACCESS_DENIED INTEGER,\tCONSTRAINT Fk_Dir_Id FOREIGN KEY (DirId) REFERENCES File(FileId));";
   private static final String DRIVE_TABLE = "create Table Drive (\tDRIVEID INTEGER PRIMARY KEY ON CONFLICT IGNORE,\tSOURCE TEXT,\tTYPE INTEGER,\tSERIAL TEXT,\tFILESYSTEM TEXT,\tOPTIONS TEXT,\tCONSTRAINT Fk_Drive_Id FOREIGN KEY (DriveId) REFERENCES File(FileId));";
   private static final String HASH_TABLE = "create Table Hash (\tHASHID\tINTEGER PRIMARY KEY ON CONFLICT IGNORE,\tSHA1\tTEXT,\tMD5\t\tTEXT,\tSHA256\tTEXT,\tSHA512\tTEXT,\tCONSTRAINT Fk_Drive_Id FOREIGN KEY (HashId) REFERENCES File(FileId));";
   private static final String ATTRIBUTE_TABLE = "Create Table FileAttributes (\tAttrId\t\t\t\t\tINTEGER PRIMARY KEY ON CONFLICT REPLACE,\tArchive\t\t\t\t\tINTEGER,\tCompressed\t\t\t\tINTEGER,\tEncrypted\t\t\t\tINTEGER,\tHidden\t\t\t\t\tINTEGER,\tOffline\t\t\t\t\tINTEGER,\tReadOnly\t\t\t\tINTEGER,\tReparsePoint\t\t\tINTEGER,\tSparseFile\t\t\t\tINTEGER,\tSystem\t\t\t\t\tINTEGER,\tTemporary\t\t\t\tINTEGER,\tNotContentIndexed\t\tINTEGER,\tDevice\t\t\t\t\tINTEGER,\tOwnerRead\t\t\t\tINTEGER,\tOwnerWrite\t\t\t\tINTEGER,\tOwnerExec\t\t\t\tINTEGER,\tGroupRead\t\t\t\tINTEGER,\tGroupWrite\t\t\t\tINTEGER,\tGroupExec\t\t\t\tINTEGER,\tWorldRead\t\t\t\tINTEGER,\tWorldWrite\t\t\t\tINTEGER,\tWorldExec\t\t\t\tINTEGER,\tSetUid\t\t\t\t\tINTEGER,\tSetGid\t\t\t\t\tINTEGER,\tStickyBit\t\t\t\tINTEGER,\tOwner_Name\t\t\t\tTEXT,\tOwner_Id\t\t\t\tINTEGER,\tGroup_Name\t\t\t\tTEXT,\tGroup_Id\t\t\t\tINTEGER,\tCharacterSpecialFile\tINTEGER,\tBlockSpecialFile\t\tINTEGER,\tUnixFamilySocket\t\tINTEGER,\tNamedPipe\t\t\t\tINTEGER,\tSymbolicLink\t\t\tINTEGER,\tHardLinks\t\t\t\tINTEGER,\tinode\t\t\t\t\tINTEGER,\tCONSTRAINT Fk_Attr_Id FOREIGN KEY (AttrId) REFERENCES File(FileId));";
   private static final String HISTORY_TABLE = "Create Table History (\tFILEID INTEGER,\tTASKID INTEGER,   OPERATION STRING,\tACTION INTEGER,\tCOMMAND INTEGER,\tTIME TEXT,\tARGUMENT TEXT,\tCONSTRAINT Pk_History_Table PRIMARY KEY (FileId, Operation, TaskId) ON CONFLICT IGNORE );";
   static final String[] SCHEMA;
   public static final Long ROOT;
   public static final Long HOSTS;
   public static final Long DEAD;
   ScheduledThreadPoolExecutor databaseExec = new ScheduledThreadPoolExecutor(1, UtilityConstants.createThreadFactory("FileSystemModel - databaseExec"));
   static ScheduledThreadPoolExecutor fileChangedExec;
   FileSystemModel.Status status;
   EventListenerList listeners;
   final Object UPDATED_FILES_LOCK;
   List<FileObject> updatedFiles2;
   Comparator<FileObject> FileObjectComparator;
   CoreController core;
   String hostId;
   Connection connection;
   PreparedStatement directoryListing;
   PreparedStatement subdirectories;
   PreparedStatement addAttributes;
   PreparedStatement retrieveFile;
   PreparedStatement retrieveFullFile;
   PreparedStatement makeDirectory;
   PreparedStatement makeDrive;
   PreparedStatement createFile;
   PreparedStatement createFileBatch;
   PreparedStatement createHash;
   PreparedStatement findFile;
   PreparedStatement findFileSimple;
   PreparedStatement addHistory;
   PreparedStatement dumpHistory;
   PreparedStatement listOfChildren;
   PreparedStatement getSearchFile;
   PreparedStatement getDrives;
   PreparedStatement getPath;
   PreparedStatement deleteFile;
   PreparedStatement deleteDirectory;
   PreparedStatement deleteDrive;
   int createdFileCount;
   boolean autoGenerated;
   final Object PENDING_REQUESTS;
   Set<Long> pendingRequests;
   PreparedStatement[] Statements;
   final Object PENDING_FINDS;
   List<FileSystemModel.FindFile> pendingFinds;
   final FileSystemModel.FindFile2 FINDFILE2;
   final Object PENDING_FILES;
   List<FileObject> pendingSaves;
   List<Long> pendingFileAttributes;
   List<Long> pendingDirectories;
   List<Long> pendingDrives;
   List<Long> pendingDeletes;
   final FileSystemModel.SaveFile2 SAVEFILE2;
   List<FileSystemModel.AddHistoryItem> pendingHistory;
   final Object PENDING_GETNODEFORPATH;
   List<FileSystemModel.GetNodeForPath> pendingGetNodeForPath;
   final FileSystemModel.GetNodeForPath2 GETNODEFORPATH2;
   final Object PENDING_TRANSLATION;
   Set<Long> pendingTranslations;
   private boolean needsInit;
   private File tempDatabase;
   private final Map<Task, Long> furthestOrdinal;
   private final Runnable CreateDatabase;
   private final Runnable ConnectToDatabase;
   private final Runnable SendNotifications;
   private MutableTabbableStatus tabbableStatus;
   private final DirCrawler dirCrawler;
   FileSystemModel.PathRecord pathToId2;
   private final Set<Long> mergeIds;
   private boolean mergeRunning;

   public FileSystemModel(CoreController var1, String var2) throws Exception {
      this.status = FileSystemModel.Status.Starting;
      this.listeners = new EventListenerList();
      this.UPDATED_FILES_LOCK = new Object();
      this.updatedFiles2 = new ArrayList();
      this.FileObjectComparator = new Comparator<FileObject>() {
         public int compare(FileObject var1, FileObject var2) {
            return var1.getId().intValue() - var2.getId().intValue();
         }
      };
      this.createdFileCount = 0;
      this.autoGenerated = false;
      this.PENDING_REQUESTS = new Object();
      this.pendingRequests = new HashSet();
      this.Statements = new PreparedStatement[FileObjectFields.values().length];
      this.PENDING_FINDS = new Object();
      this.pendingFinds = new Vector();
      this.FINDFILE2 = new FileSystemModel.FindFile2();
      this.PENDING_FILES = new Object();
      this.pendingSaves = new Vector();
      this.pendingFileAttributes = new Vector();
      this.pendingDirectories = new Vector();
      this.pendingDrives = new Vector();
      this.pendingDeletes = new Vector();
      this.SAVEFILE2 = new FileSystemModel.SaveFile2();
      this.pendingHistory = new Vector();
      this.PENDING_GETNODEFORPATH = new Object();
      this.pendingGetNodeForPath = new Vector();
      this.GETNODEFORPATH2 = new FileSystemModel.GetNodeForPath2();
      this.PENDING_TRANSLATION = new Object();
      this.pendingTranslations = new HashSet();
      this.needsInit = true;
      this.tempDatabase = null;
      this.furthestOrdinal = new HashMap();
      this.CreateDatabase = new Runnable() {
         public void run() {
            try {
               File var1 = new File(FileSystemModel.this.core.getUserConfigDirectory(), "FileBrowser");
               var1.mkdirs();
               Calendar var2 = Calendar.getInstance();
               FileSystemModel.this.tempDatabase = new File(var1, String.format("%s_%04d_%02d_%02d_%02dh%02dm%02ds.%03d.db", FileSystemModel.this.hostId, var2.get(1), var2.get(2) + 1, var2.get(5), var2.get(11), var2.get(12), var2.get(13), var2.get(14)));
               FileSystemModel.this.tempDatabase.deleteOnExit();
               Class var3 = Class.forName("org.sqlite.JDBC");
               if (FileSystemModel.this.tempDatabase.exists()) {
                  FileSystemModel.this.needsInit = false;
               }

               if (FileSystemModel.this.core.isLiveOperation()) {
                  FileSystemModel.this.core.setCommandEnvironmentVariable("File_Database", FileSystemModel.this.tempDatabase.getName(), FileSystemModel.this.core.getHostById(FileSystemModel.this.hostId));
               }
            } catch (Exception var4) {
               var4.printStackTrace();
            }

         }
      };
      this.ConnectToDatabase = new Runnable() {
         public void run() {
            try {
               FileSystemModel.this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:/%s", FileSystemModel.this.tempDatabase.getAbsolutePath()));
               String var1 = String.format("Connecting to %s with %s %s (%d.%d)", FileSystemModel.this.tempDatabase.getAbsolutePath(), FileSystemModel.this.connection.getMetaData().getDatabaseProductName(), FileSystemModel.this.connection.getMetaData().getDatabaseProductVersion(), FileSystemModel.this.connection.getMetaData().getDatabaseMajorVersion(), FileSystemModel.this.connection.getMetaData().getDatabaseMinorVersion());
               FileSystemModel.this.core.logEvent(Level.INFO, "FileSystemModel", var1);
               String[] var2 = new String[]{"PRAGMA synchronous=OFF", "PRAGMA cache_size=20000", "PRAGMA foreign_keys = ON", "PRAGMA ignore_check_constraints=true", "PRAGMA journal_mode=MEMORY"};
               Statement var3 = FileSystemModel.this.connection.createStatement();
               String[] var4 = var2;
               int var5 = var2.length;

               int var6;
               String var7;
               for(var6 = 0; var6 < var5; ++var6) {
                  var7 = var4[var6];
                  var3.execute(var7);
               }

               if (FileSystemModel.this.needsInit) {
                  FileSystemModel.this.needsInit = false;
                  var4 = FileSystemModel.SCHEMA;
                  var5 = var4.length;

                  for(var6 = 0; var6 < var5; ++var6) {
                     var7 = var4[var6];
                     var3.execute(var7);
                  }

                  var3.execute(String.format("INSERT INTO File (FileId, Parent) VALUES(%d, NULL)", FileSystemModel.ROOT));
                  var3.execute(String.format("INSERT INTO File (FileId, Parent) VALUES(%d, NULL)", FileSystemModel.DEAD));
                  var3.execute(String.format("INSERT INTO Directory (DirId) VALUES(%d)", FileSystemModel.ROOT));
               }

               FileSystemModel.this.connection.setAutoCommit(true);
               FileSystemModel.this.retrieveFile = FileSystemModel.this.connection.prepareStatement("Select *, (Select count(*) from File as FC where F.FileId = FC.Parent) as Children From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.FileId = ?", 1003, 1007);
               FileSystemModel.this.listOfChildren = FileSystemModel.this.connection.prepareStatement("Select FileId from File left outer join Directory on File.FileId = Directory.DirId Where Parent = ? Order By Directory.DirId ISNULL Collate Nocase, File.Name Collate Nocase", 1003, 1007);
               FileSystemModel.this.retrieveFullFile = FileSystemModel.this.connection.prepareStatement("SELECT *, (SELECT count(*) FROM File as FC where F.FileId = FC.Parent) as CHILDREN From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId LEFT OUTER JOIN FileAttributes ON F.FileId = FileAttributes.AttrId LEFT OUTER JOIN Hash ON F.FileId = Hash.HashId WHERE F.FileId = ?", 1003, 1007);
               FileSystemModel.this.directoryListing = FileSystemModel.this.connection.prepareStatement("Select FileId, Created, Accessed, Modified, Size, Name, Parent, DirId, (Select count(*) from File as FC where F.FileId = FC.Parent) as CHILDREN, Access_Denied, DriveId, Type From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.Parent = ? Order By Directory.DirId ISNULL Collate Nocase, F.Name", 1003, 1007);
               FileSystemModel.this.subdirectories = FileSystemModel.this.connection.prepareStatement("Select FileId, Name, Parent, DirId, (Select count(*) from File as FC where F.FileId = FC.Parent) as CHILDREN, DriveId, Type From Directory INNER JOIN File As F ON Directory.DirId = F.FileId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.Parent = ? Order By Directory.DirId ISNULL Collate Nocase, F.Name", 1003, 1007);
               FileSystemModel.this.makeDirectory = FileSystemModel.this.connection.prepareStatement("insert or ignore into Directory (DirId) Values (?)");
               FileSystemModel.this.makeDrive = FileSystemModel.this.connection.prepareStatement("insert or ignore into Drive (DriveId) Values (?)");
               FileSystemModel.this.addAttributes = FileSystemModel.this.connection.prepareStatement("insert or ignore into FileAttributes (AttrId) Values(?)");

               try {
                  FileSystemModel.this.createFile = FileSystemModel.this.connection.prepareStatement("insert into File (Name, Parent) values(?,?)", 1);
                  FileSystemModel.this.autoGenerated = true;
                  FileSystemModel.this.core.logEvent(Level.INFO, FileSystemModel.class.getSimpleName(), "Autogenerated keys are available");
               } catch (Exception var9) {
                  FileSystemModel.this.createFile = FileSystemModel.this.connection.prepareStatement("insert or ignore into File (Name, Parent) values(?,?)");
                  FileSystemModel.this.autoGenerated = false;
                  FileSystemModel.this.core.logEvent(Level.INFO, FileSystemModel.class.getSimpleName(), "Autogenerated keys are not available");
               }

               FileSystemModel.this.createFileBatch = FileSystemModel.this.connection.prepareStatement("insert or ignore into File (Name, Parent) values(?,?)");
               FileSystemModel.this.createHash = FileSystemModel.this.connection.prepareStatement("insert or ignore into Hash (HashId) values (?)");
               FileSystemModel.this.findFile = FileSystemModel.this.connection.prepareStatement("select * From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.parent = ? and (F.Name = ?)", 1003, 1007);
               FileSystemModel.this.findFileSimple = FileSystemModel.this.connection.prepareStatement("select FileId, Name, Parent, DirId, DriveId, Access_Denied From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.parent = ? and (F.Name = ?)", 1003, 1007);
               FileSystemModel.this.addHistory = FileSystemModel.this.connection.prepareStatement("Insert into History (FileId, Operation, TaskId, Action, Command, Time, Argument) values (?,?,?,?,?,?,?)");
               FileSystemModel.this.dumpHistory = FileSystemModel.this.connection.prepareStatement("SELECT * FROM History WHERE FileId = ? ORDER BY TaskId", 1003, 1007);
               FileSystemModel.this.getSearchFile = FileSystemModel.this.connection.prepareStatement("select * From File AS F LEFT OUTER JOIN Directory ON F.FileId = Directory.DirId LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId Where F.FileId = ?");
               FileSystemModel.this.getDrives = FileSystemModel.this.connection.prepareStatement("select * From Drive INNER JOIN Directory ON Drive.DriveId = Directory.DirId INNER JOIN File ON Drive.DriveId = File.FileId Where File.Parent = " + FileSystemModel.ROOT, 1003, 1007);
               FileSystemModel.this.getPath = FileSystemModel.this.connection.prepareStatement("Select FileId, Parent, Name From File Where FileId = ?", 1003, 1007);
               FileSystemModel.this.deleteFile = FileSystemModel.this.connection.prepareStatement("DELETE FROM File WHERE FileId = ?");
               FileSystemModel.this.deleteDirectory = FileSystemModel.this.connection.prepareStatement("DELETE FROM Directory WHERE DirId = ?");
               FileSystemModel.this.deleteDrive = FileSystemModel.this.connection.prepareStatement("DELETE FROM Drive WHERE DriveId = ?");
               new FileObject(FileSystemModel.this);
               FileObjectFields[] var11 = FileObjectFields.DirFields;
               var6 = var11.length;

               FileObjectFields var8;
               int var12;
               for(var12 = 0; var12 < var6; ++var12) {
                  var8 = var11[var12];
                  FileSystemModel.this.Statements[var8.ordinal()] = FileSystemModel.this.connection.prepareStatement(String.format("Update Directory SET %s=? WHERE DirId=?", var8.getName()));
               }

               var11 = FileObjectFields.DriveFields;
               var6 = var11.length;

               for(var12 = 0; var12 < var6; ++var12) {
                  var8 = var11[var12];
                  FileSystemModel.this.Statements[var8.ordinal()] = FileSystemModel.this.connection.prepareStatement(String.format("Update Drive SET %s=? WHERE DriveId=?", var8.getName()));
               }

               var11 = FileObjectFields.AttrFields;
               var6 = var11.length;

               for(var12 = 0; var12 < var6; ++var12) {
                  var8 = var11[var12];
                  FileSystemModel.this.Statements[var8.ordinal()] = FileSystemModel.this.connection.prepareStatement(String.format("Update FileAttributes SET %s=? WHERE AttrId=?", var8.getName()));
               }

               var11 = FileObjectFields.FileFields;
               var6 = var11.length;

               for(var12 = 0; var12 < var6; ++var12) {
                  var8 = var11[var12];
                  FileSystemModel.this.Statements[var8.ordinal()] = FileSystemModel.this.connection.prepareStatement(String.format("Update File SET %s=? WHERE FileId=?", var8.getName()));
               }

               var11 = FileObjectFields.HashFields;
               var6 = var11.length;

               for(var12 = 0; var12 < var6; ++var12) {
                  var8 = var11[var12];
                  FileSystemModel.this.Statements[var8.ordinal()] = FileSystemModel.this.connection.prepareStatement(String.format("Update Hash SET %s=? WHERE HashId=?", var8.getName()));
               }

               FileSystemModel.this.status = FileSystemModel.Status.Running;
            } catch (Exception var10) {
               var10.printStackTrace();
            }

         }
      };
      this.SendNotifications = new Runnable() {
         public void run() {
            FileSystemListener[] var1 = (FileSystemListener[])FileSystemModel.this.listeners.getListeners(FileSystemListener.class);
            List var2;
            synchronized(FileSystemModel.this.UPDATED_FILES_LOCK) {
               var2 = FileSystemModel.this.updatedFiles2;
               FileSystemModel.this.updatedFiles2 = new ArrayList();
            }

            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               FileObject var4 = (FileObject)var3.next();
               FileSystemListener[] var5 = var1;
               int var6 = var1.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  FileSystemListener var8 = var5[var7];

                  try {
                     var8.fileChanged(var4);
                  } catch (Exception var10) {
                     var10.printStackTrace();
                  }
               }
            }

         }
      };
      this.tabbableStatus = new TabbableStatusImpl((Tabbable)null);
      this.pathToId2 = new FileSystemModel.PathRecord(20);
      this.mergeIds = new TreeSet();
      this.mergeRunning = false;
      this.hostId = var2;
      this.core = var1;
      this.dirCrawler = new DirCrawler(var1, this);
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "cd", "Dsz", new CdListener(var1, this)));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "copy", "Dsz", new CopyListener(var1, this)));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "drives", "Dsz", new DrivesListener(var1, this)));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "get", "Dsz", new GetListener(var1, this)));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "move", "Dsz", new MoveListener(var1, this)));
      this.transformer.addClosure(ClosureFactory.newVariableClosure(var1, "pwd", "Dsz", new PwdListener(var1, this)));
      this.databaseExec.submit(this.CreateDatabase);
      this.databaseExec.submit(this.ConnectToDatabase);
      fileChangedExec.scheduleWithFixedDelay(this.SendNotifications, 1000L, 250L, TimeUnit.MILLISECONDS);
      this.databaseExec.scheduleWithFixedDelay(this.FINDFILE2, 250L, 250L, TimeUnit.MILLISECONDS);
      this.databaseExec.scheduleWithFixedDelay(this.SAVEFILE2, 250L, 250L, TimeUnit.MILLISECONDS);
      this.databaseExec.scheduleWithFixedDelay(this.GETNODEFORPATH2, 50L, 50L, TimeUnit.MILLISECONDS);
      this.addFileSystemListener(new FileSystemListener() {
         public void fileChanged(FileObject var1) {
            if (var1 != null) {
               if (var1.getParent() >= 0L) {
                  FileSystemModel.this.addMergeId(var1.getParent());
                  FileSystemModel.this.addMergeId(var1.getId());
               }
            }
         }
      });
   }

   public void setPendingData(int var1) {
      if (var1 > 0) {
         this.tabbableStatus.setDetails(String.format("Pending Dir Entries:  %d", var1));
      } else {
         this.tabbableStatus.setDetails("");
      }

      this.tabbableStatus.setIndeterminate(var1 > 0);
      this.tabbableStatus.notifyObservers();
   }

   public File getDatabaseFile() {
      return this.tempDatabase;
   }

   public TabbableStatus getTabbableStatus() {
      return this.tabbableStatus;
   }

   public void updateOrdinal(Task var1, Long var2) {
      if (var1 != null && var2 != null && var2 != -1L) {
         synchronized(this.furthestOrdinal) {
            Long var4 = (Long)this.furthestOrdinal.get(var1);
            if (var4 == null || var2 >= var4) {
               this.furthestOrdinal.put(var1, var2 + 1L);
            }
         }
      }
   }

   public String getOrdinalDetails() {
      long var1 = 0L;
      long var3 = 0L;
      synchronized(this.furthestOrdinal) {
         Task var7;
         for(Iterator var6 = this.furthestOrdinal.keySet().iterator(); var6.hasNext(); var3 += (Long)this.furthestOrdinal.get(var7)) {
            var7 = (Task)var6.next();
            var1 += (long)var7.getNextOrdinal();
         }

         return var1 == var3 ? null : String.format("Processed %d of %d records", var3, var1);
      }
   }

   public FileSystemModel.Status getStatus() {
      return this.status;
   }

   public void makeDirectory(long var1) {
      synchronized(this.PENDING_FILES) {
         this.pendingDirectories.add(var1);
      }
   }

   public void makeDrive(long var1) {
      synchronized(this.PENDING_FILES) {
         this.pendingDrives.add(var1);
      }
   }

   public void taskingDone(long var1) {
      synchronized(this.PENDING_REQUESTS) {
         this.pendingRequests.remove(var1);
      }

      this.fireFileChanged(var1);
   }

   public void taskingStarted(long var1) {
      synchronized(this.PENDING_REQUESTS) {
         this.pendingRequests.add(var1);
      }

      this.fireFileChanged(var1);
   }

   public void addTask(Task var1) {
      if (var1 != null && var1.getCommandName() != null && !var1.getCommandName().equals("")) {
         if (var1.getCommandName().equalsIgnoreCase("dir")) {
            this.dirCrawler.addTask(var1);
         }

         if (INTERESTING_TASKS.contains(var1.getDataName().toLowerCase())) {
            this.transformer.addTask(var1);
         }

      }
   }

   public void addHistoryItem(FileObject var1, TaskId var2, ActionType var3, CommandType var4, Calendar var5) {
      this.addHistoryItem(var1, var2, var3, var4, var5, (String)null);
   }

   public void addHistoryItem(FileObject var1, TaskId var2, ActionType var3, CommandType var4, Calendar var5, String var6) {
      if (var1 != null) {
         this.databaseExec.submit(new FileSystemModel.AddHistoryItem(var1, var2, var3, var4, var5, var6));
      }
   }

   public FileObject getChildNode(FileObject var1, String var2, boolean var3, CommandType var4, TaskId var5, Calendar var6, boolean var7, Closure var8) {
      return this.getChildNode(var1.getId(), var1.getPath(), var2, var3, var4, var5, var6, var7, var8);
   }

   public FileObject getChildNode(Long var1, String var2, String var3, boolean var4, CommandType var5, TaskId var6, Calendar var7, boolean var8, Closure var9) {
      FileSystemModel.FindFile var10 = new FileSystemModel.FindFile(var1, var3, var4, var6, var5, var7, var2, var8, var9);
      if (var9 != null) {
         synchronized(this.PENDING_FINDS) {
            this.pendingFinds.add(var10);
            return null;
         }
      } else {
         Future var11 = this.databaseExec.submit(var10);

         try {
            FileObject var12 = (FileObject)var11.get();
            return var12;
         } catch (Exception var14) {
            var14.printStackTrace();
            return null;
         }
      }
   }

   public FileObject getNodeForPath(String var1, boolean var2, CommandType var3, TaskId var4, Calendar var5, boolean var6, Closure var7) {
      if (var1 == null) {
         return null;
      } else {
         return !var1.startsWith("/") && !var1.startsWith("\\") ? this.getNodeForPath(ROOT, (String)null, Arrays.asList(var1.split("[\\\\/]")), var2, var3, var4, var5, var6, var7) : null;
      }
   }

   public FileObject getNodeForPath(Long var1, String var2, List<String> var3, boolean var4, CommandType var5, TaskId var6, Calendar var7, boolean var8, Closure var9) {
      if (var9 != null) {
         synchronized(this.PENDING_GETNODEFORPATH) {
            this.pendingGetNodeForPath.add(new FileSystemModel.GetNodeForPath(var1, var3, var4, var5, var6, var7, var8, var9));
            return null;
         }
      } else {
         Future var10 = this.databaseExec.submit(new FileSystemModel.GetNodeForPath(var1, var3, var4, var5, var6, var7, var8, var9));

         try {
            return (FileObject)var10.get();
         } catch (Exception var13) {
            var13.printStackTrace();
            return null;
         }
      }
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void addFileSystemListener(FileSystemListener var1) {
      this.listeners.add(FileSystemListener.class, var1);
   }

   public void removeFileSystemListener(FileSystemListener var1) {
      this.listeners.remove(FileSystemListener.class, var1);
   }

   void fireFileChanged(FileObject var1) {
      synchronized(this.UPDATED_FILES_LOCK) {
         int var3 = Collections.binarySearch(this.updatedFiles2, var1, this.FileObjectComparator);
         if (var3 < 0) {
            ++var3;
            var3 = -var3;
            this.updatedFiles2.add(var3, var1);
         }
      }
   }

   void fireFileChanged(Long var1) {
      this.databaseExec.submit(new FileSystemModel.GetFile(var1, false, "", new Closure() {
         public void execute(Object var1) {
            FileSystemModel.this.fireFileChanged((FileObject)FileObject.class.cast(var1));
         }
      }));
   }

   public FileObject getFile(long var1, String var3) {
      return this.getFile(var1, var3, (Closure)null);
   }

   public FileObject getFile(long var1, String var3, Closure var4) {
      Future var5 = this.databaseExec.submit(new FileSystemModel.GetFile(var1, false, var3, var4));
      if (var4 == null) {
         try {
            return (FileObject)var5.get();
         } catch (Exception var7) {
            var7.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public FileObject getFullFile(long var1, String var3) {
      return this.getFullFile(var1, var3, (Closure)null);
   }

   public FileObject getFullFile(long var1, String var3, Closure var4) {
      Future var5 = this.databaseExec.submit(new FileSystemModel.GetFile(var1, true, var3, var4));
      if (var4 == null) {
         try {
            return (FileObject)var5.get();
         } catch (Exception var7) {
            var7.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public List<FileObject> getDirectoryListing(Long var1, String var2) {
      Future var3 = this.databaseExec.submit(new FileSystemModel.GetList(var1, this.directoryListing, var2));

      try {
         return (List)var3.get();
      } catch (Exception var5) {
         var5.printStackTrace();
         return NULL_LIST;
      }
   }

   public List<Long> getChildren(Long var1, String var2, Closure var3) {
      Future var4 = this.databaseExec.submit(new FileSystemModel.GetChildren(var1, var3));
      if (var3 != null) {
         return null;
      } else {
         try {
            return (List)var4.get();
         } catch (Exception var6) {
            var6.printStackTrace();
            return Collections.emptyList();
         }
      }
   }

   public List<FileObject> getSubdirectories(Long var1, String var2) {
      Future var3 = this.databaseExec.submit(new FileSystemModel.GetList(var1, this.subdirectories, var2));

      try {
         return (List)var3.get();
      } catch (Exception var5) {
         var5.printStackTrace();
         return NULL_LIST;
      }
   }

   public FileObject getSearchFile(Long var1) {
      Future var2 = this.databaseExec.submit(new FileSystemModel.GetSearchFile(var1));

      try {
         return (FileObject)var2.get();
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public List<FileObject> getDrives() {
      Future var1 = this.databaseExec.submit(new FileSystemModel.GetDrives());

      try {
         return (List)var1.get();
      } catch (Exception var3) {
         var3.printStackTrace();
         return NULL_LIST;
      }
   }

   public List<FileObject> getArbitraryQuery(PreparedStatement var1) {
      Future var2 = this.databaseExec.submit(new FileSystemModel.ExecArbitrary(var1));

      try {
         return (List)var2.get();
      } catch (Exception var4) {
         var4.printStackTrace();
         return NULL_LIST;
      }
   }

   public void getPath(FileObject var1) {
      this.getPath(var1, (Closure)null);
   }

   public void getPath(FileObject var1, Closure var2) {
      try {
         this.databaseExec.submit(new FileSystemModel.GetPath(var1, var2));
      } catch (RejectedExecutionException var4) {
      }

   }

   public PreparedStatement prepareStatement(String var1) {
      Future var2 = this.databaseExec.submit(new FileSystemModel.PrepareStatement(var1));

      try {
         return (PreparedStatement)var2.get();
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void retrieveFileInformation(FileObject var1, Runnable var2) {
      try {
         this.databaseExec.submit(new FileSystemModel.GetFileInformation(var1, var2));
      } catch (RejectedExecutionException var4) {
      }

   }

   public void save(FileObject var1) {
      synchronized(this.PENDING_FILES) {
         this.pendingSaves.add(var1);
      }

      if (!var1.getDataElement(FileObjectFields.File_TranslatedName).hasValue()) {
         this.translate(var1);
      }

   }

   public void delete(FileObject var1) {
      synchronized(this.PENDING_FILES) {
         this.pendingDeletes.add(var1.getId());
      }
   }

   public void createAttributes(long var1) {
      synchronized(this.PENDING_FILES) {
         this.pendingFileAttributes.add(var1);
      }
   }

   public void recover(SQLException var1) {
      var1.printStackTrace();
      this.status = FileSystemModel.Status.Exception;
      this.databaseExec.submit(this.ConnectToDatabase);
   }

   void translate(FileObject var1) {
   }

   public void DumpHistory(FileObject var1, PrintStream var2) {
      this.DumpHistory(var1.getId(), System.out);
   }

   public void DumpHistory(Long var1, PrintStream var2) {
      this.databaseExec.submit(new FileSystemModel.DumpHistory(var1, var2));
   }

   private void Dump(String var1) {
      (new FileSystemModel.Dumper(var1)).run();
   }

   private void addMergeId(Long var1) {
      if (var1 != null) {
         synchronized(this.mergeIds) {
            this.mergeIds.add(var1);
            if (!this.mergeRunning) {
               this.core.submit(new FileSystemModel.MergeDirectories());
               this.mergeRunning = true;
            }

         }
      }
   }

   private Long getNextMergeId() {
      synchronized(this.mergeIds) {
         if (this.mergeIds.size() == 0) {
            return null;
         } else {
            Long var2 = (Long)this.mergeIds.iterator().next();
            if (var2 == null) {
               return null;
            } else {
               this.mergeIds.remove(var2);
               return var2;
            }
         }
      }
   }

   private void doneMerge() {
      synchronized(this.mergeIds) {
         if (!this.mergeIds.isEmpty()) {
            this.core.submit(new FileSystemModel.MergeDirectories());
         } else {
            this.mergeRunning = false;
         }

      }
   }

   static {
      onFinishExec = new ThreadPoolExecutor(20, Integer.MAX_VALUE, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue(), UtilityConstants.createThreadFactory("FileSystemModel - onFinishExec"));
      INTERESTING_TASKS.add("cd");
      INTERESTING_TASKS.add("copy");
      INTERESTING_TASKS.add("drives");
      INTERESTING_TASKS.add("get");
      INTERESTING_TASKS.add("move");
      INTERESTING_TASKS.add("pwd");
      SCHEMA = new String[]{"create Table File (\tFILEID INTEGER PRIMARY KEY ON CONFLICT IGNORE AUTOINCREMENT,PARENT INTEGER DEFAULT -1,\tNAME TEXT NOT NULL DEFAULT '.' COLLATE NOCASE,\tTRANSLATED_NAME TEXT COLLATE NOCASE,\tALT_NAME TEXT COLLATE NOCASE,\tSIZE INTEGER,\tCREATED TEXT,\tMODIFIED TEXT,\tACCESSED TEXT, UNIQUE (Parent, Name) ON CONFLICT IGNORE, CONSTRAINT Fk_Parent FOREIGN KEY (Parent) REFERENCES File(FileId));", "create Table Directory (\tDIRID INTEGER PRIMARY KEY ON CONFLICT IGNORE,\tLAST_PARTIAL TEXT,\tLAST_FULL TEXT,\tACCESS_DENIED INTEGER,\tCONSTRAINT Fk_Dir_Id FOREIGN KEY (DirId) REFERENCES File(FileId));", "create Table Drive (\tDRIVEID INTEGER PRIMARY KEY ON CONFLICT IGNORE,\tSOURCE TEXT,\tTYPE INTEGER,\tSERIAL TEXT,\tFILESYSTEM TEXT,\tOPTIONS TEXT,\tCONSTRAINT Fk_Drive_Id FOREIGN KEY (DriveId) REFERENCES File(FileId));", "Create Table FileAttributes (\tAttrId\t\t\t\t\tINTEGER PRIMARY KEY ON CONFLICT REPLACE,\tArchive\t\t\t\t\tINTEGER,\tCompressed\t\t\t\tINTEGER,\tEncrypted\t\t\t\tINTEGER,\tHidden\t\t\t\t\tINTEGER,\tOffline\t\t\t\t\tINTEGER,\tReadOnly\t\t\t\tINTEGER,\tReparsePoint\t\t\tINTEGER,\tSparseFile\t\t\t\tINTEGER,\tSystem\t\t\t\t\tINTEGER,\tTemporary\t\t\t\tINTEGER,\tNotContentIndexed\t\tINTEGER,\tDevice\t\t\t\t\tINTEGER,\tOwnerRead\t\t\t\tINTEGER,\tOwnerWrite\t\t\t\tINTEGER,\tOwnerExec\t\t\t\tINTEGER,\tGroupRead\t\t\t\tINTEGER,\tGroupWrite\t\t\t\tINTEGER,\tGroupExec\t\t\t\tINTEGER,\tWorldRead\t\t\t\tINTEGER,\tWorldWrite\t\t\t\tINTEGER,\tWorldExec\t\t\t\tINTEGER,\tSetUid\t\t\t\t\tINTEGER,\tSetGid\t\t\t\t\tINTEGER,\tStickyBit\t\t\t\tINTEGER,\tOwner_Name\t\t\t\tTEXT,\tOwner_Id\t\t\t\tINTEGER,\tGroup_Name\t\t\t\tTEXT,\tGroup_Id\t\t\t\tINTEGER,\tCharacterSpecialFile\tINTEGER,\tBlockSpecialFile\t\tINTEGER,\tUnixFamilySocket\t\tINTEGER,\tNamedPipe\t\t\t\tINTEGER,\tSymbolicLink\t\t\tINTEGER,\tHardLinks\t\t\t\tINTEGER,\tinode\t\t\t\t\tINTEGER,\tCONSTRAINT Fk_Attr_Id FOREIGN KEY (AttrId) REFERENCES File(FileId));", "create Table Hash (\tHASHID\tINTEGER PRIMARY KEY ON CONFLICT IGNORE,\tSHA1\tTEXT,\tMD5\t\tTEXT,\tSHA256\tTEXT,\tSHA512\tTEXT,\tCONSTRAINT Fk_Drive_Id FOREIGN KEY (HashId) REFERENCES File(FileId));", "Create Table History (\tFILEID INTEGER,\tTASKID INTEGER,   OPERATION STRING,\tACTION INTEGER,\tCOMMAND INTEGER,\tTIME TEXT,\tARGUMENT TEXT,\tCONSTRAINT Pk_History_Table PRIMARY KEY (FileId, Operation, TaskId) ON CONFLICT IGNORE );"};
      ROOT = new Long(0L);
      HOSTS = new Long(-1L);
      DEAD = new Long(-2L);
      fileChangedExec = new ScheduledThreadPoolExecutor(1, UtilityConstants.createThreadFactory("FileSystemModel - fileChangedExec"));
   }

   private class MergeDirectories implements Runnable {
      private MergeDirectories() {
      }

      public void run() {
         try {
            Long var1 = FileSystemModel.this.getNextMergeId();
            if (var1 != null) {
               List var2 = FileSystemModel.this.getChildren(var1, (String)null, (Closure)null);
               Collections.sort(var2);
               Vector var3 = new Vector(var2.size());
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  Long var5 = (Long)var4.next();
                  var3.add(FileSystemModel.this.getFullFile(var5, (String)null));
               }

               var4 = var3.iterator();

               while(var4.hasNext()) {
                  FileObject var11 = (FileObject)var4.next();
                  Vector var6 = new Vector();
                  var6.addAll(var3);
                  var6.remove(var11);
                  FileObject var7 = this.findMatch(var6, var11.getName(), (String)var11.getDataElement(FileObjectFields.File_AlternateName).getValue());
                  if (var11 != var7 && var7 != null && var11.getParent() == var7.getParent()) {
                     if (var11.getDataElement(FileObjectFields.File_AlternateName).hasValue()) {
                        this.merge(var7, var11);
                     } else {
                        this.merge(var11, var7);
                     }
                  }
               }

               return;
            }
         } finally {
            FileSystemModel.this.doneMerge();
         }

      }

      private FileObject findMatch(List<FileObject> var1, String var2, String var3) {
         Iterator var4 = var1.iterator();

         FileObject var5;
         String var6;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (FileObject)var4.next();
            var6 = var5.getDataElement(FileObjectFields.File_AlternateName).hasValue() ? var5.getDataElement(FileObjectFields.File_AlternateName).getValue().toString() : null;
            if (var6 != null && var6.trim().length() == 0) {
               var6 = null;
            }

            if (var2 != null && var2.trim().length() > 0 && (var2.equalsIgnoreCase(var5.getName()) || var2.equalsIgnoreCase(var6))) {
               return var5;
            }
         } while(var3 == null || var3.trim().length() <= 0 || !var3.equalsIgnoreCase(var5.getName()) && !var3.equalsIgnoreCase(var6));

         return var5;
      }

      private void merge(FileObject var1, FileObject var2) {
         if (var1.getParent() == var2.getParent()) {
            FileObjectFields[] var3 = FileObjectFields.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               FileObjectFields var6 = var3[var5];
               if (!var2.getDataElement(var6).hasValue() && var1.getDataElement(var6).hasValue()) {
                  var2.getDataElement(var6).setValue(var1.getDataElement(var6).getValue());
               }
            }

            this.adoptChildren(var1, var2);
            FileSystemModel.this.save(var2);
            var1.getDataElement(FileObjectFields.File_Parent).setValue(FileSystemModel.DEAD, true);
            var1.setName(String.format("%d", var1.getId()));
            FileSystemModel.this.save(var1);
         }
      }

      private void adoptChildren(FileObject var1, FileObject var2) {
         List var3 = FileSystemModel.this.getChildren(var1.getId(), (String)null, (Closure)null);
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Long var5 = (Long)var4.next();
            FileObject var6 = FileSystemModel.this.getFile(var5, (String)null);
            if (var6 != null) {
               var6.setParent(var2.getId());
               FileSystemModel.this.save(var6);
            }
         }

      }

      // $FF: synthetic method
      MergeDirectories(Object var2) {
         this();
      }
   }

   private class Dumper implements Runnable {
      String table;

      public Dumper(String var2) {
         this.table = var2;
      }

      public void run() {
         try {
            Statement var1 = FileSystemModel.this.connection.createStatement();

            try {
               ResultSet var2 = var1.executeQuery(String.format("Select * from %s", this.table));

               try {
                  System.out.println("------------------------------------------------");
                  System.out.printf("-----------------%s DUMP-----------------------", this.table);
                  System.out.println("------------------------------------------------");

                  int var3;
                  for(var3 = 1; var3 <= var2.getMetaData().getColumnCount(); ++var3) {
                     System.out.printf("%-10s", var2.getMetaData().getColumnName(var3));
                     System.out.print("\t");
                  }

                  System.out.println();
                  System.out.println("------------------------------------------------");

                  while(var2.next()) {
                     for(var3 = 1; var3 <= var2.getMetaData().getColumnCount(); ++var3) {
                        System.out.printf("%10s", var2.getString(var3));
                        System.out.print("\t");
                     }

                     System.out.println();
                  }

                  System.out.println("------------------------------------------------");
               } finally {
                  var2.close();
               }
            } finally {
               var1.close();
            }
         } catch (SQLException var14) {
            var14.printStackTrace();
         }

      }
   }

   private class GetPath extends FileSystemModel.DatabaseAction<Object> {
      FileObject fo;
      Closure onFinish;

      public GetPath(FileObject var2, Closure var3) {
         super(FileSystemModel.Priority.QUERY);
         this.fo = var2;
         this.onFinish = var3;
      }

      public String call() throws Exception {
         FileSystemModel.this.connection.setAutoCommit(false);

         Object var3;
         try {
            String var1 = null;

            try {
               FileObject var4;
               for(long var2 = this.fo.getParent(); var2 != FileSystemModel.ROOT; var2 = var4.getParent()) {
                  var4 = null;
                  FileSystemModel.this.getPath.setInt(1, Long.valueOf(var2).intValue());
                  ResultSet var5 = FileSystemModel.this.getPath.executeQuery();
                  List var6 = FileObject.getFiles(FileSystemModel.this, var5, var1, 1);
                  if (var6.size() <= 0) {
                     Object var7 = null;
                     return (String)var7;
                  }

                  var4 = (FileObject)var6.get(0);
                  if (var1 == null) {
                     var1 = var4.getName();
                  } else {
                     var1 = String.format("%s/%s", var4.getName(), var1);
                  }
               }

               if (this.onFinish != null) {
                  FileSystemModel.onFinishExec.submit(FileSystemModel.this.new ExecuteClosure(this.fo, this.onFinish));
               }

               var4 = null;
               return String.valueOf(var4);
            } catch (SQLException var16) {
               FileSystemModel.this.recover(var16);
               var3 = null;
            } finally {
               this.fo.setPath(var1);
            }
         } finally {
            FileSystemModel.this.connection.setAutoCommit(true);
         }

         return (String)var3;
      }
   }

   private class ExecuteClosure implements Runnable {
      Object obj;
      Closure closure;

      public ExecuteClosure(Object var2, Closure var3) {
         this.obj = var2;
         this.closure = var3;
      }

      public void run() {
         this.closure.execute(this.obj);
      }
   }

   private class PrepareStatement extends FileSystemModel.DatabaseAction<PreparedStatement> {
      String query;

      public PrepareStatement(String var2) {
         super(FileSystemModel.Priority.QUERY);
         this.query = var2;
      }

      public PreparedStatement call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            try {
               return FileSystemModel.this.connection.prepareStatement(this.query);
            } catch (SQLException var2) {
               FileSystemModel.this.recover(var2);
               return null;
            }
         }
      }
   }

   private class ExecArbitrary extends FileSystemModel.DatabaseAction<List<FileObject>> {
      PreparedStatement statement;

      public ExecArbitrary(PreparedStatement var2) {
         super(FileSystemModel.Priority.QUERY);
         this.statement = var2;
      }

      public List<FileObject> call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            try {
               ResultSet var1 = this.statement.executeQuery();
               return FileObject.getFiles(FileSystemModel.this, var1, (String)null);
            } catch (SQLException var2) {
               FileSystemModel.this.recover(var2);
               return FileSystemModel.NULL_LIST;
            }
         }
      }
   }

   private class GetNodeForPath2 implements Runnable {
      private GetNodeForPath2() {
      }

      public void run() {
         if (FileSystemModel.this.status != FileSystemModel.Status.Exception) {
            try {
               FileSystemModel.GetNodeForPath[] var1 = null;
               synchronized(FileSystemModel.this.PENDING_GETNODEFORPATH) {
                  var1 = (FileSystemModel.GetNodeForPath[])FileSystemModel.this.pendingGetNodeForPath.toArray(new FileSystemModel.GetNodeForPath[FileSystemModel.this.pendingGetNodeForPath.size()]);
                  FileSystemModel.this.pendingGetNodeForPath.clear();
               }

               if (var1 == null || var1.length == 0) {
                  return;
               }

               this.get(var1);
            } catch (SQLException var5) {
               FileSystemModel.this.recover(var5);
            } catch (Throwable var6) {
               var6.printStackTrace();
            }

         }
      }

      private void get(FileSystemModel.GetNodeForPath[] var1) throws SQLException {
         FileSystemModel.this.connection.setAutoCommit(false);

         try {
            FileSystemModel.GetNodeForPath[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               FileSystemModel.GetNodeForPath var5 = var2[var4];

               try {
                  var5.call();
               } catch (Exception var10) {
                  var10.printStackTrace();
               }
            }
         } finally {
            FileSystemModel.this.connection.setAutoCommit(true);
         }

      }

      // $FF: synthetic method
      GetNodeForPath2(Object var2) {
         this();
      }
   }

   private class GetNodeForPath extends FileSystemModel.DatabaseAction<FileObject> {
      List<String> path;
      boolean create;
      CommandType cmd;
      TaskId taskId;
      Calendar timestamp;
      Closure onFinish;

      public GetNodeForPath(Long var2, List<String> var3, boolean var4, CommandType var5, TaskId var6, Calendar var7, boolean var8, Closure var9) {
         super(FileSystemModel.Priority.QUERY);
         this.path = var3;
         this.create = var4;
         this.cmd = var5;
         this.taskId = var6;
         this.timestamp = var7;
         this.onFinish = var9;
      }

      public FileObject call() throws Exception {
         if (this.path == null) {
            return null;
         } else {
            try {
               long var1 = FileSystemModel.ROOT;
               FileObject var3 = null;
               int var4 = 0;

               int var5;
               for(var5 = this.path.size() - 1; var5 > 0; --var5) {
                  Long var6 = FileSystemModel.this.pathToId2.GetPath(this.path.subList(0, var5));
                  if (var6 != null) {
                     var1 = var6;
                     var4 = var5;
                     break;
                  }
               }

               for(var5 = var4; var5 < this.path.size(); ++var5) {
                  String var11 = (String)this.path.get(var5);
                  FileObject var7 = this.getFile(var11, var1);
                  if (var7 == null) {
                     if (!this.create) {
                        return null;
                     }

                     var3 = this.createFile(var11, var1);
                     FileSystemModel.this.addHistoryItem(var3, this.taskId, ActionType.CREATED, this.cmd, this.timestamp);
                     if (var3 == null) {
                        return null;
                     }

                     FileSystemModel.this.fireFileChanged(var3);
                     FileSystemModel.this.translate(var3);
                  } else {
                     var3 = var7;
                  }

                  FileSystemModel.this.pathToId2.AddPath(this.path.subList(0, var5), var1);
                  var1 = var3.getId();
                  if (var5 + 1 < this.path.size()) {
                     var3.setDirectory();
                  }
               }

               if (var3 == null) {
                  return null;
               } else {
                  String var12 = null;
                  Iterator var13 = this.path.iterator();

                  while(var13.hasNext()) {
                     String var14 = (String)var13.next();
                     if (var12 == null) {
                        var12 = var14 + "/";
                     } else {
                        var12 = var12 + var14 + "/";
                     }
                  }

                  var3.setPath(var12);
                  synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                     var3.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var3.getId()));
                  }

                  if (this.onFinish != null) {
                     FileSystemModel.onFinishExec.submit(FileSystemModel.this.new ExecuteClosure(var3, this.onFinish));
                     return null;
                  } else {
                     return var3;
                  }
               }
            } catch (SQLException var10) {
               FileSystemModel.this.recover(var10);
               return null;
            }
         }
      }

      FileObject getFile(String var1, long var2) throws SQLException {
         FileSystemModel.this.findFileSimple.setLong(1, var2);
         FileSystemModel.this.findFileSimple.setString(2, var1);
         ResultSet var4 = FileSystemModel.this.findFileSimple.executeQuery();
         List var5 = FileObject.getFiles(FileSystemModel.this, var4, (String)null, 1);
         if (var5.size() != 0) {
            FileObject var6 = (FileObject)var5.get(0);
            FileSystemModel.this.createAttributes(var6.getId());
            return var6;
         } else {
            return null;
         }
      }

      public FileObject createFile(String var1, long var2) throws SQLException {
         if (!this.create) {
            return null;
         } else {
            FileSystemModel.this.createFile.setString(1, var1);
            FileSystemModel.this.createFile.setLong(2, var2);
            if (FileSystemModel.this.autoGenerated) {
               FileSystemModel.this.createFile.executeUpdate();
               ResultSet var4 = FileSystemModel.this.createFile.getGeneratedKeys();

               try {
                  if (var4.next()) {
                     ++FileSystemModel.this.createdFileCount;
                     FileObject var5 = new FileObject(FileSystemModel.this, (long)var4.getInt(1));
                     var5.setDataElement((FileObjectFields)FileObjectFields.File_Parent, var2);
                     var5.setDataElement((FileObjectFields)FileObjectFields.File_Name, var1);
                     FileObject var6 = var5;
                     return var6;
                  }
               } finally {
                  var4.close();
               }
            } else {
               FileSystemModel.this.createFile.executeUpdate();
               if (FileSystemModel.this.createFile.getUpdateCount() > 0) {
                  ++FileSystemModel.this.createdFileCount;
                  return this.getFile(var1, var2);
               }
            }

            return null;
         }
      }
   }

   private class PathRecord {
      final int maximumLength;
      final Long[] ArrayOfIds;
      final int[] ArrayOfHashes;
      final List<?>[] ArrayOfPaths;
      int index;
      int length;

      public PathRecord(int var2) {
         this.index = 0;
         this.length = 0;
         this.maximumLength = var2;
         this.ArrayOfIds = new Long[this.maximumLength];
         this.ArrayOfHashes = new int[this.maximumLength];
         this.ArrayOfPaths = new List[this.maximumLength];
      }

      public PathRecord() {
         this(100);
      }

      public synchronized void AddPath(List<String> var1, Long var2) {
         if (this.GetPath(var1, false) == null) {
            this.ArrayOfIds[this.index] = var2;
            this.ArrayOfHashes[this.index] = var1.hashCode();
            this.ArrayOfPaths[this.index] = var1;
            ++this.index;
            if (this.index >= this.maximumLength) {
               this.index = 0;
            } else if (this.index > this.length) {
               this.length = this.index;
            }

         }
      }

      public Long GetPath(List<String> var1) {
         return this.GetPath(var1, true);
      }

      public synchronized Long GetPath(List<String> var1, boolean var2) {
         int var3 = var1.hashCode();

         for(int var4 = 0; var4 < this.length; ++var4) {
            if (this.ArrayOfHashes[var4] == var3 && var1.equals(this.ArrayOfPaths[var4])) {
               return this.ArrayOfIds[var4];
            }
         }

         return null;
      }
   }

   private class GetDrives extends FileSystemModel.DatabaseAction<List<FileObject>> {
      public GetDrives() {
         super(FileSystemModel.Priority.QUERY);
      }

      public List<FileObject> call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            try {
               ResultSet var1 = FileSystemModel.this.getDrives.executeQuery();
               List var2 = FileObject.getFiles(FileSystemModel.this, var1, (String)null);
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  FileObject var4 = (FileObject)var3.next();
                  synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                     var4.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var4.getId()));
                  }
               }

               return var2;
            } catch (SQLException var8) {
               FileSystemModel.this.recover(var8);
               return FileSystemModel.NULL_LIST;
            }
         }
      }
   }

   private class GetSearchFile extends FileSystemModel.DatabaseAction<FileObject> {
      long id;

      public GetSearchFile(long var2) {
         super(FileSystemModel.Priority.QUERY);
         this.id = var2;
      }

      public FileObject call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            try {
               ResultSet var1 = null;
               FileSystemModel.this.getSearchFile.setLong(1, this.id);
               var1 = FileSystemModel.this.getSearchFile.executeQuery();
               List var2 = FileObject.getFiles(FileSystemModel.this, var1, (String)null, 1);
               if (var2.size() > 0) {
                  FileObject var3 = (FileObject)var2.get(0);
                  FileSystemModel.this.getPath(var3);
                  synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                     var3.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var3.getId()));
                  }

                  return var3;
               } else {
                  return null;
               }
            } catch (SQLException var7) {
               FileSystemModel.this.recover(var7);
               return null;
            }
         }
      }
   }

   private class SaveFile2 implements Runnable {
      private SaveFile2() {
      }

      public void run() {
         if (FileSystemModel.this.status != FileSystemModel.Status.Exception) {
            try {
               List var1 = null;
               List var2 = null;
               List var3 = null;
               List var4 = null;
               List var5 = null;
               synchronized(FileSystemModel.this.PENDING_FILES) {
                  var4 = FileSystemModel.this.pendingDirectories;
                  var3 = FileSystemModel.this.pendingDrives;
                  var2 = FileSystemModel.this.pendingFileAttributes;
                  var1 = FileSystemModel.this.pendingSaves;
                  var5 = FileSystemModel.this.pendingDeletes;
                  FileSystemModel.this.pendingDirectories = new Vector();
                  FileSystemModel.this.pendingDrives = new Vector();
                  FileSystemModel.this.pendingFileAttributes = new Vector();
                  FileSystemModel.this.pendingSaves = new Vector();
                  FileSystemModel.this.pendingDeletes = new Vector();
               }

               try {
                  if (var1 != null && var1.size() > 0) {
                     this.save(var1);
                  }

                  if (var4 != null && var4.size() > 0) {
                     this.directory(var4);
                  }

                  if (var3 != null && var3.size() > 0) {
                     this.drive(var3);
                  }

                  if (var2 != null && var2.size() > 0) {
                     this.createAttributes(var2);
                  }

                  if (var5 != null && var5.size() > 0) {
                     this.delete(var5);
                  }
               } finally {
                  ;
               }
            } catch (SQLException var15) {
               FileSystemModel.this.recover(var15);
            } catch (Throwable var16) {
               var16.printStackTrace();
            }

         }
      }

      private void helper(List<Long> var1, PreparedStatement var2) throws SQLException {
         Collections.sort(var1);
         FileSystemModel.this.connection.setAutoCommit(false);

         try {
            long var3 = -1L;
            Iterator var5 = var1.iterator();

            while(var5.hasNext()) {
               Long var6 = (Long)var5.next();
               if (var6 != null && var6 != var3) {
                  var3 = var6;
                  var2.setLong(1, var6);
                  var2.addBatch();
               }
            }

            var2.executeBatch();
         } finally {
            FileSystemModel.this.connection.setAutoCommit(true);
         }

      }

      private void delete(List<Long> var1) throws SQLException {
         Collections.sort(var1, Collections.reverseOrder());
         PreparedStatement[] var2 = new PreparedStatement[]{FileSystemModel.this.deleteDrive, FileSystemModel.this.deleteDirectory, FileSystemModel.this.deleteFile};
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PreparedStatement var5 = var2[var4];
            FileSystemModel.this.connection.setAutoCommit(false);

            try {
               long var6 = -1L;
               Iterator var8 = var1.iterator();

               while(var8.hasNext()) {
                  Long var9 = (Long)var8.next();
                  if (var9 != null && var9 != var6) {
                     var6 = var9;
                     var5.setLong(1, var9);
                     var5.addBatch();
                  }
               }
            } finally {
               var5.executeBatch();
               FileSystemModel.this.connection.setAutoCommit(true);
            }
         }

         this.helper(var1, FileSystemModel.this.deleteDrive);
         this.helper(var1, FileSystemModel.this.deleteDirectory);
         this.helper(var1, FileSystemModel.this.deleteFile);
      }

      private void directory(List<Long> var1) throws SQLException {
         this.helper(var1, FileSystemModel.this.makeDirectory);
      }

      private void drive(List<Long> var1) throws SQLException {
         this.helper(var1, FileSystemModel.this.makeDrive);
      }

      private void createAttributes(List<Long> var1) throws SQLException {
         this.helper(var1, FileSystemModel.this.addAttributes);
      }

      private void save(List<FileObject> var1) throws SQLException {
         FileSystemModel.this.connection.setAutoCommit(false);

         try {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               FileObject var3 = (FileObject)var2.next();
               long var4 = var3.getId();
               boolean var6 = false;
               FileObjectFields[] var7 = FileObjectFields.HashFields;
               int var8 = var7.length;

               int var9;
               FileObjectFields var10;
               Data var11;
               for(var9 = 0; var9 < var8; ++var9) {
                  var10 = var7[var9];
                  var11 = var3.getDataElement(var10);
                  if (var11.hasValue() && var11.isModified()) {
                     var6 = true;
                  }
               }

               if (var6) {
                  FileSystemModel.this.createHash.setLong(1, var3.getId());
                  FileSystemModel.this.createHash.addBatch();
               }

               var7 = FileObjectFields.values();
               var8 = var7.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  var10 = var7[var9];
                  var11 = var3.getDataElement(var10);
                  if (var11.isModified() && var11.hasValue()) {
                     PreparedStatement var12 = FileSystemModel.this.Statements[var10.ordinal()];
                     if (var12 != null) {
                        var12.setLong(2, var4);
                        var11.prepare(var12, 1);
                        var12.addBatch();
                     }
                  }
               }
            }

            FileSystemModel.this.createHash.executeBatch();
            FileSystemModel.this.createHash.clearBatch();
            PreparedStatement[] var19 = FileSystemModel.this.Statements;
            int var20 = var19.length;

            for(int var21 = 0; var21 < var20; ++var21) {
               PreparedStatement var5 = var19[var21];
               if (var5 != null) {
                  var5.executeBatch();
                  var5.clearBatch();
               }
            }
         } finally {
            FileSystemModel.this.connection.setAutoCommit(true);
         }

         synchronized(FileSystemModel.this.UPDATED_FILES_LOCK) {
            if (FileSystemModel.this.updatedFiles2.size() == 0) {
               FileSystemModel.this.updatedFiles2 = var1;
            } else {
               FileSystemModel.this.updatedFiles2.addAll(var1);
               Collections.sort(FileSystemModel.this.updatedFiles2, FileSystemModel.this.FileObjectComparator);
            }

         }
      }

      // $FF: synthetic method
      SaveFile2(Object var2) {
         this();
      }
   }

   private class GetList extends FileSystemModel.DatabaseAction<List<FileObject>> {
      Long id;
      PreparedStatement query;
      String path;

      public GetList(Long var2, PreparedStatement var3, String var4) {
         super(FileSystemModel.Priority.QUERY);
         this.id = var2;
         this.query = var3;
         this.path = var4;
      }

      public List<FileObject> call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return FileSystemModel.NULL_LIST;
         } else {
            if (this.id != null) {
               try {
                  this.query.setLong(1, this.id);
                  List var1 = FileObject.getFiles(FileSystemModel.this, this.query.executeQuery(), this.path);
                  Iterator var2 = var1.iterator();

                  while(var2.hasNext()) {
                     FileObject var3 = (FileObject)var2.next();
                     synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                        var3.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var3.getId()));
                     }
                  }

                  return var1;
               } catch (SQLException var7) {
                  FileSystemModel.this.recover(var7);
               }
            }

            return new ArrayList();
         }
      }
   }

   private class GetFileInformation extends FileSystemModel.DatabaseAction<Object> {
      FileObject fo;
      Runnable exec;

      public GetFileInformation(FileObject var2, Runnable var3) {
         super(FileSystemModel.Priority.QUERY);
         this.fo = var2;
         this.exec = var3;
      }

      public Object call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else if (this.fo.isDiscard()) {
            return null;
         } else {
            try {
               FileSystemModel.this.retrieveFullFile.setLong(1, this.fo.getId());
               ResultSet var1 = FileSystemModel.this.retrieveFullFile.executeQuery();
               if (var1 != null) {
                  try {
                     if (var1.next()) {
                        this.fo.retrieve(var1);
                        synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                           this.fo.setPendingRequest(FileSystemModel.this.pendingRequests.contains(this.fo.getId()));
                        }
                     }
                  } finally {
                     var1.close();
                  }
               }
            } catch (SQLException var10) {
               FileSystemModel.this.recover(var10);
            }

            if (this.exec != null) {
               this.exec.run();
            }

            return null;
         }
      }
   }

   private class GetFile extends FileSystemModel.DatabaseAction<FileObject> {
      long id;
      boolean full;
      String path;
      Closure closure;

      public GetFile(long var2, boolean var4, String var5, Closure var6) {
         super(FileSystemModel.Priority.QUERY);
         this.id = var2;
         this.full = var4;
         this.path = var5;
         this.closure = var6;
      }

      public FileObject call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            try {
               PreparedStatement var1 = FileSystemModel.this.retrieveFullFile;
               if (!this.full) {
                  var1 = FileSystemModel.this.retrieveFile;
               }

               var1.setLong(1, this.id);
               ResultSet var2 = var1.executeQuery();
               List var3 = FileObject.getFiles(FileSystemModel.this, var2, this.path, 1);
               if (var3.size() > 0) {
                  FileObject var4 = (FileObject)var3.get(0);
                  synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                     var4.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var4.getId()));
                  }

                  if (this.closure == null) {
                     return var4;
                  }

                  this.closure.execute(var4);
               }

               return null;
            } catch (SQLException var8) {
               FileSystemModel.this.recover(var8);
               return null;
            }
         }
      }
   }

   private class GetChildren extends FileSystemModel.DatabaseAction<List<Long>> {
      Long id;
      Closure onFinish;

      public GetChildren(Long var2, Closure var3) {
         super(FileSystemModel.Priority.QUERY);
         this.id = var2;
         this.onFinish = var3;
      }

      public List<Long> call() throws Exception {
         if (this.id != null) {
            FileSystemModel.this.listOfChildren.setLong(1, this.id);
            ResultSet var1 = FileSystemModel.this.listOfChildren.executeQuery();
            if (var1 != null) {
               Vector var3;
               try {
                  Vector var2 = new Vector();

                  while(var1.next()) {
                     var2.add(var1.getLong(1));
                  }

                  if (this.onFinish != null) {
                     this.onFinish.execute(var2);
                  }

                  var3 = var2;
               } finally {
                  var1.close();
               }

               return var3;
            }
         }

         return Collections.emptyList();
      }
   }

   private class FindFile2 implements Runnable {
      private FindFile2() {
      }

      public void run() {
         try {
            FileSystemModel.FindFile[] var1 = null;
            synchronized(FileSystemModel.this.PENDING_FINDS) {
               var1 = (FileSystemModel.FindFile[])FileSystemModel.this.pendingFinds.toArray(new FileSystemModel.FindFile[FileSystemModel.this.pendingFinds.size()]);
               FileSystemModel.this.pendingFinds.clear();
            }

            FileSystemModel.this.connection.setAutoCommit(false);

            try {
               this.create(var1);
               this.find(var1);
            } finally {
               FileSystemModel.this.connection.setAutoCommit(true);
            }
         } catch (SQLException var10) {
            FileSystemModel.this.recover(var10);
         }

      }

      public void create(FileSystemModel.FindFile[] var1) throws SQLException {
         FileSystemModel.FindFile[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            FileSystemModel.FindFile var5 = var2[var4];
            if (var5.create) {
               FileSystemModel.this.createFileBatch.setString(1, var5.name);
               FileSystemModel.this.createFileBatch.setLong(2, var5.parentId);
               FileSystemModel.this.createFileBatch.addBatch();
            }
         }

         FileSystemModel.this.createFileBatch.executeBatch();
      }

      public void find(FileSystemModel.FindFile[] var1) throws SQLException {
         FileSystemModel.FindFile[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            FileSystemModel.FindFile var5 = var2[var4];
            FileObject var6 = var5.findFile();
            synchronized(FileSystemModel.this.PENDING_REQUESTS) {
               var6.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var6.getId()));
            }

            if (var6 != null && var5.onFinish != null) {
               FileSystemModel.onFinishExec.submit(FileSystemModel.this.new ExecuteClosure(var6, var5.onFinish));
            }
         }

      }

      // $FF: synthetic method
      FindFile2(Object var2) {
         this();
      }
   }

   private class FindFile extends FileSystemModel.DatabaseAction<FileObject> {
      long parentId;
      String name;
      boolean create;
      String path;
      boolean simple;
      Closure onFinish;

      public FindFile(long var2, String var4, boolean var5, TaskId var6, CommandType var7, Calendar var8, String var9, boolean var10, Closure var11) {
         super(FileSystemModel.Priority.QUERY);
         this.parentId = var2;
         this.name = var4;
         this.create = var5;
         this.path = var9;
         this.simple = var10;
         this.onFinish = var11;
      }

      public FileObject call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else {
            FileObject var1 = null;
            var1 = this.findFile();
            if (var1 != null) {
               return var1;
            } else {
               var1 = null;

               FileObject var2;
               try {
                  var1 = this.createFile();
                  if (var1 != null) {
                     FileSystemModel.this.createAttributes(var1.getId());
                  } else {
                     var1 = this.findFile();
                  }

                  if (var1 != null && this.onFinish != null) {
                     FileSystemModel.onFinishExec.submit(FileSystemModel.this.new ExecuteClosure(var1, this.onFinish));
                     var2 = null;
                     return var2;
                  }

                  var2 = var1;
               } catch (SQLException var7) {
                  FileSystemModel.this.recover(var7);
                  Object var3 = null;
                  return (FileObject)var3;
               } finally {
                  if (var1 != null) {
                     FileSystemModel.this.fireFileChanged(var1);
                  }

               }

               return var2;
            }
         }
      }

      public FileObject createFile() throws SQLException {
         if (!this.create) {
            return null;
         } else {
            FileSystemModel.this.createFile.setString(1, this.name);
            FileSystemModel.this.createFile.setLong(2, this.parentId);
            ResultSet var1;
            FileObject var3;
            if (FileSystemModel.this.autoGenerated) {
               FileSystemModel.this.createFile.executeUpdate();
               var1 = FileSystemModel.this.createFile.getGeneratedKeys();

               try {
                  if (var1.next()) {
                     FileObject var2 = new FileObject(FileSystemModel.this, (long)var1.getInt(1));
                     var2.setParent(this.parentId);
                     var2.setName(this.name);
                     var3 = var2;
                     return var3;
                  }
               } finally {
                  var1.close();
               }
            } else {
               FileSystemModel.this.createFile.executeUpdate();
               if (FileSystemModel.this.createFile.getUpdateCount() > 0) {
                  FileSystemModel.this.findFileSimple.setLong(1, this.parentId);
                  FileSystemModel.this.findFileSimple.setString(2, this.name);
                  var1 = FileSystemModel.this.findFileSimple.executeQuery();
                  List var7 = FileObject.getFiles(FileSystemModel.this, var1, (String)null, 1);
                  if (var7.size() != 0) {
                     var3 = (FileObject)var7.get(0);
                     FileSystemModel.this.createAttributes(var3.getId());
                     return var3;
                  }
               }
            }

            return null;
         }
      }

      public FileObject findFile() throws SQLException {
         PreparedStatement var1 = FileSystemModel.this.findFile;
         if (this.simple) {
            var1 = FileSystemModel.this.findFileSimple;
         }

         var1.setLong(1, this.parentId);
         var1.setString(2, this.name);
         ResultSet var2 = var1.executeQuery();
         if (var2 != null) {
            List var3 = FileObject.getFiles(FileSystemModel.this, var2, this.path, 1);
            if (var3.size() > 0) {
               FileObject var4 = (FileObject)var3.get(0);
               if (var4 != null) {
                  synchronized(FileSystemModel.this.PENDING_REQUESTS) {
                     var4.setPendingRequest(FileSystemModel.this.pendingRequests.contains(var4.getId()));
                  }

                  FileSystemModel.this.fireFileChanged(var4);
               }

               return var4;
            }
         }

         return null;
      }
   }

   private class DumpHistory implements Runnable {
      Long id;
      PrintStream out;

      public DumpHistory(Long var2, PrintStream var3) {
         this.id = var2;
         this.out = var3;
      }

      public void run() {
         if (FileSystemModel.this.status != FileSystemModel.Status.Exception) {
            try {
               FileSystemModel.this.dumpHistory.setLong(1, this.id);
               ResultSet var1 = FileSystemModel.this.dumpHistory.executeQuery();
               String var2 = "-------------------------------------------------------------";
               this.out.println(var2);

               while(var1.next()) {
                  this.out.printf("%6d | %11s | %s\n", var1.getInt("TASKID"), CommandType.values()[var1.getInt("COMMAND")], ActionType.values()[var1.getInt("ACTION")].getAction(CommandType.values()[var1.getInt("COMMAND")], var1.getString("ARGUMENT")));
                  int var3 = var1.getInt("TASKID");
                  String var4 = var1.getString("OPERATION");
                  Guid var5 = Guid.GenerateGuid(var4);
                  TaskId var6 = TaskId.GenerateTaskId(var3, FileSystemModel.this.core.getOperationById(Guid.GenerateGuid(var5)));
                  Task var7 = FileSystemModel.this.core.getTaskById(var6);
                  if (var7 != null) {
                     this.out.printf("%6s   %s\n", "", var7.getFullCommandLine());
                  }
               }

               this.out.println(var2);
            } catch (Throwable var8) {
               var8.printStackTrace();
            }

         }
      }
   }

   private class AddHistoryItem extends FileSystemModel.DatabaseAction<Object> {
      FileObject fileObject;
      TaskId task;
      ActionType action;
      CommandType command;
      Calendar timestamp;
      String arg;

      public AddHistoryItem(FileObject var2, TaskId var3, ActionType var4, CommandType var5, Calendar var6, String var7) {
         super(FileSystemModel.Priority.SAVE_HISTORY);
         this.fileObject = var2;
         this.task = var3;
         this.action = var4;
         this.command = var5;
         if (var6 == null) {
            this.timestamp = Calendar.getInstance();
         } else {
            this.timestamp = var6;
         }

         this.arg = var7;
      }

      public Object call() throws Exception {
         if (FileSystemModel.this.status == FileSystemModel.Status.Exception) {
            return null;
         } else if (this.fileObject == null) {
            return null;
         } else {
            try {
               FileSystemModel.this.addHistory.setLong(1, this.fileObject.getId());
               FileSystemModel.this.addHistory.setString(2, this.task.getOperation().getGuid().asString());
               FileSystemModel.this.addHistory.setInt(3, this.task.getId());
               FileSystemModel.this.addHistory.setInt(4, this.action.ordinal());
               FileSystemModel.this.addHistory.setInt(5, this.command.ordinal());
               FileSystemModel.this.addHistory.setTimestamp(6, new Timestamp(this.timestamp.getTimeInMillis()), this.timestamp);
               FileSystemModel.this.addHistory.setString(7, this.arg);
               FileSystemModel.this.addHistory.executeUpdate();
            } catch (SQLException var2) {
               var2.printStackTrace();
               FileSystemModel.this.recover(var2);
            }

            return null;
         }
      }
   }

   private abstract class DatabaseAction<E> implements Callable<E> {
      FileSystemModel.Priority priority;

      public DatabaseAction(FileSystemModel.Priority var2) {
         this.priority = var2;
      }

      public FileSystemModel.Priority getPriority() {
         return this.priority;
      }
   }

   public static enum Status {
      Starting,
      Running,
      Exception;
   }

   public static enum Priority {
      QUERY,
      SAVE_DETAILS,
      SAVE_ATTRIBUTES,
      SAVE_HISTORY;
   }
}
