package ddb.dsz.core.command;

import ddb.dsz.core.task.Task;
import java.util.Comparator;

public abstract class CommandEventAdapter implements CommandEventListener {
   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
   }

   @Override
   public boolean handlesPromptsForTask(Task task, int var2) {
      return false;
   }

   @Override
   public boolean caresAboutLocalEvents() {
      return false;
   }

   @Override
   public boolean caresAboutRepeatedEvents() {
      return false;
   }

   @Override
   public Comparator<CommandEvent> getComparator() {
      return null;
   }
}
