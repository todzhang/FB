package ddb.writequeue.text;

import ddb.writequeue.AbstractWritable;
import ddb.writequeue.Writable;

public class TextWrite extends AbstractWritable {
   protected StringBuilder sb = new StringBuilder();

   public TextWrite(String var1) {
      this.sb.append(var1);
   }

   public boolean combine(Writable var1) {
      if (var1 instanceof TextWrite) {
         TextWrite var2 = (TextWrite)var1;
         this.sb.append(var2.getText());
         return true;
      } else {
         return false;
      }
   }

   public String getText() {
      return this.sb.toString();
   }
}
