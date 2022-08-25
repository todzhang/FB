package ds.core;

import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.task.Task;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;

public class PopupPromptWindow extends JFrame {
   CoreController core;
   Task task;
   int promptId;
   private JButton bOkay;
   private JTextField input;
   private JLabel prompt;

   private PopupPromptWindow() {
      this.initComponents();
   }

   public PopupPromptWindow(CoreController core, Task task, int promptId, String text) {
      this();
      this.core = core;
      this.task = task;
      this.promptId = promptId;
      this.setTitle(String.format("%d: %s", task.getId().getId(), task.getTypedCommand()));
      this.prompt.setText(String.format("<html>%s</html>", text));
      this.pack();
   }

   public PopupPromptWindow(CoreController core, Task task, CommandEvent commandEvent) {
      this(core, task, commandEvent.getReqId(), commandEvent.getText());
   }

   private void initComponents() {
      this.prompt = new JLabel();
      this.input = new JTextField();
      this.bOkay = new JButton();
      this.setDefaultCloseOperation(0);
      this.setResizable(false);
      this.prompt.setText("<html>blah<br/>blah</html>");
      this.input.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent var1) {
            PopupPromptWindow.this.inputKeyPressed(var1);
         }

         @Override
         public void keyTyped(KeyEvent var1) {
            PopupPromptWindow.this.inputKeyTyped(var1);
         }
      });
      this.bOkay.setText("Submit");
      this.bOkay.addActionListener(actionEvent -> PopupPromptWindow.this.bOkayActionPerformed(actionEvent));
      GroupLayout var1 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(var1.createParallelGroup(1).add(2, var1.createSequentialGroup().add(this.bOkay).add(10, 10, 10)).add(2, var1.createSequentialGroup().add(var1.createParallelGroup(2).add(this.prompt, -1, 380, 32767).add(this.input, -1, 380, 32767)).addContainerGap()))));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(this.prompt, -1, 32, 32767).addPreferredGap(0).add(this.input, -2, -1, -2).addPreferredGap(0).add(this.bOkay).addContainerGap()));
      this.pack();
   }

   private void inputKeyPressed(KeyEvent var1) {
      if (var1.getKeyCode() == 10) {
         this.doSend();
      }

   }

   private void inputKeyTyped(KeyEvent var1) {
      if (var1.getKeyCode() == 10) {
         this.doSend();
      }

   }

   private void bOkayActionPerformed(ActionEvent var1) {
      this.doSend();
   }

   public static void main(String[] var0) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            (new PopupPromptWindow()).setVisible(true);
         }
      });
   }

   private void doSend() {
      try {
         this.core.sendPromptReply(this.promptId, this.task.getId(), this.input.getText());
         this.input.setText("");
      } catch (DispatcherException var2) {
         this.done();
      }

   }

   private void done() {
      this.setVisible(false);
      this.dispose();
   }

   public String getPromptText() {
      return this.prompt.getText();
   }

   // $FF: synthetic method
   PopupPromptWindow(Object var1) {
      this();
   }
}
