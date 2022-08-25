package ddb.bcb.ui;

import ddb.bcb.BreadcrumbBar;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

public class BasicBreadcrumbParticleUI extends BasicLabelUI {
   protected BreadcrumbParticle particle;
   protected FocusListener baseFocusListener;
   protected MouseListener baseMouseListener;
   protected KeyListener baseKeyListener;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicBreadcrumbParticleUI();
   }

   @Override
   public void installUI(JComponent var1) {
      this.particle = (BreadcrumbParticle)var1;
      this.installDefaults(this.particle);
      this.installComponents(this.particle);
      this.installListeners(this.particle);
   }

   @Override
   public void uninstallUI(JComponent var1) {
      var1.setLayout((LayoutManager)null);
      this.uninstallListeners((BreadcrumbParticle)var1);
      this.uninstallComponents((BreadcrumbParticle)var1);
      this.uninstallDefaults((BreadcrumbParticle)var1);
      this.particle = null;
   }

   protected void installDefaults(BreadcrumbParticle var1) {
      Font var2 = UIManager.getFont("BreadcrumbBar.font");
      if (var2 == null) {
         var2 = new Font("Tahoma", 0, 11);
      }

      var1.setFont(var2);
      var1.setCursor(Cursor.getPredefinedCursor(12));
      var1.setBorder(new EmptyBorder(0, 2, 0, 2));
   }

   protected void installComponents(BreadcrumbParticle var1) {
   }

   protected void installListeners(final BreadcrumbParticle var1) {
      this.baseFocusListener = new FocusListener() {
         @Override
         public void focusGained(FocusEvent var1x) {
            var1.getModel().setArmed(true);
            var1.getBar().repaint();
         }

         @Override
         public void focusLost(FocusEvent var1x) {
            var1.getModel().setArmed(false);
            var1.getBar().repaint();
         }
      };
      var1.addFocusListener(this.baseFocusListener);
      this.baseMouseListener = new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1x) {
            var1.getModel().setRollover(true);
            var1.getBar().repaint();
         }

         @Override
         public void mouseExited(MouseEvent var1x) {
            var1.getModel().setRollover(false);
            var1.getBar().repaint();
         }

         @Override
         public void mouseClicked(MouseEvent var1x) {
            if (var1x.getModifiers() == 16) {
               var1.validateElement();
            } else if (!System.getProperty("os.name").startsWith("Mac") && System.getProperty("mrj.version") == null) {
               if (var1x.getModifiers() == 4 && var1.getBar().getPopupListener() != null) {
                  var1.getBar().getPopupListener().mouseClicked(var1x);
               }
            } else if ((!var1x.isAltDown() || var1x.isControlDown()) && var1x.isPopupTrigger() && var1.getBar().getPopupListener() != null) {
               var1.getBar().getPopupListener().mouseClicked(var1x);
            }

         }
      };
      var1.addMouseListener(this.baseMouseListener);
      this.baseKeyListener = new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent var1x) {
            if (var1x.getKeyCode() == 40) {
               var1.getBar().getUI().popup(var1.getIndex());
            }

         }

         @Override
         public void keyTyped(KeyEvent var1x) {
            if (var1x.getSource() instanceof JLabel) {
               if (var1x.getKeyChar() != 27) {
                  if (var1x.getKeyChar() == '\n') {
                     var1.validateElement();
                  }
               }
            }
         }
      };
      var1.addKeyListener(this.baseKeyListener);
   }

   protected void uninstallDefaults(BreadcrumbParticle var1) {
   }

   protected void uninstallComponents(BreadcrumbParticle var1) {
   }

   protected void uninstallListeners(BreadcrumbParticle var1) {
      var1.removeKeyListener(this.baseKeyListener);
      this.baseKeyListener = null;
      var1.removeMouseListener(this.baseMouseListener);
      this.baseMouseListener = null;
      var1.removeFocusListener(this.baseFocusListener);
      this.baseFocusListener = null;
   }

   @Override
   public void paint(Graphics var1, JComponent var2) {
      Graphics2D var3 = (Graphics2D)var1.create();
      boolean var4 = this.particle.getModel().isSelected() || this.particle.getModel().isArmed() || this.particle.getModel().isRollover();
      if (var4) {
         BreadcrumbBar var5 = this.particle.getBar();
         BreadcrumbBarUI var6 = var5.getUI();
         ChoicesSelector var7 = var6.getSelector(this.particle.getIndex() + 1);
         this.paintSelectedBackground(var3, var2, var7);
      }

      super.paint(var1, var2);
      if (var4) {
         this.paintSelectedForeground(var3, var2);
      }

      var3.dispose();
   }

   protected void paintSelectedBackground(Graphics2D var1, JComponent var2, ChoicesSelector var3) {
   }

   protected void paintSelectedForeground(Graphics2D var1, JComponent var2) {
      var1.setColor(this.particle.getBar().getUnderlineColor() == null ? this.particle.getForeground() : this.particle.getBar().getUnderlineColor());
      Rectangle var3 = new Rectangle();
      Rectangle var4 = new Rectangle();
      Rectangle var5 = new Rectangle();
      Insets var6 = new Insets(0, 0, 0, 0);
      Insets var7 = var2.getInsets(var6);
      var3.x = var7.left;
      var3.y = var7.top;
      var3.width = var2.getWidth() - (var7.left + var7.right);
      var3.height = var2.getHeight() - (var7.top + var7.bottom);
      SwingUtilities.layoutCompoundLabel(this.particle, this.particle.getFontMetrics(this.particle.getFont()), this.particle.getText(), this.particle.getIcon(), this.particle.getVerticalAlignment(), this.particle.getHorizontalAlignment(), this.particle.getVerticalTextPosition(), this.particle.getHorizontalTextPosition(), var3, var4, var5, this.particle.getIconTextGap());
      var1.drawLine(var5.x, var5.y + var5.height, var5.x + var5.width, var5.y + var5.height);
   }
}
