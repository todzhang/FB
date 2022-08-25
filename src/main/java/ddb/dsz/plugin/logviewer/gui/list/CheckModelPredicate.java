package ddb.dsz.plugin.logviewer.gui.list;

import javax.swing.ButtonModel;
import org.apache.commons.collections.Predicate;

public class CheckModelPredicate implements Predicate {
   ButtonModel model;

   public CheckModelPredicate(ButtonModel model) {
      this.model = model;
   }

   @Override
   public boolean evaluate(Object object) {
      return this.model.isSelected();
   }

   @Override
   public String toString() {
      return "CheckModel Predicate";
   }
}
