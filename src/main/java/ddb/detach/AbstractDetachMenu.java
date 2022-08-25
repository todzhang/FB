package ddb.detach;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class AbstractDetachMenu extends JPopupMenu {
   protected Workbench workbench;

   public AbstractDetachMenu(final Workbench workbench) {
      this.workbench = workbench;
      JMenu var2;
      final JCheckBoxMenuItem var6;
      if (this.allowTabRotate()) {
         var2 = new JMenu("Align Tabs");
         JCheckBoxMenuItem var3 = new JCheckBoxMenuItem("Top", workbench.getTabPlacement() == 1);
         JCheckBoxMenuItem var4 = new JCheckBoxMenuItem("Bottom", workbench.getTabPlacement() == 3);
         JCheckBoxMenuItem var5 = new JCheckBoxMenuItem("Right", workbench.getTabPlacement() == 4);
         var6 = new JCheckBoxMenuItem("Left", workbench.getTabPlacement() == 2);
         var2.add(var3);
         var2.add(var5);
         var2.add(var6);
         var2.add(var4);
         var3.addActionListener(new AbstractDetachMenu.RotatePlacement(1));
         var5.addActionListener(new AbstractDetachMenu.RotatePlacement(4));
         var6.addActionListener(new AbstractDetachMenu.RotatePlacement(2));
         var4.addActionListener(new AbstractDetachMenu.RotatePlacement(3));
         this.add(var2);
         JMenu var7 = new JMenu("Tab Policy");
         JCheckBoxMenuItem var8 = new JCheckBoxMenuItem("Scrolling", workbench.getTabLayoutPolicy() == 1);
         JCheckBoxMenuItem var9 = new JCheckBoxMenuItem("Wrapping", workbench.getTabLayoutPolicy() == 0);
         var7.add(var8);
         var7.add(var9);
         var8.addActionListener(new AbstractDetachMenu.TabPolicy(1));
         var9.addActionListener(new AbstractDetachMenu.TabPolicy(0));
         this.add(var7);
      }

      if (this.allowVisibleMenu()) {
         var2 = new JMenu(this.getVisibleItemName());
         Vector var10 = new Vector();
         Iterator var11 = workbench.getTabs().iterator();

         while(var11.hasNext()) {
            final Tabbable var12 = (Tabbable)var11.next();
            if (var12 != null && var12.isHideable() && !var12.isDetached()) {
               JCheckBoxMenuItem var61 = new JCheckBoxMenuItem(var12.getName(), !var12.isHidden());
               var61.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent var1x) {
                     if (var61.isSelected()) {
                        workbench.enqueAction(Workbench.WorkbenchAction.UNHIDETAB, var12);
                        workbench.enqueAction(Workbench.WorkbenchAction.SETSELECTEDTAB, var12);
                     } else {
                        workbench.enqueAction(Workbench.WorkbenchAction.HIDETAB, var12);
                     }

                  }
               });
               var10.add(var61);
            }
         }

         if (var10.size() > 0) {
            this.add(var2);
            var11 = var10.iterator();

            while(var11.hasNext()) {
               JMenuItem var13 = (JMenuItem)var11.next();
               var2.add(var13);
            }
         }
      }

   }

   protected String getVisibleItemName() {
      return "Visible Items";
   }

   protected boolean allowVisibleMenu() {
      return true;
   }

   protected boolean allowTabRotate() {
      return true;
   }

   private class TabPolicy implements ActionListener {
      int policy;

      public TabPolicy(int var2) {
         this.policy = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         AbstractDetachMenu.this.workbench.setTabLayoutPolicy(this.policy);
      }
   }

   private class RotatePlacement implements ActionListener {
      int placement;

      public RotatePlacement(int var2) {
         this.placement = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         AbstractDetachMenu.this.workbench.setTabPlacement(this.placement);
      }
   }
}
