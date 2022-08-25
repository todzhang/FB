package ddb.dsz.core.controller;

import java.util.List;
import java.util.Vector;

public class CommandInfo implements Comparable<CommandInfo> {
   protected String name;
   protected CommandInfo.CommandInfoEnum type;
   protected String expansion;
   protected List<String> options;
   protected String optionPrefix;

   public CommandInfo(String name, CommandInfo.CommandInfoEnum commandInfoEnum) {
      if (commandInfoEnum.equals(CommandInfo.CommandInfoEnum.ALIAS)) {
         throw new IllegalArgumentException("Use CommandInfo(String,String) to create aliases");
      } else {
         this.name = name;
         this.type = commandInfoEnum;
         this.expansion = null;
         this.options = new Vector();
      }
   }

   public CommandInfo(String name, String expansion) {
      this.name = name;
      this.type = CommandInfo.CommandInfoEnum.ALIAS;
      if (expansion.length() > 0) {
         this.expansion = expansion;
      } else {
         this.expansion = null;
      }

      this.options = new Vector();
   }

   public CommandInfo.CommandInfoEnum getType() {
      return this.type;
   }

   public boolean isCommand() {
      return this.type.equals(CommandInfo.CommandInfoEnum.COMMAND);
   }

   public boolean isPrefix() {
      return this.type.equals(CommandInfo.CommandInfoEnum.PREFIX) || this.type.equals(CommandInfo.CommandInfoEnum.ALIASPREFIX);
   }

   public boolean isAlias() {
      return this.type.equals(CommandInfo.CommandInfoEnum.ALIAS);
   }

   public boolean isOrContainsCommand() {
      if (this.isPrefix()) {
         return false;
      } else if (this.isCommand()) {
         return true;
      } else {
         return this.isAlias() && this.expansion != null;
      }
   }

   public String getExpansionCommand() {
      return this.expansion;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getOptions() {
      return this.options;
   }

   public String getOptionPrefix() {
      return this.optionPrefix;
   }

   public void setOptionPrefix(String var1) {
      this.optionPrefix = var1;
   }

   public void addOption(String option) {
      this.options.add(option);
   }

   public String debugToString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("name: " + this.name + "\n");
      buffer.append("type: " + this.type + "\n");
      buffer.append("expansion: " + this.expansion);
      return buffer.toString();
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public int compareTo(CommandInfo commandInfo) {
      return this.name.compareTo(commandInfo.getName());
   }

   @Override
   public int hashCode() {
      byte var2 = 1;
      int var3 = 31 * var2 + (this.name == null ? 0 : this.name.hashCode());
      var3 = 31 * var3 + (this.type == null ? 0 : this.type.hashCode());
      return var3;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other == null) {
         return false;
      } else if (this.getClass() != other.getClass()) {
         return false;
      } else {
         CommandInfo commandInfo = (CommandInfo)other;
         if (this.name == null) {
            if (commandInfo.name != null) {
               return false;
            }
         } else if (!this.name.equals(commandInfo.name)) {
            return false;
         }

         if (this.type == null) {
            if (commandInfo.type != null) {
               return false;
            }
         } else if (!this.type.equals(commandInfo.type)) {
            return false;
         }

         return true;
      }
   }

   public void setCommandType(CommandInfo.CommandInfoEnum commandInfoEnum) {
      this.type = commandInfoEnum;
   }

   public enum CommandInfoEnum {
      COMMAND,
      PREFIX,
      ALIAS,
      ALIASPREFIX;
   }
}
