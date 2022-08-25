package ddb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Dumper implements Runnable {
   private BufferedReader reader;
   private final StringAppender outputArea;
   private boolean keepGoing;
   Logger logger;
   String name;

   public Dumper(BufferedReader reader, StringAppender outputArea, String name, Logger logger) {
      this.reader = reader;
      this.outputArea = outputArea;
      this.keepGoing = true;
      this.logger = logger;
      this.name = name;
   }

   public void endDumperThread() {
      this.keepGoing = false;
   }

   @Override
   public void run() {
      String line = "";

      while(line != null && this.keepGoing) {
         try {
            line = this.reader.readLine();
            System.out.printf("%s:  %s\n", this.name, line);
         } catch (IOException var6) {
            LogRecord record = new LogRecord(Level.INFO, "IOException in DumperThread.run().  This is most likely caused by a closed stream.");
            record.setThrown(var6);
            record.setSourceClassName(this.getClass().getSimpleName());
            record.setSourceMethodName("run()");
            this.logger.log(record);
            return;
         }

         if (line != null) {
            synchronized(this.outputArea) {
               this.outputArea.append(line + "\n");
            }

            if (line.length() > 0) {
            }
         }
      }

   }
}
