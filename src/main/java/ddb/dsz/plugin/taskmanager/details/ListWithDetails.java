package ddb.dsz.plugin.taskmanager.details;

import java.awt.BorderLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.layout.GroupLayout;

public abstract class ListWithDetails extends JPanel {
   private JPanel details;
   private JSplitPane displaySplitter;
   private JList list;
   private JScrollPane listScroll;
   protected DefaultListModel model = new DefaultListModel();

   public ListWithDetails() {
      this.initComponents();
      this.details.setLayout(new BorderLayout());
   }

   protected ListCellRenderer getRenderer() {
      return new DefaultListCellRenderer();
   }

   protected abstract JPanel getDetailed(Object var1);

   private void initComponents() {
      this.displaySplitter = new JSplitPane();
      this.listScroll = new JScrollPane();
      this.list = new JList();
      this.details = new JPanel();
      this.list.setModel(this.model);
      this.list.setCellRenderer(this.getRenderer());
      this.list.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            ListWithDetails.this.listValueChanged(evt);
         }
      });
      this.listScroll.setViewportView(this.list);
      this.displaySplitter.setLeftComponent(this.listScroll);
      GroupLayout detailsLayout = new GroupLayout(this.details);
      this.details.setLayout(detailsLayout);
      detailsLayout.setHorizontalGroup(detailsLayout.createParallelGroup(1).add(0, 369, 32767));
      detailsLayout.setVerticalGroup(detailsLayout.createParallelGroup(1).add(0, 298, 32767));
      this.displaySplitter.setRightComponent(this.details);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(2, this.displaySplitter, -1, 400, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(2, this.displaySplitter, -1, 300, 32767));
   }

   private void listValueChanged(ListSelectionEvent evt) {
      JPanel panel = this.getDetailed(this.list.getSelectedValue());
      this.details.removeAll();
      this.details.add(panel, "Center");
      this.details.validate();
   }
}
