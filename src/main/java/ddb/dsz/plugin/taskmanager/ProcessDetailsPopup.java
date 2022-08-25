package ddb.dsz.plugin.taskmanager;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.Generator;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;

public class ProcessDetailsPopup extends JFrame {
   DateFormat START_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   ProcessInformation process;
   private JPanel advancedTab;
   private JPanel basicInfo;
   private JPanel basicTab;
   private ButtonGroup buttonGroup1;
   private JButton close;
   private JTextArea comment;
   private JTextField cpuTime;
   private JTextField display;
   private JPanel groupsInfo;
   private JLabel jLabel1;
   private JLabel jLabel10;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JPanel jPanel5;
   private JPanel jPanel6;
   private JScrollPane jScrollPane1;
   private JTabbedPane jTabbedPane1;
   private JPanel modulesInfo;
   private JTextField parentId;
   private JPanel privilegesInfo;
   private JTextField procId;
   private JTextField procName;
   private JTextField procPath;
   private JTextField procUser;
   private JRadioButton showBasic;
   private JRadioButton showGroups;
   private JRadioButton showModules;
   private JRadioButton showPrivileges;
   private JTextField startTime;
   private JTextField type;

   public ProcessDetailsPopup(CoreController core, ProcessInformation process) {
      this.initComponents();
      this.process = process;
      this.procId.setText(process.getId().toString());
      this.parentId.setText(process.getParent().toString());
      this.display.setText(process.getDisplay());
      this.procName.setText(process.getProcName());
      this.procPath.setText(process.getProcPath());
      this.procUser.setText(process.getUserName());
      if (process.getCreateTime() != null) {
         this.startTime.setText(this.START_TIME_FORMAT.format(process.getCreateTime().getTime()));
      } else {
         this.startTime.setText("");
      }

      long time = process.getCpuTime();
      long seconds = time % 60L;
      time /= 60L;
      long minutes = time % 60L;
      time /= 60L;
      this.cpuTime.setText(String.format("%d:%02d:%02d", time, minutes, seconds));
      switch(process.getType()) {
      case CORE_OS:
         this.type.setText("Core OS file");
         break;
      case MALICIOUS_SOFTWARE:
         this.type.setText("Malicious software");
         break;
      case SAFE:
         this.type.setText("Safe file");
         break;
      case SECURITY_PRODUCT:
         this.type.setText("Security Product");
         break;
      case NONE:
      default:
         this.type.setText("Unknown file");
      }

      this.comment.setText(process.getComment());
      if (process.hasProcessInfo()) {
         if (process.getBasicInfo() != null) {
            this.basicInfo.setLayout(new BorderLayout());
            this.basicInfo.add(Generator.makeBasicInfoDisplay(core, process.getBasicInfo()), "Center");
         }

         if (process.getGroups().size() != 0) {
            this.groupsInfo.setLayout(new BorderLayout());
            this.groupsInfo.add(Generator.makeGroupsDisplay(core, process.getGroups()));
         }

         this.privilegesInfo.setLayout(new BorderLayout());
         this.privilegesInfo.add(Generator.makePrivilegesDisplay(core, process));
         this.modulesInfo.setLayout(new BorderLayout());
         this.modulesInfo.add(Generator.makeModulesDisplay(core, process));
         this.advancedHideFunction((ActionEvent)null);
      } else {
         this.jTabbedPane1.remove(this.advancedTab);
      }

   }

