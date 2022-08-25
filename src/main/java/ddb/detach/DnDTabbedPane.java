package ddb.detach;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class DnDTabbedPane extends JTabbedPane {
   private static final int LINEWIDTH = 3;
   private static final String NAME = "DnDTabbedPane_Tab";
   private final DnDTabbedPane.GhostGlassPane glassPane = new DnDTabbedPane.GhostGlassPane();
   private final Rectangle lineRect = new Rectangle();
   private final Color lineColor = new Color(0, 100, 255);
   private int dragTabIndex = -1;
   private static Rectangle rBackward = new Rectangle();
   private static Rectangle rForward = new Rectangle();
   private static int rwh = 20;
   private static int buttonsize = 30;
   private boolean hasGhost = true;

   private void clickArrowButton(String var1) {
      ActionMap actionMap = this.getActionMap();
      if (actionMap != null) {
         Action action = actionMap.get(var1);
         if (action != null && action.isEnabled()) {
            action.actionPerformed(new ActionEvent(this, 1001, (String)null, 0L, 0));
         }
      }

   }

   private void autoScrollTest(Point point) {
      Rectangle var2 = this.getTabAreaBounds();
      int var3 = this.getTabPlacement();
      if (var3 != 1 && var3 != 3) {
         if (var3 == 2 || var3 == 4) {
            rBackward.setBounds(var2.x, var2.y, var2.width, rwh);
            rForward.setBounds(var2.x, var2.y + var2.height - rwh - buttonsize, var2.width, rwh + buttonsize);
         }
      } else {
         rBackward.setBounds(var2.x, var2.y, rwh, var2.height);
         rForward.setBounds(var2.x + var2.width - rwh - buttonsize, var2.y, rwh + buttonsize, var2.height);
      }

      if (rBackward.contains(point)) {
         this.clickArrowButton("scrollTabsBackwardAction");
      } else if (rForward.contains(point)) {
         this.clickArrowButton("scrollTabsForwardAction");
      }

   }

   public DnDTabbedPane() {
      try {
         this.init();
      } catch (ClassNotFoundException var2) {
         var2.printStackTrace();
      }

   }

   public DnDTabbedPane(int tabPlacement) {
      super(tabPlacement);

      try {
         this.init();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

   }

   public DnDTabbedPane(int tabPlacement, int tabLayoutPolicy) {
      super(tabPlacement, tabLayoutPolicy);

      try {
         this.init();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }

   }

   private void init() throws ClassNotFoundException {
      final DragSourceListener dragSourceListener = new DragSourceListener() {
         @Override
         public void dragEnter(DragSourceDragEvent var1) {
            var1.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
         }

         @Override
         public void dragExit(DragSourceEvent var1) {
            var1.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            DnDTabbedPane.this.lineRect.setRect(0.0D, 0.0D, 0.0D, 0.0D);
            DnDTabbedPane.this.glassPane.setPoint(new Point(-1000, -1000));
            DnDTabbedPane.this.glassPane.repaint();
         }

         @Override
         public void dragOver(DragSourceDragEvent var1) {
            Point var2 = var1.getLocation();
            SwingUtilities.convertPointFromScreen(var2, DnDTabbedPane.this.glassPane);
            int var3 = DnDTabbedPane.this.getTargetTabIndex(var2);
            if (DnDTabbedPane.this.getTabAreaBounds().contains(var2) && var3 >= 0 && var3 != DnDTabbedPane.this.dragTabIndex && var3 != DnDTabbedPane.this.dragTabIndex + 1) {
               var1.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            } else {
               var1.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }

         }

         @Override
         public void dragDropEnd(DragSourceDropEvent var1) {
            DnDTabbedPane.this.lineRect.setRect(0.0D, 0.0D, 0.0D, 0.0D);
            DnDTabbedPane.this.dragTabIndex = -1;
            DnDTabbedPane.this.glassPane.setVisible(false);
            if (DnDTabbedPane.this.hasGhost()) {
               DnDTabbedPane.this.glassPane.setVisible(false);
               DnDTabbedPane.this.glassPane.setImage((BufferedImage)null);
            }

         }

         @Override
         public void dropActionChanged(DragSourceDragEvent var1) {
         }
      };
      final Transferable transferable = new Transferable() {
         private final DataFlavor FLAVOR = new DataFlavor("application/x-java-jvm-local-objectref", "DnDTabbedPane_Tab");

         @Override
         public Object getTransferData(DataFlavor var1) {
            return DnDTabbedPane.this;
         }

         @Override
         public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] var1 = new DataFlavor[]{this.FLAVOR};
            return var1;
         }

         @Override
         public boolean isDataFlavorSupported(DataFlavor var1) {
            return var1.getHumanPresentableName().equals("DnDTabbedPane_Tab");
         }
      };
      DragGestureListener dragGestureListener = new DragGestureListener() {
         @Override
         public void dragGestureRecognized(DragGestureEvent var1x) {
            if (DnDTabbedPane.this.getTabCount() > 1) {
               Point var2x = var1x.getDragOrigin();
               DnDTabbedPane.this.dragTabIndex = DnDTabbedPane.this.indexAtLocation(var2x.x, var2x.y);
               if (DnDTabbedPane.this.dragTabIndex >= 0 && DnDTabbedPane.this.isEnabledAt(DnDTabbedPane.this.dragTabIndex)) {
                  DnDTabbedPane.this.initGlassPane(var1x.getComponent(), var1x.getDragOrigin());

                  try {
                     var1x.startDrag(DragSource.DefaultMoveDrop, transferable, dragSourceListener);
                  } catch (InvalidDnDOperationException var4) {
                     var4.printStackTrace();
                  }

               }
            }
         }
      };
      new DropTarget(this.glassPane, 3, new DnDTabbedPane.CDropTargetListener(), true);
      (new DragSource()).createDefaultDragGestureRecognizer(this, 3, dragGestureListener);
   }

   public void setPaintGhost(boolean hasGhost) {
      this.hasGhost = hasGhost;
   }

   public boolean hasGhost() {
      return this.hasGhost;
   }

   private int getTargetTabIndex(Point point) {
      Point var2 = SwingUtilities.convertPoint(this.glassPane, point, this);
      boolean var3 = this.getTabPlacement() == 1 || this.getTabPlacement() == 3;

      for(int var4 = 0; var4 < this.getTabCount(); ++var4) {
         Rectangle var5 = this.getBoundsAt(var4);
         if (var3) {
            var5.setRect((double)(var5.x - var5.width / 2), (double)var5.y, (double)var5.width, (double)var5.height);
         } else {
            var5.setRect((double)var5.x, (double)(var5.y - var5.height / 2), (double)var5.width, (double)var5.height);
         }

         if (var5.contains(var2)) {
            return var4;
         }
      }

      Rectangle var6 = this.getBoundsAt(this.getTabCount() - 1);
      if (var3) {
         var6.setRect((double)(var6.x + var6.width / 2), (double)var6.y, (double)var6.width, (double)var6.height);
      } else {
         var6.setRect((double)var6.x, (double)(var6.y + var6.height / 2), (double)var6.width, (double)var6.height);
      }

      return var6.contains(var2) ? this.getTabCount() : -1;
   }

   protected void convertTab(int oldIndex, int newIndex) {
      if (newIndex >= 0 && oldIndex != newIndex) {
         Component var3 = this.getComponentAt(oldIndex);
         Component var4 = this.getTabComponentAt(oldIndex);
         String var5 = this.getTitleAt(oldIndex);
         Icon var6 = this.getIconAt(oldIndex);
         String var7 = this.getToolTipTextAt(oldIndex);
         boolean var8 = this.isEnabledAt(oldIndex);
         int var9 = oldIndex > newIndex ? newIndex : newIndex - 1;
         this.remove(oldIndex);
         this.insertTab(var5, var6, var3, var7, var9);
         this.setEnabledAt(var9, var8);
         if (var8) {
            this.setSelectedIndex(var9);
         }

         this.setTabComponentAt(var9, var4);
      }
   }

   private void initTargetLeftRightLine(int var1) {
      if (var1 >= 0 && this.dragTabIndex != var1 && var1 - this.dragTabIndex != 1) {
         Rectangle var2;
         if (var1 == 0) {
            var2 = SwingUtilities.convertRectangle(this, this.getBoundsAt(0), this.glassPane);
            this.lineRect.setRect((double)(var2.x - 1), (double)var2.y, 3.0D, (double)var2.height);
         } else {
            var2 = SwingUtilities.convertRectangle(this, this.getBoundsAt(var1 - 1), this.glassPane);
            this.lineRect.setRect((double)(var2.x + var2.width - 1), (double)var2.y, 3.0D, (double)var2.height);
         }
      } else {
         this.lineRect.setRect(0.0D, 0.0D, 0.0D, 0.0D);
      }

   }

   private void initTargetTopBottomLine(int var1) {
      if (var1 >= 0 && this.dragTabIndex != var1 && var1 - this.dragTabIndex != 1) {
         Rectangle var2;
         if (var1 == 0) {
            var2 = SwingUtilities.convertRectangle(this, this.getBoundsAt(0), this.glassPane);
            this.lineRect.setRect((double)var2.x, (double)(var2.y - 1), (double)var2.width, 3.0D);
         } else {
            var2 = SwingUtilities.convertRectangle(this, this.getBoundsAt(var1 - 1), this.glassPane);
            this.lineRect.setRect((double)var2.x, (double)(var2.y + var2.height - 1), (double)var2.width, 3.0D);
         }
      } else {
         this.lineRect.setRect(0.0D, 0.0D, 0.0D, 0.0D);
      }

   }

   private void initGlassPane(Component component, Point point) {
      this.getRootPane().setGlassPane(this.glassPane);
      if (this.hasGhost()) {
         Rectangle var3 = this.getBoundsAt(this.dragTabIndex);
         BufferedImage var4 = new BufferedImage(component.getWidth(), component.getHeight(), 2);
         Graphics var5 = var4.getGraphics();
         component.paint(var5);
         var3.x = var3.x < 0 ? 0 : var3.x;
         var3.y = var3.y < 0 ? 0 : var3.y;
         var4 = var4.getSubimage(var3.x, var3.y, var3.width, var3.height);
         this.glassPane.setImage(var4);
      }

      Point var6 = SwingUtilities.convertPoint(component, point, this.glassPane);
      this.glassPane.setPoint(var6);
      this.glassPane.setVisible(true);
   }

   private Rectangle getTabAreaBounds() {
      Rectangle var1 = this.getBounds();
      Component var2 = this.getSelectedComponent();

      for(int var3 = 0; var2 == null && var3 < this.getTabCount(); var2 = this.getComponentAt(var3++)) {
      }

      Rectangle var4 = var2 == null ? new Rectangle() : var2.getBounds();
      switch(this.getTabPlacement()) {
      case 1:
         var1.height -= var4.height;
         break;
      case 2:
         var1.width -= var4.width;
         break;
      case 3:
         var1.y = var1.y + var4.y + var4.height;
         var1.height -= var4.height;
         break;
      case 4:
         var1.x = var1.x + var4.x + var4.width;
         var1.width -= var4.width;
      }

      var1.grow(2, 2);
      return var1;
   }

   class GhostGlassPane extends JPanel {
      private final AlphaComposite composite;
      private Point location = new Point(0, 0);
      private BufferedImage draggingGhost = null;

      public GhostGlassPane() {
         this.setOpaque(false);
         this.composite = AlphaComposite.getInstance(3, 0.5F);
         this.setCursor((Cursor)null);
      }

      public void setImage(BufferedImage var1) {
         this.draggingGhost = var1;
      }

      public void setPoint(Point var1) {
         this.location = var1;
      }

      @Override
      public void paintComponent(Graphics g) {
         Graphics2D var2 = (Graphics2D)g;
         var2.setComposite(this.composite);
         if (this.draggingGhost != null) {
            double var3 = this.location.getX() - (double)this.draggingGhost.getWidth(this) / 2.0D;
            double var5 = this.location.getY() - (double)this.draggingGhost.getHeight(this) / 2.0D;
            var2.drawImage(this.draggingGhost, (int)var3, (int)var5, (ImageObserver)null);
         }

         if (DnDTabbedPane.this.dragTabIndex >= 0) {
            var2.setPaint(DnDTabbedPane.this.lineColor);
            var2.fill(DnDTabbedPane.this.lineRect);
         }

      }
   }

   class CDropTargetListener implements DropTargetListener {
      private Point pt_ = new Point();

      @Override
      public void dragEnter(DropTargetDragEvent var1) {
         if (this.isDragAcceptable(var1)) {
            var1.acceptDrag(var1.getDropAction());
         } else {
            var1.rejectDrag();
         }

      }

      @Override
      public void dragExit(DropTargetEvent var1) {
      }

      @Override
      public void dropActionChanged(DropTargetDragEvent var1) {
      }

      @Override
      public void dragOver(DropTargetDragEvent var1) {
         Point var2 = var1.getLocation();
         if (DnDTabbedPane.this.getTabPlacement() != 1 && DnDTabbedPane.this.getTabPlacement() != 3) {
            DnDTabbedPane.this.initTargetTopBottomLine(DnDTabbedPane.this.getTargetTabIndex(var2));
         } else {
            DnDTabbedPane.this.initTargetLeftRightLine(DnDTabbedPane.this.getTargetTabIndex(var2));
         }

         if (DnDTabbedPane.this.hasGhost()) {
            DnDTabbedPane.this.glassPane.setPoint(var2);
         }

         if (!this.pt_.equals(var2)) {
            DnDTabbedPane.this.glassPane.repaint();
         }

         this.pt_ = var2;
         DnDTabbedPane.this.autoScrollTest(var2);
      }

      @Override
      public void drop(DropTargetDropEvent var1) {
         if (this.isDropAcceptable(var1)) {
            DnDTabbedPane.this.convertTab(DnDTabbedPane.this.dragTabIndex, DnDTabbedPane.this.getTargetTabIndex(var1.getLocation()));
            var1.dropComplete(true);
         } else {
            var1.dropComplete(false);
         }

         DnDTabbedPane.this.repaint();
      }

      public boolean isDragAcceptable(DropTargetDragEvent var1) {
         Transferable var2 = var1.getTransferable();
         if (var2 == null) {
            return false;
         } else {
            DataFlavor[] var3 = var1.getCurrentDataFlavors();
            return var2.isDataFlavorSupported(var3[0]) && DnDTabbedPane.this.dragTabIndex >= 0;
         }
      }

      public boolean isDropAcceptable(DropTargetDropEvent var1) {
         Transferable var2 = var1.getTransferable();
         if (var2 == null) {
            return false;
         } else {
            DataFlavor[] var3 = var2.getTransferDataFlavors();
            return var2.isDataFlavorSupported(var3[0]) && DnDTabbedPane.this.dragTabIndex >= 0;
         }
      }
   }
}
