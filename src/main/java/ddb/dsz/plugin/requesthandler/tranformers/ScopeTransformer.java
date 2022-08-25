package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.Format;

public class ScopeTransformer extends FormatTransformer {
   public static final ScopeTransformer INSTANCE = new ScopeTransformer();

   public static final ScopeTransformer getInstance() {
      return INSTANCE;
   }

   private ScopeTransformer() {
   }

   @Override
   protected Format getFormat(ActionType actionType) {
      return actionType != null ? actionType.getScope() : null;
   }

   @Override
   protected String getUserAliasFormat() {
      return "(%s)";
   }

   protected boolean isShowUser() {
      return false;
   }
}
