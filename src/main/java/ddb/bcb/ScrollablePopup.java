package ddb.bcb;

import ddb.bcb.ui.BasicScrollablePopupUI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

public class ScrollablePopup extends JPopupMenu {
   public static final int ITEMS_MAX_NUMBER = 10;
   private int maxSize;
   private List<Action> actions;
   private int maxWidth;
   public static final String SELECTED_PROP = "ScrollablePopup.selected";
   private static final String uiClassID = "ScrollablePopupUI";

   public ScrollablePopup() {
      this(10);
   }

   public ScrollablePopup(int var1) {
      this.maxSize = -1;
      this.actions = null;
      this.maxWidth = 0;
      this.maxSize = var1;
      this.actions = new Vector();
   }

   public void removeAll() {
      super.removeAll();
      this.actions.clear();
      this.maxWidth = 0;
   }

   public JMenuItem add(Action var1) {
      return this.add(var1, this.getFontMetrics(this.getFont()).stringWidth((String)var1.getValue("Name")));
   }

   private JMenuItem add(Action var1, int var2) {
      if (this.getUI().getScrollUp() != null) {
         this.getUI().getScrollUp().setEnabled(false);
      }

      this.actions.add(var1);
      this.maxWidth = Math.max(var2 + 20, this.maxWidth);

      try {
         ((JMenuItem)this.getComponent(2)).setPreferredSize(new Dimension(this.maxWidth, (int)this.getComponent(2).getPreferredSize().getHeight()));
      } catch (Exception var4) {
      }

      if (this.actions.size() >= this.maxSize + 1) {
         this.getUI().addScrollers();
         return null;
      } else {
         JMenuItem var3 = super.add(var1);
         return var3;
      }
   }

   public void setVisible(boolean var1) {
      super.setVisible(var1);
      if (!var1) {
         this.getInvoker().repaint();
      } else {
         Point var2 = this.getInvoker().getLocationOnScreen();
         if (this.getLocationOnScreen().getY() != var2.getY() + this.getInvoker().getPreferredSize().getHeight()) {
            var2.translate((int)this.getInvoker().getPreferredSize().getWidth(), 0);
            this.setLocation(var2);
         }
      }

   }

   public void setSelected(Component var1) {
      if (var1 instanceof JLabel) {
         JLabel var2 = (JLabel)var1;
         String var3 = var2.getText();
         Action var4 = null;

         Action var6;
         for(Iterator var5 = this.actions.iterator(); var5.hasNext(); var6.putValue("ScrollablePopup.selected", Boolean.FALSE)) {
            var6 = (Action)var5.next();
            if (var6.getValue("Name").toString().toLowerCase(Locale.ENGLISH).equals(var3.toLowerCase(Locale.ENGLISH))) {
               var4 = var6;
            }
         }

         if (var4 != null) {
            var4.putValue("ScrollablePopup.selected", Boolean.TRUE);
            this.getUI().scrollTo(this.actions.indexOf(var4), true);
         }
      } else {
         if (var1 instanceof JMenuItem) {
            Font var7 = var1.getFont();
            ((JMenuItem)var1).setFont(var7.deriveFont(var7.getStyle() | 1));
         }

         super.setSelected(var1);
         var1.requestFocus();
      }

   }

   public List<Action> getActions() {
      return Collections.unmodifiableList(this.actions);
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public void setUI(BasicScrollablePopupUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      if (UIManager.get(this.getUIClassID()) != null) {
         this.setUI((BasicScrollablePopupUI)UIManager.getUI(this));
      } else {
         this.setUI(BasicScrollablePopupUI.createUI(this));
      }

   }

   public BasicScrollablePopupUI getUI() {
      return (BasicScrollablePopupUI)this.ui;
   }

   public String getUIClassID() {
      return "ScrollablePopupUI";
   }
}
