package ddb.dsz.core.command;

public interface CommandEventManager {
   void addCommandEventListener(CommandEventListener commandEventListener);

   void removeCommandEventListener(CommandEventListener commandEventListenerS);
}
