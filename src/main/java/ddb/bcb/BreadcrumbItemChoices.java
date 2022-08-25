package ddb.bcb;

import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;

public final class BreadcrumbItemChoices {
   private JComponent component = null;
   private List<BreadcrumbItem> choices;
   private int index = 0;

   public BreadcrumbItemChoices(BreadcrumbItem[] var1) {
      assert var1 != null;

      this.choices = new Vector();
      BreadcrumbItem[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BreadcrumbItem var5 = var2[var4];
         this.choices.add(var5);
      }

   }

   public int getPosition(String var1) {
      assert var1 != null && var1.length() > 0;

      for(int var2 = 0; var2 < this.choices.size(); ++var2) {
         BreadcrumbItem var3 = (BreadcrumbItem)this.choices.get(var2);
         if (var1.equals(var3.getName())) {
            return var2;
         }
      }

      return -1;
   }

   public JComponent getComponent() {
      return this.component;
   }

   public void setComponent(JComponent var1) {
      this.component = var1;
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int var1) {
      this.index = var1;
   }

   public BreadcrumbItem[] getChoices() {
      return (BreadcrumbItem[])this.choices.toArray(new BreadcrumbItem[0]);
   }

   public void addItem(BreadcrumbItem var1) {
      this.addItem(this.choices.size(), var1);
   }

   public void addItem(int var1, BreadcrumbItem var2) {
      this.choices.add(var1, var2);
   }
}
