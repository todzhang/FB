package ds.core;

import ddb.detach.Tabbable;
import ddb.detach.TabbableStatus;
import ddb.detach.TabbableStatusImpl;
import ddb.dsz.core.connection.ConnectionChangeEvent;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.Plugin;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import ddb.misc.ColorPalette;
import ddb.util.UtilityConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.jdesktop.layout.GroupLayout;

public class StatusBar extends JPanel implements Observer, ConnectionChangeListener {
   private static final BoundedRangeModel DEFAULT_MODEL = new DefaultBoundedRangeModel(0, 1, 0, 100);
   private static final TabbableStatus DEFAULT_STATUS = new TabbableStatusImpl((Tabbable)null);
   private static final ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(1, UtilityConstants.createThreadFactory("StatusBar"));
   private static final int MAX_ITER = 10;
   private static final int FACTOR = 4;
   private static final int FLASH_PER_SECOND = 2;
   private final Map<HostInfo, Integer> flashCount = Collections.synchronizedMap(new HashMap());
   private static final ColorPalette disconnected;
   private static final ColorPalette local;
   private static final ColorPalette connected;
   TabbableStatus status = null;
   Color hostDefaultBackground;
   Color hostDefaultForeground;
   Color detailsDefaultBackground;
   Color detailsDefaultForeground;
   HostInfo currentHost = null;
   Runnable updater = new Runnable() {
      @Override
      public void run() {
         if (EventQueue.isDispatchThread()) {
            this.run2();
         } else {
            EventQueue.invokeLater(this);
         }

      }

      public void run2() {
         Integer var1 = 0;
         String var2 = null;
         ColorPalette var3 = null;
         if (StatusBar.this.currentHost == null) {
            var2 = "";
            var3 = StatusBar.connected;
         } else if (StatusBar.this.currentHost.isLocal()) {
            StatusBar.this.flashCount.put(StatusBar.this.currentHost, 0);
            var2 = StatusBar.this.currentHost.getId();
            var3 = StatusBar.local;
         } else if (StatusBar.this.currentHost.isConnected()) {
            StatusBar.this.flashCount.put(StatusBar.this.currentHost, 0);
            var2 = StatusBar.this.currentHost.getId();
            var3 = StatusBar.connected;
         } else if (!StatusBar.this.currentHost.isConnected()) {
            var2 = "Disconnected";
            var1 = (Integer)StatusBar.this.flashCount.get(StatusBar.this.currentHost);
            if (var1 == null) {
               var1 = 40;
            }

            if (var1 < 40) {
               var1 = var1 + 1;
               StatusBar.this.flashCount.put(StatusBar.this.currentHost, var1);
            }

            var3 = StatusBar.disconnected;
         }

         if (var2 != null && var3 != null) {
            StatusBar.this.statusHost.setText(var2);
            StatusBar.this.statusHost.setForeground(var3.getForeground(var1 * 2 / 4));
            StatusBar.this.statusHost.setBackground(var3.getBackground(var1 * 2 / 4));
            StatusBar.this.statusHost.setOpaque(var3.isOpaque());
         }
      }
   };
   private JPanel detailsPanel;
   private JPanel hostPanel;
   private JLabel jLabel3;
   private JPanel progressPanel;
   private JLabel statusDetails;
   private JLabel statusHost;
   private JLabel statusIcon;
   private JProgressBar statusProgress;
   private final Object STATUS_LOCK = new Object();
   private StatusBar.SetStatus pending = null;

   public StatusBar() {
      this.initComponents();
      this.hostDefaultBackground = this.statusHost.getBackground();
      this.hostDefaultForeground = this.hostPanel.getForeground();
      this.detailsDefaultBackground = this.statusDetails.getBackground();
      this.detailsDefaultForeground = this.detailsPanel.getForeground();
      exec.scheduleWithFixedDelay(this.updater, 250L, 250L, TimeUnit.MILLISECONDS);
   }

