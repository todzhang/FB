package ds.util.datatransforms;

import ddb.dsz.core.task.TaskDataAccess;
import ddb.util.XmlCache;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class TaskClosure {
   Closure handleError = ClosureUtils.nopClosure();
   protected final List<DataTransformerImpl2> transformers = new Vector();
   private Reference<TransformerFactory> TransformerFactoryRef = new WeakReference((Object)null);
   private Reference<DocumentBuilder> DocBuilder = new WeakReference((Object)null);

   protected TaskClosure(Closure var1) {
      if (var1 != null) {
         this.handleError = var1;
      }

   }

   protected synchronized DocumentBuilder getDocumentBuilder() {
      DocumentBuilder var1 = (DocumentBuilder)this.DocBuilder.get();
      if (var1 != null) {
         return var1;
      } else {
         var1 = XmlCache.getBuilder();
         this.DocBuilder = new SoftReference(var1);
         var1.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException var1) throws SAXException {
               TaskClosure.this.handleError.execute(var1);
            }

            public void error(SAXParseException var1) throws SAXException {
               TaskClosure.this.handleError.execute(var1);
            }

            public void fatalError(SAXParseException var1) throws SAXException {
               TaskClosure.this.handleError.execute(var1);
            }
         });
         return var1;
      }
   }

   protected synchronized TransformerFactory getTransformerFactory() {
      TransformerFactory var1 = (TransformerFactory)this.TransformerFactoryRef.get();
      if (var1 == null) {
         var1 = TransformerFactory.newInstance();
         var1.setErrorListener(new ErrorListener() {
            public void warning(TransformerException var1) throws TransformerException {
               TaskClosure.this.handleError.execute(var1);
            }

            public void error(TransformerException var1) throws TransformerException {
               TaskClosure.this.handleError.execute(var1);
            }

            public void fatalError(TransformerException var1) throws TransformerException {
               TaskClosure.this.handleError.execute(var1);
            }
         });

         try {
            var1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.FALSE);
         } catch (Exception var3) {
         }

         this.TransformerFactoryRef = new SoftReference(var1);
      }

      return var1;
   }

   void registerTransformer(DataTransformerImpl2 var1) {
      synchronized(this.transformers) {
         this.transformers.add(var1);
      }
   }

   void unregisterTransformer(DataTransformerImpl2 var1) {
      synchronized(this.transformers) {
         this.transformers.remove(var1);
      }
   }

   protected Transformer createTransformer(String var1, String var2, String[] var3) {
      return this.createTransformer(new File(var1), var2, var3);
   }

   protected Transformer createTransformer(File var1, String var2, String[] var3) {
      if (var1 != null && var1.exists()) {
         TransformerFactory var4 = this.getTransformerFactory();
         if (var4 == null) {
            return null;
         } else {
            try {
               var4.setURIResolver(new URIResolverImpl(var1.getParentFile(), var2, var3));
               return var4.newTransformer(new StreamSource(var1));
            } catch (Exception var6) {
               this.handleError(var6);
               return null;
            }
         }
      } else {
         return null;
      }
   }

   protected void handleError(Exception var1) {
      if (this.handleError != null) {
         this.handleError.execute(var1);
      }

   }

   protected void requeue(TaskDataAccess var1) {
      Vector var2 = new Vector();
      synchronized(this.transformers) {
         var2.addAll(this.transformers);
      }

      Iterator var3 = this.transformers.iterator();

      while(var3.hasNext()) {
         DataTransformerImpl2 var4 = (DataTransformerImpl2)var3.next();
         var4.requeue(var1);
      }

   }
}
