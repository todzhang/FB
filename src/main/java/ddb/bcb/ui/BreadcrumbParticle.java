package ddb.bcb.ui;

import ddb.bcb.BreadcrumbBar;
import ddb.bcb.BreadcrumbBarEvent;
import ddb.bcb.BreadcrumbItem;
import ddb.bcb.BreadcrumbItemChoices;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.LabelUI;

public class BreadcrumbParticle extends JLabel implements BreadcrumbBar.BreadcrumbBarElement {
   private BreadcrumbItem item = null;
   private ButtonModel model;
   protected BreadcrumbBar bar;
   private static final String uiClassID = "BreadcrumbParticleUI";

   public BreadcrumbParticle(BreadcrumbBar var1, BreadcrumbItem var2, int var3) {
      this.bar = var1;
      this.setText(var2.getName());
      this.setIcon(var2.getIcon());
      if (var2.getValue().length > 1) {
         this.setName(var2.getValue()[1]);
      }

      this.setFocusable(true);
      this.item = var2;
      this.setHorizontalAlignment(10);
      this.setOpaque(false);
      this.setBorder(BorderFactory.createEmptyBorder());
      this.model = new DefaultButtonModel();
      this.model.setSelected(false);
      this.updateUI();
   }

   @Override
   public int getIndex() {
      return this.item.getIndex();
   }

   public void validateElement() {
      BreadcrumbItem[] var1 = this.bar.getPath();
      BreadcrumbItem[] var2 = this.bar.getPath(this.item.getIndex());

      while(this.bar.getStack().size() > this.item.getIndex() + 1) {
         this.bar.pop();
      }

      BreadcrumbItemChoices var3 = this.bar.getCallback().getChoices(var2);
      if (var3 != null) {
         this.bar.pushChoices(var3);
      }

      this.bar.getUI().updateComponents();
      this.bar.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this.bar, 0, var1, this.bar.getPath()));
   }

   @Override
   public void setUI(LabelUI var1) {
      super.setUI(var1);
   }

   @Override
   public void updateUI() {
      if (UIManager.get(this.getUIClassID()) != null) {
         this.setUI((LabelUI)UIManager.getUI(this));
      } else {
         this.setUI(new BasicBreadcrumbParticleUI());
      }

   }

   @Override
   public LabelUI getUI() {
      return (LabelUI)this.ui;
   }

   @Override
   public String getUIClassID() {
      return "BreadcrumbParticleUI";
   }

   public BreadcrumbBar getBar() {
      return this.bar;
   }

   public ButtonModel getModel() {
      return this.model;
   }
}
