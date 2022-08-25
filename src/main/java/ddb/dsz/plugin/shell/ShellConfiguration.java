package ddb.dsz.plugin.shell;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.shell.jaxb.shellcommands.SystemType;
import ddb.swing.DszTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.jdesktop.layout.GroupLayout;

public class ShellConfiguration extends JPanel {
   Shell owner;
   Component[] disable;
   DefaultComboBoxModel model = new DefaultComboBoxModel();
   List<SystemType> systems = new Vector();
   Predicate uniqueHost = PredicateUtils.uniquePredicate();
   ListCellRenderer hostRenderer = new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         String var7 = "";
         String var8 = "??";
         String var9 = "000000";
         if (var6 instanceof JLabel) {
            JLabel var10 = (JLabel)var6;
            if (var2 instanceof HostInfo) {
               HostInfo var11 = (HostInfo)var2;
               var7 = var11.getId();
               if (var11.isLocal()) {
                  var8 = "Local";
                  var9 = "ff0000";
               } else {
                  var8 = "Remote";
               }
            } else if (var2 != null) {
               var7 = var2.toString();
            } else {
               var7 = "Unknown";
            }

            var10.setText(String.format("<html><b>%s</b> - <font color=\"#%s\">%s</font></html>", var7, var9, var8));
         }

