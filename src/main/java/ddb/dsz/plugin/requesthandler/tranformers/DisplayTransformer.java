package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.Format;

public class DisplayTransformer extends FormatTransformer {
   public static final DisplayTransformer INSTANCE = new DisplayTransformer();

   public static final DisplayTransformer getInstance() {
      return INSTANCE;
   }

   private DisplayTransformer() {
   }

   @Override
   protected Format getFormat(ActionType actionType) {
      return actionType != null ? actionType.getDisplay() : null;
   }

   @Override
   protected String getUserAliasFormat() {
      return "(%s)";
   }
}
