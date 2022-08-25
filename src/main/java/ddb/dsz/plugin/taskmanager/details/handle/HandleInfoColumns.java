package ddb.dsz.plugin.taskmanager.details.handle;

import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;

public enum HandleInfoColumns {
   ID("Id", Long.class),
   TYPE("Type", Handle.HandleType.class),
   METADATA("MetaData", String.class);

   String str;
   Class<?> clazz;

   private HandleInfoColumns(String str, Class<?> clazz) {
      this.str = str;
      this.clazz = clazz;
   }

   public String getCaption() {
      return this.str;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }
}
