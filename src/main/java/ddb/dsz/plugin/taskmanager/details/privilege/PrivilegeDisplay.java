package ddb.dsz.plugin.taskmanager.details.privilege;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

public class PrivilegeDisplay extends JPanel {
   protected CoreController core;

   public PrivilegeDisplay(CoreController core, Privilege privilege) {
      this.core = core;
      this.initComponents();
   }

   private void initComponents() {
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(0, 400, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(0, 300, 32767));
   }
}
