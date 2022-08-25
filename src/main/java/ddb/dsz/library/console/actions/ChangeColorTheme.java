package ddb.dsz.library.console.actions;

import ddb.console.ColorTheme;
import ddb.console.OptionPane;
import ddb.dsz.library.console.Console;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ChangeColorTheme extends AbstractAction {
   private final Console console;

   public ChangeColorTheme(Console console) {
      this.console = console;
   }

   @Override
   public void actionPerformed(ActionEvent actionEvent) {
      ColorTheme colorTheme = OptionPane.getInstance().displayThemeSelectionDialog(this.console.getCurrentTheme());
      if (colorTheme != null) {
         this.console.setTheme(colorTheme);
      }
   }
}
