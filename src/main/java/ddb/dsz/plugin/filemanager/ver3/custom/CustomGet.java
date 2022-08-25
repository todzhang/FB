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
import javax.swing.JLabel;
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

public class CustomGet extends JDialog {
   CoreController core;
   List<String> parameters;
   private JSpinner AfterField;
   private JTextField AgeField;
   private JSpinner BeforeField;
   private ButtonGroup actionLimit;
   private JButton cancel;
   private JSpinner chunkSize;
   private JRadioButton fileRange;
   private JRadioButton fileTail;
   private JRadioButton fileWhole;
   private JButton go;
   private JLabel jLabel1;
   private JCheckBox maximum;
   private JSpinner maximumField;
   private JSpinner maximumFileSize;
   private JSpinner minimumFileSize;
   private JRadioButton noTimeAtAll;
   private JSpinner rangeEnd;
   private JSpinner rangeStart;
   private JCheckBox recursive;
   private JCheckBox setChunkSize;
   private JCheckBox setMaximumFileSize;
   private JCheckBox setMinimumFileSize;
   private JSpinner tail;
   private JRadioButton timeByAccessed;
   private JCheckBox timeByAfter;
   private JCheckBox timeByAge;
   private JCheckBox timeByBefore;
   private JRadioButton timeByCreated;
   private JRadioButton timeByModified;
   private ButtonGroup timeLimit;
   private BindingGroup bindingGroup;

   public CustomGet(Frame var1, boolean var2, CoreController var3, List<String> var4) {
      super(var1, var2);
      this.core = var3;
      this.parameters = var4;
      this.initComponents();
   }

