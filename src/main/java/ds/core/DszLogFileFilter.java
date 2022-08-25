package ds.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class DszLogFileFilter implements FilenameFilter {
   public static final Pattern PATTERN = Pattern.compile("0000.*\\.[xX][mM][lL]");

   @Override
   public boolean accept(File dir, String name) {
      return PATTERN.matcher(name).matches();
   }
}
