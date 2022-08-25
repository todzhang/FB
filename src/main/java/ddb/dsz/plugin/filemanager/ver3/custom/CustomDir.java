package ddb.dsz.plugin.filemanager.ver3.custom;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

public class CustomDir extends JDialog {
   CoreController core;
   List<String> parameters;
   private JSpinner AfterField;
   private JTextField AgeField;
   private JSpinner BeforeField;
   private JButton cancel;
   private JSpinner chunkSize;
   private JCheckBox getDirsOnly;
   private JCheckBox getHashes;
   private JButton go;
   private JCheckBox hashMd5;
   private JCheckBox hashSha1;
   private JCheckBox hashSha256;
   private JCheckBox hashSha512;
   private JCheckBox maximum;
   private JSpinner maximumField;
   private JRadioButton noTimeAtAll;
   private JCheckBox recursive;
   private JCheckBox setChunkSize;
   private JRadioButton timeByAccessed;
   private JCheckBox timeByAfter;
   private JCheckBox timeByAge;
   private JCheckBox timeByBefore;
   private JRadioButton timeByCreated;
   private JRadioButton timeByModified;
   private ButtonGroup timeFilter;
   private BindingGroup bindingGroup;

   public CustomDir(Frame var1, boolean var2, CoreController var3, List<String> var4) {
      super(var1, var2);
      this.core = var3;
      this.parameters = var4;
      this.initComponents();
   }

