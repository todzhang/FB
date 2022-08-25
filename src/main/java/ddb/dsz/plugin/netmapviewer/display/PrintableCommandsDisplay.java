package ddb.dsz.plugin.netmapviewer.display;

import ddb.console.OptionPane;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.library.console.ConsoleOutputPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class PrintableCommandsDisplay extends JPanel {
   private ConsoleOutputPane output;
   private JPanel rootPane;

   public PrintableCommandsDisplay(CoreController var1, Task var2, String var3) {
      this.initComponents();
      this.output = new ConsoleOutputPane(var1);
      this.output.setTheme(OptionPane.getInstance().getSharedTheme());
      this.output.setWordWrap(false);
      this.output.setAutoScroll(false);
      this.output.appendDisplay(var3);
      this.rootPane.add(this.output, "Center");
   }

   private void initComponents() {
      this.rootPane = new JPanel();
      this.setLayout(new BorderLayout());
      this.rootPane.setLayout(new BorderLayout());
      this.add(this.rootPane, "Center");
   }
}
