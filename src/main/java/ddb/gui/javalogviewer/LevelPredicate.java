package ddb.gui.javalogviewer;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.commons.collections.Predicate;

public class LevelPredicate implements Predicate {
   Level level;

   public LevelPredicate() {
      this.level = Level.INFO;
   }

   public boolean evaluate(Object var1) {
      if (var1 instanceof LogRecord) {
         var1 = ((LogRecord)LogRecord.class.cast(var1)).getLevel();
      }

      if (var1 instanceof Level) {
         return ((Level)Level.class.cast(var1)).intValue() >= this.level.intValue();
      } else {
         return false;
      }
   }
}
