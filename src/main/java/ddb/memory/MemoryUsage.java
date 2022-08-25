package ddb.memory;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

public class MemoryUsage extends JLayeredPane {
   MemoryGraph graph = new MemoryGraph();
   MemoryText text = new MemoryText();
   Calendar last = null;
   MemoryDisplay[] displays;
   boolean stop = false;

   public MemoryUsage() {
      this.displays = new MemoryDisplay[]{this.graph, this.text};
      this.setLayer(this.graph, JLayeredPane.DEFAULT_LAYER);
      this.setLayer(this.text, JLayeredPane.DEFAULT_LAYER);
      this.moveToBack(this.graph);
      this.add(this.graph);
      this.add(this.text);
   }

   public void start() {
      Thread var1 = new Thread(new Runnable() {
         public void run() {
            while(!MemoryUsage.this.stop) {
               Calendar var1 = Calendar.getInstance();
               long var2 = Runtime.getRuntime().totalMemory();
               long var4 = Runtime.getRuntime().freeMemory();
               long var6;
               if (MemoryUsage.this.last == null) {
                  var6 = 0L;
                  MemoryUsage.this.last = var1;
               } else {
                  var6 = var1.getTimeInMillis() - MemoryUsage.this.last.getTimeInMillis();
                  MemoryUsage.this.last = var1;
               }

               MemoryDisplay[] var8 = MemoryUsage.this.displays;
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  MemoryDisplay var11 = var8[var10];
                  var11.setMemory(var4, var2, var6);
               }

               try {
                  TimeUnit.SECONDS.sleep(1L);
               } catch (Exception var12) {
               }
            }

         }
      });
      var1.setDaemon(true);
      var1.setName("Memory query");
      var1.start();
   }

   public void stop() {
      this.stop = true;
   }

   public void resume() {
      this.stop = false;
      this.start();
   }

   public static void main() {
      MemoryUsage var0 = new MemoryUsage();
      JFrame var1 = new JFrame("Memory");
      var1.setDefaultCloseOperation(3);
      var1.setLayout(new BorderLayout());
      var1.add(var0);
      var1.pack();
      var1.setVisible(true);
      var0.start();
   }
}
