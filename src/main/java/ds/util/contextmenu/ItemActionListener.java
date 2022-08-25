package ds.util.contextmenu;

import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.contextmenu.ContextMenuAction;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.host.HostInfo;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JOptionPane;

final class ItemActionListener implements ActionListener {
   private final Collection<ItemActionListener.IndividualCommand> commands;
   private final String Label;
   private final IdCallback idcb;
   private final boolean showResult;
   private final boolean verify;
   private final ContextMenuAction actionHandler;
   private final CoreController core;
   private final HostInfo host;
   private final List<String> raw;
   private final Object extraContext;

   public ItemActionListener(CoreController var1, Collection<ItemActionListener.IndividualCommand> var2, String var3, IdCallback var4, HostInfo var5, ContextMenuAction var6, boolean var7, boolean var8, List<String> var9, Object var10) {
      this.core = var1;
      this.commands = new ArrayList();
      this.commands.addAll(var2);
      this.Label = var3;
      this.idcb = var4;
      this.host = var5;
      this.showResult = var7;
      this.verify = var8;
      this.actionHandler = var6;
      this.raw = new Vector();
      this.raw.addAll(var9);
      this.extraContext = var10;
   }

   public void actionPerformed(ActionEvent var1) {
      if (!this.verify || JOptionPane.showConfirmDialog((Component)null, "Are you sure you wish to do this:  " + this.Label, "System Change!", 0) == 0) {
         Iterator var2 = this.commands.iterator();

         while(true) {
            while(var2.hasNext()) {
               ItemActionListener.IndividualCommand var3 = (ItemActionListener.IndividualCommand)var2.next();
               if (var3.parameters != null && var3.parameters.size() != 0) {
                  this.actionHandler.action(var3.parameters);
               } else {
                  try {
                     this.core.startCommand(var3.command, this.showResult ? this.idcb : null, var3.command, this.host);
                  } catch (DispatcherException var5) {
                     this.core.logEvent(Level.WARNING, "Failure on " + this.Label);
                  }
               }
            }

            if (this.raw != null && this.extraContext != null) {
               this.actionHandler.action(this.raw, this.extraContext);
            }

            return;
         }
      }
   }

   public static class IndividualCommand {
      public String command;
      public List<String> parameters;
   }
}
