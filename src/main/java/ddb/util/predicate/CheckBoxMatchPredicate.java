package ddb.util.predicate;

import javax.swing.JToggleButton.ToggleButtonModel;

public class CheckBoxMatchPredicate extends CheckBoxPredicate {
   private ToggleButtonModel model;

   public CheckBoxMatchPredicate() {
      this(true);
   }

   public CheckBoxMatchPredicate(boolean var1) {
      this.model = new ToggleButtonModel();
      this.model.setSelected(var1);
   }

   @Override
   protected boolean isChecked() {
      return this.model.isSelected();
   }

   @Override
   public ToggleButtonModel getModel() {
      return this.model;
   }

   @Override
   public boolean evaluate(Object o) {
      return this.isChecked() ? Boolean.TRUE.equals(o) : true;
   }

   @Override
   public String toString() {
      return "Checkbox Match Predicate";
   }
}
