package ddb.bcb.ui;

import ddb.bcb.BreadcrumbBar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;

public class BasicChoicesSelectorUI extends ChoicesSelectorUI {
   protected ChoicesSelector choicesSelector;
   protected FocusListener baseFocusListener;
   protected MouseListener baseMouseListener;
   protected KeyListener baseKeyListener;
   protected PopupMenuListener basePopupMenuListener;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicChoicesSelectorUI();
   }

   @Override
   public void installUI(JComponent var1) {
      this.choicesSelector = (ChoicesSelector)var1;
      this.installDefaults(this.choicesSelector);
      this.installComponents(this.choicesSelector);
      this.installListeners(this.choicesSelector);
      var1.setLayout(this.createLayoutManager());
   }

   @Override
   public void uninstallUI(JComponent var1) {
      var1.setLayout((LayoutManager)null);
      this.uninstallListeners((ChoicesSelector)var1);
      this.uninstallComponents((ChoicesSelector)var1);
      this.uninstallDefaults((ChoicesSelector)var1);
      this.choicesSelector = null;
   }

   protected void installDefaults(ChoicesSelector var1) {
      var1.setFont(var1.getOwnerBar().getFont());
      var1.setCursor(Cursor.getPredefinedCursor(12));
      var1.setFocusable(true);
      var1.setOpaque(false);
      var1.setForeground(var1.getOwnerBar().getForeground());
   }

   protected void installComponents(ChoicesSelector var1) {
   }

   protected void installListeners(final ChoicesSelector var1) {
      this.baseFocusListener = new FocusListener() {
         @Override
         public void focusGained(FocusEvent var1x) {
            var1.getModel().setArmed(true);
            var1.repaint();
         }

         @Override
         public void focusLost(FocusEvent var1x) {
            var1.getModel().setArmed(false);
            var1.repaint();
         }
      };
      var1.addFocusListener(this.baseFocusListener);
      this.baseMouseListener = new MouseAdapter() {
         private boolean showPopup(ChoicesSelector var1x) {
            return var1x.getOwnerBar().getUI().popup(var1.getIndex());
         }

         @Override
         public void mousePressed(MouseEvent var1x) {
            var1.getModel().setPressed(true);
            var1.getModel().setSelected(true);
            var1.repaint();
            BreadcrumbBar var2 = var1.getOwnerBar();
            int var3 = var2.getUI().getPopupInitiatorIndex();
            if (var3 < 0) {
               boolean var4 = this.showPopup(var1);
               if (!var4) {
                  var1.getModel().setSelected(false);
                  var1.repaint();
               }
            }

         }

         @Override
         public void mouseEntered(MouseEvent var1x) {
            var1.getModel().setRollover(true);
            BasicChoicesSelectorUI.this.synchronizeWithParticle();
            var1.getOwnerBar().repaint();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  BreadcrumbBar var1x = var1.getOwnerBar();
                  int var2 = var1x.getUI().getPopupInitiatorIndex();
                  if (var2 >= 0 && var1.getIndex() != var2) {
                     var1x.getUI().hidePopup();
                     var1.getModel().setSelected(true);
                     BasicChoicesSelectorUI.this.synchronizeWithParticle();
                     if (!showPopup(var1)) {
                        var1.getModel().setSelected(false);
                        BasicChoicesSelectorUI.this.synchronizeWithParticle();
                     }
                  }

               }
            });
         }

         @Override
         public void mouseExited(MouseEvent var1x) {
            var1.getModel().setRollover(false);
            BasicChoicesSelectorUI.this.synchronizeWithParticle();
            var1.getOwnerBar().repaint();
         }

         @Override
         public void mouseReleased(MouseEvent var1x) {
            var1.getModel().setPressed(false);
            BasicChoicesSelectorUI.this.synchronizeWithParticle();
            var1.getOwnerBar().repaint();
         }
      };
      var1.addMouseListener(this.baseMouseListener);
      this.baseKeyListener = new KeyAdapter() {
         public void keyPressed(KeyEvent var1x) {
            if (var1.getModel().isArmed() && var1x.getKeyCode() == 40 || var1x.getKeyCode() == 32 || var1x.getKeyCode() == 10) {
               var1.getModel().setSelected(true);
               var1.repaint();
               if (!var1.getOwnerBar().getUI().popup(var1.getIndex())) {
                  var1.getModel().setSelected(false);
                  var1.repaint();
               }
            }

         }
      };
      var1.addKeyListener(this.baseKeyListener);
      this.basePopupMenuListener = new PopupMenuListener() {
         @Override
         public void popupMenuCanceled(PopupMenuEvent var1x) {
            var1.getModel().setSelected(false);
            var1.getModel().setRollover(false);
            BasicChoicesSelectorUI.this.synchronizeWithParticle();
         }

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent var1x) {
            var1.getModel().setSelected(false);
            var1.getModel().setRollover(false);
            BasicChoicesSelectorUI.this.synchronizeWithParticle();
         }

         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent var1x) {
         }
      };
      var1.getOwnerBar().getUI().getPopup().addPopupMenuListener(this.basePopupMenuListener);
   }

   protected void uninstallDefaults(ChoicesSelector var1) {
   }

   protected void uninstallComponents(ChoicesSelector var1) {
   }

   protected void uninstallListeners(ChoicesSelector var1) {
      var1.removeKeyListener(this.baseKeyListener);
      this.baseKeyListener = null;
      var1.removeMouseListener(this.baseMouseListener);
      this.baseMouseListener = null;
      var1.removeFocusListener(this.baseFocusListener);
      this.baseFocusListener = null;
      var1.getOwnerBar().getUI().getPopup().removePopupMenuListener(this.basePopupMenuListener);
      this.basePopupMenuListener = null;
   }

   protected BreadcrumbParticle synchronizeWithParticle() {
      BreadcrumbBar var1 = this.choicesSelector.getOwnerBar();
      BreadcrumbBarUI var2 = var1.getUI();
      BreadcrumbParticle var3 = var2.getParticle(this.choicesSelector.getIndex());
      if (var3 != null) {
         var3.getModel().setArmed(this.choicesSelector.getModel().isArmed());
         var3.getModel().setSelected(this.choicesSelector.getModel().isSelected());
         var3.getModel().setRollover(this.choicesSelector.getModel().isRollover());
         this.choicesSelector.getOwnerBar().repaint();
      }

      return var3;
   }

   protected LayoutManager createLayoutManager() {
      return new BasicChoicesSelectorUI.ChoicesSelectorLayout();
   }

   @Override
   public void paint(Graphics var1, JComponent var2) {
      Graphics2D var3 = (Graphics2D)var1.create();
      var3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      BreadcrumbBar var4 = this.choicesSelector.getOwnerBar();
      BreadcrumbBarUI var5 = var4.getUI();
      BreadcrumbParticle var6 = var5.getParticle(this.choicesSelector.getIndex());
      this.paintBackground(var3, var2, var6);
      this.paintForeground(var3, var2);
      var3.dispose();
   }

   protected void paintBackground(Graphics2D var1, JComponent var2, BreadcrumbParticle var3) {
      ChoicesSelector var4 = (ChoicesSelector)var2;
      boolean var5 = var4.getModel().isArmed() || var4.getModel().isRollover() || var4.getModel().isSelected() || var4.getModel().isPressed();
      if (var5) {
         int var6 = var2.getWidth();
         Color var7 = this.choicesSelector.getOwnerBar().getBoxColor();
         if (var7 == null) {
            var7 = this.choicesSelector.getForeground();
         }

         var1.setColor(var7);
         var1.drawRect(0, 0, var6 - 2, var6 - 1);
      }
   }

   protected void paintForeground(Graphics2D var1, JComponent var2) {
      ChoicesSelector var3 = (ChoicesSelector)var2;
      if (!var3.getModel().isSelected() && !var3.getModel().isPressed()) {
         this.paintRegularState(var1, var2);
      } else {
         this.paintPressedState(var1, var2);
      }

   }

   protected void paintRegularState(Graphics2D var1, JComponent var2) {
      int var3 = var2.getWidth();
      var1.setColor(this.choicesSelector.getForeground());
      var1.fillPolygon(new int[]{3 * var3 / 8, 5 * var3 / 8, 3 * var3 / 8}, new int[]{var3 / 4, var3 / 2, 3 * var3 / 4}, 3);
   }

   protected void paintPressedState(Graphics2D var1, JComponent var2) {
      int var3 = var2.getWidth();
      var1.rotate(1.5707963267948966D, (double)(var3 / 2), (double)(var3 / 2));
      var1.setColor(this.choicesSelector.getForeground());
      var1.fillPolygon(new int[]{3 * var3 / 8, 5 * var3 / 8, 3 * var3 / 8}, new int[]{var3 / 4, var3 / 2, 3 * var3 / 4}, 3);
   }

   protected class ChoicesSelectorLayout implements LayoutManager {
      public ChoicesSelectorLayout() {
      }

      @Override
      public void addLayoutComponent(String var1, Component var2) {
      }

      @Override
      public void removeLayoutComponent(Component var1) {
      }

      @Override
      public Dimension preferredLayoutSize(Container var1) {
         return new Dimension(15, 16);
      }

      @Override
      public Dimension minimumLayoutSize(Container var1) {
         return this.preferredLayoutSize(var1);
      }

      @Override
      public void layoutContainer(Container var1) {
      }
   }
}
