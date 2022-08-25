package ddb.dsz.plugin.netmapviewer.display;

import ddb.console.OptionPane;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.task.Task;
import ddb.dsz.library.console.ConsoleOutputPane;
import ddb.dsz.plugin.netmapviewer.data.Service;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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

public class ServiceDisplay extends JPanel {
   private DefaultListModel serviceListModel = new DefaultListModel() {
      public int indexOf(Object var1) {
         int var2 = -1;
         Enumeration var3 = this.elements();
         Service var4 = null;

         while(var3.hasMoreElements()) {
            var4 = (Service)var3.nextElement();
            if (var4.getServiceName().equalsIgnoreCase((String)var1)) {
               var2 = this.indexOf(var4, 0);
               break;
            }
         }

         return var2;
      }
   };
   private ConsoleOutputPane outPane;
   private DataTransformer dataTransform;
   private String lastKeyUsed = null;
   private TreeMap<String, String> serviceData = new TreeMap();
   private JScrollPane listScroller;
   private JButton nextButton;
   private JPanel outputPanel;
   private JButton previousButton;
   private JTextField searchField;
   private JLabel searchLabel;
   private JList serviceList;

   public ServiceDisplay(CoreController var1, List<Service> var2) {
      ArrayList var3 = new ArrayList(var2);
      Collections.sort(var3);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Service var5 = (Service)var4.next();
         this.serviceListModel.addElement(var5);
      }

