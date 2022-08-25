package ddb.dsz.plugin.screenshot;

import java.io.File;
import java.util.Calendar;

public class ImageData {
   public final File file;
   public final long time;
   private boolean viewed = false;

   public ImageData(File var1, Calendar var2) {
      this.file = var1;
      this.time = var2.getTimeInMillis();
   }

   public boolean isViewed() {
      return this.viewed;
   }

   public void setViewed() {
      this.viewed = true;
   }
}