   private void initComponents() {
      this.bindingGroup = new BindingGroup();
      this.timeFilter = new ButtonGroup();
      this.noTimeAtAll = new JRadioButton();
      this.timeByAccessed = new JRadioButton();
      this.timeByCreated = new JRadioButton();
      this.timeByModified = new JRadioButton();
      this.timeByAge = new JCheckBox();
      this.AgeField = new JTextField();
      this.timeByBefore = new JCheckBox();
      this.timeByAfter = new JCheckBox();
      this.AfterField = new JSpinner();
      this.BeforeField = new JSpinner();
      this.getHashes = new JCheckBox();
      this.hashMd5 = new JCheckBox();
      this.hashSha1 = new JCheckBox();
      this.hashSha256 = new JCheckBox();
      this.hashSha512 = new JCheckBox();
      this.getDirsOnly = new JCheckBox();
      this.setChunkSize = new JCheckBox();
      this.chunkSize = new JSpinner();
      this.cancel = new JButton();
      this.go = new JButton();
      this.maximum = new JCheckBox();
      this.maximumField = new JSpinner();
      this.recursive = new JCheckBox();
      this.setDefaultCloseOperation(2);
      this.setTitle("Specify Dir Parameters");
      this.setModal(true);
      this.timeFilter.add(this.noTimeAtAll);
      this.noTimeAtAll.setSelected(true);
      this.noTimeAtAll.setText("No Time Filter");
      this.timeFilter.add(this.timeByAccessed);
      this.timeByAccessed.setText("Accessed");
      this.timeFilter.add(this.timeByCreated);
      this.timeByCreated.setText("Created");
      this.timeFilter.add(this.timeByModified);
      this.timeByModified.setText("Modified");
      this.timeByAge.setText("Age:");
      this.timeByAge.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomDir.this.timeByAgeStateChanged(var1);
         }
      });
      this.AgeField.setText("3h");
      AutoBinding var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByAge, ELProperty.create("${selected}"), this.AgeField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.timeByBefore.setText("Before:");
      this.timeByBefore.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomDir.this.timeByBeforeStateChanged(var1);
         }
      });
      this.timeByAfter.setText("After:");
      this.timeByAfter.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomDir.this.timeByAfterStateChanged(var1);
         }
      });
      this.AfterField.setModel(new SpinnerDateModel());
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByAfter, ELProperty.create("${selected}"), this.AfterField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.BeforeField.setModel(new SpinnerDateModel());
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByBefore, ELProperty.create("${selected}"), this.BeforeField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.getHashes.setText("Hash");
      this.hashMd5.setSelected(true);
      this.hashMd5.setText("MD5");
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.getHashes, ELProperty.create("${selected}"), this.hashMd5, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.hashSha1.setSelected(true);
      this.hashSha1.setText("SHA1");
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.getHashes, ELProperty.create("${selected}"), this.hashSha1, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.hashSha256.setSelected(true);
      this.hashSha256.setText("SHA256");
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.getHashes, ELProperty.create("${selected}"), this.hashSha256, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.hashSha512.setSelected(true);
      this.hashSha512.setText("SHA512");
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.getHashes, ELProperty.create("${selected}"), this.hashSha512, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.getDirsOnly.setText("Dirs Only");
      this.setChunkSize.setText("Chunk Size:");
      this.chunkSize.setModel(new SpinnerNumberModel(16384, 0, 1000000, 1024));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.setChunkSize, ELProperty.create("${selected}"), this.chunkSize, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.cancel.setText("Cancel");
      this.cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            CustomDir.this.cancelActionPerformed(var1);
         }
      });
      this.go.setText("Go");
      this.go.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            CustomDir.this.goActionPerformed(var1);
         }
      });
      this.maximum.setText("Maximum:");
      this.maximumField.setModel(new SpinnerNumberModel(25, 0, 1000000, 1));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.maximum, ELProperty.create("${selected}"), this.maximumField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.recursive.setText("Recursive");
      GroupLayout var2 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.setChunkSize).addComponent(this.maximum)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.chunkSize, -2, -1, -2).addComponent(this.maximumField, -2, -1, -2)).addContainerGap()).addGroup(var2.createSequentialGroup().addGap(27, 27, 27).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.timeByBefore).addComponent(this.timeByAge).addComponent(this.timeByAfter)).addGap(18, 18, 18).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.BeforeField, -2, -1, -2).addComponent(this.AfterField, -2, -1, -2).addComponent(this.AgeField, -1, 284, 32767)).addContainerGap(10, 32767)).addGroup(var2.createSequentialGroup().addGap(6, 6, 6).addComponent(this.noTimeAtAll).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByAccessed).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByCreated).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByModified).addContainerGap(102, 32767)).addGroup(var2.createSequentialGroup().addGap(6, 6, 6).addComponent(this.getHashes).addContainerGap(345, 32767)).addGroup(var2.createSequentialGroup().addGap(27, 27, 27).addComponent(this.hashMd5).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.hashSha1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.hashSha256).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.hashSha512).addContainerGap(149, 32767)).addGroup(var2.createSequentialGroup().addGap(6, 6, 6).addComponent(this.getDirsOnly).addContainerGap(325, 32767)).addGroup(Alignment.TRAILING, var2.createSequentialGroup().addContainerGap(274, 32767).addComponent(this.go).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.cancel).addContainerGap()).addGroup(var2.createSequentialGroup().addContainerGap().addComponent(this.recursive).addContainerGap(321, 32767)));
      var2.setVerticalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.noTimeAtAll).addComponent(this.timeByAccessed).addComponent(this.timeByCreated).addComponent(this.timeByModified)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByAge).addComponent(this.AgeField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByBefore).addComponent(this.BeforeField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByAfter).addComponent(this.AfterField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.getHashes).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.hashMd5).addComponent(this.hashSha1).addComponent(this.hashSha256).addComponent(this.hashSha512)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.getDirsOnly).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.setChunkSize).addComponent(this.chunkSize, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.maximum).addComponent(this.maximumField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.recursive).addPreferredGap(ComponentPlacement.RELATED, 22, 32767).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.cancel).addComponent(this.go)).addContainerGap()));
      this.bindingGroup.bind();
      this.pack();
   }

   private void timeByAgeStateChanged(ChangeEvent var1) {
      if (this.timeByAge.isSelected()) {
         this.timeByBefore.setSelected(false);
         this.timeByAfter.setSelected(false);
      }

   }

   private void timeByBeforeStateChanged(ChangeEvent var1) {
      if (this.timeByBefore.isSelected()) {
         this.timeByAge.setSelected(false);
      }

   }

   private void timeByAfterStateChanged(ChangeEvent var1) {
      if (this.timeByAfter.isSelected()) {
         this.timeByAge.setSelected(false);
      }

   }

   private void goActionPerformed(ActionEvent var1) {
      StringBuilder var2 = new StringBuilder();
      if (this.timeByAccessed.isSelected()) {
         this.parameters.add("time=accessed");
      } else if (this.timeByCreated.isSelected()) {
         this.parameters.add("time=created");
      } else if (this.timeByModified.isSelected()) {
         this.parameters.add("time=modified");
      }

      if (this.timeByAge.isSelected()) {
         this.parameters.add("age=" + this.AgeField.getText());
      }

      SimpleDateFormat var3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      if (this.timeByAfter.isSelected()) {
         this.parameters.add("after=" + var3.format(this.AfterField.getValue()));
      }

      if (this.timeByBefore.isSelected()) {
         this.parameters.add("before=" + var3.format(this.AfterField.getValue()));
      }

      if (this.maximum.isSelected()) {
         this.parameters.add("max=" + this.maximumField.getValue());
      }

      if (this.recursive.isSelected()) {
         this.parameters.add("recursive=true");
      }

      if (this.getHashes.isSelected() && (this.hashMd5.isSelected() || this.hashSha1.isSelected() || this.hashSha256.isSelected() || this.hashSha512.isSelected())) {
         var2.append(" -hash ");
         if (this.hashMd5.isSelected()) {
            var2.append(" md5 ");
         }

         if (this.hashSha1.isSelected()) {
            var2.append(" sha1 ");
         }

         if (this.hashSha256.isSelected()) {
            var2.append(" sha256 ");
         }

         if (this.hashSha512.isSelected()) {
            var2.append(" sha512 ");
         }
      }

      if (this.getDirsOnly.isSelected()) {
         var2.append(" -dirsonly ");
      }

      if (this.setChunkSize.isSelected()) {
         var2.append(" -chunksize " + this.chunkSize.getValue());
      }

      this.parameters.add("extra=" + var2.toString());
      this.core.internalCommand((InternalCommandCallback)null, this.parameters);
      this.cancelActionPerformed(var1);
   }

   private void cancelActionPerformed(ActionEvent var1) {
      this.setVisible(false);
      this.dispose();
   }
}
