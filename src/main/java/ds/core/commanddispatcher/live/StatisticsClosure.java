package ds.core.commanddispatcher.live;

import ddb.dsz.core.connection.events.StatisticsEvent;
import ds.core.commanddispatcher.LiveCommandDispatcher;
import ds.jaxb.ipc.Message;
import ds.jaxb.ipc.StatisticsType;
import java.util.Iterator;
import java.util.logging.Level;

public class StatisticsClosure extends MessageClosure {
   public StatisticsClosure(LiveCommandDispatcher var1) {
      super(var1);
   }

   @Override
   protected boolean evaluateMessage(Message message) {
      if (message.getRes() == null) {
         return false;
      } else {
         StatisticsType var2 = message.getRes().getStatistics();
         return var2 != null;
      }
   }

   @Override
   protected void handleMessage(Message message) {
      StatisticsType var2 = message.getRes().getStatistics();
      if (this.live.getMainSystem().isDebugMode()) {
         this.live.getMainSystem().logEvent(Level.FINEST, "Statistics received");
      }

      StatisticsEvent var3 = new StatisticsEvent(this.live, message.getRes().getReqId());
      Iterator var4 = var2.getHost().iterator();

      while(var4.hasNext()) {
         StatisticsType.Host var5 = (StatisticsType.Host)var4.next();
         var3.addHost(var5.getAddress(), var5.getSent(), var5.getReceived());
      }

      this.live.getMainSystem().updateStatistics(var3);
   }
}
