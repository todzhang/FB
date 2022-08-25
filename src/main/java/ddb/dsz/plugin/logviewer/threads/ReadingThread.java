package ddb.dsz.plugin.logviewer.threads;

import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.plugin.logviewer.gui.detail.TaskDetailInterface;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadingThread implements Runnable {
   Task task;
   TaskDetailInterface detail;
   InputStreamReader isr;
   boolean shouldStop = false;

   public boolean isShouldStop() {
      synchronized(this) {
         return this.shouldStop;
      }
   }

   public void setShouldStop(boolean shouldStop) {
      synchronized(this) {
         this.shouldStop = shouldStop;
      }
   }

   public ReadingThread(Task t, TaskDetailInterface td) throws IOException {
      this.task = t;
      this.detail = td;
   }

   public void run() {
      try {
         boolean keepGoing = true;
         int read = 0;
         char[] ch = new char[1024];

         while(keepGoing && !this.shouldStop) {
            read = this.isr.read(ch);
            if (read == -1) {
               break;
            }

            if (!this.shouldStop) {
               this.detail.appendString(new String(ch, 0, read));
            }

            if ((this.task.getState().equals(TaskState.FAILED) || this.task.getState().equals(TaskState.KILLED) || this.task.getState().equals(TaskState.SUCCEEDED)) && read == 0) {
               keepGoing = false;
            }
         }

         this.isr.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      this.detail.finished();
   }
}
