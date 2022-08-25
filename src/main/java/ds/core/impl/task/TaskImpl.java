package ds.core.impl.task;

import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.util.FileManips;
import ddb.util.Guid;
import ddb.util.Pair;
import ddb.util.UtilityConstants;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TaskImpl extends Observable implements Task, MutableTask {
   public static TaskDatabase DATABASE = null;
   static final TimeZone GMT = TimeZone.getTimeZone("GMT+00");
   private static final String RESOURCE_DIR = "ResourceDir";
   private static final Executor EXEC = Executors.newSingleThreadExecutor(UtilityConstants.createThreadFactory("TaskImpl"));
   private static ReentrantReadWriteLock[] LOCKS = null;
   private static int LOCK_COUNT = 0;
   static final SAXParserFactory RetrieveResourceDirectoryFactory = SAXParserFactory.newInstance();
   private TaskId id;
   private Guid taskId;
   private int tempId;
   private static int nextLocalId = 1;
   public static final int RESERVED_ID = 0;
   private HostInfo host;
   private HostInfo prospectiveHost;
   private Task parent;
   private String commandName;
   private String[] prefixes;
   private String[] arguments;
   private final List<Pair<String, String>> guiFlags;
   private long creationTime;
   private String typedCommand;
   private String fullCommand;
   private TaskState state;
   private String resultString;
   private boolean internallyGenerated;
   private String displayTransform;
   private String storageTransform;
   private String resourceDirectory;
   private boolean inPromptMode;
   private long created;
   private ReentrantReadWriteLock logLock = getLock();
   private int dataCount = 0;
   private boolean hasTaskingInformation = false;
   private TaskImpl.RetrieveResourceDirectory retriever = null;

   private static synchronized ReentrantReadWriteLock getLock() {
      if (LOCKS == null) {
         LOCKS = new ReentrantReadWriteLock[256];
      }

      int var0 = LOCK_COUNT % LOCKS.length;
      if (LOCKS[var0] == null) {
         LOCKS[var0] = new ReentrantReadWriteLock();
      }

      ++LOCK_COUNT;
      return LOCKS[var0];
   }

   public TaskImpl(String fullCommand, HostInfo prospectiveHost) {
      this.typedCommand = fullCommand;
      this.fullCommand = fullCommand;
      this.id = TaskId.UNINITIALIZED_ID;
      this.tempId = nextLocalId++;
      this.host = null;
      this.prospectiveHost = prospectiveHost;
      this.parent = null;
      this.commandName = null;
      this.prefixes = new String[0];
      this.arguments = new String[0];
      this.state = TaskState.INITIALIZED;
      this.creationTime = System.currentTimeMillis();
      this.internallyGenerated = false;
      this.displayTransform = null;
      this.storageTransform = null;
      this.guiFlags = new ArrayList(1);
      this.inPromptMode = false;
      this.created = Calendar.getInstance(GMT).getTimeInMillis();
      this.resourceDirectory = "Dsz";
   }

   @Override
   public Task getParentTask() {
      return this.parent;
   }

   @Override
   public void setParent(Task task) {
      this.parent = task;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public TaskId getParentId() {
      synchronized(this) {
         return this.parent == null ? TaskId.UNINITIALIZED_ID : this.parent.getId();
      }
   }

   @Override
   public List<String> getArguments() {
      return Collections.unmodifiableList(Arrays.asList(this.arguments));
   }

   @Override
   public void addArguments(List<String> arguments) {
      String[] var2 = this.arguments;
      String[] var3 = new String[var2.length + arguments.size()];

      int var4;
      for(var4 = 0; var4 < var2.length; ++var4) {
         var3[var4] = var2[var4];
      }

      for(var4 = 0; var4 < arguments.size(); ++var4) {
         var3[var4 + var2.length] = (String) arguments.get(var4);
      }

      this.arguments = var3;
   }

   @Override
   public List<String> getPrefixes() {
      return Collections.unmodifiableList(Arrays.asList(this.prefixes));
   }

   @Override
   public void addPrefixes(List<String> prefixes) {
      String[] var2 = this.prefixes;
      String[] var3 = new String[var2.length + prefixes.size()];

      int var4;
      for(var4 = 0; var4 < var2.length; ++var4) {
         var3[var4] = var2[var4];
      }

      for(var4 = 0; var4 < prefixes.size(); ++var4) {
         var3[var4 + var2.length] = (String) prefixes.get(var4);
      }

      this.prefixes = var3;
      String var7 = "guiflag=";
      Iterator var5 = prefixes.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (var6.toLowerCase().startsWith(var7)) {
            this.addGuiFlag(var6.substring(var7.length()));
         }
      }

   }

   @Override
   public String getCommandName() {
      return this.commandName;
   }

   @Override
   public void setCommandName(String commandName) {
      this.commandName = commandName;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public TaskId getId() {
      return this.id;
   }

   @Override
   public int getTempId() {
      return this.tempId;
   }

   @Override
   public void setId(TaskId taskId) {
      this.id = taskId;
      this.id.setTask(this);
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public void addGuiFlag(String flag) {
      int var2 = flag.indexOf(61);
      if (var2 == -1) {
         this.addGuiFlag(flag, "");
      } else {
         String var3 = flag.substring(0, var2);
         String var4 = flag.substring(var2 + 1);
         this.addGuiFlag(var3, var4);
      }

      this.setChanged();
   }

   private synchronized void addGuiFlag(String flag, String value) {
      synchronized(this.guiFlags) {
         Iterator var4 = this.guiFlags.iterator();

         Pair var5;
         do {
            if (!var4.hasNext()) {
               this.guiFlags.add(new Pair(flag, value));
               return;
            }

            var5 = (Pair)var4.next();
         } while(!((String)var5.getFirst()).equalsIgnoreCase(flag));

         var5.setSecond(value);
      }
   }

   @Override
   public String getDataName() {
      String var1 = this.getGuiFlagValue("dataas");
      return var1 != null ? var1 : this.commandName;
   }

   @Override
   public void addGuiFlags(String flags) {
      StringTokenizer var2 = new StringTokenizer(flags, ";");

      while(var2.hasMoreTokens()) {
         this.addGuiFlag(var2.nextToken());
      }

   }

   @Override
   public String getGuiFlagValue(String flag) {
      synchronized(this.guiFlags) {
         Iterator var3 = this.guiFlags.iterator();

         Pair var4;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            var4 = (Pair)var3.next();
         } while(!((String)var4.getFirst()).equalsIgnoreCase(flag));

         return (String)var4.getSecond();
      }
   }

   @Override
   public long getCreationTime() {
      return this.creationTime;
   }

   @Override
   public TaskState getState() {
      return this.state;
   }

   @Override
   public void setState(TaskState taskState) {
      this.state = taskState;
      switch(taskState) {
      case FAILED:
      case KILLED:
      case SUCCEEDED:
         ReentrantReadWriteLock var2 = this.logLock;
         var2.writeLock().lock();

         try {
            this.logLock = getLock();
         } finally {
            var2.writeLock().unlock();
         }
      default:
         this.notifyObservers(this.GetStateAccess());
      }
   }

   @Override
   public String getStateString() {
      return this.state.toString();
   }

   @Override
   public String getTypedCommand() {
      return this.typedCommand;
   }

   @Override
   public boolean getInternallyGenerated() {
      return this.internallyGenerated;
   }

   @Override
   public void setInternallyGenerated(boolean internallyGenerated) {
      this.internallyGenerated = internallyGenerated;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public String getDisplayTransform() {
      return this.displayTransform;
   }

   @Override
   public void setDisplayTransform(String displayTransform) {
      this.displayTransform = displayTransform;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public String getStorageTransform() {
      return this.storageTransform;
   }

   @Override
   public void setStorageTransform(String storageTransform) {
      this.storageTransform = storageTransform;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public String toString() {
      return this.typedCommand;
   }

   @Override
   public String getTargetId() {
      return this.host != null ? this.host.getId() : null;
   }

   @Override
   public void setResultString(String resultString) {
      this.resultString = resultString;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public String getResultString() {
      return this.resultString;
   }

   @Override
   public boolean isInPromptMode() {
      return this.inPromptMode;
   }

   @Override
   public void setInPromptMode(boolean inPromptMode) {
      this.inPromptMode = inPromptMode;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + (this.id == null ? 0 : this.id.hashCode());
      return var3;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         TaskImpl var2 = (TaskImpl)obj;
         if (this.id == null) {
            if (var2.id != null) {
               return false;
            }
         } else if (!this.id.equals(var2.id)) {
            return false;
         }

         return true;
      }
   }

   @Override
   public Calendar getCreated() {
      Calendar var1 = Calendar.getInstance(GMT);
      var1.setTimeInMillis(this.created);
      return var1;
   }

   @Override
   public void setCreated(Calendar created) {
      if (created != null) {
         this.created = created.getTimeInMillis();
         this.notifyObservers(this.GetStateAccess());
      }

   }

   @Override
   public String getResourceDirectory() {
      if (this.resourceDirectory == null) {
         if (this.retriever != null && !this.retriever.hasRun()) {
            return null;
         } else {
            this.retriever = new TaskImpl.RetrieveResourceDirectory(this);
            EXEC.execute(this.retriever);
            return null;
         }
      } else {
         return this.resourceDirectory;
      }
   }

   @Override
   public void setResourceDirectory(String resourceDirectory) {
      this.resourceDirectory = resourceDirectory;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public HostInfo getHost() {
      return this.host;
   }

   @Override
   public void setHost(HostInfo hostInfo) {
      this.host = hostInfo;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public HostInfo getProspectiveHost() {
      return this.prospectiveHost;
   }

   @Override
   public int getDataCount() {
      return this.dataCount;
   }

   @Override
   public Reader getTaskingInformation() {
      String var1 = DATABASE.getLog(this.id, DataType.TASKING);
      if (var1 == null) {
         return null;
      } else {
         try {
            return FileManips.createFileReader(new File(var1));
         } catch (IOException var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   @Override
   public boolean hasTaskingInformation() {
      return this.hasTaskingInformation;
   }

   @Override
   public Reader getDataInformation(int index) {
      String databaseLog = DATABASE.getLog(this.id, DataType.DATA, index);
      if (databaseLog == null) {
         return null;
      } else {
         try {
            return FileManips.createFileReader(new File(databaseLog));
         } catch (IOException e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   @Override
   public TaskDataAccess getDataAccess(int index) {
      String databaseLog = DATABASE.getLog(this.id, DataType.DATA, index);
      return databaseLog == null ? null : new FileAccess(this, DataType.DATA, new File(databaseLog), databaseLog, index);
   }

   private TaskDataAccess[] getDataAccesses() {
      List databaseLogs = DATABASE.getLogs(this.id, DataType.DATA);
      TaskDataAccess[] taskDataAccesses = new TaskDataAccess[databaseLogs.size()];

      for(int i = 0; i < databaseLogs.size(); ++i) {
         taskDataAccesses[i] = new FileAccess(this, DataType.DATA, new File((String)databaseLogs.get(i)), (String)databaseLogs.get(i), i);
      }

      return taskDataAccesses;
   }

   private TaskDataAccess[] getAllAccesses() {
      List var1 = DATABASE.getAllLogs(this.id);
      TaskDataAccess[] var2 = new TaskDataAccess[var1.size()];
      int var3 = 0;

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         TaskDatabase.LogInformation var5 = (TaskDatabase.LogInformation)var1.get(var4);
         if (var5 != null) {
            var2[var4] = new FileAccess(this, var5.type, new File(var5.file), var5.file, var3);
            if (var5.type == DataType.DATA) {
               ++var3;
            }
         }
      }

      return var2;
   }

   @Override
   public void setTaskingInformation(TaskDataAccess taskDataAccess) {
      DATABASE.addLog(this.id, DataType.TASKING, taskDataAccess.getLocation());
      this.hasTaskingInformation = true;
      this.notifyObservers(taskDataAccess);
   }

   @Override
   public TaskDataAccess getTaskLogAccess() {
      String var1 = DATABASE.getLog(this.id, DataType.LOG);
      return var1 == null ? null : new FileAccess(this, DataType.LOG, new File(var1), var1, 1);
   }

   @Override
   public TaskDataAccess getTaskingAccess() {
      String var1 = DATABASE.getLog(this.id, DataType.TASKING);
      return var1 == null ? null : new FileAccess(this, DataType.TASKING, new File(var1), var1, 1);
   }

   @Override
   public Reader getTaskLog() {
      String var1 = DATABASE.getLog(this.id, DataType.LOG);
      if (var1 == null) {
         return null;
      } else {
         try {
            return FileManips.createFileReader(new File(var1));
         } catch (IOException var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   @Override
   public void setTaskLog(TaskDataAccess taskDataAccess) {
      DATABASE.addLog(this.id, DataType.LOG, taskDataAccess.getLocation());
      this.hasTaskingInformation = true;
      this.notifyObservers(taskDataAccess);
   }

   @Override
   public void addDataInformation(TaskDataAccess taskDataAccess) {
      ReentrantReadWriteLock logLock = this.logLock;
      logLock.writeLock().lock();

      int var3;
      try {
         var3 = this.dataCount++;
      } finally {
         logLock.writeLock().unlock();
      }

      DATABASE.addLog(this.id, DataType.DATA, var3, taskDataAccess.getLocation());
      this.notifyObservers(taskDataAccess);
   }

   @Override
   public void subscribe(Observer observer, boolean var2) {
      ReentrantReadWriteLock var3 = this.logLock;
      var3.readLock().lock();

      try {
         this.addObserver(observer);
         TaskDataAccess[] var4 = this.getAllAccesses();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            TaskDataAccess var7 = var4[var6];
            observer.update(this, var7);
         }
      } finally {
         var3.readLock().unlock();
      }

   }

   @Override
   public void unsubscribe(Observer observer) {
      ReentrantReadWriteLock var2 = this.logLock;
      var2.readLock().lock();

      try {
         this.deleteObserver(observer);
      } finally {
         var2.readLock().unlock();
      }

   }

   @Override
   public boolean isReadyForParsing() {
      ReentrantReadWriteLock var1 = this.logLock;
      var1.readLock().lock();

      boolean var2;
      try {
         var2 = this.hasTaskingInformation;
      } finally {
         var1.readLock().unlock();
      }

      return var2;
   }

   @Override
   public Guid getTaskId() {
      return this.taskId;
   }

   @Override
   public void setTaskId(Guid guid) {
      this.taskId = guid;
      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public void setFullCommandLine(String fullCommandLine) {
      if (fullCommandLine.equals(this.typedCommand)) {
         this.fullCommand = this.typedCommand;
      } else {
         this.fullCommand = fullCommandLine;
      }

      this.notifyObservers(this.GetStateAccess());
   }

   @Override
   public String getFullCommandLine() {
      return this.fullCommand;
   }

   @Override
   public void notifyObservers(Object arg) {
      super.setChanged();
      super.notifyObservers(arg);
   }

   @Override
   public int getNextOrdinal() {
      ReentrantReadWriteLock logLock = this.logLock;
      logLock.readLock().lock();

      int var2;
      try {
         if (!this.hasTaskingInformation) {
            byte var6 = 0;
            return var6;
         }

         var2 = this.dataCount + 1;
      } finally {
         logLock.readLock().unlock();
      }

      return var2;
   }

   @Override
   public boolean isAlive() {
      switch(this.state) {
      case FAILED:
      case KILLED:
      case SUCCEEDED:
         return false;
      default:
         return true;
      }
   }

   private TaskStateAccess GetStateAccess() {
      return new TaskStateAccess(this);
   }

   private static class RetrieveResourceDirectory implements Runnable {
      TaskImpl task;
      boolean ran = false;

      public RetrieveResourceDirectory(TaskImpl task) {
         this.task = task;
      }

      @Override
      public void run() {
         try {
            Reader var1 = this.task.getTaskingAccess().getReader();
            if (var1 == null) {
               this.ran = true;
               return;
            }

            DefaultHandler var2 = new DefaultHandler() {
               boolean record = false;
               StringBuilder sb = new StringBuilder();

               @Override
               public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
                  super.startElement(var1, var2, var3, var4);
                  if ("ResourceDir".equals(var3)) {
                     this.record = true;
                  }

               }

               @Override
               public void endElement(String var1, String var2, String var3) throws SAXException {
                  super.endElement(var1, var2, var3);
                  if (this.record) {
                     throw new TaskImpl.FoundResourceDirectory(this.sb.toString());
                  }
               }

               @Override
               public void characters(char[] var1, int var2, int var3) throws SAXException {
                  super.characters(var1, var2, var3);
                  if (this.record) {
                     this.sb.append(new String(var1, var2, var3));
                  }

               }
            };
            SAXParser var3 = TaskImpl.RetrieveResourceDirectoryFactory.newSAXParser();

            try {
               var3.parse(new InputSource(var1), var2);
            } catch (TaskImpl.FoundResourceDirectory var9) {
               this.task.setResourceDirectory(var9.getString());
            }

            var1.close();
         } catch (Exception e) {
            e.printStackTrace();
            this.task.retriever = null;
            return;
         } finally {
            this.ran = true;
         }

      }

      public boolean hasRun() {
         return this.ran;
      }
   }

   private static class FoundResourceDirectory extends RuntimeException {
      private final String str;

      public FoundResourceDirectory(String str) {
         this.str = str;
      }

      public String getString() {
         return this.str;
      }
   }
}
