package ddb.dsz.plugin.taskmanager.details.module;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class ModuleDisplay extends JPanel {
   protected CoreController core;
   private JTextField fieldBaseAddress;
   private JTextField fieldEntryPoint;
   private JTextField fieldHashMD5;
   private JTextField fieldHashSHA1;
   private JTextField fieldHashSHA256;
   private JTextField fieldHashSHA512;
   private JTextField fieldName;
   private JTextField fieldSize;
   private JLabel labelBaseAddress;
   private JLabel labelEntryPoint;
   private JLabel labelHashMD5;
   private JLabel labelHashSHA1;
   private JLabel labelHashSHA256;
   private JLabel labelHashSHA512;
   private JLabel labelName;
   private JLabel labelSize;

   public ModuleDisplay(CoreController core, Module module) {
      this.core = core;
      this.initComponents();
      this.fieldBaseAddress.setText(String.format("0x%08x", module.getBaseAddress()));
      this.fieldEntryPoint.setText(String.format("0x%08x", module.getEntryPoint()));
      this.fieldSize.setText(String.format("0x%08x", module.getImageSize()));
      this.fieldName.setText(module.getName());
      this.fieldHashMD5.setText(module.getHash(Module.Hash.MD5));
      this.fieldHashSHA1.setText(module.getHash(Module.Hash.SHA1));
      this.fieldHashSHA256.setText(module.getHash(Module.Hash.SHA256));
      this.fieldHashSHA512.setText(module.getHash(Module.Hash.SHA512));
      String name = ModuleRenderer.getLastPathComponent(module.getName());
      if (name == null || name.trim().length() == 0) {
         name = "Unknown module";
      }

      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), ModuleRenderer.getLastPathComponent(module.getName())));
   }

   private void initComponents() {
      this.labelName = new JLabel();
      this.labelSize = new JLabel();
      this.labelBaseAddress = new JLabel();
      this.labelEntryPoint = new JLabel();
      this.fieldName = new JTextField();
      this.fieldSize = new JTextField();
      this.fieldBaseAddress = new JTextField();
      this.fieldEntryPoint = new JTextField();
      this.labelHashMD5 = new JLabel();
      this.fieldHashMD5 = new JTextField();
      this.fieldHashSHA1 = new JTextField();
      this.labelHashSHA1 = new JLabel();
      this.fieldHashSHA256 = new JTextField();
      this.labelHashSHA256 = new JLabel();
      this.fieldHashSHA512 = new JTextField();
      this.labelHashSHA512 = new JLabel();
      this.labelName.setText("Name:");
      this.labelSize.setText("Size:");
      this.labelBaseAddress.setText("Base Address:");
      this.labelEntryPoint.setText("Entry Point:");
      this.fieldName.setEditable(false);
      this.fieldSize.setEditable(false);
      this.fieldBaseAddress.setEditable(false);
      this.fieldEntryPoint.setEditable(false);
      this.labelHashMD5.setText("MD5 Hash:");
      this.fieldHashMD5.setEditable(false);
      this.fieldHashSHA1.setEditable(false);
      this.labelHashSHA1.setText("SHA1 Hash:");
      this.fieldHashSHA256.setEditable(false);
      this.labelHashSHA256.setText("SHA256 Hash:");
      this.fieldHashSHA512.setEditable(false);
      this.labelHashSHA512.setText("SHA512 Hash:");
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(1).add(this.labelName).add(this.labelSize).add(this.labelBaseAddress).add(this.labelEntryPoint).add(this.labelHashMD5).add(this.labelHashSHA1).add(this.labelHashSHA256).add(this.labelHashSHA512)).addPreferredGap(0).add(layout.createParallelGroup(1).add(this.fieldName, -1, 202, 32767).add(this.fieldHashSHA1, -1, 202, 32767).add(this.fieldHashMD5, -1, 202, 32767).add(this.fieldSize, -1, 202, 32767).add(this.fieldBaseAddress, -1, 202, 32767).add(this.fieldHashSHA512, -1, 202, 32767).add(this.fieldEntryPoint, -1, 202, 32767).add(this.fieldHashSHA256, -1, 202, 32767)).addContainerGap()));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(layout.createSequentialGroup().addContainerGap().add(layout.createParallelGroup(3).add(this.labelName).add(this.fieldName, -2, -1, -2)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.labelSize).add(this.fieldSize, -2, -1, -2)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.labelBaseAddress).add(this.fieldBaseAddress, -2, -1, -2)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.labelEntryPoint).add(this.fieldEntryPoint, -2, -1, -2)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.fieldHashMD5, -2, -1, -2).add(this.labelHashMD5)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.fieldHashSHA1, -2, -1, -2).add(this.labelHashSHA1)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.fieldHashSHA256, -2, -1, -2).add(this.labelHashSHA256)).addPreferredGap(0).add(layout.createParallelGroup(3).add(this.fieldHashSHA512, -2, -1, -2).add(this.labelHashSHA512)).addContainerGap(-1, 32767)));
   }
}
