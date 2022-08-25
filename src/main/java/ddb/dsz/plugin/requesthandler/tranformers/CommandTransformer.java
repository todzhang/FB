package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.Format;

public class CommandTransformer extends FormatTransformer {
   public static final CommandTransformer INSTANCE = new CommandTransformer();

   public static final CommandTransformer getInstance() {
      return INSTANCE;
   }

   private CommandTransformer() {
   }

   @Override
   protected Format getFormat(ActionType actionType) {
      return actionType != null ? actionType.getCommand() : null;
   }

   @Override
   protected String getUserAliasFormat() {
      return "user=%s";
   }
}
