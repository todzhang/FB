package ddb.detach;

import ddb.actions.tabnav.NavigationDirection;
import ddb.actions.tabnav.NavigationListener;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class TabNavigationListener implements NavigationListener {
   public void navigationActionPerformed(NavigationDirection var1, ActionEvent var2) {
      Component var3 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      this.navigateRecursive(var1, var3);
   }

   private boolean navigateRecursive(NavigationDirection var1, Component var2) {
      if (var2 == null) {
         return false;
      } else if (this.adjust(var1, var2, false)) {
         return true;
      } else {
         if (var2 instanceof TabbableFrame) {
            if (this.navigateRecursive(var1, ((TabbableFrame)TabbableFrame.class.cast(var2)).getWorkbench())) {
               return true;
            }
         } else if (this.navigateRecursive(var1, var2.getParent())) {
            return true;
         }

         return this.adjust(var1, var2, true);
      }
   }

   private boolean adjust(NavigationDirection var1, Component var2, boolean var3) {
      if (var2 instanceof JTextField) {
         switch(var1) {
         case NEXT:
            return jumpRight((JComponent)JTextField.class.cast(var2));
         case PREVIOUS:
            return jumpLeft((JComponent)JTextField.class.cast(var2));
         }
      } else {
         if (var2 instanceof Workbench) {
            Workbench var6 = (Workbench)var2;
            return var6.navigate(var1, var3, false);
         }

         if (var2 instanceof JTabbedPane) {
            JTabbedPane var4 = (JTabbedPane)var2;
            int var5 = var4.getSelectedIndex();
            switch(var1) {
            case NEXT:
               if (var5 + 1 < var4.getTabCount()) {
                  var4.setSelectedIndex(var5 + 1);
                  return true;
               }

               if (var3) {
                  var4.setSelectedIndex(0);
               }
               break;
            case PREVIOUS:
               if (var5 > 0) {
                  var4.setSelectedIndex(var5 - 1);
                  return true;
               }

               if (var3) {
                  var4.setSelectedIndex(var4.getTabCount() - 1);
               }
            }
         }
      }

      return false;
   }

   public static boolean jumpRight(JComponent var0) {
      if (!(var0 instanceof JTextField)) {
         return false;
      } else {
         JTextField var1 = (JTextField)var0;
         String var2 = var1.getText();
         int var3 = var1.getSelectionEnd();
         if (var3 >= var2.length()) {
            return false;
         } else {
            for(boolean var4 = false; var3 < var2.length(); ++var3) {
               boolean var5 = false;
               switch(var2.charAt(var3)) {
               case '\t':
               case ' ':
                  var4 = true;
                  break;
               default:
                  var5 = var4;
               }

               if (var5) {
                  break;
               }
            }

            var1.setSelectionStart(var3);
            var1.setSelectionEnd(var3);
            return true;
         }
      }
   }

   public static boolean jumpLeft(JComponent var0) {
      if (!(var0 instanceof JTextField)) {
         return false;
      } else {
         JTextField var1 = (JTextField)var0;
         String var2 = var1.getText();
         int var3 = var1.getSelectionStart() - 1;
         if (var3 < 0) {
            return false;
         } else {
            for(boolean var4 = false; var3 > 0; --var3) {
               boolean var5 = false;
               switch(var2.charAt(var3)) {
               case '\t':
               case ' ':
                  if (var4) {
                     ++var3;
                     var5 = true;
                  }
                  break;
               default:
                  var4 = true;
               }

               if (var5) {
                  break;
               }
            }

            var1.setSelectionStart(var3);
            var1.setSelectionEnd(var3);
            return true;
         }
      }
   }
}
