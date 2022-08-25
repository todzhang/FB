package ddb.bcb.ui;

import ddb.bcb.BreadcrumbBar;
import ddb.bcb.BreadcrumbItem;
import ddb.bcb.BreadcrumbItemChoices;
import ddb.bcb.ScrollablePopup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;

public class BasicBreadcrumbBarUI extends BreadcrumbBarUI {
   protected BreadcrumbBar breadcrumbBar;
   protected JLabel iconLabel;
   protected JPanel mainPanel;
   protected BasicBreadcrumbBarUI.ScrollablePanel scrollerPanel;
   protected ComponentListener componentListener;
   protected int popupInitiatorIndex;
   private JPopupMenu popup = null;
   private Map<Object, Component> comps = new HashMap();

   public static ComponentUI createUI(JComponent var0) {
      return new BasicBreadcrumbBarUI();
   }

   @Override
   public void installUI(JComponent var1) {
      this.breadcrumbBar = (BreadcrumbBar)var1;
      this.installDefaults(this.breadcrumbBar);
      this.installComponents(this.breadcrumbBar);
      this.installListeners(this.breadcrumbBar);
      var1.setLayout(this.createLayoutManager());
      this.popupInitiatorIndex = -1;
   }

   @Override
   public void uninstallUI(JComponent var1) {
      var1.setLayout((LayoutManager)null);
      this.uninstallListeners((BreadcrumbBar)var1);
      this.uninstallComponents((BreadcrumbBar)var1);
      this.uninstallDefaults((BreadcrumbBar)var1);
      this.breadcrumbBar = null;
   }

   protected void installDefaults(BreadcrumbBar var1) {
      var1.setFont(new Font("dialog", 0, 12));
   }

   protected void installComponents(BreadcrumbBar var1) {
      this.mainPanel = new JPanel(new FlowLayout(0, 0, 0));
      this.mainPanel.setOpaque(false);
      this.scrollerPanel = new BasicBreadcrumbBarUI.ScrollablePanel(this.mainPanel);
      var1.add(this.scrollerPanel, "Center");
      Icon var2 = var1.getIcon();
      if (var2 != null) {
         this.iconLabel = new JLabel(var2);
         this.iconLabel.setOpaque(false);
         var1.add(this.iconLabel, "West");
      }

      if (var1.getOkButton() != null) {
         var1.add(var1.getOkButton(), "East");
      }

   }

