package ds.core;

import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.util.JaxbCache;
import ds.jaxb.ipc.AddPrefixesType;
import ds.jaxb.ipc.CommandListType;
import ds.jaxb.ipc.CommandType;
import ds.jaxb.ipc.GetHelpType;
import ds.jaxb.ipc.GetStatisticsType;
import ds.jaxb.ipc.GuiCommandResponse;
import ds.jaxb.ipc.HelpType;
import ds.jaxb.ipc.InfoType;
import ds.jaxb.ipc.InterruptCommandType;
import ds.jaxb.ipc.ListCommandsType;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.ObjectFactory;
import ds.jaxb.ipc.PingType;
import ds.jaxb.ipc.PongType;
import ds.jaxb.ipc.PromptStoppedType;
import ds.jaxb.ipc.RequestType;
import ds.jaxb.ipc.ResponseType;
import ds.jaxb.ipc.RestartOutputType;
import ds.jaxb.ipc.ShutdownType;
import ds.jaxb.ipc.StartCommandType;
import ds.jaxb.ipc.StartPromptType;
import ds.jaxb.ipc.StatisticsType;
import ds.jaxb.ipc.StopCommandType;
import ds.jaxb.ipc.StopOutputType;
import ds.jaxb.ipc.StopPromptType;
import ds.jaxb.ipc.UserEntryType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;

public class CommandFormatter {
   protected File schema;
   protected ObjectFactory objFact;
   protected JAXBContext jaxbContext;
   protected final Object MARSH_LOCK;
   protected Marshaller marshaller;
   protected final Object UNMARSH_LOCK;
   protected Unmarshaller unmarshaller;
   private int nextRequestId;
   Transformer createMessage;
   Transformer createRequest;
   Transformer commandListTransformer;
   Transformer getStatisticsTransformer;
   Transformer getPingTransformer;
   Transformer createShutdown;
   Transformer createStartCommand;
   Transformer createInterruptCommand;
   Transformer createStopCommand;
   Transformer createStopOutput;
   Transformer createRestartOutput;
   Transformer createResponse;

   public CommandFormatter(String schema) throws JAXBException {
      this(new File(schema));
   }

   public CommandFormatter(File schema) throws JAXBException {
      this.MARSH_LOCK = new Object();
      this.UNMARSH_LOCK = new Object();
      this.nextRequestId = 1;
      this.createMessage = new CommandFormatter.CreateMessage();
      this.createRequest = ChainedTransformer.getInstance(new CommandFormatter.CreateRequest(), this.createMessage);
      this.commandListTransformer = ChainedTransformer.getInstance(new CommandFormatter.CommandList(), this.createRequest);
      this.getStatisticsTransformer = ChainedTransformer.getInstance(new CommandFormatter.GetStatistics(), this.createRequest);
      this.getPingTransformer = ChainedTransformer.getInstance(new CommandFormatter.GetPing(), this.createRequest);
      this.createShutdown = ChainedTransformer.getInstance(new CommandFormatter.CreateShutdown(), this.createMessage);
      this.createStartCommand = ChainedTransformer.getInstance(new CommandFormatter.CreateStartCommand(), this.createMessage);
      this.createInterruptCommand = ChainedTransformer.getInstance(new CommandFormatter.CreateInteruptCommand(), this.createMessage);
      this.createStopCommand = ChainedTransformer.getInstance(new CommandFormatter.CreateStopCommand(), this.createMessage);
      this.createStopOutput = ChainedTransformer.getInstance(new CommandFormatter.CreateStopOutput(), this.createMessage);
      this.createRestartOutput = ChainedTransformer.getInstance(new CommandFormatter.CreateRestartOutput(), this.createMessage);
      this.createResponse = ChainedTransformer.getInstance(new CommandFormatter.CreateResponse(), this.createMessage);
      this.schema = schema;
      this.objFact = new ObjectFactory();
      this.jaxbContext = JaxbCache.getContext(ObjectFactory.class);
      this.marshaller = this.jaxbContext.createMarshaller();
      this.marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
      this.unmarshaller = this.jaxbContext.createUnmarshaller();
   }

