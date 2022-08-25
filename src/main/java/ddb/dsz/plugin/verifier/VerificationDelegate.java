package ddb.dsz.plugin.verifier;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.util.UtilityConstants;
import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

public class VerificationDelegate {
   static final ScheduledExecutorService exec = UtilityConstants.createScheduledExecutorService(10, "VerificationDelegate");
   final Task task;
   final TaskDataAccess access;
   final CoreController core;
   boolean issueOccurred = false;
   boolean shouldRepeat = false;
   StringBuilder log = new StringBuilder();

   public VerificationDelegate(Task var1, TaskDataAccess var2, CoreController var3) {
      this.task = var1;
      this.access = var2;
      this.core = var3;
   }

   public void verify() {
      String var2 = "Commands//Schemas";
      File var1 = new File(String.format("%s/%sTest/%s/%s.xsd", this.core.getResourceDirectory(), this.task.getResourceDirectory(), var2, this.task.getCommandName()));
      if (!var1.exists()) {
         this.log.append(String.format("File (%s) does not exist", var1.getAbsolutePath()));
         this.issueOccurred = true;
      } else {
         VerificationDelegate.ErrorHandlerImpl var3 = new VerificationDelegate.ErrorHandlerImpl("Schema");

         try {
            SchemaFactory var4 = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            var4.setErrorHandler(var3);
            var4.setResourceResolver(new VerificationDelegate.LSResourceResolverImpl(this.core.getResourceDirectory(), var2, new String[]{this.task.getResourceDirectory(), String.format("%sTest", this.core.getDefaultPackage())}));
            Schema var9 = var4.newSchema(var1);
            SAXParserFactory var6 = SAXParserFactory.newInstance();
            var6.setNamespaceAware(true);
            var6.setSchema(var9);
            SAXParser var7 = var6.newSAXParser();
            var7.parse(new InputSource(this.access.getReader()), new VerificationDelegate.ErrorHandlerImpl(this.access.toString()));
         } catch (Exception var8) {
            String var5 = var8.getMessage();
            this.log.append(var5 + "\n");
            if (var5.equals("XML document structures must start and end within the same entity.")) {
               this.log.append("*******************************************************************\nThis error has been known to occur in rare occasions where the data\nfile has not been flushed to disk.  If it is the first time you are\nseeing it, reverify the command (right-click on the command, select\n'Reverify'; or click the Revefify button on the bottom right).  If\nthe command still has the issue, you have a problem.  Otherwise,\nyou are fine.\n*******************************************************************\n");
               this.shouldRepeat = true;
            }

            this.issueOccurred = true;
         }

      }
   }

   public boolean isValid() {
      return !this.issueOccurred;
   }

   public String getDetails() {
      return this.log.toString();
   }

   public boolean shouldRepeat() {
      return this.shouldRepeat;
   }

   private class ErrorHandlerImpl extends DefaultHandler2 implements ErrorHandler {
      String obj;

      private ErrorHandlerImpl(String var2) {
         this.obj = var2;
      }

      private void printMessage(String var1, SAXParseException var2) {
         VerificationDelegate.this.issueOccurred = true;
         StringBuilder var3 = new StringBuilder();
         var3.append(var1);
         var3.append(":  ");
         var3.append(var2.getLocalizedMessage());
         var3.append("\n");
         var3.append("\t");
         var3.append(this.obj);
         var3.append(" (line ");
         var3.append(var2.getLineNumber());
         var3.append(", column ");
         var3.append(var2.getColumnNumber());
         var3.append(")\n\n");
         this.append(var3.toString());
      }

      private void append(String var1) {
         VerificationDelegate.this.log.append(var1);
      }

      public void warning(SAXParseException var1) throws SAXException {
         this.printMessage("Warning", var1);
      }

      public void error(SAXParseException var1) throws SAXException {
         this.printMessage("Error", var1);
         throw var1;
      }

      public void fatalError(SAXParseException var1) throws SAXException {
         this.printMessage("Fatal Error", var1);
         throw var1;
      }

      // $FF: synthetic method
      ErrorHandlerImpl(String var2, Object var3) {
         this(var2);
      }
   }

   private static class LSResourceResolverImpl implements LSResourceResolver {
      String resourceDir;
      String suffix;
      String[] paths;

      public LSResourceResolverImpl(String var1, String var2, String... var3) {
         this.resourceDir = var1;
         this.suffix = var2;
         this.paths = var3;
      }

      public LSInput resolveResource(String var1, String var2, String var3, String var4, String var5) {
         String[] var6 = this.paths;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            File var10 = new File(String.format("%s/%s/%s/%s", this.resourceDir, var9, this.suffix, var4));
            if (var10.exists()) {
               return new SchemaResource(var10);
            }
         }

         return null;
      }
   }
}
