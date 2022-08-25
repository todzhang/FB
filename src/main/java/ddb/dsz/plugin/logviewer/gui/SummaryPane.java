package ddb.dsz.plugin.logviewer.gui;

import ddb.delegate.UpdateLabel;
import ddb.detach.Alignment;
import ddb.dsz.core.command.CommandEvent;
import ddb.imagemanager.ImageManager;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;

public class SummaryPane extends LogViewerDetachable {
   JLabel implantVersion = new JLabel("");
   JLabel lpVersion = new JLabel("");
   JLabel sessionEnd = new JLabel("");
   JLabel sessionId = new JLabel("");
   JLabel sessionKey = new JLabel("");
   JLabel sessionStart = new JLabel("");
   JLabel targetVersion = new JLabel("");
   private GridBagLayout gbLayout = new GridBagLayout();

   public SummaryPane() {
      this.display.setLayout(this.gbLayout);
      super.setName("Summary");
      super.setAlignment(Alignment.LEFT);
      super.setShowButtons(false);
      super.setLogo("images/gkrellm2.png", ImageManager.SIZE16);
      this.addComponent(new JLabel("LP Version:"), 0, 0, 1, 1);
      this.addComponent(new JLabel("Implant Version:"), 1, 0, 1, 1);
      this.addComponent(new JLabel("Session Id:"), 2, 0, 1, 1);
      this.addComponent(new JLabel("Session Key:"), 3, 0, 1, 1);
      this.addComponent(new JLabel("Session Start:"), 4, 0, 1, 1);
      this.addComponent(new JLabel("Session End:"), 5, 0, 1, 1);
      this.addComponent(new JLabel("Target Version:"), 6, 0, 1, 1);
      this.addComponent(this.lpVersion, 0, 1, 0, 1);
      this.addComponent(this.implantVersion, 1, 1, 0, 1);
      this.addComponent(this.sessionId, 2, 1, 0, 1);
      this.addComponent(this.sessionKey, 3, 1, 0, 1);
      this.addComponent(this.sessionStart, 4, 1, 0, 1);
      this.addComponent(this.sessionEnd, 5, 1, 0, 1);
      this.addComponent(this.targetVersion, 6, 1, 0, 1);
   }

   private void addComponent(Component c, int row, int column, int width, int height) {
      GridBagConstraints gbConstraints = new GridBagConstraints();
      gbConstraints.fill = 1;
      gbConstraints.gridx = column;
      gbConstraints.gridy = row;
      gbConstraints.gridheight = height;
      gbConstraints.gridwidth = width;
      this.gbLayout.setConstraints(c, gbConstraints);
      this.display.add(c);
   }

   public String getImplantVersion() {
      return this.implantVersion.getText();
   }

   public void setImplantVersion(String implantVersion) {
      EventQueue.invokeLater(new UpdateLabel(this.implantVersion, implantVersion));
   }

   public String getLpVersion() {
      return this.lpVersion.getText();
   }

   public void setLpVersion(String lpVersion) {
      EventQueue.invokeLater(new UpdateLabel(this.lpVersion, lpVersion));
   }

   public String getSessionEnd() {
      return this.sessionEnd.getText();
   }

   public void setSessionEnd(String sessionEnd) {
      EventQueue.invokeLater(new UpdateLabel(this.sessionEnd, sessionEnd));
   }

   public String getSessionId() {
      return this.sessionId.getText();
   }

   public void setSessionId(String sessionId) {
      EventQueue.invokeLater(new UpdateLabel(this.sessionId, sessionId));
   }

   public String getSessionKey() {
      return this.sessionKey.getText();
   }

   public void setSessionKey(String sessionKey) {
      EventQueue.invokeLater(new UpdateLabel(this.sessionKey, sessionKey));
   }

   public String getSessionStart() {
      return this.sessionStart.getText();
   }

   public void setSessionStart(String sessionStart) {
      EventQueue.invokeLater(new UpdateLabel(this.sessionStart, sessionStart));
   }

   public String getTargetVersion() {
      return this.targetVersion.getText();
   }

   public void setTargetVersion(String targetVersion) {
      EventQueue.invokeLater(new UpdateLabel(this.targetVersion, targetVersion));
   }

   public void commandEventReceived(CommandEvent event) {
      Calendar latest = event.getTimestamp();
      if (latest != null) {
         EventQueue.invokeLater(new SummaryPane.UpdateSession(calendarToString(latest)));
      }
   }

   public static String calendarToString(Calendar cal) {
      if (cal == null) {
         return "";
      } else {
         SimpleDateFormat dateFormatter = new SimpleDateFormat("M/d/yyyy HH:mm:ss aaa");
         return dateFormatter.format(cal.getTime());
      }
   }

   @Override
   public boolean isClosable() {
      return false;
   }

   private class UpdateSession implements Runnable {
      String time;

      public UpdateSession(String time) {
         this.time = time;
      }

      public void run() {
         SummaryPane.this.sessionEnd.setText(this.time);
         if (SummaryPane.this.sessionStart.getText().length() == 0) {
            SummaryPane.this.sessionStart.setText(this.time);
         }

      }
   }
}