      this.outPane = new ConsoleOutputPane(var1);
      this.outPane.setTheme(OptionPane.getInstance().getSharedTheme());
      this.outPane.setWordWrap(false);
      this.outPane.setAutoScroll(false);
      this.initComponents();
      this.nextButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            String var2 = ((JButton)var1.getSource()).getText();
            ServiceDisplay.this.search(var2);
         }
      });
      this.previousButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            String var2 = ((JButton)var1.getSource()).getText();
            ServiceDisplay.this.search(var2);
         }
      });
      this.dataTransform = DataTransformer.newInstance();
      this.dataTransform.addClosure(ClosureFactory.newDisplayClosure(var1, "services", "Dsz", new Closure() {
         public void execute(Object var1) {
            ServiceDisplay.this.parseTaskOutput(var1.toString());
         }
      }));
      Task var6 = var1.getTaskById(((Service)var2.get(0)).getTaskId());
      this.dataTransform.addTask(var6);
      this.outputPanel.add(this.outPane);
      this.initializeOutput((Service)var2.get(0));
      this.serviceList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent var1) {
            JList var2 = (JList)var1.getSource();
            Service var3 = (Service)var2.getSelectedValue();
            ServiceDisplay.this.setOutputText(var3);
         }
      });
   }

   private void initializeOutput(final Service var1) {
      String var2 = (String)this.serviceData.get(var1.getServiceName().toLowerCase());
      if (var2 == null) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               ServiceDisplay.this.initializeOutput(var1);
            }
         });
      } else {
         this.outPane.appendDisplay(var2);
      }

   }

   private void setOutputText(Service var1) {
      this.outPane.clearAndReplace((String)this.serviceData.get(var1.getServiceName().toLowerCase()));
   }

   private void parseTaskOutput(String var1) {
      int var2 = var1.indexOf("Service :");
      boolean var3 = true;
      int var4 = var1.indexOf("----------", var2);

      for(String var5 = null; var2 != -1; var4 = var1.indexOf("----------", var2)) {
         int var8 = var1.substring(0, var2).lastIndexOf("\n") + 1;
         int var6 = var1.indexOf("\n", var2 + 10);
         var5 = var1.substring(var2 + 10, var6).trim();
         String var7 = var1.substring(var8, var4);
         this.serviceData.put(var5.toLowerCase(), var7);
         var2 = var1.indexOf("Service :", var4);
      }

   }

   private void search(String var1) {
      String var2 = this.searchField.getText().toLowerCase();
      String var3 = null;
      String var4 = null;
      boolean var5 = false;
      boolean var6 = true;
      if (this.lastKeyUsed == null) {
         var3 = ((String)this.serviceData.firstKey()).toLowerCase();
         var4 = ((String)this.serviceData.get(var3)).toLowerCase();
      } else {
         var6 = false;
         if (var1.equalsIgnoreCase("previous") && !var6) {
            var3 = (String)this.serviceData.lowerKey(this.lastKeyUsed);
         } else {
            var3 = (String)this.serviceData.higherKey(this.lastKeyUsed);
         }

         if (var3 != null) {
            var4 = (String)this.serviceData.get(var3);
         }
      }

      if (var3 != null) {
         if (var4 == null || !var3.toLowerCase().contains(var2) && !var4.toLowerCase().contains(var2)) {
            label86: {
               do {
                  do {
                     do {
                        if (var3 == null) {
                           break label86;
                        }

                        if (var1.equalsIgnoreCase("previous") && !var6) {
                           var3 = (String)this.serviceData.lowerKey(var3);
                        } else {
                           var3 = (String)this.serviceData.higherKey(var3);
                        }
                     } while(var3 == null);

                     var4 = (String)this.serviceData.get(var3);
                  } while(var4 == null);
               } while(!var3.toLowerCase().contains(var2) && !var4.toLowerCase().contains(var2));

               var5 = true;
            }
         } else {
            var5 = true;
         }

         this.lastKeyUsed = var3;
      }

      int var7;
      if (var5) {
         var7 = this.serviceListModel.indexOf(var3);
         this.serviceList.setSelectedValue(this.serviceListModel.getElementAt(var7), true);
         this.previousButton.setEnabled(true);
      } else {
         this.previousButton.setEnabled(false);
         this.serviceList.setSelectedValue(this.serviceListModel.getElementAt(0), true);
         if (!var6) {
            var7 = JOptionPane.showConfirmDialog(this, "No entry was found containing " + var2 + "\nRetry search from the beginning?");
            if (var7 == 0) {
               this.lastKeyUsed = null;
               this.search(var1);
            }
         } else {
            JOptionPane.showMessageDialog(this, "No entry was found containing " + var2);
         }
      }

   }

   private void initComponents() {
      this.listScroller = new JScrollPane();
      this.serviceList = new JList();
      this.outputPanel = new JPanel();
      this.searchField = new JTextField();
      this.searchLabel = new JLabel();
      this.previousButton = new JButton();
      this.nextButton = new JButton();
      this.serviceList.setBorder(BorderFactory.createTitledBorder("Services"));
      this.serviceList.setModel(this.serviceListModel);
      this.serviceList.setSelectionMode(1);
      this.serviceList.setSelectedIndex(0);
      this.listScroller.setViewportView(this.serviceList);
      this.outputPanel.setBorder(BorderFactory.createTitledBorder("Service Properties"));
      this.outputPanel.setLayout(new BorderLayout());
      this.searchField.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent var1) {
            ServiceDisplay.this.searchFieldKeyReleased(var1);
         }
      });
      this.searchLabel.setText("Search:");
      this.previousButton.setText("Previous");
      this.previousButton.setEnabled(false);
      this.nextButton.setText("Next");
      this.nextButton.setEnabled(false);
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.listScroller, -2, 242, -2).addPreferredGap(0).add(this.outputPanel, -1, 252, 32767)).add(var1.createSequentialGroup().add(this.searchLabel).addPreferredGap(0).add(this.searchField, -1, 319, 32767).addPreferredGap(0).add(this.previousButton).addPreferredGap(0).add(this.nextButton))).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().addContainerGap().add(var1.createParallelGroup(1).add(this.outputPanel, -1, 238, 32767).add(this.listScroller, -1, 238, 32767)).addPreferredGap(0).add(var1.createParallelGroup(3).add(this.searchField, -2, -1, -2).add(this.searchLabel).add(this.previousButton).add(this.nextButton)).addContainerGap()));
      this.outputPanel.add(this.outPane);
   }

   private void searchFieldKeyReleased(KeyEvent var1) {
      if (!this.searchField.getText().equals("")) {
         this.nextButton.setEnabled(true);
      } else {
         this.nextButton.setEnabled(false);
         this.previousButton.setEnabled(false);
      }

   }
}
