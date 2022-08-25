package ds.core.impl.task;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.GeneralUtilities;
import ddb.util.Guid;
import ddb.util.UtilityConstants;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TaskDatabase {
   CoreController core;
   private static final String LOG_TABLE = "CREATE TABLE Log ( \tTaskId INTEGER NOT NULL,\tOperation INTEGER NOT NULL,\tType INTEGER NOT NULL,\tLogIndex INTEGER NOT NULL,\tPath STRING NOT NULL,\tPRIMARY KEY (TaskId, Operation, Type, LogIndex) ON CONFLICT REPLACE)";
   private static final String[] SCHEMA = new String[]{"CREATE TABLE Log ( \tTaskId INTEGER NOT NULL,\tOperation INTEGER NOT NULL,\tType INTEGER NOT NULL,\tLogIndex INTEGER NOT NULL,\tPath STRING NOT NULL,\tPRIMARY KEY (TaskId, Operation, Type, LogIndex) ON CONFLICT REPLACE)"};
   private PreparedStatement GetLogs;
   private PreparedStatement GetAllLogs;
   private PreparedStatement GetLogByIndex;
   private PreparedStatement AddLog;
   private PreparedStatement Dump;
   private Connection connection;
   private boolean bInitialized = false;
   private Map<Guid, Integer> OperationMap = new HashMap();
   private ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(UtilityConstants.createThreadFactory("TaskDatabase"));
   private final Runnable Dumper = new Runnable() {
      public void run() {
         try {
            ResultSet var1 = TaskDatabase.this.Dump.executeQuery();

            try {
               System.out.println("------------------------------------------------");
               System.out.println("-----------------SQL DUMP-----------------------");
               System.out.println("------------------------------------------------");

               int var2;
               for(var2 = 1; var2 <= var1.getMetaData().getColumnCount(); ++var2) {
                  System.out.printf("%10s", var1.getMetaData().getColumnName(var2));
                  System.out.print("\t");
               }

               System.out.println();
               System.out.println("------------------------------------------------");

               while(var1.next()) {
                  for(var2 = 1; var2 <= var1.getMetaData().getColumnCount(); ++var2) {
                     System.out.printf("%10s", var1.getString(var2));
                     System.out.print("\t");
                  }

                  System.out.println();
               }

               System.out.println("------------------------------------------------");
            } finally {
               var1.close();
            }
         } catch (SQLException var7) {
            var7.printStackTrace();
         }

      }
   };

   public TaskDatabase(CoreController var1) {
      this.core = var1;

      try {
         try {
            File var2 = new File(var1.getUserConfigDirectory(), "Tasks");
            var2.mkdirs();
            File var3 = null;

            for(int var4 = 0; var4 < 10; ++var4) {
               var3 = new File(var2, String.format("Tasks_%s.db", GeneralUtilities.CalendarToStringFile(Calendar.getInstance())));
               if (!var3.exists()) {
                  break;
               }

               try {
                  TimeUnit.MILLISECONDS.sleep(50L);
               } catch (Exception var16) {
               }
            }

            var3.deleteOnExit();
            Class var19 = Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:/%s", var3.getAbsolutePath()));
            String var5 = String.format("Connecting to %s with %s %s (%d.%d)", var3.getAbsolutePath(), this.connection.getMetaData().getDatabaseProductName(), this.connection.getMetaData().getDatabaseProductVersion(), this.connection.getMetaData().getDatabaseMajorVersion(), this.connection.getMetaData().getDatabaseMinorVersion());
            var1.logEvent(Level.INFO, "TaskDatabase", var5);
            String[] var6 = new String[]{"PRAGMA synchronous=OFF", "PRAGMA cache_size=20000", "PRAGMA foreign_keys = ON", "PRAGMA ignore_check_constraints=true", "PRAGMA journal_mode=MEMORY"};
            Statement var7 = this.connection.createStatement();
            String[] var8 = var6;
            int var9 = var6.length;

            int var10;
            String var11;
            for(var10 = 0; var10 < var9; ++var10) {
               var11 = var8[var10];
               var7.execute(var11);
            }

            var8 = SCHEMA;
            var9 = var8.length;

            for(var10 = 0; var10 < var9; ++var10) {
               var11 = var8[var10];
               var7.execute(var11);
            }

            this.GetLogs = this.connection.prepareStatement("Select Path From Log where \tTaskId = ? AND \tOperation = ? AND \tType = ?    ORDER BY LogIndex ASC", 1003, 1007);
            this.GetAllLogs = this.connection.prepareStatement("Select Path, Type From Log where \tTaskId = ? AND \tOperation = ?    ORDER BY Type ASC, LogIndex ASC", 1003, 1007);
            this.GetLogByIndex = this.connection.prepareStatement("Select Path From Log where \tTaskId = ? AND \tOperation = ? AND \tType = ? AND \tLogIndex = ?", 1003, 1007);
            this.AddLog = this.connection.prepareStatement("Insert into Log \t(TaskId, Operation, Type, LogIndex, Path) \tVALUES \t(?, ?, ?, ?, ?)");
            this.Dump = this.connection.prepareStatement("Select * From Log ORDER BY TaskId, Operation, Type, LogIndex", 1003, 1007);
            this.bInitialized = true;
         } catch (Exception var17) {
            var17.printStackTrace();
         }

      } finally {
         ;
      }
   }

   public boolean isInitialize() {
      return this.bInitialized;
   }

   public void addLog(TaskId var1, DataType var2, String var3) {
      this.addLog(var1, var2, 0, var3);
   }

   public void addLog(TaskId var1, DataType var2, int var3, String var4) {
      this.exec.execute(new TaskDatabase.AddLog(var1, var2, var3, var4));
   }

   public String getLog(TaskId var1, DataType var2) {
      return this.getLog(var1, var2, 0);
   }

   public String getLog(TaskId taskId, DataType dataType, int index) {
      Future future = this.exec.submit(new TaskDatabase.GetLogByIndex(taskId, dataType, index));

      try {
         return (String)future.get();
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public List<String> getLogs(TaskId taskId, DataType dataType) {
      Future future = this.exec.submit(new TaskDatabase.GetLogs(taskId, dataType));

      try {
         return (List)future.get();
      } catch (Exception e) {
         e.printStackTrace();
         return Collections.emptyList();
      }
   }

   public List<TaskDatabase.LogInformation> getAllLogs(TaskId taskId) {
      Future future = this.exec.submit(new TaskDatabase.GetAllLogs(taskId));

      try {
         return (List)future.get();
      } catch (Exception e) {
         e.printStackTrace();
         return Collections.emptyList();
      }
   }

   private int getOperationId(TaskId taskId) {
      return this.getOperationId(taskId.getOperation());
   }

   private int getOperationId(Operation operation) {
      return this.getOperationId(operation.getGuid());
   }

   private int getOperationId(Guid guid) {
      Integer var2 = (Integer)this.OperationMap.get(guid);
      if (var2 == null) {
         var2 = this.OperationMap.keySet().size();
         this.OperationMap.put(guid, var2);
      }

      return var2;
   }

   private class GetAllLogs extends TaskDatabase.DatabaseTransaction<List<TaskDatabase.LogInformation>> {
      private final TaskId id;

      public GetAllLogs(TaskId var2) {
         super(TaskDatabase.this.GetAllLogs);
         this.id = var2;
      }

      protected void prepare() throws Exception {
         TaskDatabase.this.GetAllLogs.setInt(1, this.id.getId());
         TaskDatabase.this.GetAllLogs.setInt(2, TaskDatabase.this.getOperationId(this.id));
      }

      protected List<TaskDatabase.LogInformation> handle(ResultSet var1) throws Exception {
         Vector var2 = new Vector();

         while(var1.next()) {
            var2.add(TaskDatabase.this.new LogInformation(DataType.values()[var1.getInt(2)], var1.getString(1)));
         }

         return var2;
      }
   }

   private class GetLogByIndex extends TaskDatabase.DatabaseTransaction<String> {
      private final TaskId id;
      private final DataType type;
      private final int index;

      public GetLogByIndex(TaskId var2, DataType var3, int var4) {
         super(TaskDatabase.this.GetLogByIndex);
         this.id = var2;
         this.type = var3;
         this.index = var4;
      }

      protected void prepare() throws Exception {
         TaskDatabase.this.GetLogByIndex.setInt(1, this.id.getId());
         TaskDatabase.this.GetLogByIndex.setInt(2, TaskDatabase.this.getOperationId(this.id));
         TaskDatabase.this.GetLogByIndex.setInt(3, this.type.ordinal());
         TaskDatabase.this.GetLogByIndex.setInt(4, this.index);
      }

      protected String handle(ResultSet var1) throws Exception {
         return var1.next() ? var1.getString(1) : null;
      }
   }

   private class GetLogs extends TaskDatabase.DatabaseTransaction<List<String>> {
      private final TaskId id;
      private final DataType type;

      public GetLogs(TaskId var2, DataType var3) {
         super(TaskDatabase.this.GetLogs);
         this.id = var2;
         this.type = var3;
      }

      protected void prepare() throws Exception {
         TaskDatabase.this.GetLogs.setInt(1, this.id.getId());
         TaskDatabase.this.GetLogs.setInt(2, TaskDatabase.this.getOperationId(this.id));
         TaskDatabase.this.GetLogs.setInt(3, this.type.ordinal());
      }

      protected List<String> handle(ResultSet var1) throws Exception {
         Vector var2 = new Vector();

         while(var1.next()) {
            var2.add(var1.getString(1));
         }

         return var2;
      }
   }

   private abstract class DatabaseTransaction<T> implements Callable<T> {
      PreparedStatement stmt;

      protected DatabaseTransaction(PreparedStatement var2) {
         this.stmt = var2;
      }

      public final T call() throws Exception {
         this.prepare();
         ResultSet var1 = this.stmt.executeQuery();

         Object var2;
         try {
            var2 = this.handle(var1);
         } finally {
            var1.close();
         }

         return (T) var2;
      }

      protected abstract void prepare() throws Exception;

      protected abstract T handle(ResultSet var1) throws Exception;
   }

   private class AddLog implements Runnable {
      private final TaskId id;
      private final DataType type;
      private final int index;
      private final String path;

      public AddLog(TaskId var2, DataType var3, int var4, String var5) {
         this.id = var2;
         this.type = var3;
         this.index = var4;
         this.path = var5;
      }

      public void run() {
         try {
            TaskDatabase.this.AddLog.setInt(1, this.id.getId());
            TaskDatabase.this.AddLog.setInt(2, TaskDatabase.this.getOperationId(this.id));
            TaskDatabase.this.AddLog.setInt(3, this.type.ordinal());
            TaskDatabase.this.AddLog.setInt(4, this.index);
            TaskDatabase.this.AddLog.setString(5, this.path);
            if (TaskDatabase.this.AddLog.executeUpdate() != 1) {
               System.err.println("Writing to database fail");
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   public class LogInformation {
      public final DataType type;
      public final String file;

      LogInformation(DataType var2, String var3) {
         this.type = var2;
         this.file = var3;
      }
   }
}
