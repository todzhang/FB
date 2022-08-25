package ddb.dsz.plugin.taskmanager.details.group;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

public class GroupDisplay extends JPanel {
   protected CoreController core;

   public GroupDisplay(CoreController core, Group group) {
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
