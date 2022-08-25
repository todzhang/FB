package ddb.dsz.core.command;

import ddb.Factory;

public abstract class CommandEventDemultiplexor extends Factory implements CommandEventListener, CommandEventManager {
   public static final String DEFAULT_IMPL = "CommandEventDemultiplexor.impl";

   public static final CommandEventDemultiplexor newInstance() {
      return (CommandEventDemultiplexor)Factory.newObject(System.getProperty("CommandEventDemultiplexor.impl"), new Class[0]);
   }

   public abstract void addCommandEventListener(String var1, CommandEventListener commandEventListener);

   @Override
   public abstract void addCommandEventListener(CommandEventListener commandEventListener);

   public abstract void addCommandEventListenerAll(CommandEventListener commandEventListener);

   public abstract void addCommandEventListenerDefault(CommandEventListener commandEventListener);

   public abstract void removeCommandEventListener(String var1, CommandEventListener commandEventListener);

   public abstract void removeCommandEventListenerAll(CommandEventListener commandEventListener);

   public abstract void removeCommandEventListenerDefault(CommandEventListener commandEventListener);

   public abstract void stop();
}