   protected void installListeners(BreadcrumbBar var1) {
      this.componentListener = new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            BasicBreadcrumbBarUI.this.updateComponents();
         }
      };
      var1.addComponentListener(this.componentListener);
   }

   protected void uninstallDefaults(BreadcrumbBar var1) {
   }

   protected void uninstallComponents(BreadcrumbBar var1) {
      if (this.iconLabel != null) {
         var1.remove(this.iconLabel);
         this.iconLabel = null;
      }

      this.mainPanel.removeAll();
      this.comps.clear();
      var1.remove(this.scrollerPanel);
      if (var1.getOkButton() != null) {
         var1.remove(var1.getOkButton());
      }

   }

   protected void uninstallListeners(BreadcrumbBar var1) {
      var1.removeComponentListener(this.componentListener);
      this.componentListener = null;
   }

   protected LayoutManager createLayoutManager() {
      return new BasicBreadcrumbBarUI.BreadcrumbBarLayout();
   }

   @Override
   public JPopupMenu getPopup() {
      if (this.popup == null) {
         this.popup = new ScrollablePopup(15);
         this.popup.setFont(this.breadcrumbBar.getFont());
         this.popup.setBorder(BorderFactory.createLineBorder(Color.black));
         this.popup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent var1) {
               BasicBreadcrumbBarUI.this.popupInitiatorIndex = -1;
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
               BasicBreadcrumbBarUI.this.popupInitiatorIndex = -1;
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
            }
         });
      }

      return this.popup;
   }

   @Override
   public void hidePopup() {
      this.getPopup().setVisible(false);
   }

   @Override
   public int updateComponents() {
      if (this.breadcrumbBar.getStack() == null) {
         return 0;
      } else if (!this.breadcrumbBar.isVisible()) {
         return 0;
      } else {
         this.mainPanel.removeAll();
         this.comps.clear();
         this.scrollerPanel.removeScrollers();
         int var1 = 0;
         Object var2 = null;
         Iterator var3 = this.breadcrumbBar.getStack().iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 instanceof BreadcrumbItemChoices) {
               BreadcrumbItemChoices var5 = (BreadcrumbItemChoices)var4;
               var2 = new ChoicesSelector(this.breadcrumbBar, var5, this.breadcrumbBar.getSeparator());
               this.comps.put(var5, (Component) var2);
            } else if (var4 instanceof BreadcrumbItem) {
               BreadcrumbItem var6 = (BreadcrumbItem)var4;
               var2 = new BreadcrumbParticle(this.breadcrumbBar, var6, this.breadcrumbBar.getFontMetrics(this.breadcrumbBar.getFont()).stringWidth(var6.getName()));
               this.comps.put(var6, (Component) var2);
            }

            var1 = (int)((double)var1 + ((JComponent)var2).getPreferredSize().getWidth());
            this.mainPanel.add((Component)var2);
         }

         this.scrollerPanel.validateScrolling(var1);
         this.mainPanel.validate();
         this.mainPanel.repaint();
         if (var2 != null && this.breadcrumbBar.getStack().lastElement() instanceof BreadcrumbItem) {
            ((JComponent)var2).requestFocus();
         }

         return var1;
      }
   }

   @Override
   public boolean popup(int var1) {
      if (var1 >= 0 && var1 < this.mainPanel.getComponentCount()) {
         Component var2 = this.mainPanel.getComponent(var1);
         if (var2 != null && var2 instanceof ChoicesSelector) {
            ChoicesSelector var3 = (ChoicesSelector)var2;
            return this.showPopup(var3.getIndex(), var3.getBreadcrumbChoices());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean showPopup(int var1, BreadcrumbItemChoices var2) {
      JPopupMenu var3 = this.getPopup();
      this.popupInitiatorIndex = var1;
      var3.removeAll();
      JLabel var4 = null;

      for(int var5 = 0; var5 < var2.getChoices().length; ++var5) {
         BreadcrumbItem var6 = var2.getChoices()[var5];
         BasicBreadcrumbBarUI.PopupAction var7 = new BasicBreadcrumbBarUI.PopupAction(var6, var2.getIndex());
         JMenuItem var8 = var3.add(var7);
         if (var8 != null) {
            var8.setFont(this.breadcrumbBar.getFont());
            var3.setBackground(var8.getBackground());
            var8.setCursor(Cursor.getPredefinedCursor(12));
         }
      }

      if (this.breadcrumbBar.getItemCount() > var2.getIndex() + 1) {
         BreadcrumbItem var9 = this.breadcrumbBar.getItem(var2.getIndex() + 1);
         if (var9 != null && var9 instanceof BreadcrumbItem) {
            var4 = new JLabel(((BreadcrumbItem)var9).getName());
         }
      }

      if (var2.getChoices().length > 0) {
         var3.show((Component)this.comps.get(var2), 0, ((Component)this.comps.get(var2)).getHeight());
         if (var4 != null) {
            var3.setSelected(var4);
         }

         return true;
      } else {
         this.popupInitiatorIndex = -1;
         return false;
      }
   }

   public BreadcrumbParticle getParticle(int var1) {
      if (var1 != 0 && var1 - 1 < this.mainPanel.getComponentCount()) {
         Component var2 = this.mainPanel.getComponent(var1);
         return var2 != null && var2 instanceof ChoicesSelector ? (BreadcrumbParticle)this.mainPanel.getComponent(var1 - 1) : null;
      } else {
         return null;
      }
   }

   @Override
   public ChoicesSelector getSelector(int var1) {
      if (var1 != 0 && var1 - 1 < this.mainPanel.getComponentCount()) {
         Component var2 = this.mainPanel.getComponent(var1);
         return var2 != null && var2 instanceof ChoicesSelector ? (ChoicesSelector)var2 : null;
      } else {
         return null;
      }
   }

   protected JButton createScrollingButton() {
      JButton var1 = new JButton();
      var1.setBorder(BorderFactory.createEmptyBorder());
      var1.setMargin(new Insets(0, 0, 0, 0));
      var1.setPreferredSize(new Dimension(13, 14));
      var1.setCursor(Cursor.getPredefinedCursor(12));
      return var1;
   }

   protected JButton getLeftScroller() {
      JButton var1 = this.createScrollingButton();
      var1.setText("<");
      return var1;
   }

   protected JButton getRightScroller() {
      JButton var1 = this.createScrollingButton();
      var1.setText(">");
      return var1;
   }

   @Override
   public int getPopupInitiatorIndex() {
      return this.popupInitiatorIndex;
   }

   private class PopupAction extends AbstractAction {
      private static final String BreadCrumbItem_Key = "BreadCrumbItem";
      private static final String ParentIndex_Key = "ParentIndex";

      public PopupAction(BreadcrumbItem var2, int var3) {
         super(var2.getName(), var2.getIcon());
         this.putValue("BreadCrumbItem", var2);
         this.putValue("ParentIndex", new Integer(var3));
      }

      @Override
      public void actionPerformed(ActionEvent var1) {
         BreadcrumbItem var2 = (BreadcrumbItem)this.getValue("BreadCrumbItem");
         if (var2 != null) {
            int var3 = (Integer)this.getValue("ParentIndex");

            while(BasicBreadcrumbBarUI.this.breadcrumbBar.getItemCount() > var3 + 1) {
               BasicBreadcrumbBarUI.this.breadcrumbBar.pop();
            }

            BasicBreadcrumbBarUI.this.breadcrumbBar.pushChoice(var2, false);
            BreadcrumbItemChoices var4 = BasicBreadcrumbBarUI.this.breadcrumbBar.getCallback().getChoices(BasicBreadcrumbBarUI.this.breadcrumbBar.getPath());
            if (var4 != null) {
               BasicBreadcrumbBarUI.this.breadcrumbBar.pushChoices(var4, false);
            }

            BasicBreadcrumbBarUI.this.updateComponents();
         }
      }
   }

   protected class ScrollablePanelLayout implements LayoutManager {
      public ScrollablePanelLayout() {
      }

      @Override
      public void addLayoutComponent(String var1, Component var2) {
      }

      @Override
      public void removeLayoutComponent(Component var1) {
      }

      @Override
      public Dimension preferredLayoutSize(Container var1) {
         return new Dimension(var1.getWidth(), 21);
      }

      @Override
      public Dimension minimumLayoutSize(Container var1) {
         return this.preferredLayoutSize(var1);
      }

      @Override
      public void layoutContainer(Container var1) {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         BasicBreadcrumbBarUI.ScrollablePanel var4 = BasicBreadcrumbBarUI.this.scrollerPanel;
         boolean var5 = var4.leftScroller.getParent() == BasicBreadcrumbBarUI.this.scrollerPanel;
         int var6 = var5 ? var2 - var4.leftScroller.getPreferredSize().width - var4.rightScroller.getPreferredSize().width : var2;
         int var7 = 0;
         int var8;
         int var9;
         if (var5) {
            var8 = var4.leftScroller.getPreferredSize().width;
            var9 = var4.leftScroller.getPreferredSize().height;
            var4.leftScroller.setBounds(0, (var3 - var9) / 2, var8, var9);
            var7 += var8;
         }

         var4.scrollPane.setBounds(var7, 0, var6, var3);
         var7 += var6;
         if (var5) {
            var8 = var4.rightScroller.getPreferredSize().width;
            var9 = var4.rightScroller.getPreferredSize().height;
            var4.rightScroller.setBounds(var7, (var3 - var9) / 2, var8, var9);
         }

      }
   }

   public class ScrollablePanel extends JPanel {
      private JButton leftScroller;
      private JButton rightScroller;
      private JScrollPane scrollPane;
      private JComponent view = null;
      private MouseListener scrollMouseListener = null;
      private int widthToScrollTo = 0;

      public ScrollablePanel(JComponent var2) {
         this.view = var2;
         this.scrollPane = new JScrollPane();
         this.scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
         this.scrollPane.setOpaque(false);
         this.scrollPane.setHorizontalScrollBarPolicy(31);
         this.scrollPane.setVerticalScrollBarPolicy(21);
         this.scrollPane.setAutoscrolls(false);
         this.scrollPane.getViewport().setOpaque(false);
         this.scrollPane.setViewportView(var2);
         this.setOpaque(false);
         this.add(this.scrollPane);
         this.leftScroller = BasicBreadcrumbBarUI.this.getLeftScroller();
         this.leftScroller.setName("leftScroller");
         this.leftScroller.addMouseListener(this.getScrollerMouseListener());
         this.rightScroller = BasicBreadcrumbBarUI.this.getRightScroller();
         this.rightScroller.setName("rightScroller");
         this.rightScroller.addMouseListener(this.getScrollerMouseListener());
         this.setLayout(BasicBreadcrumbBarUI.this.new ScrollablePanelLayout());
      }

      public void increaseWidthBy(int var1) {
         this.validateScrolling(this.widthToScrollTo + var1);
      }

      public void validateScrolling(int var1) {
         this.widthToScrollTo = var1;
         int var2 = this.view.getVisibleRect().width - 4;
         if (var2 > 0 && var2 < this.widthToScrollTo) {
            int var3;
            if (this.getComponentCount() <= 1) {
               this.addScrollers();
               var3 = this.widthToScrollTo + this.leftScroller.getWidth() + this.rightScroller.getWidth() - 4;
            } else {
               var3 = this.widthToScrollTo - 4;
            }

            this.view.scrollRectToVisible(new Rectangle(var3, 0, 4, 4));
         } else {
            this.removeScrollers();
         }

      }

      public void removeScrollers() {
         this.view.scrollRectToVisible(new Rectangle(0, 0, 2, 2));
         this.remove(this.leftScroller);
         this.remove(this.rightScroller);
         this.revalidate();
         this.repaint();
      }

      private void addScrollers() {
         this.add(this.leftScroller, "West");
         this.add(this.rightScroller, "East");
         this.revalidate();
         this.repaint();
      }

      public MouseListener getScrollerMouseListener() {
         if (this.scrollMouseListener == null) {
            this.scrollMouseListener = new MouseAdapter() {
               private boolean isPressed = false;

               public void mousePressed(MouseEvent var1) {
                  this.isPressed = true;
                  final boolean var2 = ((JComponent)var1.getSource()).getName().startsWith("left");
                  Thread var3 = new Thread() {
                     public void run() {
                        int var1 = 12;
                        int var2x = 0;

                        while(isPressed) {
                           double var3;
                           if (var2) {
                              var3 = ScrollablePanel.this.view.getVisibleRect().getX() - (double)var1;
                           } else {
                              var3 = ScrollablePanel.this.view.getVisibleRect().getX() + ScrollablePanel.this.view.getVisibleRect().getWidth();
                           }

                           if (var3 > (double)ScrollablePanel.this.widthToScrollTo) {
                              break;
                           }

                           Rectangle var5 = new Rectangle((int)var3, 0, var1, 8);
                           ScrollablePanel.this.view.scrollRectToVisible(var5);

                           try {
                              Thread.sleep(100L);
                           } catch (InterruptedException var7) {
                           }

                           if (var2x > 2) {
                              var1 += 5;
                           }

                           ++var2x;
                           Thread.yield();
                        }

                     }
                  };
                  var3.start();
               }

               public void mouseReleased(MouseEvent var1) {
                  this.isPressed = false;
               }
            };
         }

         return this.scrollMouseListener;
      }
   }

   protected class BreadcrumbBarLayout implements LayoutManager {
      public BreadcrumbBarLayout() {
      }

      @Override
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      @Override
      public Dimension preferredLayoutSize(Container var1) {
         return new Dimension(var1.getWidth(), 21);
      }

      @Override
      public Dimension minimumLayoutSize(Container var1) {
         return this.preferredLayoutSize(var1);
      }

      @Override
      public void layoutContainer(Container var1) {
         int var2 = var1.getWidth();
         int var3 = var1.getHeight();
         BreadcrumbBar var4 = BasicBreadcrumbBarUI.this.breadcrumbBar;
         int var5 = 0;
         int var6;
         if (BasicBreadcrumbBarUI.this.iconLabel != null) {
            var6 = BasicBreadcrumbBarUI.this.iconLabel.getPreferredSize().width;
            BasicBreadcrumbBarUI.this.iconLabel.setBounds(var5, 0, var6, var3);
            var5 += var6;
         }

         var6 = var2;
         JButton var7 = var4.getOkButton();
         if (var7 != null) {
            int var8 = var7.getPreferredSize().width;
            var7.setBounds(var2 - var8, 0, var8, var3);
            var6 = var2 - var8;
         }

         BasicBreadcrumbBarUI.this.scrollerPanel.setBounds(var5, 0, var6 - var5, var3);
      }
   }
}
