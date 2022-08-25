package ds.core.commanddispatcher;

import ddb.dsz.core.command.CommandEvent;

public interface EventPublisher {
   void publish(CommandEvent commandEvent);
}
