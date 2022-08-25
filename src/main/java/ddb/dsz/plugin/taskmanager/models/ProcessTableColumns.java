package ddb.dsz.plugin.taskmanager.models;

import ddb.dsz.plugin.taskmanager.enumerated.FileStatus;
import ddb.dsz.plugin.taskmanager.enumerated.HandlesStatus;
import ddb.dsz.plugin.taskmanager.enumerated.ProcessInfoStatus;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;

public enum ProcessTableColumns {
   TYPE("", false, FileStatus.class),
   PROCESSINFO("", false, ProcessInfoStatus.class),
   HANDLEINFO("", false, HandlesStatus.class),
   PROCESSID("Process ID", false, Long.class),
   PARENTID("Parent ID", false, Long.class),
   PROCESSNAME("Process Name", false, String.class),
   PROCESSPATH("Process Path", false, String.class),
   USERNAME("User Name", false, String.class),
   ARCH_TYPE("Type", false, String.class),
   CPUTIME("CPU Time", true, String.class),
   CREATIONTIME("Creation Time", true, String.class),
   DISPLAY("Display", true, String.class),
   EXPLANATION("Comment", false, String.class),
   HIGHLIGHT("", true, Boolean.class),
   PROCESS("", true, ProcessInformation.class);

   String name;
   boolean hidden;
   Class<?> clazz;

   private ProcessTableColumns(String name, boolean hidden, Class<?> clazz) {
      this.name = name;
      this.hidden = hidden;
      this.clazz = clazz;
   }

   public String getColumnName() {
      return this.name;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public String toString() {
      return this.name;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }
}
