package ddb.detach;

import ddb.actions.tabnav.NavigationDirection;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class TabbableFrame extends JFrame implements FocusListener {
   protected Tabbable tab;
   protected Workbench bench;

   public TabbableFrame(Tabbable var1, Workbench var2, Dimension var3, Point var4) {
      this.tab = var1;
      this.bench = var2;
      this.setDefaultCloseOperation(0);
      this.addFocusListener(this);
      this.getRootPane().getInputMap(1).put(KeyStroke.getKeyStroke(37, 128), NavigationDirection.PREVIOUS.getName());
      this.getRootPane().getInputMap(1).put(KeyStroke.getKeyStroke(39, 128), NavigationDirection.NEXT.getName());
      NavigationDirection.fill(new TabNavigationListener() {
         public void navigationActionPerformed(NavigationDirection var1, ActionEvent var2) {
            TabbableFrame.this.bench.setCurrentTab(TabbableFrame.this.tab);
            super.navigationActionPerformed(var1, var2);
         }
      }, this.getRootPane().getActionMap());
      JComponent var5 = this.getStatusBar();
      this.setResizable(true);
      var1.setFrame(this);
      if (var3 == null) {
         var3 = var1.getPreferredSize();
      }

      this.addWindowListener(this.getWindowListener());
      var3 = this.cropSize(var3);
      if (var3 != null) {
         this.setSize(var3);
      } else {
         this.setSize(var1.getPreferredSize());
      }

      if (var4 != null) {
         this.setLocation(new Point(this.getLocation().x + var4.x, this.getLocation().y + var4.y));
      }

      this.getContentPane().setLayout(new BorderLayout());
      this.getContentPane().add(var1.getDisplay(), "Center");
      JPanel var6 = new JPanel(new BorderLayout());
      if (var5 != null) {
         var6.add(var5, "Center");
      }

      this.getContentPane().add(var6, "South");
      this.setTitle(var1.getDetachedTitle());
      var1.generateFloatingTitle();
   }

   protected JComponent getStatusBar() {
      return null;
   }

   protected WindowListener getWindowListener() {
      return new TabbableWindowTabbifier(this.tab, this.bench);
   }

   public void setDisplay(JComponent var1) {
      this.getContentPane().add(var1, "Center");
   }

   private Dimension cropSize(Dimension var1) {
      var1.height = this.cropInteger(var1.height, 150, 1900);
      var1.width = this.cropInteger(var1.width, 250, 1100);
      return var1;
   }

   private int cropInteger(int var1, int var2, int var3) {
      if (var1 < var2) {
         return var2;
      } else {
         return var1 > var3 ? var3 : var1;
      }
   }

   public void focusGained(FocusEvent var1) {
      this.bench.setCurrentTab(this.tab);
   }

   public void focusLost(FocusEvent var1) {
   }

   public Tabbable getTabbable() {
      return this.tab;
   }

   public Workbench getWorkbench() {
      return this.bench;
   }
}
