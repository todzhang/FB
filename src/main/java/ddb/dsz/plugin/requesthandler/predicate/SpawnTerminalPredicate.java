package ddb.dsz.plugin.requesthandler.predicate;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.tranformers.GetActionTransformer;
import org.apache.commons.collections.Predicate;

public class SpawnTerminalPredicate implements Predicate {
   public static final SpawnTerminalPredicate INSTANCE = new SpawnTerminalPredicate();

   public static final SpawnTerminalPredicate getInstance() {
      return INSTANCE;
   }

   private SpawnTerminalPredicate() {
   }

   @Override
   public boolean evaluate(Object object) {
      ActionType actionType = GetActionTransformer.getInstance().transform(object);
      return actionType == null ? false : actionType.isSpawnTerminal();
   }
}
