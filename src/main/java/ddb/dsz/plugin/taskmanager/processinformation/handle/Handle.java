package ddb.dsz.plugin.taskmanager.processinformation.handle;

import ddb.dsz.core.data.ObjectValue;

public class Handle {
   long id;
   Handle.HandleType type = null;
   String metaData;

   public Handle(ObjectValue handle) {
      this.id = handle.getInteger("id");
      this.metaData = handle.getString("metadata");
      String typeStr = handle.getString("type");
      Handle.HandleType[] arr$ = Handle.HandleType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Handle.HandleType ht = arr$[i$];
         if (ht.str.equalsIgnoreCase(typeStr)) {
            this.type = ht;
            break;
         }
      }

      if (this.type == null) {
         System.err.printf("Unable to parse type '%s'\n", typeStr);
      }

   }

   public Handle(long id, int handleTypeIndex, String metaData) {
      this.id = id;
      this.metaData = metaData;
      this.type = Handle.HandleType.values()[handleTypeIndex];
   }

   public long getId() {
      return this.id;
   }

   public String getMetaData() {
      return this.metaData;
   }

   public Handle.HandleType getType() {
      return this.type;
   }

   public static enum HandleType {
      Key("Key"),
      File("File"),
      Token("Token"),
      TmRm("TmRm"),
      TmTm("TmTm"),
      Thread("Thread"),
      Directory("Directory"),
      Event("Event"),
      Session("Session"),
      Mutant("Mutant"),
      Semaphore("Semaphore"),
      IoCompletion("IoCompletion"),
      Section("Section"),
      Timer("Timer"),
      WindowStation("WindowStation"),
      TpWorkerFactory("TpWorkerFactory"),
      KeyedEvent("KeyedEvent"),
      Desktop("Desktop"),
      Process("Process"),
      SymbolicLink("SymbolicLink"),
      WmiGuid("WmiGuid"),
      Job("Job"),
      EtwRegistration("EtwRegistration");

      private final String str;

      private HandleType(String str) {
         this.str = str;
      }

      public String getType() {
         return this.str;
      }
   }
}
