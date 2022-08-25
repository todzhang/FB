package ddb.dsz.core.controller;

import ddb.detach.Alignment;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.peer.PeerReceiver;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.peer.PeerTransferStatus;
import ddb.util.Guid;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public interface CoreController extends Executor, ThreadFactory {
   void startCommand(String var1, IdCallback idCallback, Object var3, HostInfo hostInfo) throws DispatcherException;

   void requestHelpStatement(String var1, HostInfo hostInfo) throws DispatcherException;

   void killCommand(Task task) throws DispatcherException;

   void interruptCommand(Task task) throws DispatcherException;

   void stopCommandOutput(Task task) throws DispatcherException;

   void restartCommandOutput(Task task) throws DispatcherException;

   void addPrefixesToTask(TaskId var1, List<String> var2) throws DispatcherException;

   void sendPromptReply(int var1, TaskId taskId, String var3) throws DispatcherException;

   Task getRunningTaskById(TaskId taskId);

   Task getTaskById(TaskId taskId);

   Task getTaskByTaskId(Guid guid);

   String getLogDirectory();

   String getResourceDirectory();

   String[] getResourcePackages();

   String getBuildType();

   String getUserConfigDirectory();

   File createLogFile(String var1);

   boolean isDebugMode();

   boolean isLiveOperation();

   CommandSet getCommandSet();

   Collection<Task> getTaskList();

   Logger getSystemLogger();

   void addCommandEventListener(CommandEventListener commandEventListener);

   void removeCommandEventListener(CommandEventListener commandEventListener);

   void unhidePlugin(Plugin plugin);

   void hidePlugin(Plugin plugin);

   boolean allowNewInstance(Class<?> var1);

   boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5);

   boolean startNewPlugin(Class<?> var1, String var2, List<String> var3, boolean var4, boolean var5, Alignment alignment);

   void closePlugin(Plugin plugin);

   void addInternalCommandHandler(InternalCommandHandler internalCommandHandler);

   void removeInternalCommandHandler(InternalCommandHandler internalCommandHandler);

   boolean internalCommand(InternalCommandCallback internalCommandCallback, List<String> var2);

   boolean internalCommand(InternalCommandCallback internalCommandCallback, String... var2);

   void addConnectionChangeListener(ConnectionChangeListener connectionChangeListener);

   void removeConnectionChangeListener(ConnectionChangeListener connectionChangeListener);

   void logEvent(Level level, String var2);

   void logEvent(Level level, String var2, String var3);

   void logEvent(Level level, String var2, Throwable throwable);

   void logEvent(Level level, String var2, String var3, Throwable throwable);

   void pluginStarted(Plugin plugin);

   void pluginStopped(Plugin plugin);

   void setOption(Class<? extends Plugin> plugin, String var2, Object var3);

   void setOption(Plugin plugin, String var2, Object var3);

   Object getOption(Class<? extends Plugin> plugin, String var2);

   Object getOption(Plugin plugin, String var2);

   Object getOption(Class<? extends Plugin> plugin, String var2, Object var3);

   Object getOption(Plugin plugin, String var2, Object var3);

   void setObject(Class<? extends Plugin> plugin, String var2, Serializable var3);

   void setObject(Plugin plugin, String var2, Serializable var3);

   <E extends Serializable> E getObject(Class<? extends Plugin> plugin, String var2, Class<? extends E> var3);

   <E extends Serializable> E getObject(Plugin plugin, String var2, Class<? extends E> var3);

   void commitSettings();

   <V> ScheduledFuture<V> schedule(Callable<V> callable, long var2, TimeUnit timeUnit);

   ScheduledFuture<?> schedule(Runnable runnable, long var2, TimeUnit timeUnit);

   ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long var2, long var4, TimeUnit timeUnit);

   ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long var2, long var4, TimeUnit timeUnit);

   <T> Future<T> submit(Callable<T> callable);

   <T> Future<T> submit(Runnable runnable, T var2);

   Future<?> submit(Runnable runnable);

   boolean remove(Runnable runnable);

   boolean isLocalMode();

   Operation getOperation();

   void setupKeyBindings(JComponent jComponent);

   Map<KeyStroke, String> getKeyBindings();

   void showPrompt(Task task, int var2, String var3);

   int requestStatistics();

   String getDefaultPackage();

   void StealFocus(Plugin plugin);

   boolean hasConnected();

   void addPeerReceiver(PeerReceiver peerReceiver);

   void removePeerReceiver(PeerReceiver peerReceiver);

   PeerTransferStatus sendMessageToPeer(String var1);

   PeerTransferStatus sendMessageToPeer(String var1, PeerTag peerTag);

   HostInfo getHostById(String var1);

   List<HostInfo> getHosts();

   String getOpDir();

   int getDispatcherPort();

   String getLocalhostAddress();

   String claimPrompt(Task task);

   CoreController.OperationState getOperationState();

   String getTitle();

   Collection<Operation> getOperationList();

   Operation getOperationById(Guid guid);

   Operation getOperationById(BigInteger id);

   void detachPlugin(Plugin plugin, Dimension dimension, Point point);

   void sendGuiCommandResponse(int var1, boolean var2);

   boolean isFullStop();

   Thread newThread(String var1, Runnable var2);

   String translate(String var1);

   List<TaskId> getTaskChildren(TaskId taskId);

   Dimension getLabelImageSize();

   Dimension getTabImageSize();

   void setCommandEnvironmentVariable(String var1, String var2);

   void setCommandEnvironmentVariable(String var1, String var2, HostInfo var3);

   List<String> getUserAliases(HostInfo hostInfo);

   void setTitle(String var1);

   void requestShutdown();

   public enum OperationState {
      NotConnected,
      Connected,
      Inactive;
   }
}
