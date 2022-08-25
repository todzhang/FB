package ddb.dsz.plugin.taskmanager.details.basicinfo;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.AbstractDetails;
import ddb.dsz.plugin.taskmanager.details.Generator;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.BasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.Identity;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class BasicInfoDisplay extends AbstractDetails {
   protected int nextRow = 0;
   protected CoreController core;

   public BasicInfoDisplay(CoreController core, BasicInfo basicInfo) {
      this.core = core;
      this.create(basicInfo.getUser(), "User");
      this.create(basicInfo.getOwner(), "Owner");
      this.create(basicInfo.getPrimaryGroup(), "Primary Group");
      ++this.nextRow;
   }

   protected void create(Identity identity, String label) {
      JPanel panel = Generator.makeIdentityDisplay(this.core, identity);
      panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label));
      super.addComponent(panel, -1, 0);
   }
}
