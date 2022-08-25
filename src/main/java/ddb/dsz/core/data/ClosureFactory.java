package ddb.dsz.core.data;

import ddb.Factory;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import org.apache.commons.collections.Closure;

public abstract class ClosureFactory extends Factory {
   public static final String METADATA_CLOSURE = "CommandMetaData.impl";
   public static final String VARIABLE_CLOSURE = "VariableClosure.impl";
   public static final String DISPLAY_CLOSURE = "DisplayClosure.impl";
   public static final String XML_STRING_CLOSURE = "XmlStringClosure.impl";
   private static final Class<?>[] TaskArguments = new Class[]{CoreController.class, Task.class, Closure.class, Closure.class};
   private static final Class<?>[] StringArguments = new Class[]{CoreController.class, String.class, String.class, Closure.class, Closure.class};

   public static Closure newVariableClosure(CoreController coreController, String var1, String var2, Closure closure) {
      return newVariableClosure(coreController, var1, var2, closure, null);
   }

   public static Closure newVariableClosure(CoreController coreController, String var1, String var2, Closure var3, Closure var4) {
      return (Closure)Factory.newObject(System.getProperty(VARIABLE_CLOSURE), StringArguments, coreController, var1, var2, var3, var4);
   }

   public static Closure newVariableClosure(CoreController coreController, Task task, Closure closure) {
      return newVariableClosure(coreController, task, closure, null);
   }

   public static Closure newVariableClosure(CoreController coreController, Task task, Closure var2, Closure var3) {
      return (Closure)Factory.newObject(System.getProperty(VARIABLE_CLOSURE), TaskArguments, coreController, task, var2, var3);
   }

   public static Closure newCommandMetadataClosure(CoreController coreController, String var1, String var2, Closure var3) {
      return newCommandMetadataClosure(coreController, var1, var2, var3, null);
   }

   public static Closure newCommandMetadataClosure(CoreController coreController, String var1, String var2, Closure var3, Closure var4) {
      return (Closure)Factory.newObject(System.getProperty(METADATA_CLOSURE), StringArguments, coreController, var1, var2, var3, var4);
   }

   public static Closure newCommandMetadataClosure(CoreController coreController, Task task, Closure closure) {
      return newCommandMetadataClosure(coreController, task, closure, null);
   }

   public static Closure newCommandMetadataClosure(CoreController coreController, Task task, Closure var2, Closure var3) {
      return (Closure)Factory.newObject(System.getProperty(METADATA_CLOSURE), TaskArguments, coreController, task, var2, var3);
   }

   public static Closure newDisplayClosure(CoreController coreController, String var1, String var2, Closure var3) {
      return newDisplayClosure(coreController, var1, var2, var3, null);
   }

   public static Closure newDisplayClosure(CoreController coreController, String var1, String var2, Closure var3, Closure var4) {
      return (Closure)Factory.newObject(System.getProperty(DISPLAY_CLOSURE), StringArguments, coreController, var1, var2, var3, var4);
   }

   public static Closure newDisplayClosure(CoreController coreController, Task task, Closure closure) {
      return newDisplayClosure(coreController, task, closure, null);
   }

   public static Closure newDisplayClosure(CoreController coreController, Task task, Closure var2, Closure var3) {
      return (Closure)Factory.newObject(System.getProperty(DISPLAY_CLOSURE), TaskArguments, coreController, task, var2, var3);
   }

   public static Closure newXmlStringClosure(CoreController coreController, String var1, String var2, Closure var3) {
      return newXmlStringClosure(coreController, var1, var2, var3, null);
   }

   public static Closure newXmlStringClosure(CoreController coreController, String var1, String var2, Closure var3, Closure var4) {
      return (Closure)Factory.newObject(System.getProperty(XML_STRING_CLOSURE), StringArguments, coreController, var1, var2, var3, var4);
   }

   public static Closure newXmlStringClosure(CoreController coreController, Task task, Closure closure) {
      return newXmlStringClosure(coreController, task, closure, null);
   }

   public static Closure newXmlStringClosure(CoreController coreController, Task task, Closure var2, Closure var3) {
      return (Closure)Factory.newObject(System.getProperty(XML_STRING_CLOSURE), TaskArguments, coreController, task, var2, var3);
   }
}
