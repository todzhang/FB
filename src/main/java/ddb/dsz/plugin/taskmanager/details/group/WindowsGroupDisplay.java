package ddb.dsz.plugin.taskmanager.details.group;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.group.Attribute;
import ddb.dsz.plugin.taskmanager.processinformation.group.WindowsGroup;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class WindowsGroupDisplay extends GroupDisplay {
   private JTextField fieldMask;
   private JTextField fieldName;
   private JTextField fieldType;
   private JLabel labelMask;
   private JLabel labelName;
   private JLabel labelType;
   private JList listAttributes;
   private JPanel panelAttributes;
   private JScrollPane scrollAttributes;
   private DefaultListModel attributeModel = new DefaultListModel();

   public WindowsGroupDisplay(CoreController core, WindowsGroup group) {
      super(core, group);
      this.initComponents();
      this.fieldMask.setText(String.format("0x%08x", group.getMask()));
      this.fieldName.setText(group.getName());
      this.fieldType.setText(group.getType());
      Iterator i$ = group.getAttributes().iterator();

      while(i$.hasNext()) {
         Attribute attribute = (Attribute)i$.next();
         this.attributeModel.addElement(attribute);
      }

      this.listAttributes.setModel(this.attributeModel);
   }

   private void initComponents() {
      this.labelType = new JLabel();
      this.labelName = new JLabel();
      this.panelAttributes = new JPanel();
      this.labelMask = new JLabel();
      this.fieldMask = new JTextField();
      this.scrollAttributes = new JScrollPane();
      this.listAttributes = new JList();
      this.fieldType = new JTextField();
      this.fieldName = new JTextField();
      this.labelType.setText("Type:");
      this.labelName.setText("Name:");
      this.panelAttributes.setBorder(BorderFactory.createTitledBorder("Attributes"));
      this.labelMask.setText("Mask:");
      this.fieldMask.setEditable(false);
      this.listAttributes.setModel(new AbstractListModel() {
         String[] strings = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

         public int getSize() {
            return this.strings.length;
         }

         public Object getElementAt(int i) {
            return this.strings[i];
         }
      });
      this.listAttributes.setCellRenderer(new AttributeRenderer());
      this.scrollAttributes.setViewportView(this.listAttributes);
      GroupLayout panelAttributesLayout = new GroupLayout(this.panelAttributes);
      this.panelAttributes.setLayout(panelAttributesLayout);
      panelAttributesLayout.setHorizontalGroup(panelAttributesLayout.createParallelGroup(1).add(2, panelAttributesLayout.createSequentialGroup().addContainerGap().add(panelAttributesLayout.createParallelGroup(2).add(1, this.scrollAttributes, -1, 170, 32767).add(panelAttributesLayout.createSequentialGroup().add(this.labelMask).addPreferredGap(0).add(this.fieldMask, -1, 138, 32767))).addContainerGap()));
      panelAttributesLayout.setVerticalGroup(panelAttributesLayout.createParallelGroup(1).add(panelAttributesLayout.createSequentialGroup().add(panelAttributesLayout.createParallelGroup(3).add(this.labelMask).add(this.fieldMask, -2, -1, -2)).addPreferredGap(0).add(this.scrollAttributes, -1, 24, 32767).addContainerGap()));
      this.fieldType.setEditable(false);
      this.fieldName.setEditable(false);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(1).add(this.panelAttributes, -1, -1, 32767).add(layout.createSequentialGroup().add(layout.createParallelGroup(1).add(this.labelType).add(this.labelName)).addPreferredGap(0).add(layout.createParallelGroup(1).add(this.fieldName, -1, 167, 32767).add(this.fieldType, -1, 167, 32767)))).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(3).add(this.labelType).add(this.fieldType, -2, -1, -2)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.labelName).add(this.fieldName, -2, -1, -2)).addPreferredGap(0).add(this.panelAttributes, -1, -1, 32767).addContainerGap()));
   }
}
