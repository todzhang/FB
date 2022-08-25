package ddb.detach;

import ddb.imagemanager.ImageManager;
import javax.swing.JMenuItem;

public class TabbablePopupMenu extends AbstractDetachMenu {
   public static final String DETACH_ICON = "images/desktop-enhancments.png";

   public TabbablePopupMenu(Tabbable var1, Workbench var2) {
      super(var2);
      JMenuItem var3;
      if (var1.isDetachable()) {
         var3 = this.add("Detach " + var1.getName());
         var3.addActionListener(new DetachTabbableDisplayAction(var1, var2));
         var3.setIcon(ImageManager.getIcon("images/desktop-enhancments.png", var2.getLabelImageSize()));
      }

      var3 = this.add("Rename");
      var3.addActionListener(new RenameTabbableAction(var1, var2));
      if (var1.isClosable() && var1.isUserClosable()) {
         JMenuItem var4 = this.add("Close");
         var4.addActionListener(new CloseTabbableAction(var1, var2));
      }

   }
}
