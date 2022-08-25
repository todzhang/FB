package ds.core.commanddispatcher;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.TaskId;

public abstract class AbstractCommandDispatcher implements CommandDispatcher {
   protected EventPublisher publisher;
   protected Operation op;
   protected CoreController core;
   private boolean finished = false;
   protected boolean stop = false;

   protected AbstractCommandDispatcher(EventPublisher publisher, CoreController core) {
      this.publisher = publisher;
      this.op = Operation.NULL;
      this.core = core;
   }

   public void publishEvent(CommandEvent commandEvent) {
      this.publisher.publish(commandEvent);
   }

   protected void setOperation(Operation op) {
      this.op = op;
   }

   public TaskId createTaskId(int id) {
      return TaskId.GenerateTaskId(id, this.op);
   }

   @Override
   public Operation getOperation() {
      return this.op;
   }

   public boolean isFinished() {
      return this.finished;
   }

   protected void setFinished(boolean finished) {
      this.finished = finished;
   }

   @Override
   public void stop() {
      this.stop = true;
   }
}
