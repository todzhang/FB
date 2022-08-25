package ddb.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogFileManager {
   private String logFileDirName;
   private String suffix;
   private Logger logger;

   public LogFileManager(String logFileDirName, String suffix, Logger logger) {
      this.logFileDirName = logFileDirName.trim();
      this.suffix = suffix.trim();
      this.logger = logger;
   }

   public synchronized File createLogFile(String filenamePrefix) {
      File logFile = null;
      boolean done = false;

      while(!done) {
         String logFilename = this.getFilename(filenamePrefix.trim());
         logFile = new File(logFilename);
         logFile.getParentFile().mkdirs();

         try {
            done = logFile.createNewFile();
         } catch (IOException var7) {
            LogRecord record = new LogRecord(Level.INFO, var7.getMessage());
            record.setSourceClassName(this.getClass().getSimpleName());
            record.setSourceMethodName("createLogFile()");
            this.logger.log(record);
         }
      }

      return logFile;
   }

   private String getFilename(String filenamePrefix) {
      Calendar now = Calendar.getInstance();
      return String.format("%s%s%s_%04d_%02d_%02d_%02dh%02dm%02ds.%03d.%s", this.logFileDirName, File.separator, filenamePrefix, now.get(1), now.get(2) + 1, now.get(5), now.get(11), now.get(12), now.get(13), now.get(14), this.suffix);
   }
}
