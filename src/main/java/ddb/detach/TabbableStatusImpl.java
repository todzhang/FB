package ddb.detach;

import java.awt.Color;
import java.util.Observable;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;

public class TabbableStatusImpl extends Observable implements TabbableStatus, MutableTabbableStatus {
   private TabbableStatus.State host;
   private TabbableStatus.State details;
   private BoundedRangeModel progressModel;
   private boolean indeterminate;
   private Icon statusIcon;
   private Tabbable owner;

   public TabbableStatusImpl(Tabbable var1) {
      this.owner = var1;
      this.host = new TabbableStatus.State();
      this.details = new TabbableStatus.State();
      this.progressModel = new DefaultBoundedRangeModel(0, 0, 0, 100);
      this.indeterminate = false;
      this.statusIcon = null;
   }

   public void fini() {
   }

   public TabbableStatus.State getDetails() {
      return this.details;
   }

   public void setDetails(String var1) {
      this.setDetails(var1, Color.BLACK, (Color)null);
      super.setChanged();
   }

   public void setDetails(String var1, Color var2, Color var3) {
      this.details.setText(var1);
      this.details.setForeground(var2);
      this.details.setBackground(var3);
      super.setChanged();
   }

   public TabbableStatus.State getHost() {
      return this.host;
   }

   public void setHost(String var1) {
      this.setHost(var1, Color.BLACK, (Color)null);
   }

   public void setHost(String var1, Color var2, Color var3) {
      this.host.setText(var1);
      this.host.setForeground(var2);
      this.host.setBackground(var3);
      super.setChanged();
   }

   public boolean isIndeterminate() {
      return this.indeterminate;
   }

   public void setIndeterminate(boolean var1) {
      this.indeterminate = var1;
      super.setChanged();
   }

   public BoundedRangeModel getProgressModel() {
      return this.progressModel;
   }

   public void setProgressModel(BoundedRangeModel var1) {
      this.progressModel = var1;
      super.setChanged();
   }

   public void setProgressModelChanged() {
      super.setChanged();
   }

   public Icon getStatusIcon() {
      return this.statusIcon;
   }

   public void setStatusIcon(Icon var1) {
      this.statusIcon = var1;
      super.setChanged();
   }

   public void notifyObservers() {
      super.notifyObservers(this.owner);
   }
}
