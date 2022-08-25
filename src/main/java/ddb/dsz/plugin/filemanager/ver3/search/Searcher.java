package ddb.dsz.plugin.filemanager.ver3.search;

import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import org.apache.commons.collections.Closure;

public class Searcher implements Runnable {
   public static final int MAX_PARENTS = 100;
   private boolean stop = false;
   private boolean running = false;
   Closure onFinished;
   FileSystemModel model;
   Closure addNode;
   public String name = null;
   public FileObject searchRoot = null;
   public DateSearchType dateType = null;
   public int lastDays = -1;
   public Calendar spanStart;
   public Calendar spanStop;
   public MimeTypeMap.MimeType mimeType;
   public boolean isCaseSensitive = false;
   public long minimumSize = -1L;
   public long maximumSize = -1L;
   public Searcher.SearchType searchType;
   public Pattern regexPattern;
   private Search search;

   public boolean isRunning() {
      return this.running;
   }

   public Searcher(FileSystemModel var1, Search var2, Closure var3, Closure var4) {
      this.searchType = Searcher.SearchType.GLOB;
      this.regexPattern = null;
      this.onFinished = var3;
      this.model = var1;
      this.addNode = var4;
      this.search = var2;
   }

   public void stop() {
      this.stop = true;
   }

   public void run() {
      this.running = true;
      int var1 = 0;

      try {
         String[] var2 = new String[]{"select * From ", "File LEFT OUTER JOIN Directory ON File.FileId = Directory.DirId ", "LEFT OUTER JOIN Drive ON Directory.DirId = Drive.DriveId ", "Where File.Parent IN (", "", ") AND (", "", ")"};
         String[] var3 = new String[]{"select FileId, DirId From ", "Directory INNER JOIN FILE ON File.FileId = Directory.DirId ", "Where File.Parent IN (", "", ") AND Directory.DirId NOTNULL"};
         ArrayList var4 = new ArrayList();
         Vector var5 = new Vector();
         if (this.searchRoot == null) {
            var5.add(FileSystemModel.ROOT);
         } else {
            var5.add(this.searchRoot.getId());
         }

         this.searchRoot = null;
         StringBuilder var6 = new StringBuilder();
         int var7 = 1;
         if (this.name != null) {
            if (var6.length() > 0) {
               var6.append(" AND ");
            }

            switch(this.searchType) {
            case GLOB:
               if (this.isCaseSensitive) {
                  var6.append(String.format("File.%s Glob ?", FileObjectFields.File_Name.getName()));
                  var4.add(new Searcher.SetString(var7, this.name));
               } else {
                  var6.append(String.format("lower(File.%s) Glob ?", FileObjectFields.File_Name.getName()));
                  var4.add(new Searcher.SetString(var7, this.name.toLowerCase()));
               }

               ++var7;
               break;
            case REGEX:
               var6.append("1=1");
            }
         }

         if (this.minimumSize != -1L) {
            if (var6.length() > 0) {
               var6.append(" AND ");
            }

            var6.append(String.format("File.%s >= ?", FileObjectFields.File_Size.getName()));
            var4.add(new Searcher.SetLong(var7, this.minimumSize));
            ++var7;
         }

         if (this.maximumSize != -1L) {
            if (var6.length() > 0) {
               var6.append(" AND ");
            }

            var6.append(String.format("File.%s <= ?", FileObjectFields.File_Size.getName()));
            var4.add(new Searcher.SetLong(var7, this.maximumSize));
            ++var7;
         }

         if (this.lastDays != -1) {
            if (var6.length() > 0) {
               var6.append(" AND ");
            }

            switch(this.dateType) {
            case Accessed:
               var6.append(String.format("File.%s >= ?", FileObjectFields.File_Accessed.getName()));
               break;
            case Created:
               var6.append(String.format("File.%s >= ?", FileObjectFields.File_Created.getName()));
               break;
            case Modified:
            default:
               var6.append(String.format("File.%s >= ?", FileObjectFields.File_Modified.getName()));
            }

            Calendar var8 = Calendar.getInstance();
            var8.add(5, -this.lastDays);
            var4.add(new Searcher.SetCalendar(var7++, var8));
         }

         if (this.spanStart != null && this.spanStop != null) {
            if (var6.length() > 0) {
               var6.append(" AND ");
            }

            switch(this.dateType) {
            case Accessed:
               var6.append(String.format("File.%s ", FileObjectFields.File_Accessed.getName()));
               break;
            case Created:
               var6.append(String.format("File.%s ", FileObjectFields.File_Created.getName()));
               break;
            case Modified:
            default:
               var6.append(String.format("File.%s ", FileObjectFields.File_Modified.getName()));
            }

            var6.append("BETWEEN ? AND ?");
            var4.add(new Searcher.SetCalendar(var7++, this.spanStart));
            var4.add(new Searcher.SetCalendar(var7++, this.spanStop));
         }

         if (var6.length() == 0) {
            var6.append("1=1");
         }

         var2[6] = var6.toString();

         while(!var5.isEmpty() && !this.stop) {
            List var23 = var5.subList(0, Math.min(var5.size(), 100));
            var1 += var23.size();
            StringBuilder var9 = new StringBuilder();

            Long var11;
            for(Iterator var10 = var23.iterator(); var10.hasNext(); var9.append(var11)) {
               var11 = (Long)var10.next();
               if (var9.length() > 0) {
                  var9.append(", ");
               }
            }

            var2[4] = var9.toString();
            var3[3] = var9.toString();
            StringBuilder var24 = new StringBuilder();
            String[] var25 = var2;
            int var12 = var2.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               String var14 = var25[var13];
               var24.append(var14);
            }

            PreparedStatement var26 = this.model.prepareStatement(var24.toString());
            Iterator var27 = var4.iterator();

            while(var27.hasNext()) {
               Closure var29 = (Closure)var27.next();
               var29.execute(var26);
            }

            List var28 = this.model.getArbitraryQuery(var26);
            Iterator var30 = var28.iterator();

            FileObject var32;
            while(var30.hasNext()) {
               var32 = (FileObject)var30.next();
               if (this.stop) {
                  break;
               }

               if (this.regexPattern != null) {
                  String var15 = var32.getName();
                  if (!this.isCaseSensitive) {
                     var15 = var15.toLowerCase();
                  }

                  if (!this.regexPattern.matcher(var15).matches()) {
                     continue;
                  }
               }

               if (this.mimeType != null) {
                  boolean var34 = false;
                  String[] var16 = this.mimeType.getSuffixes();
                  int var17 = var16.length;

                  for(int var18 = 0; var18 < var17; ++var18) {
                     String var19 = var16[var18];
                     if (var32.getName().toLowerCase().endsWith(var19)) {
                        var34 = true;
                        break;
                     }
                  }

                  if (!var34) {
                     continue;
                  }
               }

               this.addNode.execute(var32);
            }

            this.search.setStatus(String.format("%d directories searched", var1));
            var24 = new StringBuilder();
            String[] var31 = var3;
            int var33 = var3.length;

            for(int var36 = 0; var36 < var33; ++var36) {
               String var35 = var31[var36];
               var24.append(var35);
            }

            var26 = this.model.prepareStatement(var24.toString());
            var23.clear();
            var28 = this.model.getArbitraryQuery(var26);
            var30 = var28.iterator();

            while(var30.hasNext()) {
               var32 = (FileObject)var30.next();
               if (this.stop) {
                  break;
               }

               if (var32.isDirectory()) {
                  var5.add(var32.getId());
               }
            }
         }
      } finally {
         this.search.setStatus(String.format("Search complete.  %d directories search", var1));
         if (this.onFinished != null) {
            this.onFinished.execute((Object)null);
         }

         this.running = false;
      }

   }

   private class SetCalendar extends Searcher.SetParameter {
      Calendar cal;

      public SetCalendar(int var2, Calendar var3) {
         super(var2);
         this.cal = var3;
      }

      protected void execute(PreparedStatement var1) throws SQLException {
         var1.setTimestamp(this.index, new Timestamp(this.cal.getTimeInMillis()), this.cal);
      }
   }

   private class SetLong extends Searcher.SetParameter {
      long l;

      public SetLong(int var2, long var3) {
         super(var2);
         this.l = var3;
      }

      protected void execute(PreparedStatement var1) throws SQLException {
         var1.setLong(this.index, this.l);
      }
   }

   private class SetString extends Searcher.SetParameter {
      String s;

      public SetString(int var2, String var3) {
         super(var2);
         this.s = var3;
      }

      protected void execute(PreparedStatement var1) throws SQLException {
         var1.setString(this.index, this.s);
      }
   }

   private abstract class SetParameter implements Closure {
      int index;

      protected SetParameter(int var2) {
         this.index = var2;
      }

      public final void execute(Object var1) {
         try {
            this.execute((PreparedStatement)PreparedStatement.class.cast(var1));
         } catch (SQLException var3) {
            var3.printStackTrace();
         }

      }

      protected abstract void execute(PreparedStatement var1) throws SQLException;
   }

   public static enum SearchType {
      GLOB("Glob"),
      REGEX("Regular Expression");

      String type;

      private SearchType(String var3) {
         this.type = var3;
      }

      public String toString() {
         return this.type;
      }
   }
}
