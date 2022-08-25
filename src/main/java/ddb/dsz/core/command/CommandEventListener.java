package ddb.dsz.core.command;

import ddb.dsz.annotations.DszQueuableMethod;
import ddb.dsz.core.task.Task;
import java.util.Comparator;
import java.util.EventListener;

public interface CommandEventListener extends EventListener {
   @DszQueuableMethod
   void commandEventReceived(CommandEvent commandEvent);

   boolean handlesPromptsForTask(Task task, int var2);

   boolean caresAboutLocalEvents();

   boolean caresAboutRepeatedEvents();

   Comparator<CommandEvent> getComparator();
}
