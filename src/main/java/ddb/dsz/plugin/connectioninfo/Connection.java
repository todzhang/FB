package ddb.dsz.plugin.connectioninfo;

import ddb.dsz.core.host.HostInfo;
import ddb.util.Pair;
import java.awt.EventQueue;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import org.jdesktop.layout.GroupLayout;

public class Connection extends JPanel {
   static final BigDecimal kilo = BigDecimal.valueOf(1024L);
   static final DecimalFormat decForm = new DecimalFormat();
   static final List<Pair<String, BigDecimal>> magnitude;
   static final int signCharacters = 4;
   static final int DEFAULT_MAXIMUM = 16384;
   static DefaultFormatterFactory total;
   HostInfo hostInfo;
   long delta = 0L;
   Calendar local = null;
   Calendar gmt = null;
   Calendar connected = Calendar.getInstance();
   long timeZoneDelta;
   boolean isConnected = true;
   private final Object CONN_LOCK = new Object();
   BigInteger lastSent;
   BigInteger lastReceived;
   private JLabel durationLabel;
   private JLabel durationValue;
   private JLabel hostValue;
   private JFormattedTextField receivedField;
   private JLabel receivedLabel;
   private JFormattedTextField sentField;
   private JLabel sentLabel;
   private JLabel timeValue;

   static String simplifyNumber(Long var0) {
      return simplifyNumber(BigInteger.valueOf(var0));
   }

   static String simplifyNumber(BigInteger var0) {
      boolean var1 = false;
      BigDecimal var2 = new BigDecimal(var0);

      int var4;
      for(var4 = 1; var4 < magnitude.size() && var2.compareTo((BigDecimal)((Pair)magnitude.get(var4)).getSecond()) >= 0; ++var4) {
      }

      --var4;
      if (var4 == 0) {
         return String.format("%d %s", var0.longValue(), ((Pair)magnitude.get(var4)).getFirst());
      } else {
         String var3 = var2.divide((BigDecimal)((Pair)magnitude.get(var4)).getSecond()).toPlainString();
         if (var3.length() > 4) {
            var3 = var3.substring(0, 4);
         }

         return String.format("%s %s", var3, ((Pair)magnitude.get(var4)).getFirst());
      }
   }

   public Connection(HostInfo var1) {
      this.lastSent = BigInteger.ZERO;
      this.lastReceived = BigInteger.ZERO;
      this.hostInfo = var1;
      this.initComponents();
   }

   public void setTime(long var1, long var3, Calendar var5, Calendar var6) {
      this.local = var5;
      this.gmt = var6;
      this.delta = var1;
      this.timeZoneDelta = var3;
   }

   public HostInfo getHost() {
      return this.hostInfo;
   }

   public void disconnected() {
      synchronized(this.CONN_LOCK) {
         this.isConnected = false;
      }

      Calendar var1 = Calendar.getInstance();
      this.durationValue.setText(String.format("<html><font color=\"red\">%s", this.millisToString(var1.getTimeInMillis() - this.connected.getTimeInMillis())));
      this.timeValue.setText(String.format("<html><font color=\"red\">%04d/%02d/%02d<br>%02d:%02d:%02d", var1.get(1), var1.get(2) + 1, var1.get(5), var1.get(11), var1.get(12), var1.get(13)));
   }

