package ddb.dsz.plugin.connectioninfo;

import ddb.delegate.UpdateLabel;
import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.connection.events.DisconnectEvent;
import ddb.dsz.core.connection.events.NewHostEvent;
import ddb.dsz.core.connection.events.StatisticsEvent;
import ddb.dsz.core.connection.events.ThrottleEvent;
import ddb.dsz.core.connection.events.StatisticsEvent.Host;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;

@DszLive(
   live = true,
   replay = false
)
@DszLogo("images/clock.png")
@DszName("Connection Info")
@DszDescription("A plugin that monitors the connection bandwidth usage")
@DszUserStartable(false)
public class ConnectionInfo extends NoHostAbstractPlugin implements ConnectionChangeListener {
   private static final Collection<String> INTERESTING_COMMANDS;
   public static DateFormat FORMAT;
   public static final TimeZone GMT;
   public static final int CYCLE = 20;
   private volatile int Timer = 60;
   Calendar LastRun = Calendar.getInstance();
   private static final int MAXIMUM = 90;
   private final Runnable requestStatistics = new Runnable() {
      public void run() {
         try {
            synchronized(this) {
               if (ConnectionInfo.this.requestedStatistics) {
                  Calendar var2 = Calendar.getInstance();
                  if (var2.getTimeInMillis() - ConnectionInfo.this.LastRun.getTimeInMillis() < 90000L) {
                     return;
                  }
               }

               ConnectionInfo.this.requestedStatistics = true;
            }

            ConnectionInfo.this.core.requestStatistics();
         } finally {
            ConnectionInfo.this.core.schedule(this, (long)ConnectionInfo.this.Timer, TimeUnit.SECONDS);
         }
      }
   };
   private final Runnable updateConnectionTime = new Runnable() {
      public void run() {
         if (ConnectionInfo.DisplayType.Cycle.equals(ConnectionInfo.this.current)) {
            if (ConnectionInfo.this.cycleCount-- == 0) {
               ConnectionInfo.this.cycleCount = 20;
               ConnectionInfo.this.cycleLocal = !ConnectionInfo.this.cycleLocal;
               ConnectionInfo.this.displayTime.repaint();
            }
         } else {
            ConnectionInfo.this.cycleCount = 20;
            if (ConnectionInfo.DisplayType.Local.equals(ConnectionInfo.this.current)) {
               ConnectionInfo.this.cycleLocal = true;
            } else {
               ConnectionInfo.this.cycleLocal = false;
            }
         }

         boolean var1 = ConnectionInfo.this.cycleLocal;
         Calendar var2;
         if (!var1) {
            var2 = Calendar.getInstance(ConnectionInfo.GMT);
         } else {
            var2 = Calendar.getInstance();
         }

         ConnectionInfo.DisplayType var3 = ConnectionInfo.this.current;
         if (ConnectionInfo.DisplayType.Cycle.equals(var3)) {
            var3 = var1 ? ConnectionInfo.DisplayType.Local : ConnectionInfo.DisplayType.Gmt;
         }

         synchronized(ConnectionInfo.this.CONNECTION_LOCK) {
            Iterator var5 = ConnectionInfo.this.connections.iterator();

            while(true) {
               if (!var5.hasNext()) {
                  break;
               }

               Connection var6 = (Connection)var5.next();
               var6.update(var2, var3);
            }
         }

         EventQueue.invokeLater(new UpdateLabel(ConnectionInfo.this.lpTime, String.format("%04d/%02d/%02d %02d:%02d:%02d", var2.get(1), var2.get(2) + 1, var2.get(5), var2.get(11), var2.get(12), var2.get(13))));
      }
   };
   DefaultComboBoxModel model = new DefaultComboBoxModel(ConnectionInfo.DisplayType.values());
   int cycleCount = 20;
   boolean cycleLocal = true;
   ConnectionInfo.DisplayType current;
   final Object CONNECTION_LOCK;
   List<Connection> connections;
   boolean requestedStatistics;
   JSlider frequency;
   JComboBox displayTime;
   JLabel lpTime;
   JToolBar listOfConnections;
   JLabel freqDisplay;
   final DataTransformer transformer;
   Calendar lastStats;

