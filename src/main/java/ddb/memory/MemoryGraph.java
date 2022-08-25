package ddb.memory;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

public class MemoryGraph extends JPanel implements MemoryDisplay {
   int index = 0;
   int size = 0;
   Object lock = new Object();
   double[] values = new double[100];
   JPanel[] panels;

   public void setMemory(long var1, long var3, long var5) {
      double var7 = (double)var1;
      double var9 = (double)var3;
      double var11 = var7 / var9;
      this.values[this.index++] = var11;
      if (this.size < this.values.length) {
         ++this.size;
      }

      if (this.index >= this.values.length) {
         this.index = 0;
      }

      for(int var13 = 0; var13 < this.values.length; ++var13) {
         JPanel var14 = this.panels[var13];
         int var15 = this.getSize().height;
         int var16 = (int)((double)var15 * this.values[(var13 + this.index) % this.values.length]);
         var14.setSize(1, var16);
      }

      this.validate();
   }

   public MemoryGraph() {
      this.panels = new JPanel[this.values.length];
      GridBagLayout var1 = new GridBagLayout();
      GridBagConstraints var2 = new GridBagConstraints();
      this.initComponents();
      this.setLayout(var1);
      var2.anchor = 15;
      var2.fill = 2;
      var2.ipadx = 0;
      var2.ipady = 0;
      var2.weightx = 0.0D;
      var2.gridy = 0;

      for(int var3 = 0; var3 < this.panels.length; ++var3) {
         var2.gridx = var3;
         JPanel var4 = new JPanel();
         this.panels[var3] = var4;
         var4.setBackground(Color.GREEN);
         var4.setSize(1, 0);
         this.add(var4);
         var1.addLayoutComponent(var4, var2);
      }

   }

   private void initComponents() {
      this.setBackground(new Color(204, 204, 255));
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(0, 400, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(0, 300, 32767));
   }
}
