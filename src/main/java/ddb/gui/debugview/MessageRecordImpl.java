package ddb.gui.debugview;

import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class MessageRecordImpl implements MutableMessageRecord {
   Calendar time = Calendar.getInstance();
   int thread;
   Importance priority;
   String section;
   String message;

   public MessageRecordImpl() {
      this.time.set(1, 2000);
      this.time.set(2, 0);
      this.time.set(5, 1);
   }

   public static MutableMessageRecord ParseMessage(String text) {
      Scanner s;
      MatchResult result;
      MessageRecordImpl record;
      try {
         s = new Scanner(text);
         s.findInLine("(\\d+):(\\d+):(\\d+)\\.(\\d+)\\s*-\\s*(-{0,1}\\d+): \\<(\\d+)\\>\\[(\\S+)\\]\\s*(.+)");
         result = s.match();
         record = new MessageRecordImpl();
         record.time.set(11, Integer.parseInt(result.group(1)));
         record.time.set(12, Integer.parseInt(result.group(2)));
         record.time.set(13, Integer.parseInt(result.group(3)));
         record.time.set(14, Integer.parseInt(result.group(4)));
         record.thread = Integer.parseInt(result.group(5));
         record.priority = Importance.values()[Integer.parseInt(result.group(6))];
         record.section = result.group(7);
         record.message = result.group(8);
         return record;
      } catch (Exception var6) {
         try {
            s = new Scanner(text);
            s.findInLine("(\\d+):(\\d+):(\\d+)\\s*-\\s*(-{0,1}\\d+): \\<(\\d+)\\>\\[(\\S+)\\]\\s*(.+)");
            result = s.match();
            record = new MessageRecordImpl();
            record.time.set(11, Integer.parseInt(result.group(1)));
            record.time.set(12, Integer.parseInt(result.group(2)));
            record.time.set(13, Integer.parseInt(result.group(3)));
            record.thread = Integer.parseInt(result.group(4));
            record.priority = Importance.values()[Integer.parseInt(result.group(5))];
            record.section = result.group(6);
            record.message = result.group(7);
            return record;
         } catch (Exception var5) {
            try {
               s = new Scanner(text);
               s.findInLine("(\\d{4})(\\d{2})(\\d{2}):(\\d{2})(\\d{2})(\\d{2})\\s*-\\s*(-{0,1}\\d+): \\<(\\d+)\\>\\[(\\S+)\\]\\s*(.+)");
               result = s.match();
               record = new MessageRecordImpl();
               record.time.set(1, Integer.parseInt(result.group(1)));
               record.time.set(2, Integer.parseInt(result.group(2)) + 1);
               record.time.set(5, Integer.parseInt(result.group(3)));
               record.time.set(11, Integer.parseInt(result.group(4)));
               record.time.set(12, Integer.parseInt(result.group(5)));
               record.time.set(13, Integer.parseInt(result.group(6)));
               record.thread = Integer.parseInt(result.group(7));
               record.priority = Importance.values()[Integer.parseInt(result.group(8))];
               record.section = result.group(9);
               record.message = result.group(10);
               return record;
            } catch (Exception var4) {
               return null;
            }
         }
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("[%d:%d:%d]-", this.time.get(11), this.time.get(12), this.time.get(13)));
      sb.append(String.format("\n\tThread: %d", this.thread));
      sb.append(String.format("\n\tPriority: %s", this.priority.toString()));
      sb.append(String.format("\n\tSection: %s", this.section));
      sb.append(String.format("\n\tMessage: %s", this.message.replaceAll("\n", "\n\t\t")));
      return sb.toString();
   }

   public void append(String text) {
      this.message = String.format("%s\n%s", this.message, text);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Importance getPriority() {
      return this.priority;
   }

   public void setPriority(Importance priority) {
      this.priority = priority;
   }

   public String getSection() {
      return this.section;
   }

   public void setSection(String section) {
      this.section = section;
   }

   public int getThread() {
      return this.thread;
   }

   public void setThread(int thread) {
      this.thread = thread;
   }

   public Calendar getTime() {
      return this.time;
   }

   public void setTime(Calendar time) {
      this.time = time;
   }
}
