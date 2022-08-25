package ddb.dsz.plugin.targetdetails;

import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.plugin.multitarget.MultipleTargetPlugin;
import ddb.dsz.plugin.multitarget.SingleTargetImpl;
import ddb.dsz.plugin.multitarget.SingleTargetInterface;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TargetDetailsHost extends SingleTargetImpl {
   MultipleTargetPlugin parent;
   TargetDetailsWorkbench detailsWorkbench;
   List<CommandEventListener> listeners = new ArrayList();

   public TargetDetailsHost(HostInfo var1, CoreController var2, MultipleTargetPlugin var3, Collection<TargetDetails.DisplayEntry> var4) {
      super(var1, var2);
      this.parent = var3;
      this.detailsWorkbench = new TargetDetailsWorkbench(var3);
      this.detailsWorkbench.setTabPlacement(3);
      super.setDisplay(this.detailsWorkbench);
      Object[] var5 = new Object[]{var1, var2, var3, var4};
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         TargetDetails.DisplayEntry var7 = (TargetDetails.DisplayEntry)var6.next();
         boolean var8 = false;
         Constructor[] var9 = var7.clazz.getConstructors();
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Constructor var12 = var9[var11];
            Object[] var13 = new Object[var12.getParameterTypes().length];
            boolean var14 = true;

            for(int var15 = 0; var15 < var12.getParameterTypes().length; ++var15) {
               boolean var16 = false;
               Object[] var17 = var5;
               int var18 = var5.length;

               for(int var19 = 0; var19 < var18; ++var19) {
                  Object var20 = var17[var19];
                  if (var12.getParameterTypes()[var15].isInstance(var20)) {
                     var16 = true;
                     var13[var15] = var20;
                     break;
                  }
               }

               if (!var16) {
                  var14 = false;
                  break;
               }
            }

            if (var14) {
               try {
                  Object var22 = var12.newInstance(var13);
                  if (var22 instanceof SingleTargetInterface) {
                     SingleTargetInterface var23 = (SingleTargetInterface)var22;
                     var23.setName(var7.name);
                     this.detailsWorkbench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var23});
                     this.listeners.add(var23);
                     var8 = true;
                  }
                  break;
               } catch (Exception var21) {
                  var21.printStackTrace();
               }
            }
         }

         if (!var8) {
            System.err.printf("%s couldn't be loaded\n", var7.name);
         }
      }

   }

   @Override
   public void commandEventReceived(CommandEvent commandEvent) {
      super.commandEventReceived(commandEvent);
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         CommandEventListener var3 = (CommandEventListener)var2.next();

         try {
            var3.commandEventReceived(commandEvent);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }

   @Override
   public String getDetachedTitle() {
      return String.format("%s - %s [%s]", this.parent.getName(), this.target.getId(), this.core.getTitle());
   }
}