   private void initComponents() {
      this.bindingGroup = new BindingGroup();
      this.timeLimit = new ButtonGroup();
      this.actionLimit = new ButtonGroup();
      this.BeforeField = new JSpinner();
      this.AfterField = new JSpinner();
      this.recursive = new JCheckBox();
      this.timeByAfter = new JCheckBox();
      this.setChunkSize = new JCheckBox();
      this.AgeField = new JTextField();
      this.chunkSize = new JSpinner();
      this.timeByBefore = new JCheckBox();
      this.cancel = new JButton();
      this.go = new JButton();
      this.timeByAge = new JCheckBox();
      this.timeByModified = new JRadioButton();
      this.noTimeAtAll = new JRadioButton();
      this.maximumField = new JSpinner();
      this.maximum = new JCheckBox();
      this.timeByCreated = new JRadioButton();
      this.timeByAccessed = new JRadioButton();
      this.setMinimumFileSize = new JCheckBox();
      this.minimumFileSize = new JSpinner();
      this.setMaximumFileSize = new JCheckBox();
      this.maximumFileSize = new JSpinner();
      this.fileWhole = new JRadioButton();
      this.fileRange = new JRadioButton();
      this.rangeStart = new JSpinner();
      this.jLabel1 = new JLabel();
      this.rangeEnd = new JSpinner();
      this.fileTail = new JRadioButton();
      this.tail = new JSpinner();
      this.setDefaultCloseOperation(2);
      this.setTitle("Specify Get Parameters");
      this.BeforeField.setModel(new SpinnerDateModel());
      AutoBinding var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByBefore, ELProperty.create("${selected}"), this.BeforeField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.AfterField.setModel(new SpinnerDateModel());
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByAfter, ELProperty.create("${selected}"), this.AfterField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.recursive.setText("Recursive");
      this.timeByAfter.setText("After:");
      this.timeByAfter.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomGet.this.timeByAfterStateChanged(var1);
         }
      });
      this.setChunkSize.setText("Chunk Size:");
      this.AgeField.setText("3h");
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.timeByAge, ELProperty.create("${selected}"), this.AgeField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.chunkSize.setModel(new SpinnerNumberModel(16384, 1024, 1000000, 1024));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.setChunkSize, ELProperty.create("${selected}"), this.chunkSize, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.timeByBefore.setText("Before:");
      this.timeByBefore.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomGet.this.timeByBeforeStateChanged(var1);
         }
      });
      this.cancel.setText("Cancel");
      this.cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            CustomGet.this.cancelActionPerformed(var1);
         }
      });
      this.go.setText("Go");
      this.go.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            CustomGet.this.goActionPerformed(var1);
         }
      });
      this.timeByAge.setText("Age:");
      this.timeByAge.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            CustomGet.this.timeByAgeStateChanged(var1);
         }
      });
      this.timeLimit.add(this.timeByModified);
      this.timeByModified.setText("Modified");
      this.timeLimit.add(this.noTimeAtAll);
      this.noTimeAtAll.setSelected(true);
      this.noTimeAtAll.setText("No Time Filter");
      this.maximumField.setModel(new SpinnerNumberModel(25, 0, 1000000, 5));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.maximum, ELProperty.create("${selected}"), this.maximumField, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.maximum.setText("Maximum:");
      this.timeLimit.add(this.timeByCreated);
      this.timeByCreated.setText("Created");
      this.timeLimit.add(this.timeByAccessed);
      this.timeByAccessed.setText("Accessed");
      this.setMinimumFileSize.setText("Minimum File Size:");
      this.minimumFileSize.setModel(new SpinnerNumberModel(1048576, 0, (Comparable)null, 16384));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.setMinimumFileSize, ELProperty.create("${selected}"), this.minimumFileSize, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.setMaximumFileSize.setText("Maximum File Size:");
      this.setMaximumFileSize.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            CustomGet.this.setMaximumFileSizeActionPerformed(var1);
         }
      });
      this.maximumFileSize.setModel(new SpinnerNumberModel(1048576, 0, (Comparable)null, 16384));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.setMaximumFileSize, ELProperty.create("${selected}"), this.maximumFileSize, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.actionLimit.add(this.fileWhole);
      this.fileWhole.setSelected(true);
      this.fileWhole.setText("Whole File");
      this.actionLimit.add(this.fileRange);
      this.fileRange.setText("Range of the file");
      this.rangeStart.setModel(new SpinnerNumberModel(0, 0, (Comparable)null, 1024));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.fileRange, ELProperty.create("${selected}"), this.rangeStart, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.jLabel1.setText("to");
      this.rangeEnd.setModel(new SpinnerNumberModel(0, 0, (Comparable)null, 1024));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.fileRange, ELProperty.create("${selected}"), this.rangeEnd, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.actionLimit.add(this.fileTail);
      this.fileTail.setText("Tail of the file");
      this.tail.setModel(new SpinnerNumberModel(0, 0, (Comparable)null, 1024));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.fileTail, ELProperty.create("${selected}"), this.tail, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      GroupLayout var2 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addGap(6, 6, 6).addComponent(this.noTimeAtAll).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByAccessed).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByCreated).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.timeByModified).addContainerGap(102, 32767)).addGroup(var2.createSequentialGroup().addGap(27, 27, 27).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.timeByBefore).addComponent(this.timeByAge).addComponent(this.timeByAfter)).addGap(18, 18, 18).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.BeforeField, -2, -1, -2).addComponent(this.AfterField, -2, -1, -2).addComponent(this.AgeField, -1, 284, 32767)).addContainerGap(10, 32767)).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.setMinimumFileSize).addComponent(this.setMaximumFileSize)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.maximumFileSize, -2, 100, -2).addComponent(this.minimumFileSize, -2, 100, -2))).addComponent(this.recursive).addGroup(var2.createSequentialGroup().addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.setChunkSize).addComponent(this.maximum)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.LEADING).addComponent(this.chunkSize, -2, -1, -2).addComponent(this.maximumField, -2, -1, -2)))).addContainerGap(177, 32767)).addGroup(var2.createSequentialGroup().addContainerGap().addComponent(this.fileWhole).addContainerGap(319, 32767)).addGroup(Alignment.TRAILING, var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, var2.createSequentialGroup().addGap(21, 21, 21).addComponent(this.rangeStart, -2, 129, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.rangeEnd, -1, 119, 32767)).addComponent(this.fileRange, Alignment.LEADING)).addGap(107, 107, 107)).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addGap(21, 21, 21).addComponent(this.tail, -2, 119, -2)).addComponent(this.fileTail)).addContainerGap(254, 32767)).addGroup(Alignment.TRAILING, var2.createSequentialGroup().addContainerGap(274, 32767).addComponent(this.go).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.cancel).addContainerGap()));
      var2.setVerticalGroup(var2.createParallelGroup(Alignment.LEADING).addGroup(var2.createSequentialGroup().addContainerGap().addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.noTimeAtAll).addComponent(this.timeByAccessed).addComponent(this.timeByCreated).addComponent(this.timeByModified)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByAge).addComponent(this.AgeField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByBefore).addComponent(this.BeforeField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.timeByAfter).addComponent(this.AfterField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.setChunkSize).addComponent(this.chunkSize, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.maximum).addComponent(this.maximumField, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.recursive).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.setMinimumFileSize).addComponent(this.minimumFileSize, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.setMaximumFileSize).addComponent(this.maximumFileSize, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.fileWhole).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.fileRange).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.rangeStart, -2, -1, -2).addComponent(this.jLabel1).addComponent(this.rangeEnd, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.fileTail).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.tail, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(var2.createParallelGroup(Alignment.BASELINE).addComponent(this.cancel).addComponent(this.go)).addContainerGap(-1, 32767)));
      this.bindingGroup.bind();
      this.pack();
   }

   private void timeByAfterStateChanged(ChangeEvent var1) {
      if (this.timeByAfter.isSelected()) {
         this.timeByAge.setSelected(false);
      }

   }

   private void timeByBeforeStateChanged(ChangeEvent var1) {
      if (this.timeByBefore.isSelected()) {
         this.timeByAge.setSelected(false);
      }

   }

   private void cancelActionPerformed(ActionEvent var1) {
      this.setVisible(false);
      this.dispose();
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

      if (this.setChunkSize.isSelected()) {
         var2.append(" -chunksize " + this.chunkSize.getValue());
      }

      if (this.setMinimumFileSize.isSelected()) {
         var2.append(" -minsize " + this.minimumFileSize.getValue());
      }

      if (this.setMaximumFileSize.isSelected()) {
         var2.append(" -maxsize " + this.maximumFileSize.getValue());
      }

      if (this.fileRange.isSelected()) {
         Long var4 = Long.parseLong(this.rangeStart.getValue().toString());
         Long var5 = Long.parseLong(this.rangeEnd.getValue().toString());
         if (var4.compareTo(var5) < 0) {
            var2.append(" -range " + var4 + " " + var5);
         } else {
            var2.append(" -range " + var4);
         }
      }

      if (this.fileTail.isSelected()) {
         var2.append(" -tail " + this.tail.getValue());
      }

      this.parameters.add("extra=" + var2.toString());
      this.core.internalCommand((InternalCommandCallback)null, this.parameters);
      this.cancelActionPerformed(var1);
   }

   private void timeByAgeStateChanged(ChangeEvent var1) {
      if (this.timeByAge.isSelected()) {
         this.timeByBefore.setSelected(false);
         this.timeByAfter.setSelected(false);
      }

   }

   private void setMaximumFileSizeActionPerformed(ActionEvent var1) {
   }
}
