package ddb.dsz.core.task;

import ddb.dsz.core.host.HostInfo;
import ddb.util.Guid;
import java.util.Calendar;
import java.util.List;

public interface MutableTask extends Task {
   void setParent(Task task);

   void addArguments(List<String> arguments);

   void addPrefixes(List<String> prefixes);

   void setCommandName(String commandName);

   void setId(TaskId taskId);

   void setTaskId(Guid guid);

   void addGuiFlag(String flag);

   void addGuiFlags(String flags);

   void setState(TaskState taskState);

   void setInternallyGenerated(boolean internallyGenerated);

   void setDisplayTransform(String displayTransform);

   void setStorageTransform(String storageTransform);

   void setResultString(String resultString);

   void setInPromptMode(boolean inPromptMode);

   void setCreated(Calendar created);

   void setResourceDirectory(String resourceDirectory);

   void setHost(HostInfo hostInfo);

   void setFullCommandLine(String fullCommandLine);

   void setTaskingInformation(TaskDataAccess taskDataAccess);

   void addDataInformation(TaskDataAccess taskDataAccess);

   void setTaskLog(TaskDataAccess taskDataAccess);

   void notifyObservers();
}
