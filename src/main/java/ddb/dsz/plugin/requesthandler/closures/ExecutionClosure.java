package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.requesthandler.RequestReceiver;
import ddb.dsz.plugin.requesthandler.predicate.SpawnTerminalPredicate;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.CommandTransformer;
import ddb.dsz.plugin.requesthandler.tranformers.GetActionTransformer;
import java.util.Vector;
import org.apache.commons.collections.Closure;

public class ExecutionClosure implements Closure {
   protected CoreController core;
   protected RequestReceiver recv;

   public static final Closure getInstance(RequestReceiver recv, CoreController core) {
      return new ExecutionClosure(recv, core);
   }

   protected ExecutionClosure(RequestReceiver recv, CoreController core) {
      this.core = core;
      this.recv = recv;
   }

   @Override
   public void execute(Object input) {
      final RequestedOperation requestedOperation = (RequestedOperation)input;
      String var3 = CommandTransformer.getInstance().transform(requestedOperation);
      if (var3 != null) {
         if (requestedOperation.getData("host") != null) {
            var3 = String.format("dst=%s %s", requestedOperation.getData("host"), var3);
         }

         if (SpawnTerminalPredicate.getInstance().evaluate(requestedOperation.getKey())) {
            if (this.core.internalCommand((InternalCommandCallback)null, new String[]{"terminal", String.format("guiflag=focus %s", var3)})) {
               return;
            }

            Vector var4 = new Vector();
            var4.add(String.format("-cmd=%s", var3));

            try {
               this.core.startNewPlugin(Class.forName(GetActionTransformer.getInstance().getTerminalPath(), false, ExecutionClosure.class.getClassLoader()), "Terminal", var4, false, false);
               this.recv.sendStarted(requestedOperation.getId(), (TaskId)null, (PeerTag)null);
               if (requestedOperation.getCallback() != null) {
                  requestedOperation.getCallback().taskingExecuted(requestedOperation.getId(), (Object)null);
                  requestedOperation.setCallback((InternalCommandCallback)null);
               }

               return;
            } catch (ClassNotFoundException var7) {
               var7.printStackTrace();
            }
         }

         try {
            this.core.startCommand(var3, (taskId, var2x) -> {
               if (!requestedOperation.isLocal()) {
                  ExecutionClosure.this.recv.registerRemoteCommand(taskId);
               }

               requestedOperation.setTaskId(taskId);
               ExecutionClosure.this.recv.sendStarted(requestedOperation.getId(), taskId, (PeerTag)null);
               if (requestedOperation.getCallback() != null) {
                  requestedOperation.getCallback().taskingExecuted(requestedOperation.getId(), taskId);
                  requestedOperation.setCallback((InternalCommandCallback)null);
               }

            }, (Object)null, (HostInfo)null);
         } catch (DispatcherException var6) {
            var6.printStackTrace();
         }

      }
   }
}
