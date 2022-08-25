package ds.util.datatransforms;

import ddb.dsz.core.data.ClosureOrder;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.util.UtilityConstants;
import ds.core.impl.task.DocumentBlobAccess;
import ds.core.impl.task.TaskStateAccess;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.commons.collections.Closure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DataTransformerImpl2 extends AbstractDataTransformer {
   final int MAX_NODES = 1000;
   private List<DataTransformerImpl2.ClosureInformation> closures = new Vector();
   private static final Executor REQUEUER = Executors.newCachedThreadPool(UtilityConstants.createThreadFactory("DataTransformer Requeue Thread"));

   private DataTransformerImpl2() {
   }

   private DataTransformerImpl2(boolean var1) {
      super(var1);
   }

   private DataTransformerImpl2(String var1) {
      super(var1);
   }

   private DataTransformerImpl2(String var1, boolean var2) {
      super(var1, var2);
   }

   private DataTransformerImpl2(ThreadFactory var1) {
      super(var1);
   }

   private DataTransformerImpl2(ThreadFactory var1, boolean var2) {
      super(var1, var2);
   }

   public void addClosure(Closure var1) {
      this.addClosure(var1, ClosureOrder.MIDDLE);
   }

   public void addClosure(Closure var1, ClosureOrder var2) {
      this.addClosure(var1, var2, (ddb.dsz.core.task.TaskDataAccess.DataType)null);
   }

   public void addClosure(Closure var1, ClosureOrder var2, ddb.dsz.core.task.TaskDataAccess.DataType var3) {
      DataTransformerImpl2.ClosureInformation var4 = new DataTransformerImpl2.ClosureInformation();
      var4.access = var3;
      var4.closure = var1;
      var4.order = var2;
      this.closures.add(var4);
      if (var1 instanceof TaskClosure) {
         ((TaskClosure)TaskClosure.class.cast(var1)).registerTransformer(this);
      }

   }

   public void removeClosure(Closure var1) {
      synchronized(this.TASK_LIST_LOCK) {
         Iterator var3 = this.closures.iterator();

         DataTransformerImpl2.ClosureInformation var4;
         do {
            if (!var3.hasNext()) {
               return;
            }

            var4 = (DataTransformerImpl2.ClosureInformation)var3.next();
         } while(var4.closure != var1);

         this.closures.remove(var4);
         if (var1 instanceof TaskClosure) {
            ((TaskClosure)TaskClosure.class.cast(var1)).unregisterTransformer(this);
         }

      }
   }

   public void removeAllClosures() {
      synchronized(this.TASK_LIST_LOCK) {
         Iterator var2 = this.closures.iterator();

         while(var2.hasNext()) {
            DataTransformerImpl2.ClosureInformation var3 = (DataTransformerImpl2.ClosureInformation)var2.next();
            if (var3.closure instanceof TaskClosure) {
               ((TaskClosure)TaskClosure.class.cast(var3.closure)).unregisterTransformer(this);
            }
         }

         this.closures.clear();
      }
   }

   protected void execute(TaskDataAccess var1) {
      if (!var1.isGenerated() && var1.getSize() > 1048576L) {
         this.enqueChunks(var1);
      } else {
         boolean var2 = false;
         if (!this.execute(var1, ClosureOrder.FIRST)) {
            var2 = true;
         }

         if (!this.execute(var1, ClosureOrder.MIDDLE)) {
            var2 = true;
         }

         if (!this.execute(var1, ClosureOrder.LAST)) {
            var2 = true;
         }

         if (var2) {
            this.enqueChunks(var1);
         }

      }
   }

   private boolean execute(TaskDataAccess var1, ClosureOrder var2) {
      boolean var3 = true;
      Vector var4 = new Vector();
      synchronized(this.TASK_LIST_LOCK) {
         var4.addAll(this.closures);
      }

      Iterator var5 = var4.iterator();

      while(true) {
         DataTransformerImpl2.ClosureInformation var6;
         do {
            do {
               if (!var5.hasNext()) {
                  return var3;
               }

               var6 = (DataTransformerImpl2.ClosureInformation)var5.next();
            } while(!var6.order.equals(var2));
         } while(var6.access != null && !var6.access.equals(var1.getType()));

         try {
            var6.closure.execute(var1);
         } catch (IncompleteDataException var8) {
            var3 = false;
         } catch (Exception var9) {
            this.handleError(var9);
         }
      }
   }

   public void stop() {
      this.removeAllClosures();
      super.stop();
   }

   protected void handleError(String var1) {
      Vector var2 = new Vector();
      synchronized(this.TASK_LIST_LOCK) {
         var2.addAll(this.closures);
      }

      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         DataTransformerImpl2.ClosureInformation var4 = (DataTransformerImpl2.ClosureInformation)var3.next();
         if (var4.order.equals(ClosureOrder.ERROR)) {
            try {
               var4.closure.execute(var1);
            } catch (Exception var6) {
            }
         }
      }

   }

   private void enqueChunks(TaskDataAccess var1) {
      if (!var1.isGenerated()) {
         if (this.isOrderImportant() && var1.getType().equals(ddb.dsz.core.task.TaskDataAccess.DataType.DATA)) {
            this.executeIndirectlyWorker(var1);
         } else {
            REQUEUER.execute(new DataTransformerImpl2.EnqueIndirectly(var1));
         }

      }
   }

   private void executeIndirectlyWorker(final TaskDataAccess var1) {
      if (!(var1 instanceof TaskStateAccess)) {
         if (var1.getTask().getDataName().equalsIgnoreCase("dir") && var1.getType().equals(ddb.dsz.core.task.TaskDataAccess.DataType.DATA)) {
            this.executeIndirectlyWorker_Dir(var1);
         } else {
            try {
               SAXParserFactory var2 = SAXParserFactory.newInstance();
               SAXParser var3 = var2.newSAXParser();
               InputStream var4 = this.createStream(var1);
               if (var4 == null) {
                  this.logger.log(Level.SEVERE, "Unable to open input stream for " + var1.toString());
                  return;
               }

               try {
                  var3.parse(var4, new PassThroughHandler((TransformerHandler)null) {
                     DOMResult result;
                     SAXTransformerFactory tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                     static final String COMMAND_DATA = "CommandData";
                     static final String COMMAND_TASKING = "CommandTasking";
                     static final String DATALOG = "DataLog";
                     String command_block = null;
                     int depth = 0;
                     boolean skip2 = true;

                     public void startElement(String var1x, String var2, String var3, Attributes var4) throws SAXException {
                        if (!this.skip2) {
                           try {
                              if (this.depth == 0) {
                                 this.start(this.command_block);
                                 this.skip = false;
                              }
                           } catch (Exception var7) {
                              var7.printStackTrace();
                           }

                           ++this.depth;
                        }

                        super.startElement(var1x, var2, var3, var4);
                        if (var3.equals("CommandData") || var3.equals("CommandTasking")) {
                           this.command_block = var3;

                           try {
                              this.skip2 = false;
                           } catch (Exception var6) {
                              var6.printStackTrace();
                           }
                        }

                     }

                     public void endElement(String var1x, String var2, String var3) throws SAXException {
                        if (var3.equals("CommandData") || var3.equals("CommandTasking")) {
                           this.skip = true;
                        }

                        super.endElement(var1x, var2, var3);
                        if (!this.skip) {
                           --this.depth;
                           if (this.depth == 0) {
                              this.finish(this.command_block);
                           }
                        }

                     }

                     private void start(String var1x) throws Exception {
                        this.hd = this.tf.newTransformerHandler();
                        this.result = new DOMResult();
                        this.hd.setResult(this.result);
                        this.hd.startDocument();
                        this.hd.startElement("", "", "DataLog", new AttributesImpl());
                        this.hd.startElement("", "", var1x, new AttributesImpl());
                     }

                     private void finish(String var1x) throws SAXException {
                        this.hd.endElement("", "", var1x);
                        this.hd.endElement("", "", "DataLog");
                        this.hd.endDocument();
                        Node var2 = this.result.getNode();
                        if (var2 instanceof Document) {
                           DataTransformerImpl2.this.requeue(new DocumentBlobAccess(var1, (Document)var2));
                        } else {
                           System.err.println("invalid result!");
                        }

                     }
                  });
               } finally {
                  var4.close();
               }
            } catch (SAXException var11) {
               this.handleError(var11);
            } catch (ParserConfigurationException var12) {
               this.handleError(var12);
            } catch (IOException var13) {
               this.handleError(var13);
            }

         }
      }
   }

   private void executeIndirectlyWorker_Dir(final TaskDataAccess var1) {
      try {
         SAXParserFactory var2 = SAXParserFactory.newInstance();
         SAXParser var3 = var2.newSAXParser();
         InputStream var4 = this.createStream(var1);
         if (var4 == null) {
            this.logger.log(Level.SEVERE, "Unable to open input stream for " + var1.toString());
            return;
         }

         try {
            var3.parse(var4, new PassThroughHandler((TransformerHandler)null) {
               int count;
               DOMResult result;
               Attributes directoryAttr;
               Attributes directoriesAttr;
               final SAXTransformerFactory tf;
               static final String DIRECTORIES = "Directories";
               static final String DIRECTORY = "Directory";
               static final String COMMAND_DATA = "CommandData";
               static final String COMMAND_TASKING = "CommandTasking";
               static final String DATALOG = "DataLog";

               {
                  this.skip = true;
                  this.count = 0;
                  this.tf = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
               }

               public void startElement(String var1x, String var2, String var3, Attributes var4) throws SAXException {
                  super.startElement(var1x, var2, var3, var4);
                  if (!var3.equals("CommandData") && !var3.equals("CommandTasking")) {
                     if (var3.equals("Directories")) {
                        this.directoriesAttr = new AttributesImpl(var4);
                     } else if (var3.equals("Directory")) {
                        this.directoryAttr = new AttributesImpl(var4);
                     }
                  } else {
                     try {
                        this.start(var3);
                        this.skip = false;
                     } catch (Exception var6) {
                        var6.printStackTrace();
                     }
                  }

               }

               public void endElement(String var1x, String var2, String var3) throws SAXException {
                  if (var3.equals("CommandData") || var3.equals("CommandTasking")) {
                     this.skip = true;
                     this.finish(var3);
                  }

                  super.endElement(var1x, var2, var3);
                  if (var3.equals("File")) {
                     if (this.count > 1000) {
                        this.count = 0;

                        try {
                           this.hd.endElement("", "", "Directory");
                           this.hd.endElement("", "", "Directories");
                           this.finish("CommandData");
                           this.start("CommandData");
                           this.hd.startElement("", "", "Directories", this.directoriesAttr);
                           this.hd.startElement("", "", "Directory", this.directoryAttr);
                        } catch (Exception var5) {
                           var5.printStackTrace();
                        }
                     } else {
                        ++this.count;
                     }
                  }

               }

               private void start(String var1x) throws Exception {
                  this.hd = this.tf.newTransformerHandler();
                  this.result = new DOMResult();
                  this.hd.setResult(this.result);
                  this.hd.startDocument();
                  this.hd.startElement("", "", "DataLog", new AttributesImpl());
                  this.hd.startElement("", "", var1x, new AttributesImpl());
               }

               private void finish(String var1x) throws SAXException {
                  this.hd.endElement("", "", var1x);
                  this.hd.endElement("", "", "DataLog");
                  this.hd.endDocument();
                  Node var2 = this.result.getNode();
                  if (var2 instanceof Document) {
                     DataTransformerImpl2.this.requeue(new DocumentBlobAccess(var1, (Document)var2));
                  } else {
                     System.err.println("invalid result!");
                  }

               }
            });
         } finally {
            var4.close();
         }
      } catch (SAXException var11) {
         this.handleError(var11);
      } catch (ParserConfigurationException var12) {
         this.handleError(var12);
      } catch (IOException var13) {
         this.handleError(var13);
      }

   }

   private InputStream createStream(TaskDataAccess var1) throws IOException {
      return var1.getStream();
   }

   private class ClosureInformation {
      ClosureOrder order;
      ddb.dsz.core.task.TaskDataAccess.DataType access;
      Closure closure;

      private ClosureInformation() {
      }

      // $FF: synthetic method
      ClosureInformation(Object var2) {
         this();
      }
   }

   private class EnqueIndirectly implements Runnable {
      TaskDataAccess tda;

      public EnqueIndirectly(TaskDataAccess var2) {
         this.tda = var2;
      }

      public void run() {
         DataTransformerImpl2.this.executeIndirectlyWorker(this.tda);
      }
   }
}
