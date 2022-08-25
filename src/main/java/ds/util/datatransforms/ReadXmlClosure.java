package ds.util.datatransforms;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ds.core.DSConstants;
import ds.core.impl.task.TaskStateAccess;
import java.io.Reader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.Closure;

public class ReadXmlClosure extends TaskClosure implements Closure {
   private static final ScheduledExecutorService READER = Executors.newScheduledThreadPool(1, DSConstants.namedFactory("ReadXmlClosure"));
   final Closure handleData;

   public ReadXmlClosure(CoreController var1, Task var2, Closure var3, Closure var4) {
      super(var4);
      this.handleData = var3;
   }

   public void execute(Object var1) {
      if (var1 != null && var1 instanceof TaskDataAccess) {
         if (!(var1 instanceof TaskStateAccess)) {
            TaskDataAccess var2 = (TaskDataAccess)var1;
            if (!var2.isGenerated()) {
               READER.submit(new ReadXmlClosure.ReadFile(var2));
            }
         }
      }
   }

   class ReadFile implements Runnable {
      final TaskDataAccess tda;
      long progress;

      public ReadFile(TaskDataAccess var2) {
         this.tda = var2;
         this.progress = 0L;
      }

      public void run() {
         boolean var1 = !this.tda.getTask().isAlive();
         long var2 = 5L;
         if (this.progress < this.tda.getSize()) {
            Reader var4 = this.tda.getReader();
            if (var4 == null) {
               this.progress = this.tda.getSize();
            } else {
               char[] var5 = new char[8096];

               try {
                  var4.skip(this.progress);
                  StringBuilder var6 = new StringBuilder();

                  while(true) {
                     int var7 = var4.read(var5);
                     if (var7 <= 0) {
                        if (var6.length() > 0) {
                           ReadXmlClosure.this.handleData.execute(var6.toString());
                        }
                        break;
                     }

                     var6.append(var5, 0, var7);
                     this.progress += (long)var7;
                     var2 = 0L;
                  }
               } catch (Exception var8) {
                  var8.printStackTrace();
               }
            }
         }

         if (!var1) {
            ReadXmlClosure.READER.schedule(this, var2, TimeUnit.SECONDS);
         }

      }
   }
}