   public byte[] formatMessageAsByteArray(Message message) throws JAXBException {
      if (message == null) {
         return new byte[0];
      } else {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         synchronized(this.MARSH_LOCK) {
            this.marshaller.marshal(message, byteArrayOutputStream);
         }

         return byteArrayOutputStream.toByteArray();
      }
   }

   public String formatMessageAsString(Message message) throws JAXBException {
      if (message == null) {
         return "";
      } else {
         byte[] var2 = this.formatMessageAsByteArray(message);
         return new String(var2);
      }
   }

   public Message unmarshallReceivedBytes(InputStream inputStream) throws JAXBException {
      synchronized(this.UNMARSH_LOCK) {
         return (Message)this.unmarshaller.unmarshal(inputStream);
      }
   }

   public Message unmarshallReceivedBytes(Reader reader) throws JAXBException {
      synchronized(this.UNMARSH_LOCK) {
         return (Message)this.unmarshaller.unmarshal(reader);
      }
   }

   public Message createStartCommand(Task task) {
      return (Message)this.createStartCommand.transform(task);
   }

   public Message createStopCommand(TaskId taskId) {
      return (Message)this.createStopCommand.transform(taskId);
   }

   public Message createGuiCommandResponse(int reqId, boolean success) {
      GuiCommandResponse var3 = new GuiCommandResponse();
      var3.setSuccess(success);
      Message var4 = (Message)this.createResponse.transform(var3);
      var4.getRes().setReqId(reqId);
      return var4;
   }

   public Message createInterruptCommand(TaskId taskId) {
      return (Message)this.createInterruptCommand.transform(taskId);
   }

   public Message createStopOutput(TaskId taskId) {
      return (Message)this.createStopOutput.transform(taskId);
   }

   public Message createRestartOutput(TaskId taskId) {
      return (Message)this.createRestartOutput.transform(taskId);
   }

   public Message createAddPrefixes(TaskId taskId, List<String> prefixes) {
      AddPrefixesType var3 = this.objFact.createAddPrefixesType();
      var3.setCmdId(taskId.getId());
      var3.getPrefix().addAll(prefixes);
      CommandType var4 = this.objFact.createCommandType();
      var4.setAddPrefixes(var3);
      var4.setCmdId(taskId.getId());
      return (Message)this.createMessage.transform(var4);
   }

   public Message createListCommands() {
      return (Message)this.commandListTransformer.transform((Object)null);
   }

   public Message createGetStatistics() {
      return (Message)this.getStatisticsTransformer.transform((Object)null);
   }

   public Message createShutdown() {
      return (Message)this.createShutdown.transform((Object)null);
   }

   public Message createPing() {
      return (Message)this.getPingTransformer.transform((Object)null);
   }

   public Message createGetHelp(String helptype, HostInfo hostInfo) {
      GetHelpType getHelpType = this.objFact.createGetHelpType();
      getHelpType.setValue(helptype);
      getHelpType.setTarget(hostInfo.getId());
      Message var4 = (Message)this.createRequest.transform(getHelpType);
      return var4;
   }

   public Message createUserEntry(int reqId, TaskId taskId, String cmdvalue) {
      UserEntryType userEntryType = this.objFact.createUserEntryType();
      userEntryType.setCmdId(taskId.getId());
      userEntryType.setValue(cmdvalue);
      ResponseType responseType = this.objFact.createResponseType();
      responseType.setUserEntry(userEntryType);
      responseType.setReqId(reqId);
      return (Message)this.createMessage.transform(responseType);
   }

   public Message createPromptStopped(int reqId, TaskId taskId) {
      PromptStoppedType promptStoppedType = this.objFact.createPromptStoppedType();
      promptStoppedType.setCmdId(taskId.getId());
      promptStoppedType.setValue("");
      ResponseType responseType = this.objFact.createResponseType();
      responseType.setPromptStopped(promptStoppedType);
      responseType.setReqId(reqId);
      return (Message)this.createMessage.transform(responseType);
   }