   private void initComponents() {
      this.jLabel3 = new JLabel();
      this.hostPanel = new JPanel();
      this.statusHost = new JLabel();
      this.detailsPanel = new JPanel();
      this.statusDetails = new JLabel();
      this.progressPanel = new JPanel();
      this.statusIcon = new JLabel();
      this.statusProgress = new JProgressBar();
      this.jLabel3.setText("jLabel3");
      this.setOpaque(false);
      this.hostPanel.setBorder(BorderFactory.createEtchedBorder());
      this.hostPanel.setOpaque(false);
      this.statusHost.setHorizontalAlignment(0);
      this.statusHost.setText("127.0.0.1");
      this.statusHost.setOpaque(true);
      GroupLayout var1 = new GroupLayout(this.hostPanel);
      this.hostPanel.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(this.statusHost, -1, 114, 32767));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(this.statusHost, -1, -1, 32767));
      this.detailsPanel.setBorder(BorderFactory.createEtchedBorder());
      this.detailsPanel.setOpaque(false);
      this.statusDetails.setText("Plugin Status");
      this.statusDetails.setOpaque(true);
      GroupLayout var2 = new GroupLayout(this.detailsPanel);
      this.detailsPanel.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(this.statusDetails, -1, 330, 32767));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(this.statusDetails, -1, -1, 32767));
      this.statusIcon.setText("icon");
      this.statusIcon.setMaximumSize(this.statusIcon.getPreferredSize());
      this.statusIcon.setMinimumSize(this.statusIcon.getPreferredSize());
      this.statusIcon.setPreferredSize(new Dimension(16, 16));
      this.statusIcon.setText("");
      GroupLayout var3 = new GroupLayout(this.progressPanel);
      this.progressPanel.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(2, var3.createSequentialGroup().add(this.statusProgress, -1, 116, 116).addPreferredGap(0).add(this.statusIcon, -2, -1, -2)));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(this.statusIcon, -2, -1, -2).add(this.statusProgress, -1, 18, 18));
      GroupLayout var4 = new GroupLayout(this);
      this.setLayout(var4);
      var4.setHorizontalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().add(this.hostPanel, -2, -1, -2).addPreferredGap(0).add(this.detailsPanel, -1, -1, 32767).addPreferredGap(0).add(this.progressPanel, -2, 139, -2)));
      var4.setVerticalGroup(var4.createParallelGroup(1).add(this.progressPanel, -1, 18, 32767).add(this.hostPanel, -1, -1, 32767).add(this.detailsPanel, -1, -1, 32767));
   }

   public void setStatus(TabbableStatus tabbableStatus) {
      this.setStatus(tabbableStatus, null);
   }

   public void setStatus(TabbableStatus status, Object var2) {
      if (this.status != null) {
         this.status.deleteObserver(this);
      }

      this.status = status;
      if (this.status != null) {
         this.status.addObserver(this);
      }

      this.update2(this.status, var2);
   }

   @Override
   public void update(Observable observable, Object var2) {
      if (observable instanceof TabbableStatus) {
         this.update2(TabbableStatus.class.cast(observable), var2);
      }

   }

   public void update2(TabbableStatus status, Object var2) {
      if (status == null) {
         status = DEFAULT_STATUS;
      }

      if (var2 instanceof Plugin) {
         this.currentHost = ((Plugin)Plugin.class.cast(var2)).getTarget();
      } else if (var2 instanceof SingleTargetInterface) {
         this.currentHost = ((SingleTargetInterface)SingleTargetInterface.class.cast(var2)).getTarget();
      } else {
         this.currentHost = null;
      }

      StatusBar.SetStatus var3 = new StatusBar.SetStatus(status.getDetails().getText());
      var3.setPanelForeground(status.getDetails().getForeground());
      var3.setProgress(status.getProgressModel(), status.isIndeterminate());
      if (status.getDetails().getBackground() != null) {
         var3.setDetails(true, status.getDetails().getForeground(), status.getDetails().getBackground());
      } else {
         var3.setDetails(false, this.detailsDefaultForeground, this.detailsDefaultBackground);
      }

      this.invokeStatus(var3);
   }

   private void invokeStatus(StatusBar.SetStatus setStatus) {
      synchronized(this.STATUS_LOCK) {
         if (this.pending != null) {
            UtilityConstants.ShallowCopy(this.pending, setStatus, StatusBar.SetStatus.class);
            return;
         }

         this.pending = setStatus;
      }

      EventQueue.invokeLater(setStatus);
   }

   @Override
   public void connectionChanged(ConnectionChangeEvent connectionChangeEvent) {
   }

   static {
      disconnected = new ColorPalette(Color.WHITE, true, new Color[]{Color.RED, Color.GREEN, Color.DARK_GRAY, Color.BLUE});
      local = new ColorPalette(Color.WHITE, true, new Color[]{Color.RED});
      connected = new ColorPalette(Color.BLACK, false, new Color[0]);
   }

   private class SetStatus implements Runnable {
      String details;
      Color detailsForeground;
      Color detailsBackground;
      Color panelForeground;
      boolean opaque;
      BoundedRangeModel model;
      boolean indeterminate;
      Icon icon;

      public SetStatus(String var2) {
         this.details = var2;
      }

      public void setPanelForeground(Color var1) {
         this.panelForeground = var1;
      }

      public void setDetails(boolean var1, Color var2, Color var3) {
         this.opaque = var1;
         this.detailsBackground = var3;
         this.detailsForeground = var2;
      }

      public void setProgress(BoundedRangeModel var1, boolean var2) {
         this.model = var1;
         this.indeterminate = var2;
      }

      public void setIcon(Icon var1) {
         this.icon = var1;
      }

      @Override
      public void run() {
         synchronized(StatusBar.this.STATUS_LOCK) {
            StatusBar.this.pending = null;
         }

         if (this.model == null) {
            this.model = StatusBar.DEFAULT_MODEL;
         }

         StatusBar.this.statusDetails.setText(this.details);
         StatusBar.this.statusDetails.setForeground(this.detailsForeground);
         StatusBar.this.statusDetails.setBackground(this.detailsBackground);
         StatusBar.this.detailsPanel.setForeground(this.panelForeground);
         StatusBar.this.statusDetails.setOpaque(this.opaque);
         StatusBar.this.statusProgress.setModel(this.model);
         StatusBar.this.statusProgress.setIndeterminate(this.indeterminate);
         StatusBar.this.statusIcon.setIcon(this.icon);
         StatusBar.this.updater.run();
      }

      @Override
      public String toString() {
         return String.format("Status (%s)", this.details);
      }
   }
}
