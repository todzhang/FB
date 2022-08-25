package ddb.dsz.plugin.terminal;

import ddb.dsz.core.controller.CommandInfo;
import ddb.dsz.core.controller.CommandSet;
import ddb.dsz.library.console.CommandCompletionAction;
import ddb.dsz.library.console.ParsedCommandLine;
import ddb.dsz.library.console.ConsoleOutputPane.OutputLevel;
import ddb.util.StringCompletor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

public class TerminalCompletionAction extends CommandCompletionAction {
   static final Pattern TGT = Pattern.compile("dst=([^\\s]*)", 2);
   static final Pattern TGT_PREFIX = Pattern.compile("\\s*dst=([^\\s]*)\\s.*", 2);
   static final Pattern LOCAL_PREFIX = Pattern.compile("\\s*local\\s.*", 2);
   private JTextField commandLine;
   private CommandSet commands;
   private final Terminal terminal;

   public TerminalCompletionAction(Terminal var1, CommandSet var2) {
      this.commandLine = var1.getCommandLine();
      this.commands = var2;
      this.terminal = var1;
   }

   public void actionPerformed(ActionEvent actionEvent) {
      if (!this.terminal.isInPromptMode()) {
         int var2 = this.commandLine.getText().length();
         int var3 = this.commandLine.getCaretPosition();
         String var4 = null;
         String var5 = null;

         try {
            var4 = this.commandLine.getText(0, var3);
         } catch (BadLocationException var26) {
            var26.printStackTrace();
            return;
         }

         try {
            var5 = this.commandLine.getText(var3, var2 - var3);
         } catch (BadLocationException var25) {
            var25.printStackTrace();
            return;
         }

         if (var4.startsWith(".")) {
            if (var4.indexOf(" ") != -1) {
               this.terminal.helpBuiltinCommand(var4);
            } else {
               this.terminal.completeBuiltinCommand(var4, var5);
            }
         } else {
            Matcher var6 = TGT.matcher(var4);
            String var9;
            if (var6.matches() && this.commandLine != null) {
               String var27 = var6.group(1);
               String var28 = this.terminal.getTargetForPrefix(var27);
               if (var28 != null) {
                  var4 = var4.substring(0, var4.length() - var27.length());
                  var9 = String.format("%s%s%s ", var4, var28, var5);
                  this.commandLine.setText(var9);
                  int var29 = var4.length() + var28.length();
                  this.commandLine.setCaretPosition(var29);
               }

            } else {
               ParsedCommandLine var7 = new ParsedCommandLine(var4, this.commands);
               boolean var8 = var7.endsWithSpace();
               var9 = var7.getLastWord();
               String var10 = var7.getLineWithoutLastWord();
               String var11;
               String var12;
               if (var8) {
                  var11 = var7.getCommandLine();
                  var12 = "";
               } else {
                  var11 = var10;
                  var12 = var9;
               }

               var7.parseLine(var11);
               boolean var13 = var7.getCommandName() != null && !var7.getCommandName().equals("");
               boolean var14 = false;
               HashSet var15 = new HashSet();
               if (var13) {
                  Iterator var16 = var7.getCommandInfo().iterator();

                  while(var16.hasNext()) {
                     CommandInfo var17 = (CommandInfo)var16.next();
                     var15.add(var17.getOptionPrefix());
                  }
               }

               boolean var30 = false;
               String var18;
               if (var12 != null) {
                  Iterator var31 = var15.iterator();

                  while(var31.hasNext()) {
                     var18 = (String)var31.next();
                     if (var18 != null && var12.startsWith(var18)) {
                        var30 = true;
                        break;
                     }
                  }
               }

               if (var13 && var15.size() > 0 && var30) {
                  var14 = true;
               }

               boolean var32;
               if (var13 && !var14) {
                  var32 = true;
               } else {
                  var32 = false;
               }

               if (var32) {
                  var18 = null;
                  Matcher var36 = TGT_PREFIX.matcher(var7.getCommandLine());
                  if (var36.matches()) {
                     var18 = var36.group(1);
                  } else {
                     var36 = LOCAL_PREFIX.matcher(var7.getCommandLine());
                     if (var36.matches()) {
                        var18 = "localhost";
                     }
                  }

                  this.terminal.requestHelpStatement(var7, var18);
               } else {
                  var18 = null;
                  Object var33;
                  if (var14) {
                     var33 = new Vector();
                     List var19 = var7.getCommandInfo();
                     Vector var20 = new Vector();
                     Iterator var21 = var19.iterator();

                     while(var21.hasNext()) {
                        CommandInfo var22 = (CommandInfo)var21.next();
                        Iterator var23 = var22.getOptions().iterator();

                        while(var23.hasNext()) {
                           String var24 = (String)var23.next();
                           var20.add(var22.getOptionPrefix() + var24);
                        }
                     }

                     var21 = var7.getOptions().iterator();

                     while(var21.hasNext()) {
                        String var38 = (String)var21.next();
                        var20.remove(var38);
                     }

                     ((List)var33).addAll(StringCompletor.complete(var12, var20));
                  } else {
                     var33 = StringCompletor.complete(var12, this.commands.getAllCommands());
                  }

                  switch(((List)var33).size()) {
                  case 0:
                     Toolkit.getDefaultToolkit().beep();
                     break;
                  case 1:
                     this.commandLine.setText(var11 + ((List)var33).get(0).toString() + " " + var5);
                     int var34 = var11.length() + ((List)var33).get(0).toString().length() + 1;
                     this.commandLine.setCaretPosition(var34);
                     return;
                  default:
                     Collections.sort((List)var33);
                     if (var12 == null) {
                        var12 = "";
                     }

                     String var35 = this.findLongestCommonPrefix((List)var33, var12);
                     if (!var35.equals(var12)) {
                        this.commandLine.setText(var11 + var35 + var5);
                        int var37 = var11.length() + var35.length();
                        this.commandLine.setCaretPosition(var37);
                        return;
                     }

                     if (var14) {
                        this.printMultipleOptionCompletions((Collection)var33, var7.getCommandName());
                     } else {
                        this.printMultipleCommandAndPrefixCompletions((List)var33);
                     }
                  }

               }
            }
         }
      }
   }

