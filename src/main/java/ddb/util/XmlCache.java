package ddb.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlCache {
   private static final XmlCache CACHE = new XmlCache();
   private final Object DocumentFactoryLock = new Object();
   private Reference<DocumentBuilderFactory> FactoryRef = new WeakReference((Object)null);
   private final Queue<Reference<DocumentBuilder>> BUILDERS_REF = new LinkedList();
   private final int MAXIMUM_BUILDERS = 5;
   private final Object ParserFactoryLock = new Object();
   private final Queue<Reference<SAXParser>> PARSERS = new LinkedList();
   private Reference<SAXParserFactory> FACTORY = new WeakReference((Object)null);
   private Reference<ErrorHandler> ERRORS = new WeakReference((Object)null);
   private final int MAX_PARSERS = 5;

   public static SAXParser getSAXParser() {
      return CACHE.getSAXParserImpl();
   }

   public static final void releaseParser(SAXParser parser) {
      CACHE.releaseParserImpl(parser);
   }

   public static DocumentBuilder getBuilder() {
      return CACHE.getBuilderImpl();
   }

   public static void releaseBuilder(DocumentBuilder builder) {
      CACHE.releaseBuilderImpl(builder);
   }

   private XmlCache() {
   }

   private synchronized ErrorHandler getHandler() {
      ErrorHandler err = (ErrorHandler)this.ERRORS.get();
      if (err != null) {
         return err;
      } else {
         err = new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
            }
         };
         this.ERRORS = new WeakReference(err);
         return err;
      }
   }

   private void releaseBuilderImpl(DocumentBuilder builder) {
      if (builder != null) {
         synchronized(this.BUILDERS_REF) {
            if (this.BUILDERS_REF.size() < 5) {
               this.BUILDERS_REF.offer(new SoftReference(builder));
            } else {
               this.BUILDERS_REF.offer(new WeakReference(builder));
            }

         }
      }
   }

   private DocumentBuilder getBuilderImpl() {
      for(int i = 0; i < 10; ++i) {
         DocumentBuilder docBuild;
         synchronized(this.BUILDERS_REF) {
            while(this.BUILDERS_REF.size() > 0) {
               Reference<DocumentBuilder> ref = (Reference)this.BUILDERS_REF.poll();
               docBuild = (DocumentBuilder)ref.get();
               if (docBuild != null) {
                  return docBuild;
               }
            }
         }

         synchronized(this.DocumentFactoryLock) {
            DocumentBuilderFactory factory = (DocumentBuilderFactory)this.FactoryRef.get();
            if (factory == null) {
               factory = DocumentBuilderFactory.newInstance();
               this.FactoryRef = new SoftReference(factory);
            }

            DocumentBuilder var10000;
            try {
               docBuild = factory.newDocumentBuilder();
               docBuild.setErrorHandler(this.getHandler());
               var10000 = docBuild;
            } catch (Exception var7) {
               var7.printStackTrace();
               continue;
            }

            return var10000;
         }
      }

      return null;
   }

   private void releaseParserImpl(SAXParser parser) {
      if (parser != null) {
         synchronized(this.PARSERS) {
            if (this.PARSERS.size() < 5) {
               this.PARSERS.offer(new SoftReference(parser));
            } else {
               this.PARSERS.offer(new WeakReference(parser));
            }

         }
      }
   }

   private SAXParser getSAXParserImpl() {
      SAXParser parser;
      synchronized(this.PARSERS) {
         while(!this.PARSERS.isEmpty()) {
            Reference<SAXParser> parserRef = (Reference)this.PARSERS.poll();
            if (parserRef != null) {
               parser = (SAXParser)parserRef.get();
               if (parser != null) {
                  parser.reset();
                  return parser;
               }
            }
         }
      }

      synchronized(this.ParserFactoryLock) {
         SAXParser var10000;
         try {
            SAXParserFactory factory = (SAXParserFactory)this.FACTORY.get();
            if (factory == null) {
               factory = SAXParserFactory.newInstance();
               if (factory == null) {
                  var10000 = null;
                  return var10000;
               }

               this.FACTORY = new SoftReference(factory);
            }

            parser = factory.newSAXParser();
            var10000 = parser;
         } catch (Exception var6) {
            return null;
         }

         return var10000;
      }
   }
}
