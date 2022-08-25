package ddb.dsz.core.command;

public interface GuiCommand extends CommandEvent {
   String getGuiCommand();

   boolean isHandled();

   void handled();
}
