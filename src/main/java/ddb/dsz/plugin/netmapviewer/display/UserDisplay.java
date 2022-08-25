package ddb.dsz.plugin.netmapviewer.display;

import ddb.console.OptionPane;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.task.Task;
import ddb.dsz.library.console.ConsoleOutputPane;
import ddb.dsz.plugin.netmapviewer.data.User;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.collections.Closure;
import org.jdesktop.layout.GroupLayout;

public class UserDisplay extends JPanel {
   private DefaultListModel userListModel = new DefaultListModel() {
      public int indexOf(Object var1) {
         int var2 = -1;
         Enumeration var3 = this.elements();
         User var4 = null;

         while(var3.hasMoreElements()) {
            var4 = (User)var3.nextElement();
            if (var4.getUserName().equalsIgnoreCase((String)var1)) {
               var2 = this.indexOf(var4, 0);
               break;
            }
         }

         return var2;
      }
   };
   private ConsoleOutputPane outPane;
   private DataTransformer dataTransform;
   private int lastIndex = -1;
   private DefaultComboBoxModel comboModel;
   private TreeMap<String, String> userData = new TreeMap();
   private JComboBox fieldComboBox;
   private JLabel jLabel1;
   private JScrollPane listScroller;
   private JButton nextButton;
   private JPanel outputPanel;
   private JButton previousButton;
   private JTextField searchField;
   private JLabel searchLabel;
   private JList userList;

   public UserDisplay(CoreController var1, List<User> var2) {
      ArrayList var3 = new ArrayList(var2);
      Collections.sort(var3);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         User var5 = (User)var4.next();
         this.userListModel.addElement(var5);
      }