         return var6;
      }
   };
   BasicComboBoxEditor comboBoxEditor = new BasicComboBoxEditor() {
      public void setItem(Object var1) {
         if (var1 instanceof HostInfo) {
            super.setItem(((HostInfo)HostInfo.class.cast(var1)).getId());
         } else {
            super.setItem(var1);
         }

      }

      public Object getItem() {
         return super.getItem();
      }
   };
   private JCheckBox bAllowDszCommands;
   private JTextField commandField;
   private JLabel commandLabel;
   private JTextField groupField;
   private JLabel groupLabel;
   private JTextField initialInputField;
   private JLabel initialInputLabel;
   private JComboBox inputField;
   private JLabel inputLabel;
   private JPanel jPanel1;
   private JPanel jPanel3;
   private JLabel osDisplay;
   private JComboBox outputField;
   private JLabel outputLabel;
   private JButton startButton;
   private JComboBox targetField;
   private JTextField userField;
   private JLabel userLabel;
   private JTextField workingField;
   private JLabel workingLabel;
   public static final String ALLOW_DSZ = "-dsz";
   public static final String COMMAND = "-cmd";
   public static final String GROUP = "-group";
   public static final String INITIALINPUT = "-initial";
   public static final String OUTPUT = "-output";
   public static final String TARGET = "-target";
   public static final String USER = "-user";
   public static final String WORKING = "-working";

   public ShellConfiguration(Shell var1) {
      this.initComponents();
      this.owner = var1;
      this.disable = new Component[]{this.commandField, this.groupField, this.initialInputField, this.inputField, this.outputField, this.startButton, this.userField, this.workingField, this.comboBoxEditor.getEditorComponent(), this.bAllowDszCommands};
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.targetField = new JComboBox();
      this.jPanel3 = new JPanel();
      this.commandLabel = new JLabel();
      this.commandField = new DszTextField("Shell Program");
      this.initialInputLabel = new JLabel();
      this.initialInputField = new JTextField();
      this.outputLabel = new JLabel();
      this.outputField = new JComboBox();
      this.workingLabel = new JLabel();
      this.workingField = new DszTextField("Working Directory");
      this.userLabel = new JLabel();
      this.userField = new JTextField();
      this.groupLabel = new JLabel();
      this.groupField = new JTextField();
      this.bAllowDszCommands = new JCheckBox();
      this.startButton = new JButton();
      this.inputLabel = new JLabel();
      this.inputField = new JComboBox();
      this.osDisplay = new JLabel();
      this.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 2));
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Target:"));
      this.targetField.setEditable(true);
      this.targetField.setModel(this.model);
      this.targetField.setEditor(this.comboBoxEditor);
      this.targetField.setPrototypeDisplayValue("123.456.789.012");
      this.targetField.setRenderer(this.hostRenderer);
      this.targetField.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent var1) {
            ShellConfiguration.this.targetSelected(var1);
         }
      });
      GroupLayout var1 = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(this.targetField, 0, 142, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(this.targetField, -2, -1, -2));
      this.commandLabel.setText("Command:");
      this.initialInputLabel.setText("Initial Input:");
      this.outputLabel.setText("Output Format:");
      this.outputField.setModel(new DefaultComboBoxModel(new String[]{"ascii", "oem", "unicode", "utf8"}));
      this.outputField.setSelectedItem((Object)null);
      this.workingLabel.setText("Working Directory:");
      this.userLabel.setText("User:");
      this.groupLabel.setText("Group:");
      this.bAllowDszCommands.setText("Allow Dsz Commands");
      this.startButton.setText("Start Shell");
      this.startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ShellConfiguration.this.startButtonActionPerformed(var1);
         }
      });
      this.inputLabel.setText("Input Format:");
      this.inputField.setModel(new DefaultComboBoxModel(new String[]{"ascii", "oem", "unicode", "utf8"}));
      this.inputField.setSelectedItem((Object)null);
      GroupLayout var2 = new GroupLayout(this.jPanel3);
      this.jPanel3.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(10, 10, 10).add(var2.createParallelGroup(1).add(this.commandLabel).add(this.initialInputLabel).add(this.workingLabel).add(this.inputLabel).add(this.outputLabel).add(this.userLabel).add(this.groupLabel)).addPreferredGap(0).add(var2.createParallelGroup(1).add(this.commandField, -1, 293, 32767).add(this.initialInputField, -1, 293, 32767).add(this.workingField, -1, 293, 32767).add(this.inputField, 0, 293, 32767).add(this.outputField, 0, 293, 32767).add(this.userField, -1, 293, 32767).add(this.groupField, -1, 293, 32767)).addContainerGap()).add(var2.createSequentialGroup().addContainerGap().add(this.bAllowDszCommands).addPreferredGap(0, 183, 32767).add(this.startButton).addContainerGap(10, 32767)));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(var2.createParallelGroup(3).add(this.commandLabel).add(this.commandField, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.initialInputLabel).add(this.initialInputField, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.workingField, -2, -1, -2).add(this.workingLabel)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.inputLabel).add(this.inputField, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.outputLabel).add(this.outputField, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.userLabel).add(this.userField, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.groupField, -2, -1, -2).add(this.groupLabel)).addPreferredGap(0).add(var2.createParallelGroup(1).add(this.bAllowDszCommands).add(this.startButton)).addContainerGap(-1, 32767)));
      this.osDisplay.setHorizontalAlignment(0);
      this.osDisplay.setText(" ");
      GroupLayout var3 = new GroupLayout(this);
      this.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().add(this.jPanel1, -2, -1, -2).addPreferredGap(0).add(this.osDisplay, -1, 239, 32767).addContainerGap()).add(this.jPanel3, -1, -1, 32767));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().add(var3.createParallelGroup(1).add(this.jPanel1, -2, -1, -2).add(var3.createSequentialGroup().add(21, 21, 21).add(this.osDisplay))).addPreferredGap(0).add(this.jPanel3, -2, -1, -2)));
   }

   private void startButtonActionPerformed(ActionEvent var1) {
      Component[] var2 = this.disable;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         var5.setEnabled(false);
      }

      StringBuilder var6 = new StringBuilder();
      Object var7 = this.targetField.getSelectedItem();
      if (var7 instanceof HostInfo) {
         var6.append(String.format("dst=%s ", ((HostInfo)HostInfo.class.cast(var7)).getId()));
      } else if (var7 instanceof String) {
         var6.append(String.format("dst=%s ", var7));
      }

      var6.append("run -command \"" + this.escape(this.commandField.getText().trim()) + "\"");
      if (this.initialInputField.getText().trim().length() > 0) {
         var6.append(" -redirect \"" + this.escape(this.initialInputField.getText().trim()) + "\"");
      } else {
         var6.append(" -redirect ");
      }

      if (this.inputField.getSelectedItem() != null) {
         var6.append(" -input " + this.inputField.getSelectedItem());
      }

      if (this.outputField.getSelectedItem() != null) {
         var6.append(" -output " + this.outputField.getSelectedItem());
      }

      if (this.workingField.getText().trim().length() > 0) {
         var6.append(" -directory \"" + this.escape(this.workingField.getText().trim()) + "\"");
      }

      if (this.userField.getText().trim().length() > 0) {
         var6.append(" -user \"" + this.escape(this.userField.getText().trim()) + "\" \"" + this.escape(this.groupField.getText().trim()) + "\"");
      }

      if (this.bAllowDszCommands.isSelected()) {
         var6.append(" -allowdsz");
      }

      this.owner.startCommand(this.targetField.getSelectedItem(), var6.toString());
   }

   private void targetSelected(ItemEvent var1) {
      Object var2 = this.targetField.getSelectedItem();
      if (var2 instanceof String) {
         this.targetField.setSelectedItem(this.owner.getHostById(var2.toString()));
      } else if (var2 instanceof HostInfo) {
         HostInfo var3 = (HostInfo)var2;
         String var4 = "Local";
         String var5 = "ff0000";
         if (!var3.isLocal()) {
            var4 = "Remote";
            var5 = "000000";
         }

         this.osDisplay.setText(String.format("<html><b><font color=\"#%s\">%s</font></b>: %s/%s [%s]</html>", var5, var4, var3.getPlatform(), var3.getArch(), var3.getVersion()));
         if (var3.getPlatform() != null) {
            Iterator var6 = this.systems.iterator();

            while(var6.hasNext()) {
               SystemType var7 = (SystemType)var6.next();

               try {
                  if (var3.getPlatform().matches(var7.getPlatformRegex())) {
                     if (var1 != null) {
                        this.commandField.setText(var7.getCommandLine());
                        this.initialInputField.setText(var7.getInitialCommand());
                        this.outputField.setSelectedItem(var7.getOutputFormat());
                        this.workingField.setText(var7.getWorkingDirectory());
                     }

                     this.showUser(var7.isEnableUser());
                     this.inputField.setEnabled(var7.isAdjustableFormat());
                     this.outputField.setEnabled(var7.isAdjustableFormat());
                     return;
                  }
               } catch (PatternSyntaxException var9) {
               }
            }
         }

         this.commandField.setText("");
         this.initialInputField.setText("");
         this.outputField.setSelectedItem("");
         this.workingField.setText("");
         this.showUser(true);
      }

   }

   public void showUser(boolean var1) {
      this.userField.setEnabled(var1);
      this.userLabel.setEnabled(var1);
      this.groupField.setEnabled(var1);
      this.groupLabel.setEnabled(var1);
      this.validate();
   }

   public void commandFailed() {
      Component[] var1 = this.disable;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Component var4 = var1[var3];
         var4.setEnabled(true);
      }

      this.targetSelected((ItemEvent)null);
   }

   public String escape(String var1) {
      return var1.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
   }

   public List<String> getConfiguration() {
      Vector var1 = new Vector();
      var1.add(String.format("%s=%s", "-dsz", this.bAllowDszCommands.isSelected() ? "true" : "false"));
      var1.add(String.format("%s=%s", "-cmd", this.commandField.getText()));
      var1.add(String.format("%s=%s", "-group", this.groupField.getText()));
      var1.add(String.format("%s=%s", "-initial", this.initialInputField.getText()));
      Object var2 = this.outputField.getSelectedItem();
      if (var2 instanceof String) {
         var1.add(String.format("%s=%s", "-output", var2.toString()));
      }

      var2 = this.targetField.getSelectedItem();
      if (var2 instanceof String) {
         var1.add(String.format("%s=%s", "-target", var2.toString()));
      } else if (var2 instanceof HostInfo) {
         var1.add(String.format("%s=%s", "-target", ((HostInfo)HostInfo.class.cast(var2)).getId()));
      }

      var1.add(String.format("%s=%s", "-user", this.userField.getText()));
      var1.add(String.format("%s=%s", "-working", this.workingField.getText()));
      return var1;
   }

   public boolean parseArgument(String var1, String var2) {
      if (var1.equals("-dsz")) {
         if (var2.equals("true")) {
            this.bAllowDszCommands.setSelected(true);
         } else {
            this.bAllowDszCommands.setSelected(false);
         }

         return true;
      } else if (var1.equals("-cmd")) {
         this.commandField.setText(var2);
         return true;
      } else if (var1.equals("-group")) {
         this.groupField.setText(var2);
         return true;
      } else if (var1.equals("-initial")) {
         this.initialInputField.setText(var2);
         return true;
      } else if (var1.equals("-output")) {
         this.outputField.setSelectedItem(var2);
         return true;
      } else if (var1.equals("-target")) {
         this.targetField.setSelectedItem(this.owner.getHostById(var2));
         return true;
      } else if (var1.equals("-user")) {
         this.userField.setText(var2);
         return true;
      } else if (var1.equals("-working")) {
         this.workingField.setText(var2);
         return true;
      } else {
         return false;
      }
   }

   public void addAllSystems(List<SystemType> var1) {
      this.systems = var1;
   }

   public void addAllHosts(List<HostInfo> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         HostInfo var3 = (HostInfo)var2.next();
         this.addNewHost(var3);
      }

   }

   public void addNewHost(HostInfo var1) {
      if (this.uniqueHost.evaluate(var1.getId())) {
         this.model.addElement(var1);
         Object var2 = this.model.getSelectedItem();
         if (var2 instanceof HostInfo) {
            if (((HostInfo)HostInfo.class.cast(var2)).isLocal() && !var1.isLocal()) {
               this.model.setSelectedItem(var1);
            }
         } else if (var2 == null) {
            this.model.setSelectedItem(var1);
         }
      }

   }

   public JComponent getDefaultElement() {
      return this.startButton;
   }

   public void configureMaps(CoreController var1) {
      Component[] var2 = this.disable;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var2[var4];
         if (var5 instanceof JComponent) {
            var1.setupKeyBindings((JComponent)var5);
         }
      }

   }

   public void disconnected() {
      Component[] var1 = this.disable;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Component var4 = var1[var3];
         var4.setEnabled(false);
      }

   }
}
