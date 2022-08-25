package ddb.util.checkedtablemodel;

import ddb.util.checkedtablemodel.listeners.HideAllListener;
import ddb.util.checkedtablemodel.listeners.HideThisListener;
import ddb.util.checkedtablemodel.listeners.ShowAllListener;
import ddb.util.checkedtablemodel.listeners.ShowOnlyThisListener;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class CheckableFilterList<T> extends JPanel {
   JTable table = new JTable();
   JPopupMenu menu = new JPopupMenu();
   Object menuSelection = null;
   CheckedTableModel<T> model;
   Comparator<Object> comp;

   public CheckableFilterList(String var1, CheckedTableSelection<T> var2, Comparator<T> var3) {
      super(new BorderLayout());
      this.setPreferredSize(new Dimension(250, 0));
      this.model = new CheckedTableModel(var2, var3);
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), var1));
      ColumnHidingModel var4 = new ColumnHidingModel(CheckedTableColumns.class);
      this.table.setColumnModel(var4);
      this.table.setModel(this.model);
      this.table.setShowGrid(false);
      this.table.setTableHeader((JTableHeader)null);
      this.table.setDefaultEditor(Boolean.class, new CheckBoxEditor(this.model));
      this.table.setDefaultRenderer(Boolean.class, new CheckBoxRenderer(this.model));
      this.table.setAutoResizeMode(3);
      this.table.setRowSelectionAllowed(false);
      this.table.setCellSelectionEnabled(false);
      this.addMenuItem("Show All", new ShowAllListener(this), false);
      this.addMenuItem("Hide All", new HideAllListener(this), false);
      this.menu.addSeparator();
      this.addMenuItem("Show Only This", new ShowOnlyThisListener(this), true);
      this.addMenuItem("Hide This", new HideThisListener(this), true);
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            this.maybeShowPopup(var1);
         }

         public void mousePressed(MouseEvent var1) {
            this.maybeShowPopup(var1);
         }

         public void mouseReleased(MouseEvent var1) {
            this.maybeShowPopup(var1);
         }

         private void maybeShowPopup(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               int var2 = var1.getY() / CheckableFilterList.this.table.getRowHeight();
               if (CheckableFilterList.this.table.getRowCount() > var2 && var2 > -1) {
                  CheckableFilterList.this.menuSelection = CheckableFilterList.this.model.getValueAt(var2, CheckedTableColumns.TAG.ordinal());
               } else {
                  CheckableFilterList.this.menuSelection = null;
               }

               CheckableFilterList.this.menu.show(CheckableFilterList.this.table, var1.getX(), var1.getY());
            }
         }
      });
      this.table.addMouseListener(new MouseAdapter() {
         public void mouseEntered(MouseEvent var1) {
            this.update(var1);
         }

         public void mouseExited(MouseEvent var1) {
            this.update(var1);
         }

         public void mouseMoved(MouseEvent var1) {
            this.update(var1);
         }

         private void update(MouseEvent var1) {
            int var2 = var1.getY() / CheckableFilterList.this.table.getRowHeight();
            if (var2 >= 0 && var2 < CheckableFilterList.this.table.getRowCount()) {
               DataEntry var3 = (DataEntry)CheckableFilterList.this.model.getValueAt(var2, CheckedTableColumns.OBJECT.ordinal());
               CheckableFilterList.this.table.setToolTipText(var3.getTooltip());
            }

         }
      });
      JScrollPane var5 = new JScrollPane(this.table);
      var5.setHorizontalScrollBarPolicy(31);
      this.add(var5);
      var4.hide(CheckedTableColumns.CAPTION);
      var4.hide(CheckedTableColumns.TAG);
      var4.hide(CheckedTableColumns.OBJECT);
   }

   private void addMenuItem(String var1, ActionListener var2, boolean var3) {
      final JMenuItem var4 = new JMenuItem(var1);
      var4.addActionListener(var2);
      this.menu.add(var4);
      if (var3) {
         this.menu.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent var1) {
               var4.setEnabled(CheckableFilterList.this.menuSelection != null);
            }
         });
      }

   }

   public void showAll() {
      this.model.showAll();
   }

   public void hideAll() {
      this.model.hideAll();
   }

   public void hideThis() {
      this.model.hide(this.menuSelection);
   }

   public void showOnlyThis() {
      this.model.showOnly(this.menuSelection);
   }

   public boolean addElement(T var1, boolean var2) {
      return this.addElement((String)null, var1, var2);
   }

   public boolean addElement(String var1, T var2, boolean var3) {
      return this.addElement(var1, var1, var2, var3);
   }

   public boolean addElement(String var1, String var2, T var3, boolean var4) {
      return this.model.addElement(var1, var2, var3, var4);
   }

   public void deleteElement(T var1) {
      this.model.deleteElement(var1);
   }

   public int getRowCount() {
      return this.model.getRowCount();
   }

   public Object getItem(int var1) {
      return this.model.getValueAt(var1, CheckedTableColumns.TAG);
   }
}
