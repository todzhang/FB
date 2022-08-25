package ds.plugin.pluginmanagement;

import ddb.dsz.plugin.Plugin;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class PluginManagerListRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
      Plugin var6 = (Plugin)var2;
      JLabel var7 = (JLabel)super.getListCellRendererComponent(var1, var2, var3, var4, var5);
      var7.setIcon(ImageManager.getIcon(var6.getLogo(), ImageManager.SIZE22));
      var7.setText(var6.getName());
      return var7;
   }
}
