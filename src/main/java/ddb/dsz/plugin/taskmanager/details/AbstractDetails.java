package ddb.dsz.plugin.taskmanager.details;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public abstract class AbstractDetails extends JPanel {
   GridBagLayout gbl = new GridBagLayout();
   GridBagConstraints gbc = new GridBagConstraints();

   public AbstractDetails() {
      super.setLayout(this.gbl);
      this.gbc.anchor = 17;
   }

   protected void addComponent(Component c, int gridx, int gridy, int width, int height, double weightx, double weighty) {
      super.add(c);
      this.gbc.gridx = gridx;
      this.gbc.gridy = gridy;
      this.gbc.gridwidth = width;
      this.gbc.gridheight = height;
      this.gbc.weightx = weightx;
      this.gbc.weighty = weighty;
      this.gbl.addLayoutComponent(c, this.gbc);
   }

   protected void addComponent(Component c, int gridx, int gridy) {
      this.addComponent(c, gridx, gridy, 1, 1, 1.0D, 1.0D);
   }
}
