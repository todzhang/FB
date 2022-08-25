package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.plugin.logviewer.gui.renderer.CustomRenderer;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class TaskVariables extends JPanel {
   static final int DEFAULT_MAX_VARS = 25;
   public static final String DATANAME = "Data Variables";
   JPanel main;
   MutableTreeNode root;
   DefaultTreeModel objectModel;
   VariableFilterTreeModel filterModel;
   JTree objects;
   ValueTableModel valueModel;
   JTable values;
   JTextField testPath = new JTextField(15);
   JButton go = new JButton("Apply");
   JButton clear = new JButton("Clear");
   JTextField dataasField = new JTextField(15);
   JSpinner maximumVars = new JSpinner(new SpinnerNumberModel(25, 0, Integer.MAX_VALUE, 5));
   JPopupMenu display = new JPopupMenu();
   CustomRenderer renderer;
   MutableTreeNode commandMetaData;

   public TaskVariables() {
      super(new BorderLayout());
      this.renderer = new CustomRenderer(this.values);
      this.commandMetaData = null;
      this.root = new DefaultMutableTreeNode("Data Variables");
      this.objectModel = new DefaultTreeModel(this.root);
      this.filterModel = new VariableFilterTreeModel(this.objectModel);
      this.objects = new JTree(this.filterModel);
      this.valueModel = new ValueTableModel();
      this.values = new JTable(this.valueModel);
      this.values.setDefaultRenderer(Object.class, this.renderer);
      JScrollPane objectScroll = new JScrollPane(this.objects);
      JPanel details = new JPanel();
      JScrollPane valueScroll = new JScrollPane(this.values);
      details.setLayout(new BorderLayout());
      details.add(valueScroll);
      JLabel filterLabel = new JLabel("Filter:");
      JLabel dataas = new JLabel("Data Name:");
      JLabel maximumVarsLabel = new JLabel("Maximum Vars:");
      this.maximumVars.setToolTipText("Roughly the maximum number of variables to show.");
      JPanel topBanner = new JPanel();
      GridBagLayout gbl = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      topBanner.setLayout(gbl);
      topBanner.add(filterLabel);
      topBanner.add(this.testPath);
      topBanner.add(this.go);
      topBanner.add(this.clear);
      topBanner.add(dataas);
      topBanner.add(this.dataasField);
      topBanner.add(maximumVarsLabel);
      topBanner.add(this.maximumVars);
      gbc.fill = 1;
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbl.addLayoutComponent(filterLabel, gbc);
      gbc.gridx = 1;
      gbc.weightx = 1000.0D;
      gbl.addLayoutComponent(this.testPath, gbc);
      gbc.gridx = 2;
      gbc.weightx = 0.0D;
      gbl.addLayoutComponent(this.go, gbc);
      gbc.gridx = 3;
      gbl.addLayoutComponent(this.clear, gbc);
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbl.addLayoutComponent(dataas, gbc);
      gbc.gridx = 1;
      gbl.addLayoutComponent(this.dataasField, gbc);
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbl.addLayoutComponent(maximumVarsLabel, gbc);
      gbc.gridx = 1;
      gbl.addLayoutComponent(this.maximumVars, gbc);
      final JSplitPane splitter = new JSplitPane(1, objectScroll, details);
      this.main = new JPanel();
      this.add(this.main);
      this.main.setLayout(new BorderLayout());
      this.main.add(splitter);
      this.main.add(topBanner, "North");
      this.main.addComponentListener(new ComponentAdapter() {
         private void update() {
            splitter.setDividerLocation(0.4D);
            TaskVariables.this.removeComponentListener(this);
         }

         public void componentResized(ComponentEvent e) {
            this.update();
         }

         public void componentMoved(ComponentEvent e) {
            this.update();
         }

         public void componentShown(ComponentEvent e) {
            this.update();
         }

         public void componentHidden(ComponentEvent e) {
            this.update();
         }
      });
      this.objects.addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            if (path == null) {
               TaskVariables.this.valueModel.setCurrent((ObjectValue)null);
            } else {
               VariableFilterTreeModel.FilterTreeNode node2 = (VariableFilterTreeModel.FilterTreeNode)VariableFilterTreeModel.FilterTreeNode.class.cast(path.getLastPathComponent());
               DefaultMutableTreeNode node = (DefaultMutableTreeNode)node2.getRawNode();
               Object o = node.getUserObject();
               if (o instanceof NodeElement) {
                  NodeElement ne = (NodeElement)o;
                  TaskVariables.this.valueModel.setCurrent(ne.value);
               } else {
                  TaskVariables.this.valueModel.setCurrent((ObjectValue)null);
               }

            }
         }
      });
      this.go.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskVariables.this.filterModel.applyFilter(TaskVariables.this.testPath.getText());
         }
      });
      this.clear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskVariables.this.testPath.setText("");
            TaskVariables.this.filterModel.applyFilter("");
         }
      });
      JMenuItem decimal = new JRadioButtonMenuItem("Decimal");
      JMenuItem octal = new JRadioButtonMenuItem("Octal");
      JMenuItem hexidecimal = new JRadioButtonMenuItem("Hexidecimal");
      ButtonGroup baseX = new ButtonGroup();
      baseX.add(decimal);
      baseX.add(octal);
      baseX.add(hexidecimal);
      this.display.add(decimal);
      this.display.add(octal);
      this.display.add(hexidecimal);
      decimal.setSelected(true);
      decimal.addActionListener(new ChangeBase(10, "", this.renderer));
      octal.addActionListener(new ChangeBase(8, "0", this.renderer));
      hexidecimal.addActionListener(new ChangeBase(16, "0x", this.renderer));
      this.values.addMouseListener(new MouseAdapter() {
         private void maybePopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
               TaskVariables.this.display.show(TaskVariables.this.values, e.getX(), e.getY());
            }

         }

         public void mouseClicked(MouseEvent e) {
            this.maybePopup(e);
         }

         public void mousePressed(MouseEvent e) {
            this.maybePopup(e);
         }

         public void mouseReleased(MouseEvent e) {
            this.maybePopup(e);
         }
      });
   }

   public void clearVariables() {
      this.commandMetaData = null;

      while(this.root.getChildCount() > 0) {
         this.root.remove(0);
      }

   }

   public void addData(ObjectValue value) {
      Integer i = (Integer)this.maximumVars.getValue();
      int count = this.root.getChildCount();
      if (i <= 0 || count < i) {
         this.setData(value, this.root, (String)null);
         this.filterModel.update();
      }
   }

   private void setData(ObjectValue value, MutableTreeNode node, String name) {
      if (name != null) {
         NodeElement ne = new NodeElement();
         ne.name = name;
         ne.value = value;
         node.setUserObject(ne);
      }

      int countVars = 0;
      Iterator i$ = value.getObjectNames().iterator();

      label42:
      while(i$.hasNext()) {
         String s = (String)i$.next();
         boolean bCmdMeta = s.equalsIgnoreCase("CommandMetaData");
         Iterator ii$ = value.getObjects(s).iterator();

         while(true) {
            while(true) {
               if (!i$.hasNext()) {
                  continue label42;
               }

               ObjectValue objVal = (ObjectValue)i$.next();
               if (bCmdMeta && this.commandMetaData != null) {
                  if (this.commandMetaData.getChildCount() < 10) {
                     this.setData(objVal, this.commandMetaData, s);
                  }
               } else if (countVars < (Integer)this.maximumVars.getValue()) {
                  MutableTreeNode childNode = new DefaultMutableTreeNode();
                  if (bCmdMeta && this.commandMetaData == null) {
                     this.commandMetaData = childNode;
                  }

                  this.setData(objVal, childNode, s);
                  node.insert(childNode, node.getChildCount());
                  ++countVars;
               }
            }
         }
      }

   }

   public void setDataAs(String dataas) {
      this.dataasField.setText(dataas);
   }
}