   private String findLongestCommonPrefix(List<?> var1, String var2) {
      String var3 = var2;
      if (var2 == null) {
         var3 = "";
      }

      int var4 = var3.length();
      String var5 = "";
      if (var1.size() > 0) {
         var5 = var1.get(0).toString();
      }

      for(int var6 = var3.length(); var6 < var5.length(); ++var6) {
         char var7;
         try {
            var7 = var5.charAt(var6);
         } catch (IndexOutOfBoundsException var13) {
            return var5.substring(0, var4);
         }

         Iterator var8 = var1.iterator();
         var8.next();

         while(var8.hasNext()) {
            String var9 = var8.next().toString();

            char var10;
            try {
               var10 = var9.charAt(var6);
            } catch (IndexOutOfBoundsException var12) {
               return var5.substring(0, var4);
            }

            if (var10 != var7) {
               return var5.substring(0, var4);
            }
         }

         ++var4;
      }

      return var5;
   }

   protected void printMultipleCommandAndPrefixCompletions(List<CommandInfo> var1) {
      int var2 = 20;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         CommandInfo var4 = (CommandInfo)var3.next();
         if (var4.getName().length() > var2) {
            var2 = var4.getName().length();
         }
      }

      int var15 = 80 / (6 + var2);
      if (var2 > 25) {
         var15 = 2;
      } else if (var2 < 15) {
         var15 = 4;
      }

      StringBuilder var16 = new StringBuilder();
      StringBuilder var5 = new StringBuilder();
      StringBuilder var6 = new StringBuilder();
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      String var10 = String.format("%6s%%-%ds", " ", var2);
      Iterator var11 = var1.iterator();

      while(var11.hasNext()) {
         CommandInfo var12 = (CommandInfo)var11.next();
         switch(var12.getType()) {
         case PREFIX:
         case ALIASPREFIX:
            var16.append(String.format(var10, var12.getName()));
            ++var7;
            if (var7 % var15 == 0) {
               var16.append("\n");
            }
            break;
         case COMMAND:
            var5.append(String.format(var10, var12.getName()));
            ++var8;
            if (var8 % var15 == 0) {
               var5.append("\n");
            }
            break;
         case ALIAS:
            var6.append(String.format(var10, var12.getName()));
            ++var9;
            if (var9 % var15 == 0) {
               var6.append("\n");
            }
         }
      }

      synchronized(this.terminal) {
         this.terminal.appendTimestampedString(this.commandLine.getText() + "\n", OutputLevel.BOLD);
         if (var16.length() > 0) {
            this.terminal.appendOutputMessage("Prefixes:\n", OutputLevel.NOTICE);
            this.terminal.appendToOutput(var16.toString());
            this.terminal.appendToOutput("\n");
         }

         if (var5.length() > 0) {
            this.terminal.appendOutputMessage("Commands:\n", OutputLevel.NOTICE);
            this.terminal.appendToOutput(var5.toString());
            this.terminal.appendToOutput("\n");
         }

         if (var6.length() > 0) {
            this.terminal.appendOutputMessage("Aliases:\n", OutputLevel.NOTICE);
            this.terminal.appendToOutput(var6.toString());
            this.terminal.appendToOutput("\n");
         }

      }
   }

   protected void printMultipleOptionCompletions(Collection<?> var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var3.append(var6 + "\t");
         ++var4;
         if (var4 % 3 == 0) {
            var3.append("\n");
         }
      }

      synchronized(this.terminal) {
         this.terminal.appendTimestampedString(this.commandLine.getText() + "\n", OutputLevel.BOLD);
         this.terminal.appendOutputMessage("Options for '" + var2 + "'\n", OutputLevel.NOTICE);
         this.terminal.appendToOutput(var3.toString());
         this.terminal.appendToOutput("\n");
      }
   }

   private String printCompletionsToStringBuffer(Vector<?> var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = var1.iterator();
      int var4 = 1;

      while(var3.hasNext()) {
         var2.append((String)var3.next() + " ");
         if (var4++ == 8) {
            var4 = 0;
            var2.append("\n");
         }
      }

      return var2.toString();
   }
}
