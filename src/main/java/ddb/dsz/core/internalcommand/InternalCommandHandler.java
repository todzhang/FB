package ddb.dsz.core.internalcommand;

import ddb.dsz.core.task.TaskId;
import java.util.EventListener;
import java.util.List;

public interface InternalCommandHandler extends EventListener {
   String INTERNAL_PARAMETER = "/";
   String LOCAL_ONLY = String.format("%sLocal-Only-Command", "/");
   String CLOSE_ON_COMPLETE = String.format("%sClose-On-Complete", "/");
   String FOCUS_ON_START = String.format("%sFocus-On-Start", "/");
   String DISABLE_ON_COMPLETE = String.format("%sDisable-On-Complete", "/");

   boolean runInternalCommand(List<String> commands, InternalCommandCallback internalCommandCallback);

   boolean runInternalCommand(List<String> commands, TaskId taskId, InternalCommandCallback internalCommandCallback);
}
