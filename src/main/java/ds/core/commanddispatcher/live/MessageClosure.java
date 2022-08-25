package ds.core.commanddispatcher.live;

import ddb.dsz.core.task.MutableTask;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskId;
import ddb.util.Guid;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

public abstract class MessageClosure implements Closure, Predicate {
   static final Map<String, Color> colorMap = new HashMap();
   protected LiveCommandDispatcher live;
   private int count = 0;

   public MessageClosure(LiveCommandDispatcher live) {
      this.live = live;
   }

   @Override
   public void execute(Object o) {
      if (o != null) {
         if (o instanceof Message) {
            this.handleMessage((Message) o);
         }

      }
   }

   @Override
   public boolean evaluate(Object o) {
      if (o == null) {
         return false;
      } else if (o instanceof Message && this.evaluateMessage((Message)(o))) {
         ++this.count;
         return true;
      } else {
         return false;
      }
   }

   protected abstract void handleMessage(Message message);

   protected abstract boolean evaluateMessage(Message message);

   protected final TaskId createTaskId(int taskId) {
      return this.live.createTaskId(taskId);
   }

   protected final MutableTask getTaskByTaskId(Guid guid) {
      Task task = this.live.getMainSystem().getTaskByTaskId(guid);
      return task instanceof MutableTask ? (MutableTask)task : null;
   }

   protected final void registerTaskId(MutableTask mutableTask) {
      this.live.getMainSystem().registerTaskId(mutableTask);
   }

   protected final MutableTask getTaskById(TaskId taskId) {
      Task task = this.live.getMainSystem().getTaskById(taskId);
      return task instanceof MutableTask ? (MutableTask)task : null;
   }

   protected final int asInteger(String s) {
      if (s == null) {
         return 0;
      } else {
         try {
            if (s.toLowerCase().startsWith("0x")) {
               return Integer.parseInt(s.substring(2), 16);
            } else {
               return s.toLowerCase().startsWith("0") && s.length() > 1 ? Integer.parseInt(s.substring(1, 8)) : Integer.parseInt(s, 10);
            }
         } catch (Exception var3) {
            this.live.getMainSystem().logEvent(Level.SEVERE, "Cannot parse integer: " + s, var3);
            return 0;
         }
      }
   }

   protected final Color lookup(String colorname) {
      Color color = colorMap.get(colorname);
      if (color == null) {
         color = Color.BLACK;
      }

      return color;
   }

   public void dump() {
      System.out.println(String.format("%s:  %d", this.getClass().getSimpleName(), this.count));
   }

   static {
      colorMap.put("black", new Color(0, 0, 0));
      colorMap.put("green", new Color(0, 128, 0));
      colorMap.put("blue", new Color(0, 0, 196));
      colorMap.put("darkblue", new Color(0, 0, 128));
      colorMap.put("yellow", new Color(128, 128, 0));
      colorMap.put("organe", new Color(255, 128, 0));
      colorMap.put("red", new Color(150, 0, 0));
      colorMap.put("brightred", new Color(255, 0, 0));
      colorMap.put("gray", new Color(128, 128, 160));
      colorMap.put("white", new Color(255, 255, 255));
   }
}
