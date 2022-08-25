package ddb.dsz.plugin.systemlogviewer;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.gui.javalogviewer.JavaLogViewer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/system-log-viewer.png")
@DszName("System Log Viewer")
@DszDescription("Monitors log events and displays them nicely")
public class SystemLogViewer extends NoHostAbstractPlugin {
   public static final String MONITOR_ICON = "images/system-log-viewer.png";
   JavaLogViewer logViewer = new JavaLogViewer();
   Handler updater = new Handler() {
      public void publish(LogRecord var1) {
         if (var1.getLevel().intValue() >= Level.WARNING.intValue()) {
            SystemLogViewer.this.importantChange();
         }

      }

      public void flush() {
      }

      public void close() throws SecurityException {
      }

      public synchronized Level getLevel() {
         return Level.WARNING;
      }
   };
   Handler forwarder = new Handler() {
      public void publish(LogRecord var1) {
         if (var1.getLevel().intValue() >= this.getLevel().intValue()) {
            SystemLogViewer.this.logViewer.append(var1);
         }
      }

      public void flush() {
      }

      public void close() throws SecurityException {
      }

      public synchronized Level getLevel() {
         return SystemLogViewer.this.core.isDebugMode() ? Level.ALL : Level.WARNING;
      }
   };

   public SystemLogViewer() {
      super.setName("System Log Viewer");
   }

   void importantChange() {
      this.fireContentsChanged();
   }

   @Override
   protected void fini2() {
      super.core.getSystemLogger().removeHandler(this.forwarder);
   }

   @Override
   protected int init2() {
      super.core.getSystemLogger().addHandler(this.forwarder);
      super.core.getSystemLogger().addHandler(this.updater);
      super.setDisplay(this.logViewer);
      return 0;
   }

   @Override
   protected boolean parseArgument2(String var1, String var2) {
      if (var1.equalsIgnoreCase("-max") && var2 != null) {
         try {
            this.logViewer.setMaximum(Integer.parseInt(var2));
            return true;
         } catch (Exception var4) {
            this.core.logEvent(Level.SEVERE, "Invalid commandline parameter for SystemLogViewer:  \n" + var1);
            return false;
         }
      } else {
         return false;
      }
   }
}
