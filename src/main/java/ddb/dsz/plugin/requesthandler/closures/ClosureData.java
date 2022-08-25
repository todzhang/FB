package ddb.dsz.plugin.requesthandler.closures;

import ddb.dsz.plugin.peer.PeerTag;
import javax.xml.bind.JAXBElement;

public class ClosureData {
   private Object data;
   private PeerTag tag;

   public ClosureData(Object var1, PeerTag var2) {
      this.tag = var2;
      if (var1 instanceof JAXBElement) {
         var1 = ((JAXBElement)JAXBElement.class.cast(var1)).getValue();
      }

      this.data = var1;
   }

   public final Object getData() {
      return this.data;
   }

   public final PeerTag getTag() {
      return this.tag;
   }
}
