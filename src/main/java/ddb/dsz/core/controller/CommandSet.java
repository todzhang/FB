package ddb.dsz.core.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class CommandSet {
   private List<CommandInfo> commands = new Vector(100);
   private Map<String, CommandInfo> nameMap = new HashMap();

   public synchronized CommandInfo getCommandByName(String name) {
      return name == null ? null : (CommandInfo)this.nameMap.get(name.toLowerCase());
   }

   public synchronized List<CommandInfo> getAllCommands() {
      return Collections.unmodifiableList(this.commands);
   }

   public void replaceAllCommands(List<CommandInfo> list) {
      HashMap hashMap = new HashMap();
      Iterator iterator = list.iterator();

      while(iterator.hasNext()) {
         CommandInfo commandInfo = (CommandInfo)iterator.next();
         hashMap.put(commandInfo.getName().toLowerCase(), commandInfo);
      }

      this.replaceAllCommands(list, hashMap);
   }

   public synchronized void replaceAllCommands(List<CommandInfo> commands, Map<String, CommandInfo> nameMap) {
      this.commands = commands;
      this.nameMap = nameMap;
   }

   public synchronized List<CommandInfo> getAllCommands(String name) {
      Vector vector = new Vector();
      Iterator iterator = this.commands.iterator();

      while(iterator.hasNext()) {
         CommandInfo commandInfo = (CommandInfo)iterator.next();
         if (commandInfo.getName().equalsIgnoreCase(name)) {
            vector.add(commandInfo);
         }
      }

      return vector;
   }
}
