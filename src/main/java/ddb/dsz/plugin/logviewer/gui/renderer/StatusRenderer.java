package ddb.dsz.plugin.logviewer.gui.renderer;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.TaskState;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class StatusRenderer extends CustomTableCellRenderer {
   private final ImageIcon RUN;
   private final ImageIcon PAUSE;
   private final ImageIcon SUCCESS;
   private final ImageIcon FAILURE;
   private final ImageIcon TASKED;
   private final ImageIcon KILLED;

   public StatusRenderer(CoreController core) {
      this.RUN = ImageManager.getIcon("images/player_play.png", core.getLabelImageSize());
      this.PAUSE = ImageManager.getIcon("images/player_pause.png", core.getLabelImageSize());
      this.SUCCESS = ImageManager.getIcon("images/button_ok.png", core.getLabelImageSize());
      this.FAILURE = ImageManager.getIcon("images/error.png", core.getLabelImageSize());
      this.TASKED = ImageManager.getIcon("images/player_end.png", core.getLabelImageSize());
      this.KILLED = ImageManager.getIcon("images/yellowled.png", core.getLabelImageSize());
   }

   protected Component modifyComponent(JLabel label, Object value) {
      if (value instanceof TaskState) {
         label.setText("");
         switch((TaskState)TaskState.class.cast(value)) {
         case FAILED:
            label.setIcon(this.FAILURE);
            break;
         case KILLED:
            label.setIcon(this.KILLED);
            break;
         case PAUSED:
            label.setIcon(this.PAUSE);
            break;
         case RUNNING:
            label.setIcon(this.RUN);
            break;
         case SUCCEEDED:
            label.setIcon(this.SUCCESS);
            break;
         case TASKED:
            label.setIcon(this.TASKED);
            break;
         default:
            label.setIcon((Icon)null);
         }
      }

      return label;
   }
}
