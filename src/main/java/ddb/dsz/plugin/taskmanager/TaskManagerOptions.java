package ddb.dsz.plugin.taskmanager;

import ddb.detach.TabbableOption;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class TaskManagerOptions extends TabbableOption {
   JPanel panel;
   TaskManagerOptions.DelegateObservable observe = new TaskManagerOptions.DelegateObservable();
   JButton safe = new JButton("Safe");
   JButton secProd = new JButton("Security Products");
   JButton coreOs = new JButton("Core OS");
   JButton malice = new JButton("Malicious");
   JButton unknown = new JButton("Unknown");
   public static final int SATURATION = 192;
   public Color safeColor = new Color(0, 192, 0);
   public Color secProdColor = new Color(192, 0, 0);
   public Color coreOsColor = new Color(0, 192, 0);
   public Color maliceColor = new Color(192, 0, 0);
   public Color unknownColor;
   CoreController core;

   @Override
   public JComponent getDefaultElement() {
      return null;
   }

   public TaskManagerOptions() {
      super(TaskManager2.class);
      this.unknownColor = Color.BLACK;
      this.core = null;
      super.setName("Processes");
      this.panel = new JPanel();
      this.panel.setBorder(BorderFactory.createTitledBorder("Process Coloration"));
   }

   private void configure(JButton button, Color foreground, ActionListener al) {
      this.panel.add(button);
      button.setForeground(foreground);
      button.addActionListener(al);
   }

   public void init(CoreController cc) {
      synchronized(this) {
         if (this.core != null) {
            return;
         }

         this.core = cc;
      }

      this.coreOsColor = (Color)this.core.getOption(TaskManager2.class, FileStatus.CORE_OS.toString(), this.coreOsColor);
      this.secProdColor = (Color)this.core.getOption(TaskManager2.class, FileStatus.SECURITY_PRODUCT.toString(), this.secProdColor);
      this.safeColor = (Color)this.core.getOption(TaskManager2.class, FileStatus.SAFE.toString(), this.safeColor);
      this.maliceColor = (Color)this.core.getOption(TaskManager2.class, FileStatus.MALICIOUS_SOFTWARE.toString(), this.maliceColor);
      this.unknownColor = (Color)this.core.getOption(TaskManager2.class, FileStatus.NONE.toString(), this.unknownColor);
      this.configure(this.safe, this.safeColor, new TaskManagerOptions.ColorChanger(FileStatus.SAFE));
      this.configure(this.secProd, this.secProdColor, new TaskManagerOptions.ColorChanger(FileStatus.SECURITY_PRODUCT));
      this.configure(this.coreOs, this.coreOsColor, new TaskManagerOptions.ColorChanger(FileStatus.CORE_OS));
      this.configure(this.malice, this.maliceColor, new TaskManagerOptions.ColorChanger(FileStatus.MALICIOUS_SOFTWARE));
      this.configure(this.unknown, this.unknownColor, new TaskManagerOptions.ColorChanger(FileStatus.NONE));
   }

   public JComponent initialFocus() {
      return null;
   }

   @Override
   public JComponent getDisplay() {
      return this.panel;
   }

   public Observable getObservable() {
      return this.observe;
   }

   void commit() {
      this.core.setOption(TaskManager2.class, FileStatus.SAFE.toString(), this.safeColor);
      this.core.setOption(TaskManager2.class, FileStatus.CORE_OS.toString(), this.coreOsColor);
      this.core.setOption(TaskManager2.class, FileStatus.MALICIOUS_SOFTWARE.toString(), this.maliceColor);
      this.core.setOption(TaskManager2.class, FileStatus.SECURITY_PRODUCT.toString(), this.secProdColor);
      this.core.setOption(TaskManager2.class, FileStatus.NONE.toString(), this.unknownColor);
   }

   private class DelegateObservable extends Observable {
      private DelegateObservable() {
      }

      public void fire() {
         super.setChanged();
         this.notifyObservers();
      }

      // $FF: synthetic method
      DelegateObservable(Object x1) {
         this();
      }
   }

   class ColorChanger implements ActionListener {
      FileStatus type;

      ColorChanger(FileStatus type) {
         this.type = type;
      }

      public void actionPerformed(ActionEvent e) {
         String text = "";
         Color color = Color.BLACK;
         switch(this.type) {
         case SAFE:
            text = TaskManagerOptions.this.safe.getText();
            color = TaskManagerOptions.this.safeColor;
            break;
         case SECURITY_PRODUCT:
            text = TaskManagerOptions.this.secProd.getText();
            color = TaskManagerOptions.this.secProdColor;
            break;
         case CORE_OS:
            text = TaskManagerOptions.this.coreOs.getText();
            color = TaskManagerOptions.this.coreOsColor;
            break;
         case MALICIOUS_SOFTWARE:
            text = TaskManagerOptions.this.malice.getText();
            color = TaskManagerOptions.this.maliceColor;
            break;
         case NONE:
            text = TaskManagerOptions.this.unknown.getText();
            color = TaskManagerOptions.this.unknownColor;
         }

         Color newColor = JColorChooser.showDialog(TaskManagerOptions.this.panel, "Color for " + text, color);
         if (newColor != null) {
            switch(this.type) {
            case SAFE:
               TaskManagerOptions.this.safeColor = newColor;
               TaskManagerOptions.this.safe.setForeground(newColor);
               break;
            case SECURITY_PRODUCT:
               TaskManagerOptions.this.secProdColor = newColor;
               TaskManagerOptions.this.secProd.setForeground(newColor);
               break;
            case CORE_OS:
               TaskManagerOptions.this.coreOsColor = newColor;
               TaskManagerOptions.this.coreOs.setForeground(newColor);
               break;
            case MALICIOUS_SOFTWARE:
               TaskManagerOptions.this.maliceColor = newColor;
               TaskManagerOptions.this.malice.setForeground(newColor);
               break;
            case NONE:
               TaskManagerOptions.this.unknownColor = newColor;
               TaskManagerOptions.this.unknown.setForeground(newColor);
            }

            TaskManagerOptions.this.observe.fire();
         }

         TaskManagerOptions.this.commit();
      }
   }
}
