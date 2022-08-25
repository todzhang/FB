package ddb.memory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;

public class MemoryText extends JPanel implements MemoryDisplay {
   private static String[] suffix = new String[]{"B", "KB", "MB", "GB"};
   private static long magnitude = 1024L;
   private JLabel jLabel1;

   public void setMemory(long var1, long var3, long var5) {
      int var7;
      for(var7 = 0; var7 < suffix.length && var3 > magnitude; ++var7) {
         var1 /= magnitude;
         var3 /= magnitude;
      }

      this.jLabel1.setText(String.format("%d/%d %s", var1, var3, suffix[var7]));
   }

   public MemoryText() {
      this.initComponents();
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jLabel1.setHorizontalAlignment(0);
      this.jLabel1.setText("217.8 / 480.2MB");
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(this.jLabel1, -1, -1, 32767).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(this.jLabel1, -1, 14, 32767).addContainerGap()));
   }
}
