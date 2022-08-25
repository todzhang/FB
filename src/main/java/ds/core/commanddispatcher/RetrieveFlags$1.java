package ds.core.commanddispatcher;

import ddb.util.FinishedProcessingException;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class RetrieveFlags$1 extends DefaultHandler {
   boolean inFlags;
   // $FF: synthetic field
   final List val$prefixes;
   // $FF: synthetic field
   final RetrieveFlags this$0;

   RetrieveFlags$1(RetrieveFlags var1, List var2) {
      this.this$0 = var1;
      this.val$prefixes = var2;
      this.inFlags = false;
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      super.startElement(var1, var2, var3, var4);
      if (this.inFlags && var3.equals("CommandFlagBackground")) {
         this.val$prefixes.add("background");
      }

      if (var3.equals("Flags")) {
         this.inFlags = true;
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      super.endElement(var1, var2, var3);
      if (this.inFlags && var3.equals("Flags")) {
         throw FinishedProcessingException.FINISHED;
      }
   }
}
