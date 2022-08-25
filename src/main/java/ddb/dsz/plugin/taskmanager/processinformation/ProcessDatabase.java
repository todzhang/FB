package ddb.dsz.plugin.taskmanager.processinformation;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.WindowsPrivilege;
import ddb.util.GeneralUtilities;
import ddb.util.UtilityConstants;
import java.awt.EventQueue;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.collections.Closure;

public class ProcessDatabase {
   private static final String MODULE_TABLE = "CREATE TABLE Module (    HostInfo STRING NOT NULL,\tProcessId INTEGER NOT NULL,\tBaseAddress INTEGER NOT NULL,\tImageSize INTEGER NOT NULL,\tEntryPoint INTEGER NOT NULL,   Name STRING NOT NULL,\tMD5 STRING,\tSHA1 STRING,\tSHA256 STRING,\tSHA512 STRING,\tPRIMARY KEY (HostInfo, ProcessId, BaseAddress) ON CONFLICT REPLACE)";
   private static final String PRIVILEGE_WINDOWS_TABLE = "CREATE TABLE WINDOWS_PRIVILEGE (   HostInfo STRING NOT NULL,   ProcessId INTEGER NOT NULL,   Name STRING NOT NULL,   Enabled INTEGER DEFAULT 0,   EnabledByDefault INTEGER DEFAULT 0,    UsedAccess INTEGER DEFAULT 0,    Mask INTEGER DEFAULT 0,    PRIMARY KEY (HostInfo, ProcessId, Name) ON CONFLICT REPLACE)";
   private static final String HANDLE_WINDOWS_TABLE = "CREATE TABLE WINDOWS_HANDLE (\tHostInfo STRING NOT NULL,   ProcessId INTEGER NOT NULL,   HandleId INTEGER NOT NULL,\tType INTEGER NOT NULL,\tMetaData STRING NOT NULL,\tPRIMARY KEY (HostInfo, ProcessId, HandleId) ON CONFLICT REPLACE)";
   private static ProcessDatabase instance = null;
   private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(UtilityConstants.createThreadFactory("ProcessDatabase"));
   private final CoreController core;
   private Connection connection;
   private static final String[] SCHEMA = new String[]{"CREATE TABLE Module (    HostInfo STRING NOT NULL,\tProcessId INTEGER NOT NULL,\tBaseAddress INTEGER NOT NULL,\tImageSize INTEGER NOT NULL,\tEntryPoint INTEGER NOT NULL,   Name STRING NOT NULL,\tMD5 STRING,\tSHA1 STRING,\tSHA256 STRING,\tSHA512 STRING,\tPRIMARY KEY (HostInfo, ProcessId, BaseAddress) ON CONFLICT REPLACE)", "CREATE TABLE WINDOWS_PRIVILEGE (   HostInfo STRING NOT NULL,   ProcessId INTEGER NOT NULL,   Name STRING NOT NULL,   Enabled INTEGER DEFAULT 0,   EnabledByDefault INTEGER DEFAULT 0,    UsedAccess INTEGER DEFAULT 0,    Mask INTEGER DEFAULT 0,    PRIMARY KEY (HostInfo, ProcessId, Name) ON CONFLICT REPLACE)", "CREATE TABLE WINDOWS_HANDLE (\tHostInfo STRING NOT NULL,   ProcessId INTEGER NOT NULL,   HandleId INTEGER NOT NULL,\tType INTEGER NOT NULL,\tMetaData STRING NOT NULL,\tPRIMARY KEY (HostInfo, ProcessId, HandleId) ON CONFLICT REPLACE)"};
   private PreparedStatement ClearModules;
   private PreparedStatement AddModules;
   private PreparedStatement GetModules;
   private PreparedStatement ClearPrivilegesWindows;
   private PreparedStatement AddPrivilegesWindows;
   private PreparedStatement GetPrivilegesWindows;
   private PreparedStatement AddHandleWindows;
   private PreparedStatement GetHandlesWindows;
   private final Runnable Dumper = new Runnable() {
      public void run() {
         try {
            String[] tables = new String[]{"WINDOWS_HANDLE"};
            Statement stmt = ProcessDatabase.this.connection.createStatement();
            String[] arr$ = tables;
            int len$ = tables.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               String s = arr$[i$];
               ResultSet rs = stmt.executeQuery(String.format("Select * from %s", s));

               try {
                  System.out.printf("------------------------------------------------\n");
                  System.out.printf("- SQL Dump %-17s -\n", s);
                  System.out.printf("------------------------------------------------\n");

                  int i;
                  for(i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
                     System.out.printf("%10s", rs.getMetaData().getColumnName(i));
                     System.out.print("\t");
                  }

                  System.out.println();
                  System.out.println("------------------------------------------------");

                  while(rs.next()) {
                     for(i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
                        System.out.printf("%10s", rs.getString(i));
                        System.out.print("\t");
                     }

                     System.out.println();
                  }

                  System.out.println("------------------------------------------------");
               } finally {
                  rs.close();
               }
            }
         } catch (SQLException var13) {
            var13.printStackTrace();
         }

      }
   };

   public static synchronized ProcessDatabase GetInstance(CoreController core) {
      if (instance == null) {
         instance = new ProcessDatabase(core);
      }

      return instance;
   }

   public static synchronized ProcessDatabase GetInstance() {
      return instance;
   }

   private ProcessDatabase(CoreController core) {
      this.core = core;

      try {
         try {
            File temp = new File(core.getUserConfigDirectory(), "Process");
            temp.mkdirs();
            File tempDatabase = null;

            for(int i = 0; i < 10; ++i) {
               tempDatabase = new File(temp, String.format("Processes_%s.db", GeneralUtilities.CalendarToStringFile(Calendar.getInstance())));
               if (!tempDatabase.exists()) {
                  break;
               }

               try {
                  TimeUnit.MILLISECONDS.sleep(50L);
               } catch (Exception var16) {
               }
            }

            tempDatabase.deleteOnExit();
            Class<?> clazz = Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:/%s", tempDatabase.getAbsolutePath()));
            String connectionInfo = String.format("Connecting to %s with %s %s (%d.%d)", tempDatabase.getAbsolutePath(), this.connection.getMetaData().getDatabaseProductName(), this.connection.getMetaData().getDatabaseProductVersion(), this.connection.getMetaData().getDatabaseMajorVersion(), this.connection.getMetaData().getDatabaseMinorVersion());
            core.logEvent(Level.INFO, "TaskDatabase", connectionInfo);
            String[] pragmas = new String[]{"PRAGMA synchronous=OFF", "PRAGMA cache_size=20000", "PRAGMA foreign_keys = ON", "PRAGMA ignore_check_constraints=true", "PRAGMA journal_mode=MEMORY"};
            Statement statement = this.connection.createStatement();
            String[] arr$ = pragmas;
            int len$ = pragmas.length;

            int i$;
            String s;
            for(i$ = 0; i$ < len$; ++i$) {
               s = arr$[i$];
               statement.execute(s);
            }

            arr$ = SCHEMA;
            len$ = arr$.length;

            for(i$ = 0; i$ < len$; ++i$) {
               s = arr$[i$];
               statement.execute(s);
            }

            this.AddModules = this.connection.prepareStatement("Insert into Module    (HostInfo, ProcessId, BaseAddress, ImageSize, EntryPoint, Name, MD5, SHA1, SHA256, SHA512)    VALUES    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            this.ClearModules = this.connection.prepareStatement("DELETE FROM Module WHERE     ProcessId = ? AND HostInfo = ? ");
            this.GetModules = this.connection.prepareStatement("Select * From Module where     ProcessId = ? AND HostInfo = ? ORDER BY BaseAddress ASC", 1003, 1007);
            this.AddPrivilegesWindows = this.connection.prepareStatement("Insert into WINDOWS_PRIVILEGE    (HostInfo, ProcessId, Name, Enabled, EnabledByDefault, UsedAccess, Mask)    VALUES    (?, ?, ?, ?, ?, ?, ?)");
            this.ClearPrivilegesWindows = this.connection.prepareStatement("DELETE FROM WINDOWS_PRIVILEGE WHERE     ProcessId = ? AND HostInfo = ? ");
            this.GetPrivilegesWindows = this.connection.prepareStatement("Select * From WINDOWS_PRIVILEGE where     ProcessId = ? AND HostInfo = ? ORDER BY Name ASC", 1003, 1007);
            this.AddHandleWindows = this.connection.prepareStatement("Insert into WINDOWS_HANDLE     (HostInfo, ProcessId, HandleId, Type, MetaData)     VALUES     (?, ?, ?, ?, ?)");
            this.GetHandlesWindows = this.connection.prepareStatement("Select * From WINDOWS_HANDLE where     ProcessId = ? AND HostInfo = ? ORDER BY HandleId ASC", 1003, 1007);
         } catch (Exception var17) {
            var17.printStackTrace();
         }

      } finally {
         ;
      }
   }

   public void addModules(HostInfo host, long processId, List<Module> modules) {
      this.exec.submit(new ProcessDatabase.AddModules(host, processId, modules));
   }

   public List<Module> getModules(HostInfo host, long processId, Closure eventQueueClosure) {
      Future<List<Module>> getChild = this.exec.submit(new ProcessDatabase.GetModules(host, processId, eventQueueClosure));
      if (eventQueueClosure != null) {
         return Collections.emptyList();
      } else {
         try {
            return (List)getChild.get();
         } catch (Exception var7) {
            var7.printStackTrace();
            return Collections.emptyList();
         }
      }
   }

   public void addPrivileges(HostInfo host, long processId, List<Privilege> privs) {
      this.exec.submit(new ProcessDatabase.AddPrivileges(host, processId, privs));
   }

   public List<Privilege> getPrivileges(HostInfo host, long processId, Closure eventQueueClosure) {
      Future<List<Privilege>> getChild = this.exec.submit(new ProcessDatabase.GetPrivileges(host, processId, eventQueueClosure));
      if (eventQueueClosure != null) {
         return Collections.emptyList();
      } else {
         try {
            return (List)getChild.get();
         } catch (Exception var7) {
            var7.printStackTrace();
            return Collections.emptyList();
         }
      }
   }

   public void addHandle(HostInfo host, long processId, Handle handle) {
      if (handle instanceof Handle) {
         this.exec.submit(new ProcessDatabase.AddWindowsHandle(host, processId, (Handle)Handle.class.cast(handle)));
      }

   }

   public List<Handle> getHandles(HostInfo host, long processId, Closure eventQueueClosure) {
      Future<List<Handle>> getChild = this.exec.submit(new ProcessDatabase.GetHandles(host, processId, eventQueueClosure));
      if (eventQueueClosure != null) {
         return Collections.emptyList();
      } else {
         try {
            return (List)getChild.get();
         } catch (Exception var7) {
            var7.printStackTrace();
            return Collections.emptyList();
         }
      }
   }

   private class GetHandles extends ProcessDatabase.DatabaseQuery<List<Handle>> {
      long processId;
      HostInfo host;

      public GetHandles(HostInfo host, long processId, Closure eventQueueClosure) {
         super(eventQueueClosure, ProcessDatabase.this.GetHandlesWindows);
         this.processId = processId;
         this.host = host;
      }

      protected void prepare() throws Exception {
         ProcessDatabase.this.GetHandlesWindows.setLong(1, this.processId);
         ProcessDatabase.this.GetHandlesWindows.setString(2, this.host.getId());
      }

      protected List<Handle> handle(ResultSet result, List<Handle> previousHandle) throws Exception {
         List<Handle> handles = new ArrayList();
         if (previousHandle != null) {
            handles.addAll(previousHandle);
         }

         while(result.next()) {
            handles.add(new Handle(result.getLong("HandleId"), result.getInt("Type"), result.getString("MetaData")));
         }

         return handles;
      }
   }

   private class AddWindowsHandle implements Runnable {
      long processId;
      Handle handle;
      HostInfo host;

      public AddWindowsHandle(HostInfo host, long processId, Handle handle) {
         this.host = host;
         this.processId = processId;
         this.handle = handle;
      }

      public void run() {
         try {
            try {
               ProcessDatabase.this.AddHandleWindows.setString(1, this.host.getId());
               ProcessDatabase.this.AddHandleWindows.setLong(2, this.processId);
               ProcessDatabase.this.AddHandleWindows.setLong(3, this.handle.getId());
               ProcessDatabase.this.AddHandleWindows.setInt(4, this.handle.getType().ordinal());
               ProcessDatabase.this.AddHandleWindows.setString(5, this.handle.getMetaData());
               ProcessDatabase.this.AddHandleWindows.execute();
            } finally {
               ;
            }
         } catch (SQLException var5) {
            var5.printStackTrace();
         }

      }
   }

   private class GetPrivileges extends ProcessDatabase.DatabaseQuery<List<Privilege>> {
      long processId;
      HostInfo host;

      public GetPrivileges(HostInfo host, long processId, Closure eventQueueClosure) {
         super(eventQueueClosure, ProcessDatabase.this.GetPrivilegesWindows);
         this.processId = processId;
         this.host = host;
      }

      protected void prepare() throws Exception {
         ProcessDatabase.this.GetPrivilegesWindows.setLong(1, this.processId);
         ProcessDatabase.this.GetPrivilegesWindows.setString(2, this.host.getId());
      }

      protected List<Privilege> handle(ResultSet result, List<Privilege> previousPrivileges) throws Exception {
         List<Privilege> privileges = new ArrayList();
         if (previousPrivileges != null) {
            privileges.addAll(previousPrivileges);
         }

         while(result.next()) {
            privileges.add(new WindowsPrivilege(result.getString("Name"), result.getInt("Enabled") == 1, result.getInt("EnabledByDefault") == 1, result.getInt("UsedAccess") == 1, result.getLong("Mask")));
         }

         return privileges;
      }
   }

   private class AddPrivileges implements Runnable {
      long processId;
      List<Privilege> privs;
      HostInfo host;

      public AddPrivileges(HostInfo host, long processId, List<Privilege> privs) {
         this.host = host;
         this.processId = processId;
         this.privs = privs;
      }

      public void run() {
         try {
            try {
               ProcessDatabase.this.connection.setAutoCommit(false);
               ProcessDatabase.this.ClearPrivilegesWindows.setLong(1, this.processId);
               ProcessDatabase.this.ClearPrivilegesWindows.setString(2, this.host.getId());
               ProcessDatabase.this.ClearPrivilegesWindows.execute();
               Iterator i$ = this.privs.iterator();

               while(i$.hasNext()) {
                  Privilege p = (Privilege)i$.next();
                  if (p instanceof WindowsPrivilege) {
                     WindowsPrivilege priv = (WindowsPrivilege)p;
                     ProcessDatabase.this.AddPrivilegesWindows.setString(1, this.host.getId());
                     ProcessDatabase.this.AddPrivilegesWindows.setLong(2, this.processId);
                     ProcessDatabase.this.AddPrivilegesWindows.setString(3, priv.getName());
                     ProcessDatabase.this.AddPrivilegesWindows.setInt(4, priv.isEnabled() ? 1 : 0);
                     ProcessDatabase.this.AddPrivilegesWindows.setInt(5, priv.isEnabledByDefault() ? 1 : 0);
                     ProcessDatabase.this.AddPrivilegesWindows.setInt(6, priv.isUsedAccess() ? 1 : 0);
                     ProcessDatabase.this.AddPrivilegesWindows.setLong(7, priv.getMask());
                     ProcessDatabase.this.AddPrivilegesWindows.addBatch();
                  }
               }

               ProcessDatabase.this.AddPrivilegesWindows.executeBatch();
               ProcessDatabase.this.AddPrivilegesWindows.clearBatch();
            } finally {
               ProcessDatabase.this.connection.setAutoCommit(true);
            }
         } catch (SQLException var8) {
            var8.printStackTrace();
         }

      }
   }

   private class GetModules extends ProcessDatabase.DatabaseQuery<List<Module>> {
      long processId;
      HostInfo host;

      public GetModules(HostInfo host, long processId, Closure eventQueueClosure) {
         super(eventQueueClosure, ProcessDatabase.this.GetModules);
         this.processId = processId;
         this.host = host;
      }

      protected void prepare() throws Exception {
         ProcessDatabase.this.GetModules.setLong(1, this.processId);
         ProcessDatabase.this.GetModules.setString(2, this.host.getId());
      }

      protected List<Module> handle(ResultSet result, List<Module> previousModules) throws Exception {
         List<Module> modules = new ArrayList();
         if (previousModules != null) {
            modules.addAll(previousModules);
         }

         while(result.next()) {
            String[] hashes = new String[Module.Hash.values().length];
            Module.Hash[] arr$ = Module.Hash.values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               Module.Hash hash = arr$[i$];
               hashes[hash.ordinal()] = result.getString(hash.key);
            }

            modules.add(new Module(ProcessDatabase.this.core, result.getLong("BaseAddress"), result.getLong("ImageSize"), result.getLong("EntryPoint"), result.getString("Name"), hashes));
         }

         return modules;
      }
   }

   private class AddModules implements Runnable {
      HostInfo host;
      long processId;
      List<Module> modules;

      public AddModules(HostInfo host, long processId, List<Module> modules) {
         this.processId = processId;
         this.modules = modules;
         this.host = host;
      }

      public void run() {
         try {
            try {
               ProcessDatabase.this.connection.setAutoCommit(false);
               ProcessDatabase.this.ClearModules.setLong(1, this.processId);
               ProcessDatabase.this.ClearModules.setString(2, this.host.getId());
               ProcessDatabase.this.ClearModules.execute();
               Iterator i$ = this.modules.iterator();

               while(i$.hasNext()) {
                  Module mod = (Module)i$.next();
                  ProcessDatabase.this.AddModules.setString(1, this.host.getId());
                  ProcessDatabase.this.AddModules.setLong(2, this.processId);
                  ProcessDatabase.this.AddModules.setLong(3, mod.getBaseAddress());
                  ProcessDatabase.this.AddModules.setLong(4, mod.getImageSize());
                  ProcessDatabase.this.AddModules.setLong(5, mod.getEntryPoint());
                  ProcessDatabase.this.AddModules.setString(6, mod.getName());
                  ProcessDatabase.this.AddModules.setString(7, mod.getHash(Module.Hash.MD5));
                  ProcessDatabase.this.AddModules.setString(8, mod.getHash(Module.Hash.SHA1));
                  ProcessDatabase.this.AddModules.setString(9, mod.getHash(Module.Hash.SHA256));
                  ProcessDatabase.this.AddModules.setString(10, mod.getHash(Module.Hash.SHA512));
                  ProcessDatabase.this.AddModules.addBatch();
               }

               ProcessDatabase.this.AddModules.executeBatch();
               ProcessDatabase.this.AddModules.clearBatch();
            } finally {
               ProcessDatabase.this.connection.setAutoCommit(true);
            }
         } catch (SQLException var7) {
            var7.printStackTrace();
         }

      }
   }

   private abstract class DatabaseQuery<T> implements Callable<T> {
      final PreparedStatement[] stmt;
      final Closure eventQueueClosure;

      protected DatabaseQuery(Closure eventQueueClosure, PreparedStatement... stmt) {
         this.stmt = stmt;
         this.eventQueueClosure = eventQueueClosure;
      }

      protected DatabaseQuery(PreparedStatement... stmt) {
         this.stmt = stmt;
         this.eventQueueClosure = null;
      }

      public final T call() throws Exception {
         this.prepare();
         T retVal = null;
         PreparedStatement[] arr$ = this.stmt;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            PreparedStatement p = arr$[i$];
            ResultSet rs = p.executeQuery();

            try {
               retVal = this.handle(rs, retVal);
            } finally {
               rs.close();
            }
         }

         if (this.eventQueueClosure != null) {
            T finalRetVal = retVal;
            EventQueue.invokeLater(new Runnable() {
               @Override
               public void run() {
                  DatabaseQuery.this.eventQueueClosure.execute(finalRetVal);
               }
            });
         }

         return retVal;
      }

      protected abstract void prepare() throws Exception;

      protected abstract T handle(ResultSet var1, T var2) throws Exception;
   }
}
