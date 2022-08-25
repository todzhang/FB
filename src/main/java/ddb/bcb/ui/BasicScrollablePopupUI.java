package ddb.bcb.ui;

import ddb.bcb.ScrollablePopup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class BasicScrollablePopupUI extends BasicPopupMenuUI {
   protected JMenuItem scrollDown = this.createScroller(false);
   protected JMenuItem scrollUp = this.createScroller(true);
   protected boolean isScrolling = false;
   protected boolean isPressed = false;
   protected MenuKeyListener menuKeyListener;
   protected MouseWheelListener baseMouseWheelListener;
   protected static BasicScrollablePopupUI INSTANCE = new BasicScrollablePopupUI();

   public static ComponentUI createUI(JComponent var0) {
      return INSTANCE;
   }

   @Override
   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.installComponents();
   }

   @Override
   public void uninstallUI(JComponent var1) {
      this.uninstallComponents();
      super.uninstallUI(var1);
   }

   protected void installComponents() {
   }

   @Override
   protected void installListeners() {
      super.installListeners();
      this.baseMouseWheelListener = new MouseWheelListener() {
         @Override
         public void mouseWheelMoved(MouseWheelEvent var1) {
            boolean var2 = BasicScrollablePopupUI.this.hasScrollers();
            if (var2) {
               boolean var3 = var1.getWheelRotation() < 0;
               boolean var4 = var3 && BasicScrollablePopupUI.this.scrollUp.isEnabled() || !var3 && BasicScrollablePopupUI.this.scrollDown.isEnabled();
               if (var4) {
                  BasicScrollablePopupUI.this.scroll(var3);
               }

            }
         }
      };
      this.popupMenu.addMouseWheelListener(this.baseMouseWheelListener);
   }

   @Override
   protected void uninstallDefaults() {
   }

   protected void uninstallComponents() {
   }

   @Override
   protected void uninstallListeners() {
      super.uninstallListeners();
      this.popupMenu.removeMouseWheelListener(this.baseMouseWheelListener);
      this.baseMouseWheelListener = null;
   }

   protected JMenuItem createScroller(final boolean var1) {
      JMenuItem var2 = new JMenuItem("") {
         @Override
         public void paint(Graphics var1x) {
            super.paint(var1x);
            BasicScrollablePopupUI.this.paintArrow(var1x, this.getWidth(), this.getHeight(), this.isEnabled() ? this.getForeground() : Color.lightGray, var1);
         }

         @Override
         public void menuSelectionChanged(boolean var1x) {
            super.menuSelectionChanged(var1x);
            if (var1x) {
               BasicScrollablePopupUI.this.scrollAction(this);
            } else {
               BasicScrollablePopupUI.this.isPressed = false;
            }

         }
      };
      var2.setEnabled(false);
      var2.addMenuKeyListener(this.getMenuListener());
      var2.setOpaque(false);
      var2.setBorder(BorderFactory.createEmptyBorder());
      var2.setSize(new Dimension(0, 16));
      var2.setPreferredSize(new Dimension(0, 16));
      return var2;
   }

   private void paintArrow(Graphics var1, int var2, int var3, Color var4, boolean var5) {
      var1.setColor(var4);
      int[] var6 = new int[]{var2 / 2 - 4, var2 / 2 + 4, var2 / 2};
      int[] var7 = var5 ? new int[]{var3 / 2 + 2, var3 / 2 + 2, var3 / 2 - 2} : new int[]{var3 / 2 - 2, var3 / 2 - 2, var3 / 2 + 2};
      var1.fillPolygon(var6, var7, 3);
   }

   protected void scrollAction(Object var1) {
      if (var1 instanceof AbstractButton) {
         AbstractButton var2 = (AbstractButton)var1;
         if (var2.isEnabled()) {
            final boolean var3 = var2.equals(this.scrollUp);
            this.isPressed = true;
            Thread var4 = new Thread() {
               public void run() {
                  if (!BasicScrollablePopupUI.this.isScrolling) {
                     BasicScrollablePopupUI.this.isScrolling = true;
                     long var1 = 200L;

                     for(int var3x = 0; BasicScrollablePopupUI.this.isPressed; ++var3x) {
                        BasicScrollablePopupUI.this.isPressed = BasicScrollablePopupUI.this.scroll(var3);

                        try {
                           Thread.sleep(var1);
                        } catch (InterruptedException var5) {
                        }

                        if (var3x > 3) {
                           var1 = Math.max(50L, var1 - 30L);
                        }
                     }

                     BasicScrollablePopupUI.this.isScrolling = false;
                  }
               }
            };
            var4.start();
         }
      }
   }

   public void scrollTo(int var1, boolean var2) {
      ScrollablePopup var3 = (ScrollablePopup)this.popupMenu;
      if (!this.hasScrollers()) {
         var3.setSelected((JMenuItem)var3.getComponent(var1));
      } else {
         int var4 = var1;
         if (var1 + var3.getMaxSize() > var3.getActions().size()) {
            var1 = var3.getActions().size() - var3.getMaxSize();
         }

         this.scrollUp.setEnabled(var1 != 0);
         JMenuItem var5 = null;

         for(int var6 = 1; var6 < var3.getMaxSize() + 1 && var1 < var3.getActions().size(); ++var6) {
            JMenuItem var7 = (JMenuItem)var3.getComponent(var6);
            if (var1 == var4) {
               var5 = var7;
            }

            Action var8 = (Action)var3.getActions().get(var1++);
            var7.setAction(var8);
            var7.setFont(var3.getFont());
         }

         this.scrollDown.setEnabled(var1 < var3.getActions().size());
         if (var2 && var5 != null) {
            var3.setSelected(var5);
         }

         Thread.yield();
         var3.repaint();
      }
   }

   private boolean scroll(final boolean var1) {
      if (!EventQueue.isDispatchThread()) {
         FutureTask var10 = new FutureTask(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
               return BasicScrollablePopupUI.this.scroll(var1);
            }
         });

         try {
            EventQueue.invokeAndWait(var10);
            return (Boolean)var10.get();
         } catch (Exception var9) {
            return false;
         }
      } else {
         ScrollablePopup var2 = (ScrollablePopup)this.popupMenu;
         JMenuItem var3 = (JMenuItem)var2.getComponent(1);
         int var4 = var2.getActions().indexOf(var3.getAction());
         int var5 = var1 ? -1 : 1;
         var4 += var5;
         if (var4 < 0) {
            this.scrollUp.setEnabled(false);
            return false;
         } else {
            this.scrollUp.setEnabled(true);

            for(int var6 = 1; var6 < var2.getMaxSize() + 1 && var4 < var2.getActions().size(); ++var6) {
               JMenuItem var7 = (JMenuItem)var2.getComponent(var6);
               Action var8 = (Action)var2.getActions().get(var4++);
               var7.setAction(var8);
               var7.setFont(var2.getFont());
               if (Boolean.TRUE.equals(var8.getValue("ScrollablePopup.selected"))) {
                  var2.setSelected(var7);
               }
            }

            boolean var11 = var4 == var2.getActions().size();
            this.scrollDown.setEnabled(!var11);
            return !var11;
         }
      }
   }

   protected MenuKeyListener getMenuListener() {
      if (this.menuKeyListener == null) {
         this.menuKeyListener = new MenuKeyListener() {
            @Override
            public void menuKeyPressed(MenuKeyEvent var1) {
               MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
               MenuElement[] var3 = var2.getSelectedPath();
               if (var3[var3.length - 1].equals(BasicScrollablePopupUI.this.scrollDown) && var1.getKeyCode() == 40) {
                  BasicScrollablePopupUI.this.popupMenu.setSelected(BasicScrollablePopupUI.this.scrollDown);
                  BasicScrollablePopupUI.this.scrollDown.requestFocus();
               }

            }

            @Override
            public void menuKeyReleased(MenuKeyEvent var1) {
            }

            @Override
            public void menuKeyTyped(MenuKeyEvent var1) {
            }
         };
      }

      return this.menuKeyListener;
   }

   public boolean hasScrollers() {
      return this.scrollUp.getParent() == this.popupMenu;
   }

   public JMenuItem getScrollDown() {
      return this.scrollDown;
   }

   public JMenuItem getScrollUp() {
      return this.scrollUp;
   }

   public void addScrollers() {
      if (!this.hasScrollers()) {
         this.popupMenu.add(this.scrollDown);
         this.scrollDown.setEnabled(true);
         this.popupMenu.insert(this.scrollUp, 0);
      }

   }
}
