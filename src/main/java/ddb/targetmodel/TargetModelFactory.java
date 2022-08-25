package ddb.targetmodel;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public final class TargetModelFactory {
   private static final TargetModelFactory Instance = new TargetModelFactory();
   private final List<TargetModel> CreatedModels = new Vector();

   private TargetModelFactory() {
   }

   public static TargetModel getTargetModel(CoreController var0, HostInfo var1) {
      return Instance.getTargetModelImpl(var0, var1);
   }

   public synchronized TargetModel getTargetModelImpl(CoreController var1, HostInfo var2) {
      if (var2 == null) {
         return null;
      } else {
         Iterator var3 = this.CreatedModels.iterator();

         TargetModel var4;
         do {
            if (!var3.hasNext()) {
               TargetModel var5 = new TargetModel(var1, var2);
               this.CreatedModels.add(var5);
               return var5;
            }

            var4 = (TargetModel)var3.next();
         } while(!var4.getHost().sameHost(var2));

         return var4;
      }
   }

   public static void main(String[] var0) throws Throwable {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", var0.getClass());
      var3.invoke((Object)null, var0);
   }
}