   private class CreateRestartOutput implements Transformer {
      private CreateRestartOutput() {
      }

      @Override
      public Object transform(Object input) {
         RestartOutputType restartOutputType = CommandFormatter.this.objFact.createRestartOutputType();
         restartOutputType.setValue("");
         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setRestartOutput(restartOutputType);
         if (input instanceof TaskId) {
            commandType.setCmdId(((TaskId)TaskId.class.cast(input)).getId());
         }

         return commandType;
      }

      // $FF: synthetic method
      CreateRestartOutput(Object var2) {
         this();
      }
   }

   private class CreateStopOutput implements Transformer {
      private CreateStopOutput() {
      }

      @Override
      public Object transform(Object input) {
         StopOutputType stopOutputType = CommandFormatter.this.objFact.createStopOutputType();
         stopOutputType.setValue("");
         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setStopOutput(stopOutputType);
         if (input instanceof TaskId) {
            commandType.setCmdId(((TaskId)TaskId.class.cast(input)).getId());
         }

         return commandType;
      }

      // $FF: synthetic method
      CreateStopOutput(Object var2) {
         this();
      }
   }

   private class CreateInteruptCommand implements Transformer {
      private CreateInteruptCommand() {
      }

      @Override
      public Object transform(Object input) {
         InterruptCommandType interruptCommandType = CommandFormatter.this.objFact.createInterruptCommandType();
         interruptCommandType.setValue("");
         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setInterruptCommand(interruptCommandType);
         if (input instanceof TaskId) {
            commandType.setCmdId(((TaskId)TaskId.class.cast(input)).getId());
         }

         return commandType;
      }

      // $FF: synthetic method
      CreateInteruptCommand(Object var2) {
         this();
      }
   }

   private class CreateStopCommand implements Transformer {
      private CreateStopCommand() {
      }

      @Override
      public Object transform(Object input) {
         StopCommandType stopCommandType = CommandFormatter.this.objFact.createStopCommandType();
         stopCommandType.setValue("");
         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setStopCommand(stopCommandType);
         if (input instanceof TaskId) {
            commandType.setCmdId(((TaskId)TaskId.class.cast(input)).getId());
         }

         return commandType;
      }

      // $FF: synthetic method
      CreateStopCommand(Object var2) {
         this();
      }
   }

   private class CreateStartCommand implements Transformer {
      private CreateStartCommand() {
      }

      @Override
      public Object transform(Object input) {
         StartCommandType startCommandType = CommandFormatter.this.objFact.createStartCommandType();
         if (input instanceof Task) {
            Task task = (Task)Task.class.cast(input);
            startCommandType.setTmpId(task.getTempId());
            startCommandType.setValue(task.getTypedCommand());
            if (task.getProspectiveHost() != null) {
               startCommandType.setTarget(task.getProspectiveHost().getId());
            }
         }

         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setStartCommand(startCommandType);
         commandType.setCmdId(0);
         return commandType;
      }

      // $FF: synthetic method
      CreateStartCommand(Object var2) {
         this();
      }
   }

   private class GetPing implements Transformer {
      private GetPing() {
      }

      @Override
      public Object transform(Object input) {
         PingType pingType = CommandFormatter.this.objFact.createPingType();
         pingType.setValue("");
         return pingType;
      }

      // $FF: synthetic method
      GetPing(Object var2) {
         this();
      }
   }

   private class GetStatistics implements Transformer {
      private GetStatistics() {
      }

      @Override
      public Object transform(Object input) {
         GetStatisticsType getStatisticsType = CommandFormatter.this.objFact.createGetStatisticsType();
         getStatisticsType.setValue("");
         return getStatisticsType;
      }

      // $FF: synthetic method
      GetStatistics(Object var2) {
         this();
      }
   }

