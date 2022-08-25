package ddb.dsz.core.task;

import ddb.dsz.core.host.HostInfo;
import ddb.util.Guid;
import java.io.Reader;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Observer;

public interface Task {
   Comparator<Task> TaskComparator = (one, two) -> {
      if (one == two) {
         return 0;
      } else if (one != null && one.getId() != null) {
         if (two == null) {
            return -1;
         } else {
            return one.getId() == two.getId() ? 0 : one.getId().compareTo(two.getId());
         }
      } else {
         return 1;
      }
   };

   Task getParentTask();

   TaskId getParentId();

   List<String> getArguments();

   List<String> getPrefixes();

   String getCommandName();

   TaskId getId();

   Guid getTaskId();

   int getTempId();

   String getDataName();

   String getGuiFlagValue(String flag);

   long getCreationTime();

   TaskState getState();

   String getStateString();

   String getTypedCommand();

   boolean getInternallyGenerated();

   String getDisplayTransform();

   String getStorageTransform();

   String getTargetId();

   String getResultString();

   boolean isInPromptMode();

   Calendar getCreated();

   String getResourceDirectory();

   HostInfo getHost();

   HostInfo getProspectiveHost();

   int getDataCount();

   Reader getTaskingInformation();

   boolean hasTaskingInformation();

   Reader getDataInformation(int var1);

   TaskDataAccess getDataAccess(int var1);

   TaskDataAccess getTaskLogAccess();

   TaskDataAccess getTaskingAccess();

   Reader getTaskLog();

   void subscribe(Observer observer, boolean var2);

   void unsubscribe(Observer observer);

   boolean isReadyForParsing();

   String getFullCommandLine();

   int getNextOrdinal();

   boolean isAlive();
}