   public ConnectionInfo() {
      this.current = ConnectionInfo.DisplayType.Gmt;
      this.CONNECTION_LOCK = new Object();
      this.connections = new Vector();
      this.requestedStatistics = false;
      this.frequency = new JSlider(1, 60, 30);
      this.displayTime = new JComboBox(this.model);
      this.lpTime = new JLabel("");
      this.listOfConnections = new JToolBar(1);
      this.freqDisplay = new JLabel("");
      this.transformer = DataTransformer.newInstance();
      this.lastStats = null;
      super.setName("Connection Info");
      super.prefferedSize = new Dimension(400, 400);
      super.setCanClose(false);
      this.frameSize = this.getPreferredSize();
      this.frameSite = new Point(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width - this.frameSize.width, 0);
      JPanel var1 = new JPanel(new BorderLayout());
      JPanel var2 = new JPanel(new BorderLayout());
      JPanel var3 = new JPanel(new BorderLayout());
      JPanel var4 = new JPanel(new BorderLayout());
      var1.add(var2, "North");
      var1.add(var3, "Center");
      var1.add(var4, "South");
      JLabel var5 = new JLabel("Frequency:");
      var4.add(var5, "West");
      var4.add(this.frequency, "Center");
      var4.add(this.freqDisplay, "East");
      this.freqDisplay.setText(String.format("%02d seconds", this.frequency.getValue()));
      var2.add(this.displayTime, "North");
      var2.add(this.lpTime, "Center");
      this.lpTime.setFont(this.lpTime.getFont().deriveFont(18.0F));
      this.lpTime.setHorizontalAlignment(0);
      this.listOfConnections.setFloatable(false);
      var3.add(new JScrollPane(this.listOfConnections, 20, 31));
      this.displayTime.setSelectedItem(ConnectionInfo.DisplayType.Cycle);
      this.displayTime.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent var1) {
            if (var1.getStateChange() == 1 && var1.getItem() instanceof ConnectionInfo.DisplayType) {
               ConnectionInfo.this.current = (ConnectionInfo.DisplayType)var1.getItem();
            }

         }
      });
      this.displayTime.setRenderer(new DefaultListCellRenderer() {
         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            if (var3 == -1 && var2.equals(ConnectionInfo.DisplayType.Cycle)) {
               var2 = ConnectionInfo.this.cycleLocal ? ConnectionInfo.DisplayType.Local : ConnectionInfo.DisplayType.Gmt;
            }

            if (var2 instanceof ConnectionInfo.DisplayType) {
               var2 = ((ConnectionInfo.DisplayType)ConnectionInfo.DisplayType.class.cast(var2)).getText();
            }

            Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
            if (var6 instanceof JLabel && var3 == -1) {
               ((JLabel)JLabel.class.cast(var6)).setHorizontalAlignment(0);
            }

            return var6;
         }
      });
      this.frequency.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            ConnectionInfo.this.Timer = ConnectionInfo.this.frequency.getValue();
            ConnectionInfo.this.freqDisplay.setText(String.format("%02d seconds", ConnectionInfo.this.Timer));
         }
      });
      super.setShowStatus(false);
      super.setDisplay(var1);
      this.Timer = this.frequency.getValue();
   }

   @Override
   protected int init2() {
      if (this.transformer == null) {
         return -1;
      } else {
         this.core.schedule(this.requestStatistics, (long)this.frequency.getValue(), TimeUnit.SECONDS);
         this.core.scheduleWithFixedDelay(this.updateConnectionTime, 500L, 250L, TimeUnit.MILLISECONDS);
         this.transformer.addClosure(ClosureFactory.newVariableClosure(this.core, "time", "Dsz", new Closure() {
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            public void execute(Object var1) {
               if (var1 != null && var1 instanceof DataEvent) {
                  DataEvent var2 = (DataEvent)var1;
                  if (DataEventType.DATA.equals(var2.getDataType()) && var2.getData().getObject("timeitem") != null) {
                     Calendar var3 = Calendar.getInstance();

                     try {
                        var3.setTime(this.format.parse(var2.getData().getString("timeitem::gui_timestamp")));
                     } catch (ParseException var16) {
                        return;
                     }

                     Calendar var4 = Calendar.getInstance();

                     try {
                        var4.setTime(this.format.parse(String.format("%sT%s", var2.getData().getString("timeitem::gmttime::date"), var2.getData().getString("timeitem::gmttime::time"))));
                     } catch (ParseException var15) {
                        return;
                     }

                     Calendar var5 = Calendar.getInstance();

                     try {
                        var5.setTime(this.format.parse(String.format("%sT%s", var2.getData().getString("timeitem::localtime::date"), var2.getData().getString("timeitem::localtime::time"))));
                     } catch (ParseException var14) {
                        return;
                     }

                     long var6 = var3.getTimeInMillis() - var4.getTimeInMillis();
                     long var8 = var5.getTimeInMillis() - var4.getTimeInMillis();
                     synchronized(ConnectionInfo.this.CONNECTION_LOCK) {
                        Iterator var11 = ConnectionInfo.this.connections.iterator();

                        while(var11.hasNext()) {
                           Connection var12 = (Connection)var11.next();
                           if (var12.getHost().sameHost(ConnectionInfo.this.core.getTaskById(var2.getTaskId()).getHost())) {
                              var12.setTime(var6, var8, var5, var4);
                           }
                        }
                     }
                  }

               }
            }
         }, ClosureUtils.nopClosure()));
         return 0;
      }
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(300, GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height);
   }

   @Override
   public void connectionChanged(final ConnectionChangeEvent connectionChangeEvent) {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               ConnectionInfo.this.connectionChanged(connectionChangeEvent);
            }
         });
      } else {
         if (connectionChangeEvent instanceof NewHostEvent) {
            NewHostEvent var2 = (NewHostEvent) connectionChangeEvent;
            if (var2.getHost() == null) {
               return;
            }

            if (var2.getHost().isLocal()) {
               return;
            }

            synchronized(this.CONNECTION_LOCK) {
               Connection var4 = new Connection(var2.getHost());
               this.listOfConnections.add(var4);
               this.connections.add(var4);
            }

            super.contentsChanged();
         } else if (connectionChangeEvent instanceof StatisticsEvent) {
            synchronized(this.requestStatistics) {
               this.requestedStatistics = false;
            }

            Calendar var19 = Calendar.getInstance();
            long var3 = 0L;
            if (this.lastStats != null) {
               var3 = var19.getTimeInMillis() - this.lastStats.getTimeInMillis();
            }

            StatisticsEvent var5 = (StatisticsEvent)StatisticsEvent.class.cast(connectionChangeEvent);
            synchronized(this.CONNECTION_LOCK) {
               Iterator var7 = this.connections.iterator();

               while(var7.hasNext()) {
                  Connection var8 = (Connection)var7.next();
                  Iterator var9 = var5.getHosts().iterator();

                  while(var9.hasNext()) {
                     Host var10 = (Host)var9.next();
                     if (var10.getId().equalsIgnoreCase(var8.getHost().getId())) {
                        var8.updateStatistics(var3, var10.getSent(), var10.getReceived());
                        break;
                     }
                  }
               }
            }

            this.lastStats = var19;
         } else {
            Iterator var22;
            Connection var23;
            if (connectionChangeEvent instanceof ThrottleEvent) {
               ThrottleEvent var20 = (ThrottleEvent) connectionChangeEvent;
               synchronized(this.CONNECTION_LOCK) {
                  var22 = this.connections.iterator();

                  while(var22.hasNext()) {
                     var23 = (Connection)var22.next();
                     if (var23.getHost().getId().equalsIgnoreCase(var20.getAddress())) {
                        var23.setThrottle(var20.getBytes());
                     }
                  }
               }
            } else if (connectionChangeEvent instanceof DisconnectEvent) {
               DisconnectEvent var21 = (DisconnectEvent) connectionChangeEvent;
               synchronized(this.CONNECTION_LOCK) {
                  var22 = this.connections.iterator();

                  while(var22.hasNext()) {
                     var23 = (Connection)var22.next();
                     if (var23.getHost().getId().equalsIgnoreCase(var21.getHost().getId())) {
                        var23.disconnected();
                     }
                  }
               }
            }
         }

      }
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      Task var2 = this.core.getTaskById(var1.getId());
      if (var2 != null) {
         if (var2.getCommandName() != null) {
            if (INTERESTING_COMMANDS.contains(var2.getCommandName().toLowerCase())) {
               this.transformer.addTask(var2);
            }

         }
      }
   }

   @Override
   protected final boolean parseArgument2(String var1, String var2) {
      if (var1.equalsIgnoreCase("delay") && var2 != null) {
      }

      if (var1.equalsIgnoreCase("mirror") && var2 != null) {
      }

      return false;
   }

   public static void main(String[] var0) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Method var2 = var1.getMethod("main", String[].class);
      var2.invoke((Object)null, var0);
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("time");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(var0);
      FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z");
      GMT = TimeZone.getTimeZone("GMT");
   }

   public static enum DisplayType {
      Local("Local Time"),
      Gmt("GMT"),
      Cycle("Cycle Through");

      String text;

      private DisplayType(String var3) {
         this.text = var3;
      }

      public String getText() {
         return this.text;
      }
   }
}
