package ddb.util;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GeneralUtilities {
   public static TimeUnit GENERAL_WAIT_UNIT;
   public static long GENERAL_WAIT_LENGTH;
   private static ExecutorService exec;

   public static Calendar stringToCalendar(String textVersion, Calendar optionalCalendar) {
      if (textVersion == null) {
         return null;
      } else if (textVersion.trim().length() == 0) {
         return null;
      } else {
         String[] bits = textVersion.split("[-T:.]");
         if (bits.length >= 6 && bits.length <= 7) {
            if (optionalCalendar == null) {
               optionalCalendar = Calendar.getInstance();
            }

            try {
               optionalCalendar.set(1, Integer.parseInt(bits[0]));
               optionalCalendar.set(2, Integer.parseInt(bits[1]) - 1);
               optionalCalendar.set(5, Integer.parseInt(bits[2]));
               optionalCalendar.set(11, Integer.parseInt(bits[3]));
               optionalCalendar.set(12, Integer.parseInt(bits[4]));
               optionalCalendar.set(13, Integer.parseInt(bits[5]));
               if (bits.length == 7) {
                  optionalCalendar.set(14, Integer.parseInt(bits[6]) / 1000000);
               } else {
                  optionalCalendar.set(14, 0);
               }

               return optionalCalendar;
            } catch (NumberFormatException var4) {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public static String CalendarToStringXml(Calendar calendar) {
      return formatCalendar("%04d-%02d-%02dT%02d:%02d:%02d.%06d", calendar);
   }

   public static String CalendarToStringFile(Calendar calendar) {
      return formatCalendar("%04d_%02d_%02d_%02dh%02dm%02ds.%06d", calendar);
   }

   public static String CalendarToStringDisplay(Calendar calendar) {
      return formatCalendar("%04d/%02d/%02d %02d:%02d:%02d.%06d", calendar);
   }

   private static String formatCalendar(String format, Calendar calendar) {
      if (calendar == null) {
         calendar = Calendar.getInstance();
         calendar.setTimeInMillis(0L);
      }

      return String.format(format, calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14) * 1000);
   }

   public static ThreadFactory createThreadFactory() {
      return createThreadFactory("General Utilities");
   }

   public static ThreadFactory createThreadFactory(final String name) {
      return new ThreadFactory() {
         int count = 0;

         @Override
         public Thread newThread(Runnable r) {
            Thread th = new Thread(r, String.format("%s %d", name, ++this.count));
            th.setPriority(1);
            th.setDaemon(true);
            return th;
         }
      };
   }

   public void shutdownRunForATime() {
      exec.shutdownNow();
   }

   public static <O> O runForATime(GeneralUtilities.StoppableCallable<O> caller) {
      return runForATime(caller, GENERAL_WAIT_UNIT, GENERAL_WAIT_LENGTH);
   }

   public static <O> O runForATime(final GeneralUtilities.StoppableCallable<O> caller, TimeUnit unit, long delay) {
      FutureTask<O> future = new FutureTask(new Callable<O>() {
         public O call() {
            try {
               return caller.call();
            } catch (Exception var2) {
               var2.printStackTrace();
               return null;
            }
         }
      });
      exec.execute(future);

      try {
         return future.get(delay, unit);
      } catch (TimeoutException var7) {
      } catch (InterruptedException var8) {
      } catch (ExecutionException var9) {
      }

      caller.cancel();
      return null;
   }

   public static void main(String[] args) throws Throwable {
      Class<?> live = Class.forName("ds.plugin.live.DSClientApp");
      Class<?> replay = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method m = live.getMethod("main", args.getClass());
      m.invoke((Object)null, args);
   }

   static {
      GENERAL_WAIT_UNIT = TimeUnit.MINUTES;
      GENERAL_WAIT_LENGTH = 10L;
      exec = Executors.newCachedThreadPool(createThreadFactory("RunForATimeDelegates"));
   }

   public abstract class StoppableCallableImpl<O> implements GeneralUtilities.StoppableCallable<O> {
      private boolean cancel = false;

      protected boolean shouldStop() {
         return this.cancel;
      }

      @Override
      public boolean isCancelled() {
         return this.cancel;
      }
   }

   public interface StoppableCallable<O> extends Callable<O> {
      void cancel();

      boolean isCancelled();
   }

}
