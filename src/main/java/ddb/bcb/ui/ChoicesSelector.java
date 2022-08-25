package ddb.bcb.ui;

import ddb.bcb.BreadcrumbBar;
import ddb.bcb.BreadcrumbItemChoices;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class ChoicesSelector extends JComponent implements BreadcrumbBar.BreadcrumbBarElement {
   protected BreadcrumbBar ownerBar;
   protected BreadcrumbItemChoices breadcrumbChoices = null;
   protected String separator;
   protected ButtonModel model;
   private static final String uiClassID = "ChoicesSelectorUI";

   public void setUI(ChoicesSelectorUI var1) {
      super.setUI(var1);
   }

   @Override
   public void updateUI() {
      if (UIManager.get(this.getUIClassID()) != null) {
         this.setUI((ChoicesSelectorUI)UIManager.getUI(this));
      } else {
         this.setUI(new BasicChoicesSelectorUI());
      }

   }

   public ChoicesSelectorUI getUI() {
      return (ChoicesSelectorUI)this.ui;
   }

   @Override
   public String getUIClassID() {
      return "ChoicesSelectorUI";
   }

   public ChoicesSelector(BreadcrumbBar var1, BreadcrumbItemChoices var2, String var3) {
      this.breadcrumbChoices = var2;
      this.ownerBar = var1;
      this.separator = var3;
      this.model = new DefaultButtonModel();
      this.updateUI();
   }

   @Override
   public String getText() {
      return this.breadcrumbChoices.getIndex() == 0 ? "" : this.separator;
   }

   @Override
   public int getIndex() {
      return this.breadcrumbChoices.getIndex();
   }

   public BreadcrumbBar getOwnerBar() {
      return this.ownerBar;
   }

   public BreadcrumbItemChoices getBreadcrumbChoices() {
      return this.breadcrumbChoices;
   }

   public void setBreadcrumbChoices(BreadcrumbItemChoices var1) {
      this.breadcrumbChoices = var1;
   }

   public ButtonModel getModel() {
      return this.model;
   }
}
