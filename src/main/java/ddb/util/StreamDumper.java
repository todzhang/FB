package ddb.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class StreamDumper {
   private BufferedReader outReader;
   private Thread outThread;
   private BufferedReader errReader;
   private Thread errThread;
   private StringAppender outputArea;
   Logger logger;

   public StreamDumper(InputStream stdOut, InputStream stdErr, StringAppender outputArea, Logger logger) {
      this.outReader = new BufferedReader(new InputStreamReader(stdOut));
      this.errReader = new BufferedReader(new InputStreamReader(stdErr));
      this.outputArea = outputArea;
      this.logger = logger;
   }

   public void start(ThreadFactory tf) {
      this.outThread = tf.newThread(new Dumper(this.outReader, this.outputArea, "out", this.logger));
      this.errThread = tf.newThread(new Dumper(this.errReader, this.outputArea, "err", this.logger));
      this.outThread.setName("stdout dumper");
      this.errThread.setName("stderr dumper");
      this.outThread.start();
      this.errThread.start();
   }

   public void stop() {
   }
}
