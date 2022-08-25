package ddb.dsz.library.console;

import ddb.dsz.core.controller.CommandInfo;
import ddb.dsz.core.controller.CommandSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class ParsedCommandLine {
   protected String commandLine;
   protected String commandName;
   protected List<String> prefixes;
   protected List<String> options;
   protected String arguments;
   protected List<CommandInfo> commandInfo;
   protected String lastWord;
   protected String lineWithoutLastWord;
   protected boolean endsWithSpace;
   protected CommandSet commandSet;

   public ParsedCommandLine(String commandLine, CommandSet commandSet) {
      this.commandSet = commandSet;
      this.parseLine(commandLine);
   }

   public ParsedCommandLine(ParsedCommandLine parsedCommandLine) {
      this(parsedCommandLine.getCommandLine(), parsedCommandLine.getCommandSet());
   }

   public void parseLine(String commandLine) {
      this.initialize();
      this.commandLine = commandLine;
      this.endsWithSpaceCheck();
      this.findLastWord();
      this.findLineWithoutLastWord();
      this.findCommand();
      this.findPrefixes();
      this.findArgumentString();
      this.findOptions();
   }

   private void initialize() {
      this.commandLine = null;
      this.commandName = null;
      this.prefixes = new Vector();
      this.options = new Vector();
      this.arguments = null;
      this.commandInfo = new Vector();
      this.lastWord = null;
      this.lineWithoutLastWord = null;
      this.endsWithSpace = false;
   }

   private void endsWithSpaceCheck() {
      if (this.commandLine.endsWith(" ")) {
         this.endsWithSpace = true;
      } else {
         this.endsWithSpace = false;
      }

   }

   private void findLastWord() {
      if (this.commandLine.trim().length() != 0) {
         for(StringTokenizer var1 = new StringTokenizer(this.commandLine); var1.hasMoreTokens(); this.lastWord = var1.nextToken()) {
         }

         if (this.lastWord == null) {
            this.lastWord = "";
         }

      }
   }

   private void findLineWithoutLastWord() {
      if (this.lastWord == null) {
         this.lineWithoutLastWord = this.commandLine;
      } else {
         int var1 = this.commandLine.lastIndexOf(this.lastWord);
         this.lineWithoutLastWord = this.commandLine.substring(0, var1);
      }
   }

   private void findCommand() {
      this.commandInfo.clear();
      StringTokenizer var1 = new StringTokenizer(this.commandLine);

      while(var1.hasMoreTokens()) {
         String var2 = var1.nextToken();
         int var3 = var2.indexOf(61);
         if (var3 != -1) {
            var2 = var2.substring(0, var3);
         }

         List var4 = this.commandSet.getAllCommands(var2);
         if (var4.size() == 0) {
            this.commandName = null;
            this.commandInfo = null;
            return;
         }

         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            CommandInfo var6 = (CommandInfo)var5.next();
            if (var6.isOrContainsCommand()) {
               this.commandInfo.add(var6);
               CommandInfo var7 = this.commandSet.getCommandByName(var6.getExpansionCommand());
               if (var6.isAlias() && var7 != null) {
                  this.commandInfo.add(var7);
               }
            }
         }

         if (this.commandInfo.size() > 0) {
            this.commandName = var2;
            break;
         }
      }

   }

   private void findPrefixes() {
      StringTokenizer var1 = new StringTokenizer(this.commandLine);

      while(var1.hasMoreTokens()) {
         String var2 = var1.nextToken();
         int var3 = var2.indexOf(61);
         if (var3 != -1) {
            var2 = var2.substring(0, var3);
         }

         CommandInfo var4 = this.commandSet.getCommandByName(var2);
         if (var4 == null) {
            return;
         }

         if (var4.isOrContainsCommand()) {
            return;
         }

         this.prefixes.add(var2);
      }

   }

   private void findArgumentString() {
      if (this.commandName != null) {
         int var1 = this.commandLine.indexOf(this.commandName);
         int var2 = var1 + this.commandName.length();
         if (var2 < this.commandLine.length()) {
            this.arguments = this.commandLine.substring(var2);
         }

      }
   }

   private void findOptions() {
      if (this.commandName != null) {
         if (this.arguments != null) {
            HashSet var1 = new HashSet();
            Iterator var2 = this.commandInfo.iterator();

            while(var2.hasNext()) {
               CommandInfo var3 = (CommandInfo)var2.next();
               Iterator var4 = var3.getOptions().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var1.add(String.format("%s%s", var3.getOptionPrefix(), var5).toLowerCase());
               }
            }

            StringTokenizer var6 = new StringTokenizer(this.arguments);

            while(var6.hasMoreTokens()) {
               String var7 = var6.nextToken().toLowerCase();
               if (var1.contains(var7)) {
                  this.options.add(var7);
               }
            }

         }
      }
   }

   public List<String> getOptions() {
      return this.options;
   }

   public String getArguments() {
      return this.arguments;
   }

   public List<CommandInfo> getCommandInfo() {
      return this.commandInfo;
   }

   public void setCommandLine(String var1) {
      this.parseLine(var1);
   }

   public String getCommandLine() {
      return this.commandLine;
   }

   public String getCommandName() {
      return this.commandName;
   }

   public boolean endsWithSpace() {
      return this.endsWithSpace;
   }

   public String getLastWord() {
      return this.lastWord;
   }

   public String getLineWithoutLastWord() {
      return this.lineWithoutLastWord;
   }

   private CommandSet getCommandSet() {
      return this.commandSet;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("line: '" + this.commandLine + "'\n");
      var1.append("command: '" + this.commandName + "'\n");
      Iterator var2 = this.commandInfo.iterator();

      while(var2.hasNext()) {
         CommandInfo var3 = (CommandInfo)var2.next();
         var1.append("commandInfo: '" + var3.debugToString() + "'\n");
      }

      var1.append("line w/o last word: '" + this.lineWithoutLastWord + "'\n");
      var1.append("last word: '" + this.lastWord + "'\n");
      var1.append("ends with space: '" + this.endsWithSpace + "'\n");
      return var1.toString();
   }
}
