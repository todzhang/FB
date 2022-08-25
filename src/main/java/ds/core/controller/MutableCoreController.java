package ds.core.controller;

import ddb.dsz.core.connection.events.StatisticsEvent;
import ddb.dsz.core.connection.events.ThrottleEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.peer.PeerTag;
import ds.core.commanddispatcher.MultipleCommandDispatcherClient;
import ds.core.pluginevents.PluginEventListener;
import ds.gui.PluginWorkbench;
import ds.jaxb.ipc.Message;
import ds.plugin.peer.Peer;

public interface MutableCoreController extends CoreController {
   void lpConnectionTerminated();

   void shutdown(boolean id);

   Task getTaskByTempId(int id);

   void registerTaskId(MutableTask mutableTask);

   Task registerId(int id, TaskId taskId);

   void addNewTask(MutableTask mutableTask);

   void updateConnectionInfo(Message message);

   void updateStatistics(StatisticsEvent statisticsEvent);

   void exceptionOccurredInPlugin(Plugin plugin);

   void offerOperation(Operation operation);

   MultipleCommandDispatcherClient getDispatcherClient();

   void setDispatcherClient(MultipleCommandDispatcherClient multipleCommandDispatcherClient);

   void taskEnded(Task task);

   void applicationEnded(String reason);

   void registerPeer(Peer peer);

   void firePeerConnected(PeerTag peerTag);

   void firePeerDisconnected(PeerTag peerTag);

   void fireReceivedMessage(String message, PeerTag peerTag);

   HostInfo addHostInfo(HostInfo hostInfo);

   void disconnected(String id);

   void stopPrompt(Task task);

   void addOperation(Operation operation);

   void addPluginEventListener(PluginEventListener pluginEventListener);

   void removePluginEventListener(PluginEventListener pluginEventListener);

   PluginWorkbench getWorkbench();

   void fireThrottleEvent(ThrottleEvent throttleEvent);
}
