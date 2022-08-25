package ds.core.commanddispatcher;

import ddb.dsz.core.command.CommandEvent.CommandEventType;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.operation.MutableOperation;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.predicate.PredicateClosure;
import ddb.predicate.PredicateClosureImpl;
import ddb.util.DszDefaultHandler;
import ddb.util.FinishedProcessingException;
import ddb.util.GeneralUtilities;
import ddb.util.Guid;
import ds.core.commandevents.CommandEventImpl;
import ds.core.controller.MutableCoreController;
import ds.core.impl.HostInfoImpl;
import ds.core.impl.OperationImpl;
import ds.core.impl.task.FileAccess;
import ds.core.impl.task.TaskImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.SwitchClosure;
import org.xml.sax.SAXException;

public class ReplayCommandDispatcher extends AbstractCommandDispatcher implements CommandDispatcher {
   public static ReplayCommandDispatcher REF = null;
   PredicateClosure[] elementHandlerList = new PredicateClosure[]{new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("DataLog"), ClosureUtils.nopClosure()), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Instance"), new Closure() {
      @Override
      public void execute(Object input) {
         DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)input;
         OperationImpl var3 = OperationImpl.GenerateOperation(Guid.GenerateGuid(var2.getAttribute("id")), GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
         ReplayCommandDispatcher.super.setOperation(var3);
         ReplayCommandDispatcher.this.setFinished(ReplayCommandDispatcher.this.guidOnly);
         ReplayCommandDispatcher.this.stop = ReplayCommandDispatcher.this.guidOnly;
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Target"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            StringBuilder var3 = new StringBuilder();
            var3.append(var2.getAttribute("majorVersion"));
            var3.append(".");
            var3.append(var2.getAttribute("minorVersion"));
            var3.append(".");
            var3.append(var2.getAttribute("otherVersion"));
            ReplayCommandDispatcher.this.mutableCore.addHostInfo(new HostInfoImpl(var2.getAttribute("addr"), "unknown", var3.toString(), var2.getAttribute("arch"), var2.getAttribute("platform"), "unknown", true, false));
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Alias"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Connect"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("targetAddr", "z0.0.0.0");
            String var5 = String.format("%s.%s.%s", var2.getAttribute("versionMajor", "0"), var2.getAttribute("versionMinor", "0"), var2.getAttribute("versionRevision", "0"));
            String var6 = var2.getAttribute("hostname", "unknown");
            String var7 = var2.getAttribute("arch", "unknown arch");
            String var8 = var2.getAttribute("os", "unknown os");
            String var9 = "unknown";
            boolean var10 = false;
            boolean var11 = true;
            HostInfoImpl var12 = new HostInfoImpl(var4, var6, var5, var7, var8, var9, var10, var11);
            ReplayCommandDispatcher.this.mutableCore.addHostInfo(var12);
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Disconnect"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("targetAddr", "z0.0.0.0");
            ReplayCommandDispatcher.this.mutableCore.disconnected(var4);
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandInput"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("defaultTarget", "0.0.0.1");
            String var5 = var2.getAttribute("id");
            String var6 = var2.getAttribute("lptimestamp");
            String var7 = var2.getAttribute("pid");
            String var8 = var2.getText();
            TaskImpl var9 = new TaskImpl(var8, (HostInfo)null);
            var9.setHost(ReplayCommandDispatcher.this.core.getHostById(var4));
            var9.setId(ReplayCommandDispatcher.this.createTaskId(Integer.parseInt(var5)));
            if (var7 != null) {
               int var10 = Integer.parseInt(var7);
               if (var10 > 0) {
                  TaskId var11 = ReplayCommandDispatcher.this.createTaskId(var10);
                  Task var12 = ReplayCommandDispatcher.this.core.getTaskById(var11);
                  if (var12 == null) {
                     System.out.println("break");
                  }

                  var9.setParent(var12);
               }
            }

            var9.setCreated(GeneralUtilities.stringToCalendar(var6, (Calendar)null));
            var9.setState(TaskState.TASKED);
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandStart"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("id");
            Guid var5 = Guid.GenerateGuid(var2.getAttribute("taskId"));
            String var6 = var2.getText();
            MutableTask var7 = (MutableTask)MutableTask.class.cast(ReplayCommandDispatcher.this.core.getTaskById(ReplayCommandDispatcher.this.createTaskId(Integer.parseInt(var4))));
            if (var7 != null) {
               var7.setState(TaskState.RUNNING);
               var7.setFullCommandLine(var6);
               var7.setTaskId(var5);
            }

            var7.setResourceDirectory((String)null);
            ReplayCommandDispatcher.this.mutableCore.addNewTask(var7);
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandXml"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("id");
            String var5 = var2.getAttribute("name");
            String var6 = var2.getAttribute("storageTransform");
            String var7 = var2.getAttribute("displayTransform");
            String var8 = var2.getAttribute("targetAddr");
            String var9 = var2.getAttribute("taskId");
            String var10 = var2.getText();
            MutableTask var11 = (MutableTask)MutableTask.class.cast(ReplayCommandDispatcher.this.core.getTaskById(ReplayCommandDispatcher.this.createTaskId(Integer.parseInt(var4))));
            if (var11 != null) {
               var11.setHost(ReplayCommandDispatcher.this.core.getHostById(var8));
               var11.setTaskingInformation(new FileAccess(var11, DataType.TASKING, new File(String.format("%s/%s", ReplayCommandDispatcher.this.core.getLogDirectory(), var10)), var10, var11.getNextOrdinal()));
               var11.setCommandName(var5);
               var11.setStorageTransform(var6);
               var11.setDisplayTransform(var7);
               if (var9 != null) {
                  var11.setTaskId(Guid.GenerateGuid(var9));
               }

               CommandEventImpl var12 = new CommandEventImpl(ReplayCommandDispatcher.this, CommandEventType.STARTED, var11);
               var12.setCurrentOperation(false);
               var12.setTimestamp(var11.getCreated());
               var12.setTargetAddress(var11.getTargetId());
               ReplayCommandDispatcher.this.publishEvent(var12);
            }
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandInterrupt"), ClosureUtils.nopClosure()), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("DataXml"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            try {
               Task var4 = null;
               String var5 = var2.getAttribute("id");
               if (var5 == null) {
                  Guid var6 = Guid.GenerateGuid(var2.getAttribute("taskId"));
                  var4 = ReplayCommandDispatcher.this.core.getTaskByTaskId(var6);
               } else {
                  int var9 = Integer.parseInt(var2.getAttribute("id"));
                  TaskId var7 = ReplayCommandDispatcher.this.createTaskId(var9);
                  var4 = ReplayCommandDispatcher.this.core.getTaskById(var7);
               }

               if (var4 == null) {
                  return;
               }

               String var10 = var2.getText();
               MutableTask var11 = (MutableTask)MutableTask.class.cast(var4);
               var11.addDataInformation(new FileAccess(var4, DataType.DATA, new File(ReplayCommandDispatcher.this.core.getLogDirectory(), var10), var10, var4.getNextOrdinal()));
            } catch (Exception var8) {
               var8.printStackTrace();
            }

         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandLog"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("id");
            String var5 = var2.getText();
            MutableTask var6 = (MutableTask)MutableTask.class.cast(ReplayCommandDispatcher.this.core.getTaskById(ReplayCommandDispatcher.this.createTaskId(Integer.parseInt(var4))));
            if (var6 != null) {
               var6.setTaskLog(new FileAccess(var6, DataType.LOG, new File(ReplayCommandDispatcher.this.core.getLogDirectory(), var5), var5, -1));
            }
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("CommandEnd"), new Closure() {
      @Override
      public void execute(Object var1) {
         if (!ReplayCommandDispatcher.this.guidOnly) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            Operation var3 = ReplayCommandDispatcher.this.getOperation();
            if (var3 instanceof MutableOperation) {
               ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
            }

            String var4 = var2.getAttribute("id");
            String var5 = var2.getAttribute("status");
            MutableTask var6 = (MutableTask)MutableTask.class.cast(ReplayCommandDispatcher.this.core.getTaskById(ReplayCommandDispatcher.this.createTaskId(Integer.parseInt(var4))));
            if (var6 != null) {
               var6.setState(TaskState.parseResult(var5));
               CommandEventImpl var7 = new CommandEventImpl(this, CommandEventType.ENDED, var6);
               var7.setCurrentOperation(false);
               var7.setTimestamp(var6.getCreated());
               var7.setTargetAddress(var6.getTargetId());
               ReplayCommandDispatcher.this.publishEvent(var7);
            }
         }
      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("Shutdown"), new Closure() {
      @Override
      public void execute(Object var1) {
         DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
         ReplayCommandDispatcher.this.stop = true;
         Operation var3 = ReplayCommandDispatcher.this.getOperation();
         if (var3 instanceof MutableOperation) {
            ((MutableOperation)MutableOperation.class.cast(var3)).setLastTime(GeneralUtilities.stringToCalendar(var2.getAttribute("lptimestamp"), (Calendar)null));
         }

      }
   }), new PredicateClosureImpl(new ReplayCommandDispatcher.ElementNameMatches("ErrorString"), ClosureUtils.nopClosure())};
   Closure elementHandler;
   File logFile;
   MutableCoreController mutableCore;
   boolean guidOnly;

   public ReplayCommandDispatcher(EventPublisher publisher, MutableCoreController mutableCore, File logFile) {
      super(publisher, mutableCore);
      this.elementHandler = SwitchClosure.getInstance(this.elementHandlerList, this.elementHandlerList, new Closure() {
         @Override
         public void execute(Object var1) {
            DszDefaultHandler.Element var2 = (DszDefaultHandler.Element)var1;
            System.out.println("Unhandled element: " + var2.name);
         }
      });
      this.guidOnly = true;
      REF = this;
      this.mutableCore = mutableCore;
      this.logFile = logFile;
      this.parseFile();
      this.guidOnly = false;
      this.setFinished(false);
      this.stop = false;
   }

   protected InputStream getFileStream(String file) throws FileNotFoundException {
      return new FileInputStream(new File(file));
   }

   public void parseFile() {
      try {
         SAXParserFactory var1 = SAXParserFactory.newInstance();
         SAXParser var2 = var1.newSAXParser();
         var2.parse(this.getFileStream(this.logFile.getAbsolutePath()), new DszDefaultHandler() {
            @Override
            protected void handleElement(DszDefaultHandler.Element element) throws SAXException {
               ReplayCommandDispatcher.this.elementHandler.execute(element);
               if (ReplayCommandDispatcher.this.stop) {
                  throw FinishedProcessingException.FINISHED;
               }
            }
         });
         this.setFinished(true);
      } catch (SAXException var3) {
         if (var3.getException() instanceof FinishedProcessingException) {
            this.setFinished(true);
            return;
         }

         var3.printStackTrace();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public long getFileSize() {
      return this.logFile.length();
   }

   public File getLogFile() {
      return this.logFile;
   }

   protected boolean noWaiting() {
      return false;
   }

   private class ElementNameMatches implements Predicate {
      String name;

      public ElementNameMatches(String name) {
         this.name = name;
      }

      @Override
      public boolean evaluate(Object object) {
         return object instanceof DszDefaultHandler.Element ? this.name.equals(((DszDefaultHandler.Element)DszDefaultHandler.Element.class.cast(object)).name) : false;
      }
   }
}
