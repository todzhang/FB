package ddb.bcb.ui;

import javax.swing.JPopupMenu;
import javax.swing.plaf.ComponentUI;

public abstract class BreadcrumbBarUI extends ComponentUI {
   public abstract int updateComponents();

   public abstract boolean popup(int var1);

   public abstract void hidePopup();

   public abstract JPopupMenu getPopup();

   public abstract int getPopupInitiatorIndex();

   public abstract BreadcrumbParticle getParticle(int var1);

   public abstract ChoicesSelector getSelector(int var1);
}
