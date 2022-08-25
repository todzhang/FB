package ddb.dsz.plugin.scripteditor;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

class NoAcceleratorMenuItem extends JMenuItem {
   KeyStroke fakeAccelerator;

   public NoAcceleratorMenuItem() {
   }

   public NoAcceleratorMenuItem(Action var1) {
      super(var1);
   }

   public NoAcceleratorMenuItem(Icon var1) {
      super(var1);
   }

   public NoAcceleratorMenuItem(String var1, Icon var2) {
      super(var1, var2);
   }

   public NoAcceleratorMenuItem(String var1, int var2) {
      super(var1, var2);
   }

   public NoAcceleratorMenuItem(String var1) {
      super(var1);
   }

   public void setAccelerator(KeyStroke var1) {
      this.fakeAccelerator = var1;
   }

   public KeyStroke getAccelerator() {
      return this.fakeAccelerator;
   }
}