   public void update(Calendar var1, ConnectionInfo.DisplayType var2) {
      synchronized(this.CONN_LOCK) {
         if (this.isConnected) {
            long var4 = this.delta;
            Calendar var6 = null;
            switch(var2) {
            case Local:
               var6 = this.local;
               break;
            case Cycle:
            case Gmt:
               var6 = this.gmt;
            }

            if (var6 != null) {
               final Calendar var7 = Calendar.getInstance();
               var7.setTimeInMillis(var6.getTimeInMillis());
               final Calendar var8 = Calendar.getInstance();
               var7.add(14, (int)(var8.getTimeInMillis() - this.connected.getTimeInMillis()));
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     Connection.this.timeValue.setText(String.format("<html>%04d/%02d/%02d<br>%02d:%02d:%02d", var7.get(1), var7.get(2) + 1, var7.get(5), var7.get(11), var7.get(12), var7.get(13)));
                     Connection.this.durationValue.setText(Connection.this.millisToString(var8.getTimeInMillis() - Connection.this.connected.getTimeInMillis()));
                  }
               });
            }
         }
      }
   }

   private String millisToString(long var1) {
      long var9 = 0L;
      long var7 = 0L;
      long var5 = 0L;
      long var3 = 0L;
      var1 /= 1000L;
      var3 = var1 % 60L;
      var1 /= 60L;
      var5 = var1 % 60L;
      var1 /= 60L;
      var7 = var1 % 24L;
      var1 /= 24L;
      String var11;
      if (var1 > 0L) {
         var11 = "%1$dd %2$02d:%3$02d:%4$02d";
      } else if (var7 >= 0L) {
         var11 = "%2$02d:%3$02d:%4$02d";
      } else {
         var11 = "%3$02d:%4$02d";
      }

      return String.format(var11, var1, var7, var5, var3);
   }

   public void updateStatistics(long var1, BigInteger var3, BigInteger var4) {
      EventQueue.invokeLater(new Connection.UpdateStatistics(var1, var3, var4));
   }

   private void updateProgress(JProgressBar var1, BigInteger var2, BigInteger var3, long var4) {
      if (var1 != null && var2 != null && var3 != null && var4 != 0L) {
         try {
            BigDecimal var6 = BigDecimal.valueOf(var2.subtract(var3).longValue());
            var6 = var6.divide(BigDecimal.valueOf(var4), RoundingMode.HALF_UP);
            var6 = var6.multiply(BigDecimal.valueOf(1000L));
            int var7 = var6.intValue();
            if (var7 > var1.getMaximum()) {
               var1.setValue(var1.getMaximum());
            } else {
               var1.setValue(var7);
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

      }
   }

   public void setThrottle(int var1) {
      if (var1 <= 0) {
         boolean var2 = true;
      }

   }

   private void initComponents() {
      this.timeValue = new JLabel();
      this.durationLabel = new JLabel();
      this.durationValue = new JLabel();
      this.sentLabel = new JLabel();
      this.receivedLabel = new JLabel();
      this.sentField = new JFormattedTextField();
      this.receivedField = new JFormattedTextField();
      this.hostValue = new JLabel();
      this.setBorder(BorderFactory.createTitledBorder(String.format("%s:  [%s]%s %s", this.hostInfo.getId(), this.hostInfo.getArch(), this.hostInfo.getPlatform(), this.hostInfo.getVersion())));
      this.timeValue.setFont(new Font("Tahoma", 0, 18));
      this.timeValue.setHorizontalAlignment(4);
      this.timeValue.setText("<html>&nbsp;<br>&nbsp;</html>");
      this.durationLabel.setText("Duration:");
      this.durationValue.setFont(new Font("Tahoma", 0, 14));
      this.durationValue.setHorizontalAlignment(4);
      this.durationValue.setText("   ");
      this.sentLabel.setHorizontalAlignment(0);
      this.sentLabel.setText("Sent:");
      this.receivedLabel.setHorizontalAlignment(0);
      this.receivedLabel.setText("Received:");
      this.sentField.setEditable(false);
      this.sentField.setFormatterFactory(total);
      this.sentField.setHorizontalAlignment(4);
      this.sentField.setFont(this.sentField.getFont());
      this.receivedField.setEditable(false);
      this.receivedField.setFormatterFactory(total);
      this.receivedField.setHorizontalAlignment(4);
      this.receivedField.setFont(this.receivedField.getFont());
      this.hostValue.setFont(this.hostValue.getFont().deriveFont(this.hostValue.getFont().getStyle() | 1, (float)(this.hostValue.getFont().getSize() + 1)));
      this.hostValue.setText(this.hostInfo.getHostname());
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(var1.createParallelGroup(1).add(this.timeValue, -1, 85, 32767).add(2, this.hostValue, -1, 85, 32767)).addPreferredGap(0).add(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.durationLabel).addPreferredGap(0).add(this.durationValue, -1, 83, 32767)).add(var1.createSequentialGroup().add(var1.createParallelGroup(1).add(this.receivedLabel).add(this.sentLabel)).add(18, 18, 18).add(var1.createParallelGroup(1).add(this.sentField, -1, 68, 32767).add(this.receivedField, -1, 68, 32767))))));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(var1.createParallelGroup(3).add(this.sentField, -2, -1, -2).add(this.sentLabel)).addPreferredGap(0).add(var1.createParallelGroup(1).add(this.receivedLabel).add(this.receivedField, -2, -1, -2)).addPreferredGap(0).add(var1.createParallelGroup(3).add(this.durationLabel).add(this.durationValue))).add(var1.createSequentialGroup().add(this.hostValue).addPreferredGap(0).add(this.timeValue, -2, -1, -2)));
   }

   static {
      ArrayList var0 = new ArrayList();
      var0.add(new Pair("B", BigDecimal.valueOf(1L)));
      var0.add(new Pair("KB", kilo));
      var0.add(new Pair("MB", kilo.pow(2)));
      var0.add(new Pair("GB", kilo.pow(3)));
      var0.add(new Pair("TB", kilo.pow(4)));
      magnitude = Collections.unmodifiableList(var0);
      total = new DefaultFormatterFactory() {
         public AbstractFormatter getFormatter(JFormattedTextField var1) {
            return new DefaultFormatter() {
               public String valueToString(Object var1) throws ParseException {
                  return var1 instanceof BigInteger ? String.format("%s", Connection.simplifyNumber((BigInteger)BigInteger.class.cast(var1))) : super.valueToString(var1);
               }
            };
         }
      };
   }

   class UpdateStatistics implements Runnable {
      long delta;
      BigInteger sent;
      BigInteger received;

      UpdateStatistics(long var2, BigInteger var4, BigInteger var5) {
         this.delta = var2;
         this.sent = var4;
         this.received = var5;
      }

      public void run() {
         Connection.this.sentField.setValue(this.sent);
         Connection.this.receivedField.setValue(this.received);
         if (this.delta > 0L) {
         }

         Connection.this.lastSent = this.sent;
         Connection.this.lastReceived = this.received;
      }
   }
}
