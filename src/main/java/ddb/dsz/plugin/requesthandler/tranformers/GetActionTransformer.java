package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ActionType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ExecutionMethodType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ObjectFactory;
import ddb.util.JaxbCache;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Transformer;

public class GetActionTransformer implements Transformer {
   public static final Object lock = new Object();
   private static GetActionTransformer instance = null;
   protected String file;
   JAXBContext context = null;
   final Unmarshaller unmarsh;
   SoftReference<ExecutionMethodType> emtRef = null;

   public static final GetActionTransformer getInstance() {
      synchronized(lock) {
         if (instance == null) {
            instance = new GetActionTransformer("/RequestHandler/execution.xml");
         }
      }

      return instance;
   }

   private GetActionTransformer(String var1) {
      this.file = var1;
      Unmarshaller var2 = null;

      try {
         this.context = JaxbCache.getContext(ObjectFactory.class);
         var2 = this.context.createUnmarshaller();
      } catch (Exception var4) {
      }

      this.unmarsh = var2;
   }

   @Override
   public final ActionType transform(Object input) {
      if (input instanceof String && this.context != null && this.unmarsh != null) {
         ExecutionMethodType var2 = this.getExecutionMethod();
         if (var2 == null) {
            return null;
         } else {
            String var3 = (String)String.class.cast(input);
            Iterator var4 = var2.getAction().iterator();

            ActionType var5;
            do {
               if (!var4.hasNext()) {
                  return null;
               }

               var5 = (ActionType)var4.next();
            } while(!var5.getKey().equals(var3));

            return var5;
         }
      } else {
         return null;
      }
   }

   private ExecutionMethodType getExecutionMethod() {
      ExecutionMethodType var1 = null;
      if (this.emtRef != null) {
         var1 = (ExecutionMethodType)this.emtRef.get();
      }

      if (var1 == null) {
         URL var2 = GetActionTransformer.class.getResource(this.file);
         Object var3;
         synchronized(this.unmarsh) {
            try {
               var3 = this.unmarsh.unmarshal(var2);
            } catch (Exception var7) {
               var7.printStackTrace();
               return null;
            }
         }

         if (var3 instanceof JAXBElement) {
            var3 = ((JAXBElement)JAXBElement.class.cast(var3)).getValue();
         }

         if (var3 instanceof ExecutionMethodType) {
            var1 = (ExecutionMethodType)ExecutionMethodType.class.cast(var3);
            this.emtRef = new SoftReference(var1);
         }
      }

      return var1;
   }

   public String getTerminalPath() {
      ExecutionMethodType var1 = this.getExecutionMethod();
      return var1 != null ? var1.getTerminalPath() : null;
   }
}
