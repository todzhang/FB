package ddb.util.predicate;

import javax.swing.JToggleButton.ToggleButtonModel;
import org.apache.commons.collections.Predicate;

public class CheckBoxPredicate implements Predicate {
   private ToggleButtonModel model;

   public CheckBoxPredicate() {
      this(true);
   }

   public CheckBoxPredicate(boolean selected) {
      this.model = new ToggleButtonModel();
      this.model.setSelected(selected);
   }

   protected boolean isChecked() {
      return this.model.isSelected();
   }

   public ToggleButtonModel getModel() {
      return this.model;
   }

   @Override
   public boolean evaluate(Object o) {
      return this.isChecked();
   }

   @Override
   public String toString() {
      return "Checkbox Predicate";
   }
}
