package ddb.dsz.plugin.taskmanager.details.privilege;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.WindowsPrivilege;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import org.jdesktop.layout.GroupLayout;

public class WindowsPrivilegeDisplay extends PrivilegeDisplay {
   private JCheckBox bEnabled;
   private JCheckBox bEnabledByDefault;
   private JCheckBox bUsed;
   private JTextField fieldMask;
   private JLabel labelMask;

   public WindowsPrivilegeDisplay(CoreController core, WindowsPrivilege privilege) {
      super(core, privilege);
      this.initComponents();
      this.bEnabled.setModel(new WindowsPrivilegeDisplay.NoChangeButtonModel(privilege.isEnabled()));
      this.bEnabledByDefault.setModel(new WindowsPrivilegeDisplay.NoChangeButtonModel(privilege.isEnabledByDefault()));
      this.bUsed.setModel(new WindowsPrivilegeDisplay.NoChangeButtonModel(privilege.isUsedAccess()));
      this.fieldMask.setText(String.format("0x%08x", privilege.getMask()));
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), privilege.getName()));
   }

   private void initComponents() {
      this.bEnabled = new JCheckBox();
      this.bEnabledByDefault = new JCheckBox();
      this.bUsed = new JCheckBox();
      this.labelMask = new JLabel();
      this.fieldMask = new JTextField();
      this.bEnabled.setText("Enabled");
      this.bEnabled.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bEnabled.setMargin(new Insets(0, 0, 0, 0));
      this.bEnabledByDefault.setText("Enabled By Default");
      this.bEnabledByDefault.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bEnabledByDefault.setMargin(new Insets(0, 0, 0, 0));
      this.bUsed.setText("Used");
      this.bUsed.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.bUsed.setMargin(new Insets(0, 0, 0, 0));
      this.labelMask.setText("Attribute Mask:");
      this.fieldMask.setEditable(false);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(1).add(2, this.bEnabledByDefault, -1, 183, 32767).add(2, this.bEnabled, -1, 183, 32767).add(2, layout.createSequentialGroup().add(this.labelMask).addPreferredGap(0).add(this.fieldMask, -1, 105, 32767)).add(2, this.bUsed, -1, 183, 32767)).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(this.bEnabled).addPreferredGap(0).add(this.bEnabledByDefault).addPreferredGap(0).add(this.bUsed).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.labelMask).add(this.fieldMask, -2, -1, -2)).addContainerGap(-1, 32767)));
   }

   private class NoChangeButtonModel extends ToggleButtonModel {
      private boolean value;

      public NoChangeButtonModel(boolean value) {
         this.value = value;
      }

      public boolean isSelected() {
         return this.value;
      }
   }
}
