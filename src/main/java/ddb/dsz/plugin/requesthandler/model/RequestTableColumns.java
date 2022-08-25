package ddb.dsz.plugin.requesthandler.model;

import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;

public enum RequestTableColumns {
   STATUS("Status", RequestStatus.class),
   DESCRIPTION("Description", RequestedOperation.class),
   SCOPE("Scope", RequestedOperation.class),
   SOURCE("Source", String.class),
   HOST("Host", String.class);

   String name;
   Class<?> type;

   private RequestTableColumns(String name, Class<?> type) {
      this.name = name;
      this.type = type;
   }

   public String getName() {
      return this.name;
   }

   public Class<?> getType() {
      return this.type;
   }

   @Override
   public String toString() {
      return this.getName();
   }
}