      this.outPane = new ConsoleOutputPane(var1);
      this.outPane.setTheme(OptionPane.getInstance().getSharedTheme());
      this.outPane.setWordWrap(false);
      this.outPane.setAutoScroll(false);
      this.comboModel = new DefaultComboBoxModel();
      this.comboModel.addElement("All");
      UserDisplay.UserFields[] var8 = UserDisplay.UserFields.values();
      int var10 = var8.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         UserDisplay.UserFields var7 = var8[var6];
         this.comboModel.addElement(var7);
      }

      this.initComponents();
      this.nextButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            String var2 = ((JButton)var1.getSource()).getText();
            UserDisplay.this.search(var2);
         }
      });
      this.previousButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            String var2 = ((JButton)var1.getSource()).getText();
            UserDisplay.this.search(var2);
         }
      });
      this.dataTransform = DataTransformer.newInstance();
      this.dataTransform.addClosure(ClosureFactory.newDisplayClosure(var1, "users", "Dsz", new Closure() {
         public void execute(Object var1) {
            UserDisplay.this.parseTaskOutput(var1.toString());
         }
      }));
      Task var9 = var1.getTaskById(((User)var2.get(0)).getTaskId());
      this.dataTransform.addTask(var9);
      this.outputPanel.add(this.outPane);
      this.initializeOutput((User)var2.get(0));
      this.userList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            JList var2 = (JList)var1.getSource();
            User var3 = (User)var2.getSelectedValue();
            UserDisplay.this.setOutputText(var3);
            if (var2.getSelectedIndex() > 0 && !UserDisplay.this.searchField.getText().equals("")) {
               UserDisplay.this.previousButton.setEnabled(true);
            } else {
               UserDisplay.this.previousButton.setEnabled(false);
            }

         }
      });
   }

   private void initializeOutput(final User var1) {
      String var2 = (String)this.userData.get(var1.getUserName().toLowerCase());
      if (var2 == null) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               UserDisplay.this.initializeOutput(var1);
            }
         });
      } else {
         this.outPane.appendDisplay(var2);
      }

   }

   private void setOutputText(User var1) {
      this.outPane.clearAndReplace((String)this.userData.get(var1.getUserName().toLowerCase()));
   }

   private void parseTaskOutput(String var1) {
      int var2 = var1.indexOf("Name  :");
      boolean var3 = true;
      int var4 = var1.indexOf("----------", var2);

      for(String var5 = null; var2 != -1; var4 = var1.indexOf("----------", var2)) {
         int var8 = var1.substring(0, var2).lastIndexOf("\n") + 1;
         int var6 = var1.indexOf(" ", var2 + 8);
         var5 = var1.substring(var2 + 7, var6).trim();
         String var7 = var1.substring(var8, var4);
         this.userData.put(var5.toLowerCase(), var7);
         var2 = var1.indexOf("Name  :", var4);
      }

   }

   private boolean isMatch(Object var1, String var2, UserDisplay.UserFields var3) {
      boolean var4 = false;
      var2 = var2.toLowerCase();
      if (var1 instanceof String) {
         if (((String)String.class.cast(var1)).toLowerCase().contains(var2)) {
            var4 = true;
         }
      } else if (var1 instanceof User) {
         User var5 = (User)var1;

         try {
            Method var6 = User.class.getMethod(var3.getMethodName());
            String var7 = ((String)var6.invoke(var5)).toLowerCase();
            if (var7.contains(var2)) {
               var4 = true;
            }
         } catch (Exception var8) {
         }
      }

      return var4;
   }

   private void search(String var1, UserDisplay.UserFields var2) {
      String var3 = this.searchField.getText().toLowerCase();
      String var4 = null;
      String var5 = null;
      boolean var6 = false;
      User var7 = null;
      boolean var8 = true;
      int var9 = this.userList.getSelectedIndex();
      if (var9 == this.lastIndex) {
         if (var1.equalsIgnoreCase("back")) {
            --var9;
         } else {
            ++var9;
         }
      }

      if (var9 > -1 && var9 <= this.userListModel.size() - 1) {
         var7 = (User)this.userListModel.get(var9);
         var4 = var7.getUserName().toLowerCase();
      }

      if (var9 != 0) {
         var8 = false;
      }

      if (var4 != null) {
         if (var2 == null) {
            var5 = (String)this.userData.get(var4);
            if (var5 != null) {
               var6 = this.isMatch(var5.toLowerCase(), var3, (UserDisplay.UserFields)null);
            }
         } else {
            var6 = this.isMatch(var7, var3, var2);
         }

         if (!var6) {
            while(var4 != null) {
               if (var1.equalsIgnoreCase("back")) {
                  --var9;
               } else {
                  ++var9;
               }

               if (var9 > -1 && var9 <= this.userListModel.size() - 1) {
                  var7 = (User)this.userListModel.get(var9);
                  var4 = var7.getUserName().toLowerCase();
               } else {
                  var4 = null;
               }

               if (var4 != null) {
                  if (var2 == null) {
                     var5 = (String)this.userData.get(var4);
                     if (var5 != null) {
                        var6 = this.isMatch(var5.toLowerCase(), var3, (UserDisplay.UserFields)null);
                     }
                  } else {
                     var6 = this.isMatch(var7, var3, var2);
                  }

                  if (var6) {
                     break;
                  }
               }
            }
         }
      }

      if (var6) {
         this.lastIndex = var9;
         this.userList.setSelectedValue(var7, true);
      } else if (!var8) {
         int var10 = JOptionPane.showConfirmDialog(this, "No entry was found containing " + var3 + "\nRetry search from the beginning?");
         if (var10 == 0) {
            this.lastIndex = -1;
            this.userList.setSelectedIndex(0);
            this.search(var1, var2);
         }
      } else {
         JOptionPane.showMessageDialog(this, "No entry was found containing " + var3);
      }

   }

   private void search(String var1) {
      Object var2 = this.fieldComboBox.getSelectedItem();
      if (var2 instanceof String) {
         this.search(var1, (UserDisplay.UserFields)null);
      } else {
         UserDisplay.UserFields var3 = (UserDisplay.UserFields)var2;
         this.search(var1, var3);
      }

   }

   private void initComponents() {
      this.listScroller = new JScrollPane();
      this.userList = new JList();
      this.outputPanel = new JPanel();
      this.previousButton = new JButton();
      this.nextButton = new JButton();
      this.searchField = new JTextField();
      this.searchLabel = new JLabel();
      this.fieldComboBox = new JComboBox();
      this.jLabel1 = new JLabel();
      this.userList.setBorder(BorderFactory.createTitledBorder("Users"));
      this.userList.setModel(this.userListModel);
      this.userList.setSelectionMode(1);
      this.userList.setSelectedIndex(0);
      this.listScroller.setViewportView(this.userList);
      this.outputPanel.setBorder(BorderFactory.createTitledBorder("User Properties"));
      this.outputPanel.setLayout(new BorderLayout());
      this.previousButton.setText("Back");
      this.previousButton.setEnabled(false);
      this.nextButton.setText("Forward");
      this.nextButton.setEnabled(false);
      this.searchField.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent var1) {
            UserDisplay.this.searchFieldKeyReleased(var1);
         }
      });
      this.searchLabel.setText("Search:");
      this.fieldComboBox.setModel(this.comboModel);
      this.jLabel1.setText("Search Field:");
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.listScroller, -2, 143, -2).addPreferredGap(0).add(this.outputPanel, -1, 232, 32767)).add(var1.createSequentialGroup().add(var1.createParallelGroup(2).add(this.searchLabel).add(this.jLabel1)).addPreferredGap(0).add(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.searchField, -2, 83, -2).addPreferredGap(0).add(this.previousButton).addPreferredGap(0).add(this.nextButton)).add(this.fieldComboBox, -2, -1, -2)))).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(2, var1.createSequentialGroup().add(34, 34, 34).add(var1.createParallelGroup(1).add(2, this.listScroller, -1, 183, 32767).add(2, this.outputPanel, -1, 183, 32767)).add(40, 40, 40).add(var1.createParallelGroup(3).add(this.searchLabel).add(this.searchField, -2, -1, -2).add(this.previousButton).add(this.nextButton)).add(18, 18, 18).add(var1.createParallelGroup(3).add(this.jLabel1).add(this.fieldComboBox, -2, -1, -2)).add(15, 15, 15)));
      this.outputPanel.add(this.outPane);
   }

   private void searchFieldKeyReleased(KeyEvent var1) {
      if (!this.searchField.getText().equals("")) {
         this.nextButton.setEnabled(true);
         if (this.userList.getSelectedIndex() > 0) {
            this.previousButton.setEnabled(true);
         }
      } else {
         this.nextButton.setEnabled(false);
         this.previousButton.setEnabled(false);
      }

   }

   private static enum UserFields {
      Name("Name", "getUserName"),
      Comment("Comment", "getComment"),
      Id("Id", "getUserId"),
      Group("Primary Group", "getGroupId"),
      HomeDir("Home Directory", "getHomeDir"),
      Shell("User Shell", "getUserShell"),
      NumLogons("# of Logons", "getNumLogins"),
      AcctExp("Account Expires", "getAccountExpiration"),
      PswdChange("Passwd Changed", "getPasswordChanged"),
      PswdExpired("Password Expired", "getPasswordExpiration"),
      Privileges("Privileges", "getUserPrivileges");

      private String fieldName;
      private String methodName;

      private UserFields(String var3, String var4) {
         this.fieldName = var3;
         this.methodName = var4;
      }

      public String getFieldName() {
         return this.fieldName;
      }

      public String getMethodName() {
         return this.methodName;
      }

      public String toString() {
         return this.fieldName;
      }
   }
}
