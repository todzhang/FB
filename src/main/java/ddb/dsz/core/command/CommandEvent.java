package ddb.dsz.core.command;

import ddb.dsz.core.task.TaskId;
import java.awt.Color;
import java.util.Calendar;

public interface CommandEvent {
   Color getColor();

   String getCommand();

   TaskId getId();

   TaskId getPid();

   int getReqId();

   String getTargetAddress();

   String getText();

   Calendar getTimestamp();

   CommandEvent.CommandEventType getType();

   CommandEvent.XmlOutput getXmlOutput();

   boolean isCurrentOperation();

   public enum XmlOutput {
      ERROR,
      GOOD,
      DEFAULT,
      WARNING;
   }

   public enum CommandEventType {
      INVALID("Invalid"),
      STARTED("Started"),
      OUTPUT("Output"),
      START_PROMPT("Start Prompt"),
      STOP_PROMPT("Stop Prompt"),
      PAUSED("Paused"),
      ENDED("Ended"),
      SET_FLAGS("Set Flags"),
      HELP("Help"),
      COMMANDLISTUPDATED("Command List Updated"),
      INFO("Information"),
      GUICOMMAND("Gui Command"),
      BACKGROUNDED("Background");

      String text;

       CommandEventType(String text) {
         this.text = text;
      }

      @Override
      public String toString() {
         return this.text;
      }
   }
}
