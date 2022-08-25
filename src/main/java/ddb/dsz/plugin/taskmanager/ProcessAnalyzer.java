package ddb.dsz.plugin.taskmanager;

import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.util.FileManips;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessAnalyzer implements Runnable {
   public static final String DATABASE_SCHEMA = "create Table ProcessInformation (\t\t\tName TEXT NOT NULL COLLATE NOCASE,\tComment TEXT,\tType TEXT,\tPRIMARY KEY (Name) ON CONFLICT IGNORE)";
   public static final String DATABASE = "Databases/SimpleProcesses.db";
   final Object databaseLock = new Object();
   Connection processConnection = null;
   boolean connected = false;
   Set<ProcessInformation> pendingEvaluation = new HashSet();
   private String resourceDirectory = null;
   private String userDirectory = null;
   ScheduledExecutorService analyzer;
   PreparedStatement getProcess = null;

   public ProcessAnalyzer(ScheduledExecutorService analyzer) {
      this.analyzer = analyzer;
      this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
   }

   public synchronized void setResourceDirectory(String resDir) {
      if (this.resourceDirectory == null) {
         this.resourceDirectory = resDir;
      }

   }

   public synchronized void setUserDirectory(String userDir) {
      if (this.userDirectory == null) {
         this.userDirectory = userDir;
      }

   }

   public void run() {
      HashSet temp = new HashSet();
      boolean var25 = false;

      label449: {
         label450: {
            label451: {
               try {
                  var25 = true;
                  if (!this.openDatabase()) {
                     var25 = false;
                     break label449;
                  }

                  if (this.getProcess == null) {
                     var25 = false;
                     break label450;
                  }

                  synchronized(this.databaseLock) {
                     temp.addAll(this.pendingEvaluation);
                     this.pendingEvaluation.clear();
                  }

                  if (temp.size() == 0) {
                     var25 = false;
                     break label451;
                  }

                  Iterator i$ = temp.iterator();

                  while(i$.hasNext()) {
                     ProcessInformation eval = (ProcessInformation)i$.next();

                     try {
                        this.getProcess.clearParameters();
                        this.getProcess.setString(1, eval.getProcName());
                        ResultSet results;
                        synchronized(this.databaseLock) {
                           results = this.getProcess.executeQuery();
                        }

                        try {
                           while(results.next()) {
                              eval.setComment(results.getString("comment"));
                              String tempStr = results.getString("type");
                              if (tempStr != null) {
                                 eval.setType(FileStatus.valueOf(tempStr));
                              }
                           }
                        } finally {
                           results.close();
                        }
                     } catch (SQLException var56) {
                        var56.printStackTrace();
                        this.connected = false;
                     } finally {
                        ;
                     }
                  }

                  var25 = false;
               } finally {
                  if (var25) {
                     synchronized(this.databaseLock) {
                        this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
                     }
                  }
               }

               synchronized(this.databaseLock) {
                  this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
                  return;
               }
            }

            synchronized(this.databaseLock) {
               this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
               return;
            }
         }

         synchronized(this.databaseLock) {
            this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
            return;
         }
      }

      synchronized(this.databaseLock) {
         this.analyzer.schedule(this, 5L, TimeUnit.SECONDS);
      }
   }

   private boolean openDatabase() {
      synchronized(this.databaseLock) {
         if (this.connected) {
            return true;
         } else if (this.resourceDirectory == null) {
            return false;
         } else {
            boolean var10000;
            try {
               String src = "Dsz";
               String dest = "Ops";
               File databaseFile = new File(String.format("%s/%s/%s", this.resourceDirectory, dest, "Databases/SimpleProcesses.db"));
               File databaseSrc = new File(String.format("%s/%s/%s", this.resourceDirectory, src, "Databases/SimpleProcesses.db"));
               databaseFile.getParentFile().mkdirs();
               boolean create = false;
               if (!databaseFile.exists()) {
                  if (!databaseSrc.exists()) {
                     create = true;
                  } else {
                     create = !FileManips.CopyFile(databaseSrc, databaseFile);
                  }
               }

               Class.forName("org.sqlite.JDBC");
               this.processConnection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", databaseFile.getAbsolutePath()));
               this.connected = true;
               if (create) {
                  this.processConnection.createStatement().execute("create Table ProcessInformation (\t\t\tName TEXT NOT NULL COLLATE NOCASE,\tComment TEXT,\tType TEXT,\tPRIMARY KEY (Name) ON CONFLICT IGNORE)");
               }

               this.getProcess = this.processConnection.prepareStatement("Select * from ProcessInformation where name == ?");
               var10000 = true;
            } catch (Throwable var8) {
               var8.printStackTrace();
               return false;
            }

            return var10000;
         }
      }
   }

   void enqueProcess(ProcessInformation pi) {
      synchronized(this.databaseLock) {
         this.pendingEvaluation.add(pi);
      }
   }
}
