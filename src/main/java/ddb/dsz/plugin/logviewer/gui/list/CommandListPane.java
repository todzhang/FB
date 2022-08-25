package ddb.dsz.plugin.logviewer.gui.list;

import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.Task;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import ddb.dsz.plugin.logviewer.gui.target.TargetLogspace;
import ddb.dsz.plugin.logviewer.models.CommandModel;
import ddb.imagemanager.ImageManager;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;

public class CommandListPane extends LogViewerDetachable {
   CommandModel model;
   CommandListPaneDetails pane;
   boolean isLocal;
   String hostname;

   public CommandListPane(String hostname, CommandModel model, TargetLogspace parent, CoreController cc, boolean isLocal, boolean invalid) {
      this.pane = new CommandListPaneDetails(model, parent, cc, isLocal, invalid);
      this.hostname = hostname;
      this.model = model;
      this.isLocal = isLocal;
      super.setLogo("images/folder_man.png", ImageManager.SIZE16);
      super.setName("Commands");
      super.setAlignment(Alignment.LEFT);
      model.addInsertObserver(new Observer() {
         public void update(Observable o, Object arg) {
            CommandListPane.this.updateStatus();
         }
      });
      this.updateStatus();
   }

   @Override
   public void fini() {
      this.model.stop();
   }

   @Override
   public JComponent getDefaultElement() {
      return this.pane;
   }

   @Override
   public JComponent getDisplay() {
      return this.pane;
   }

   public void parseTask(Task task) {
      this.pane.parseTask(task);
   }

   public void addCommandName(String name, Boolean selected) {
      this.pane.addCommandName(name, selected);
   }

   public void setOperation(Operation operation) {
      this.pane.setOperation(operation);
   }

   @Override
   public boolean isClosable() {
      return false;
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   @Override
   public boolean isHideable() {
      return true;
   }

   public boolean isLocal() {
      return this.isLocal;
   }

   void updateStatus() {
      int count = this.model.getRowCount();
      super.setStatus(String.format("%d command%s on %s", count, count != 1 ? "s" : "", this.hostname));
   }

   public void showInvalidOnly(boolean show) {
      this.pane.showInvalidOnly(show);
   }
}