   private void initComponents() {
      this.buttonGroup1 = new ButtonGroup();
      this.jTabbedPane1 = new JTabbedPane();
      this.basicTab = new JPanel();
      this.jPanel3 = new JPanel();
      this.jLabel1 = new JLabel();
      this.procId = new JTextField();
      this.jLabel2 = new JLabel();
      this.parentId = new JTextField();
      this.jLabel3 = new JLabel();
      this.display = new JTextField();
      this.jPanel4 = new JPanel();
      this.jLabel4 = new JLabel();
      this.jLabel5 = new JLabel();
      this.jLabel6 = new JLabel();
      this.procUser = new JTextField();
      this.procPath = new JTextField();
      this.procName = new JTextField();
      this.jPanel5 = new JPanel();
      this.jLabel7 = new JLabel();
      this.startTime = new JTextField();
      this.jLabel8 = new JLabel();
      this.cpuTime = new JTextField();
      this.jPanel6 = new JPanel();
      this.jLabel9 = new JLabel();
      this.jLabel10 = new JLabel();
      this.type = new JTextField();
      this.jScrollPane1 = new JScrollPane();
      this.comment = new JTextArea();
      this.advancedTab = new JPanel();
      this.basicInfo = new JPanel();
      this.groupsInfo = new JPanel();
      this.privilegesInfo = new JPanel();
      this.modulesInfo = new JPanel();
      this.showBasic = new JRadioButton();
      this.showGroups = new JRadioButton();
      this.showPrivileges = new JRadioButton();
      this.showModules = new JRadioButton();
      this.close = new JButton();
      this.setDefaultCloseOperation(2);
      this.jLabel1.setText("Process Id:");
      this.procId.setEditable(false);
      this.procId.setFont(new Font("Tahoma", 1, 11));
      this.procId.setText("jTextField1");
      this.procId.setBorder((Border)null);
      this.procId.setOpaque(false);
      this.jLabel2.setText("Parent:");
      this.parentId.setEditable(false);
      this.parentId.setFont(new Font("Tahoma", 1, 11));
      this.parentId.setText("jTextField1");
      this.parentId.setBorder((Border)null);
      this.parentId.setOpaque(false);
      this.jLabel3.setText("Display:");
      this.display.setEditable(false);
      this.display.setFont(new Font("Tahoma", 1, 11));
      this.display.setText("jTextField1");
      this.display.setBorder((Border)null);
      this.display.setOpaque(false);
      GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
      this.jPanel3.setLayout(jPanel3Layout);
      jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.procId, -1, 97, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.parentId, -1, 100, 32767).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(this.jLabel3).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.display, -1, 104, 32767)));
      jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.procId, -2, -1, -2).addComponent(this.jLabel2).addComponent(this.parentId, -2, -1, -2).addComponent(this.display, -2, -1, -2).addComponent(this.jLabel3)));
      this.jLabel4.setText("Name:");
      this.jLabel5.setText("Path:");
      this.jLabel6.setText("User:");
      this.procUser.setEditable(false);
      this.procUser.setFont(new Font("Tahoma", 1, 11));
      this.procUser.setText("jTextField1");
      this.procUser.setBorder((Border)null);
      this.procUser.setOpaque(false);
      this.procPath.setEditable(false);
      this.procPath.setFont(new Font("Tahoma", 1, 11));
      this.procPath.setText("jTextField1");
      this.procPath.setBorder((Border)null);
      this.procPath.setOpaque(false);
      this.procName.setEditable(false);
      this.procName.setFont(new Font("Tahoma", 1, 11));
      this.procName.setText("jTextField1");
      this.procName.setBorder((Border)null);
      this.procName.setOpaque(false);
      GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
      this.jPanel4.setLayout(jPanel4Layout);
      jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel4).addComponent(this.jLabel5).addComponent(this.jLabel6)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addComponent(this.procPath, -1, 410, 32767).addComponent(this.procUser, -1, 410, 32767).addComponent(this.procName, -1, 410, 32767)).addContainerGap()));
      jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel4).addComponent(this.procName, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel5).addComponent(this.procPath, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel6).addComponent(this.procUser, -2, -1, -2))));
      this.jLabel7.setText("Start Time:");
      this.startTime.setEditable(false);
      this.startTime.setFont(new Font("Tahoma", 1, 11));
      this.startTime.setText("jTextField1");
      this.startTime.setBorder((Border)null);
      this.startTime.setOpaque(false);
      this.jLabel8.setText("CPU Time:");
      this.cpuTime.setEditable(false);
      this.cpuTime.setFont(new Font("Tahoma", 1, 11));
      this.cpuTime.setText("jTextField1");
      this.cpuTime.setBorder((Border)null);
      this.cpuTime.setOpaque(false);
      GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
      this.jPanel5.setLayout(jPanel5Layout);
      jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addComponent(this.jLabel7).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.startTime, -1, 165, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel8).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.cpuTime, -1, 166, 32767).addContainerGap()));
      jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel7).addComponent(this.startTime, -2, -1, -2).addComponent(this.jLabel8).addComponent(this.cpuTime, -2, -1, -2)));
      this.jLabel9.setText("Type:");
      this.jLabel10.setText("Comment:");
      this.type.setEditable(false);
      this.type.setFont(new Font("Tahoma", 1, 11));
      this.type.setText("jTextField1");
      this.type.setBorder((Border)null);
      this.type.setOpaque(false);
      this.jScrollPane1.setBorder((Border)null);
      this.comment.setColumns(20);
      this.comment.setEditable(false);
      this.comment.setFont(new Font("Tahoma", 1, 11));
      this.comment.setRows(5);
      this.comment.setBorder((Border)null);
      this.comment.setOpaque(false);
      this.jScrollPane1.setViewportView(this.comment);
      GroupLayout jPanel6Layout = new GroupLayout(this.jPanel6);
      this.jPanel6.setLayout(jPanel6Layout);
      jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel10).addComponent(this.jLabel9)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPane1, -1, 392, 32767).addComponent(this.type, -1, 392, 32767)).addContainerGap()));
      jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addGroup(jPanel6Layout.createParallelGroup(Alignment.BASELINE).addComponent(this.jLabel9).addComponent(this.type, -2, -1, -2)).addPreferredGap(ComponentPlacement.RELATED).addGroup(jPanel6Layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel10).addComponent(this.jScrollPane1, -2, -1, -2)).addContainerGap(17, 32767)));
      GroupLayout basicTabLayout = new GroupLayout(this.basicTab);
      this.basicTab.setLayout(basicTabLayout);
      basicTabLayout.setHorizontalGroup(basicTabLayout.createParallelGroup(Alignment.LEADING).addGroup(basicTabLayout.createSequentialGroup().addContainerGap().addGroup(basicTabLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel3, -1, -1, 32767).addComponent(this.jPanel6, -1, -1, 32767).addComponent(this.jPanel4, -1, -1, 32767).addComponent(this.jPanel5, -1, -1, 32767)).addContainerGap()));
      basicTabLayout.setVerticalGroup(basicTabLayout.createParallelGroup(Alignment.LEADING).addGroup(basicTabLayout.createSequentialGroup().addComponent(this.jPanel3, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel4, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel5, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jPanel6, -2, -1, -2).addContainerGap(93, 32767)));
      this.jTabbedPane1.addTab("Basic", this.basicTab);
      this.basicInfo.setBorder(BorderFactory.createEtchedBorder());
      GroupLayout basicInfoLayout = new GroupLayout(this.basicInfo);
      this.basicInfo.setLayout(basicInfoLayout);
      basicInfoLayout.setHorizontalGroup(basicInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 434, 32767));
      basicInfoLayout.setVerticalGroup(basicInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 43, 32767));
      this.groupsInfo.setBorder(BorderFactory.createEtchedBorder());
      GroupLayout groupsInfoLayout = new GroupLayout(this.groupsInfo);
      this.groupsInfo.setLayout(groupsInfoLayout);
      groupsInfoLayout.setHorizontalGroup(groupsInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 434, 32767));
      groupsInfoLayout.setVerticalGroup(groupsInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 43, 32767));
      this.privilegesInfo.setBorder(BorderFactory.createEtchedBorder());
      GroupLayout privilegesInfoLayout = new GroupLayout(this.privilegesInfo);
      this.privilegesInfo.setLayout(privilegesInfoLayout);
      privilegesInfoLayout.setHorizontalGroup(privilegesInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 434, 32767));
      privilegesInfoLayout.setVerticalGroup(privilegesInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 43, 32767));
      this.modulesInfo.setBorder(BorderFactory.createEtchedBorder());
      GroupLayout modulesInfoLayout = new GroupLayout(this.modulesInfo);
      this.modulesInfo.setLayout(modulesInfoLayout);
      modulesInfoLayout.setHorizontalGroup(modulesInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 434, 32767));
      modulesInfoLayout.setVerticalGroup(modulesInfoLayout.createParallelGroup(Alignment.LEADING).addGap(0, 42, 32767));
      this.buttonGroup1.add(this.showBasic);
      this.showBasic.setText("Basic Information");
      this.showBasic.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ProcessDetailsPopup.this.advancedHideFunction(evt);
         }
      });
      this.buttonGroup1.add(this.showGroups);
      this.showGroups.setText("Groups");
      this.showGroups.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ProcessDetailsPopup.this.advancedHideFunction(evt);
         }
      });
      this.buttonGroup1.add(this.showPrivileges);
      this.showPrivileges.setText("Privileges");
      this.showPrivileges.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ProcessDetailsPopup.this.advancedHideFunction(evt);
         }
      });
      this.buttonGroup1.add(this.showModules);
      this.showModules.setText("Modules");
      this.showModules.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ProcessDetailsPopup.this.advancedHideFunction(evt);
         }
      });
      GroupLayout advancedTabLayout = new GroupLayout(this.advancedTab);
      this.advancedTab.setLayout(advancedTabLayout);
      advancedTabLayout.setHorizontalGroup(advancedTabLayout.createParallelGroup(Alignment.LEADING).addGroup(advancedTabLayout.createSequentialGroup().addContainerGap().addGroup(advancedTabLayout.createParallelGroup(Alignment.LEADING).addGroup(advancedTabLayout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.basicInfo, -1, -1, 32767)).addGroup(advancedTabLayout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.groupsInfo, -1, -1, 32767)).addGroup(advancedTabLayout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.privilegesInfo, -1, -1, 32767)).addGroup(advancedTabLayout.createSequentialGroup().addGap(21, 21, 21).addComponent(this.modulesInfo, -1, -1, 32767)).addComponent(this.showBasic).addComponent(this.showGroups).addComponent(this.showPrivileges).addComponent(this.showModules)).addContainerGap()));
      advancedTabLayout.setVerticalGroup(advancedTabLayout.createParallelGroup(Alignment.LEADING).addGroup(advancedTabLayout.createSequentialGroup().addContainerGap().addComponent(this.showBasic).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.basicInfo, -1, -1, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.showGroups).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.groupsInfo, -1, -1, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.showPrivileges).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.privilegesInfo, -1, -1, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.showModules).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.modulesInfo, -1, -1, 32767).addContainerGap()));
      this.jTabbedPane1.addTab("Advanced", this.advancedTab);
      this.close.setText("Close");
      this.close.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ProcessDetailsPopup.this.closeActionPerformed(evt);
         }
      });
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(409, 32767).addComponent(this.close).addContainerGap()).addComponent(this.jTabbedPane1, -1, 480, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jTabbedPane1, -1, 336, 32767).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.close).addContainerGap()));
      this.pack();
   }

   private void closeActionPerformed(ActionEvent evt) {
      this.dispose();
   }

   private void advancedHideFunction(ActionEvent evt) {
      this.basicInfo.setVisible(this.showBasic.isSelected());
      this.modulesInfo.setVisible(this.showModules.isSelected());
      this.privilegesInfo.setVisible(this.showPrivileges.isSelected());
      this.groupsInfo.setVisible(this.showGroups.isSelected());
   }
}