   private class CommandList implements Transformer {
      private CommandList() {
      }

      @Override
      public Object transform(Object input) {
         ListCommandsType listCommandsType = CommandFormatter.this.objFact.createListCommandsType();
         listCommandsType.setValue("");
         return listCommandsType;
      }

      // $FF: synthetic method
      CommandList(Object var2) {
         this();
      }
   }

   private class CreateResponse implements Transformer {
      private CreateResponse() {
      }

      @Override
      public Object transform(Object input) {
         ResponseType responseType = CommandFormatter.this.objFact.createResponseType();
         if (input instanceof CommandListType) {
            responseType.setCommandList((CommandListType)CommandListType.class.cast(input));
         } else if (input instanceof GuiCommandResponse) {
            responseType.setGuiCommand((GuiCommandResponse)GuiCommandResponse.class.cast(input));
         } else if (input instanceof HelpType) {
            responseType.setHelp((HelpType)HelpType.class.cast(input));
         } else if (input instanceof PongType) {
            responseType.setPong((PongType)PongType.class.cast(input));
         } else if (input instanceof PromptStoppedType) {
            responseType.setPromptStopped((PromptStoppedType)PromptStoppedType.class.cast(input));
         } else if (input instanceof StatisticsType) {
            responseType.setStatistics((StatisticsType)StatisticsType.class.cast(input));
         } else if (input instanceof UserEntryType) {
            responseType.setUserEntry((UserEntryType)UserEntryType.class.cast(input));
         }

         return responseType;
      }

      // $FF: synthetic method
      CreateResponse(Object var2) {
         this();
      }
   }

   private class CreateRequest implements Transformer {
      private CreateRequest() {
      }

      @Override
      public Object transform(Object input) {
         RequestType requestType = CommandFormatter.this.objFact.createRequestType();
         if (input instanceof GetHelpType) {
            requestType.setGetHelp(GetHelpType.class.cast(input));
         } else if (input instanceof GetStatisticsType) {
            requestType.setGetStatistics(GetStatisticsType.class.cast(input));
         } else if (input instanceof ListCommandsType) {
            requestType.setListCommands(ListCommandsType.class.cast(input));
         } else if (input instanceof PingType) {
            requestType.setPing(PingType.class.cast(input));
         } else if (input instanceof StartPromptType) {
            requestType.setStartPrompt(StartPromptType.class.cast(input));
         } else if (input instanceof StopPromptType) {
            requestType.setStopPrompt(StopPromptType.class.cast(input));
         }

         requestType.setReqId(CommandFormatter.this.nextRequestId++);
         return requestType;
      }

      // $FF: synthetic method
      CreateRequest(Object var2) {
         this();
      }
   }

   private class CreateMessage implements Transformer {
      private CreateMessage() {
      }

      @Override
      public Object transform(Object input) {
         Message message = CommandFormatter.this.objFact.createMessage();
         if (input instanceof CommandType) {
            message.setCmd(CommandType.class.cast(input));
         } else if (input instanceof InfoType) {
            message.setInfo(InfoType.class.cast(input));
         } else if (input instanceof RequestType) {
            message.setReq(RequestType.class.cast(input));
         } else if (input instanceof ResponseType) {
            message.setRes(ResponseType.class.cast(input));
         }

         return message;
      }

      // $FF: synthetic method
      CreateMessage(Object var2) {
         this();
      }
   }

   private class CreateShutdown implements Transformer {
      private CreateShutdown() {
      }

      @Override
      public Object transform(Object input) {
         ShutdownType shutdownType = CommandFormatter.this.objFact.createShutdownType();
         shutdownType.setValue("");
         CommandType commandType = CommandFormatter.this.objFact.createCommandType();
         commandType.setShutdown(shutdownType);
         commandType.setCmdId(0);
         return commandType;
      }

      // $FF: synthetic method
      CreateShutdown(Object var2) {
         this();
      }
   }
}
