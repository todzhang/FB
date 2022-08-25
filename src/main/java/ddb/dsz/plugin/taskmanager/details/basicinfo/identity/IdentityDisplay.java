package ddb.dsz.plugin.taskmanager.details.basicinfo.identity;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.AbstractDetails;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.Identity;
import javax.swing.JLabel;

public class IdentityDisplay extends AbstractDetails {
   JLabel labelName = new JLabel("Name:");
   JLabel labelType = new JLabel("Type:");
   JLabel labelAttr = new JLabel("Attributes:");
   JLabel fieldName = new JLabel();
   JLabel fieldType = new JLabel();
   JLabel fieldAttr = new JLabel();
   int nextRow = 0;
   protected CoreController core;

   public IdentityDisplay(CoreController core, Identity identity) {
      this.core = core;
      super.addComponent(this.labelName, 0, 0);
      super.addComponent(this.labelType, 0, 1);
      super.addComponent(this.labelAttr, 0, 2);
      super.addComponent(this.fieldName, 1, 0);
      super.addComponent(this.fieldType, 1, 1);
      super.addComponent(this.fieldAttr, 1, 2);
      this.fieldName.setText(identity.getName());
      this.fieldType.setText(identity.getType());
      this.fieldAttr.setText(identity.getAttributes());
      this.nextRow += 3;
   }
}
