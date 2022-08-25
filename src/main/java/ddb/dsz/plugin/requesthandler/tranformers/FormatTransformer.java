package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.Argument;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.Format;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import java.util.Iterator;
import org.apache.commons.collections.Transformer;

public abstract class FormatTransformer implements Transformer {
   public final String format(RequestedOperation requestedOperation, Format format) {
      if (requestedOperation != null && format != null) {
         String useralias = requestedOperation.getData("useralias");
         String prefix = format.getPrefix();
         String postfix = format.getPostfix();
         StringBuilder stringBuilder = new StringBuilder();
         if (prefix == null) {
            prefix = "";
         }

         if (useralias != null) {
            String var7 = this.getUserAliasFormat();
            if (format != null) {
               prefix = String.format("%s %s", String.format(var7, useralias), prefix);
            }
         }

         if (postfix == null) {
            postfix = "";
         }

         Iterator iterator = format.getArgument().iterator();

         while(iterator.hasNext()) {
            Argument var8 = (Argument)iterator.next();
            boolean var9 = true;

            try {
               Iterator var10 = var8.getRequires().iterator();

               String var11;
               while(var10.hasNext()) {
                  var11 = (String)var10.next();
                  if (requestedOperation.getData(var11) == null) {
                     var9 = false;
                  }
               }

               var10 = var8.getForbids().iterator();

               while(var10.hasNext()) {
                  var11 = (String)var10.next();
                  if (requestedOperation.getData(var11) != null) {
                     var9 = false;
                  }
               }

               if (var9) {
                  String var20 = var8.getString();
                  int var21 = 0;

                  while(var21 < var20.length()) {
                     int var12 = var20.indexOf("%", var21);
                     int var13 = var20.indexOf("%", var12 + 1);
                     if (var12 == -1 || var13 == -1) {
                        break;
                     }

                     if (var12 + 1 >= var13) {
                        var21 = var13 + 1;
                     } else {
                        String var14 = var20.substring(var12 + 1, var13);
                        String var15 = requestedOperation.getData(var14);
                        String var16 = "";
                        String var17 = "";
                        if (var12 > 0) {
                           var16 = var20.substring(0, var12);
                        }

                        if (var20.length() > var13 + 1) {
                           var17 = var20.substring(var13 + 1);
                        }

                        var20 = String.format("%s%s%s", var16, var15, var17);
                        var21 = var12;
                     }
                  }

                  stringBuilder.append(var20);
                  stringBuilder.append(" ");
               }
            } catch (NullPointerException var18) {
               var18.printStackTrace();
            }
         }

         return String.format("%s %s %s", prefix, stringBuilder.toString(), postfix);
      } else {
         return null;
      }
   }

   @Override
   public final String transform(Object input) {
      if (!(input instanceof RequestedOperation)) {
         return "Invalid Object";
      } else {
         RequestedOperation requestedOperation = (RequestedOperation)RequestedOperation.class.cast(input);
         ActionType actionType = GetActionTransformer.getInstance().transform(requestedOperation.getKey());
         return actionType == null ? "Unknown key: " + requestedOperation.getKey() : this.format(requestedOperation, this.getFormat(actionType));
      }
   }

   protected abstract Format getFormat(ActionType actionType);

   protected abstract String getUserAliasFormat();
}
