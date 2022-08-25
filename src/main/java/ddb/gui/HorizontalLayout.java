package ddb.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class HorizontalLayout implements LayoutManager, Serializable {
   public static final String classVersion = "3.0";
   public static final long serialVersionUID = -1972032384181039877L;
   protected int gap;

   public HorizontalLayout() {
      this(10);
   }

   public HorizontalLayout(int gap) {
      this.gap = gap;
   }

   public int getGap() {
      return this.gap;
   }

   public void setGap(int gap) {
      this.gap = gap;
   }

   public void addLayoutComponent(String name, Component component) {
   }

   public void removeLayoutComponent(Component component) {
   }

   public Dimension preferredLayoutSize(Container target) {
      synchronized(target.getTreeLock()) {
         int numComponents = target.getComponentCount();
         int w = 0;
         int h = 0;
         int numVisible = 0;

         for(int i = 0; i < numComponents; ++i) {
            Component component = target.getComponent(i);
            if (component.isVisible()) {
               ++numVisible;
               Dimension d = component.getPreferredSize();
               w = Math.max(w, d.width);
               h = Math.max(h, d.height);
            }
         }

         Insets insets = target.getInsets();
         return new Dimension(insets.left + insets.right + numVisible * w + (numVisible - 1) * this.gap, insets.top + insets.bottom + h);
      }
   }

   public Dimension minimumLayoutSize(Container target) {
      synchronized(target.getTreeLock()) {
         int numComponents = target.getComponentCount();
         int w = 0;
         int h = 0;
         int numVisible = 0;

         for(int i = 0; i < numComponents; ++i) {
            Component component = target.getComponent(i);
            if (component.isVisible()) {
               ++numVisible;
               Dimension d = component.getMinimumSize();
               w = Math.max(w, d.width);
               h = Math.max(h, d.height);
            }
         }

         Insets insets = target.getInsets();
         return new Dimension(insets.left + insets.right + numVisible * w + (numVisible - 1) * this.gap, insets.top + insets.bottom + h);
      }
   }

   public void layoutContainer(Container target) {
      synchronized(target.getTreeLock()) {
         Insets insets = target.getInsets();
         int numComponents = target.getComponentCount();
         if (numComponents != 0) {
            int w = target.getWidth() - (insets.left + insets.right);
            int h = target.getHeight() - (insets.top + insets.bottom);
            int numVisible = 0;

            int x;
            for(x = 0; x < numComponents; ++x) {
               if (target.getComponent(x).isVisible()) {
                  ++numVisible;
               }
            }

            w = (w - (numVisible - 1) * this.gap) / numVisible;
            x = insets.left;
            int y = insets.top;

            for(int i = 0; i < numComponents; ++i) {
               Component component = target.getComponent(i);
               if (component.isVisible()) {
                  component.setBounds(x, y, w, h);
                  x += w + this.gap;
               }
            }

         }
      }
   }

   public String toString() {
      return this.getClass().getName() + "[gap=" + this.gap + "]";
   }
}
