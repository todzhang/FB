package ddb.start;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import org.jdesktop.layout.GroupLayout;
import sun.misc.URLClassPath;

public class Start extends JFrame {
//   public static final String PATH_TOOLCHAIN = "java-j2se_1.6-sun";
//   public static final String PATH_LIBRARY = "lib";
   public static final String OPERATION_DISK = "OpsDisk";
   public static final String RESOURCE_DIR = "ResourceDir";
   public static final String LOG_DIR = "LogDir";
   public static final String CONFIG_DIR = "ConfigDir";
   public static final String LOCAL_ADDRESS = "LocalAddress";
   public static final String BUILD_TYPE = "BuildType";
   public static final String GUI_TYPE = "GuiType";
   public static final String OPERATION_MODE = "OpMode";
   public static final String LOAD_PREVIOUS = "LoadPrevious";
   public static final String LOCAL_MODE = "LocalMode";
   public static final String JAVA_EXE = "java.exe";
   public static final String VMARGS = "vmargs";
   public static final String RES_DIR = "res.dir";
   public static final String DEBUGVMARGS = "vmargs.debug";
//   public static final String CLASSPATH = "classpath.dirs";
//   public static final String JAR_DIRS = "jar.dirs";
//   public static final String LIVE_OPERATION = "live.operation";
//   public static final String REPLAY_OPERATION = "replay.operation";
//   public static final String WINDOWS_START = "windows.start";
//   public static final String BUILD_RELEASE = "build.release";
//   public static final String BUILD_DEBUG = "build.debug";
//   public static final String BUILD_DEBUG_WINDOWS = "build.debug.windows";
//   public static final String SHOW_OP_TYPE = "show.optype";
//   public static final String SHOW_DEBUG_CORE = "show.debug.core";
//   public static final String SHOW_DEBUG_GUI = "show.debug.gui";
//   public static final String SHOW_LOCAL_MODE = "show.local.mode";
//   public static final String SHOW_THREAD_DUMP = "show.thread.dump";
//   public static final String WINDOWS = "windows";
//   public static final String LINUX = "linux";
//   public static final String PATH_VAR = "path.var";
   public static final String TOOL_CHAIN_STR = "tool.chain";
//   public static final String PATH_SEP = "path.sep";
   public static final String THREAD_DUMP = "thread.dump";
   public static final String WAIT_FOR_OUTPUT = "wait.for.output";
   public static final String DSZ_KEYWORD = "DSZ_KEYWORD";
   public static final String LIVE_KEYWORD = String.format("live.%s", DSZ_KEYWORD);
   public static final String REPLAY_KEYWORD = String.format("replay.%s", DSZ_KEYWORD);
   public static final String DSZ_DEFAULT = "Default";
   static Properties prop = new Properties();
   static Properties userDefaults = new Properties();
   public static final String START_PROPERTIES = "start.properties";
   public static final String USER_DEFAULTS = "user.defaults";
   boolean guess = false;
   JFileChooser directoryFinder = null;
   private static final char[] INVALIDCHARACTERS = new char[]{'\t', ' ', '\b', '\n', '\r'};
   File themeSearchRoot = null;
   DefaultComboBoxModel liveOperationThemes = new DefaultComboBoxModel();
   DefaultComboBoxModel replayOperationThemes = new DefaultComboBoxModel();
   JRadioButton buildDebug;
   JRadioButton buildRelease;
   JButton configurationBrowse;
   JTextField configurationField;
   JLabel configurationLabel;
   ButtonGroup coreBuild;
   JPanel corePanel;
   JButton goButton;
   ButtonGroup guiBuild;
   JRadioButton guiDebug;
   JPanel guiPanel;
   JRadioButton guiRelease;
   JPanel jPanel1;
   JPanel jPanel2;
   JRadioButton liveOption;
   JCheckBox loadPrevious;
   JTextField localCommsAddressField;
   JLabel localCommsAddressLabel;
   JCheckBox localMode;
   JButton logBrowse;
   JTextField logField;
   JLabel logLabel;
   JButton operationBrowse;
   JTextField operationField;
   JLabel operationLabel;
   JPanel operationPanel;
   ButtonGroup operationType;
   JPanel optionsPanel;
   JRadioButton replayOption;
   JButton resourceBrowse;
   JTextField resourceField;
   JLabel resourceLabel;
   JComboBox themeSelector;
   JCheckBox threadDump;
   JCheckBox waitFor;
   static final Pattern[] patterns = new Pattern[]{Pattern.compile("[0-9a-fA-F]{1,8}"), Pattern.compile("[Zz][0-2]{0,1}[0-9]{0,2}\\.[0-2]{0,1}[0-9]{0,2}\\.[0-2]{0,1}[0-9]{0,2}\\.[0-2]{0,1}[0-9]{0,2}")};
   private static final FilenameFilter jars = (f, name) -> name.toLowerCase().endsWith(".jar");

   public Start() {
      try {
         this.directoryFinder = new JFileChooser();
         this.directoryFinder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      try {
         prop.load(new FileInputStream(START_PROPERTIES));
      } catch (FileNotFoundException var2) {
         var2.printStackTrace();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      this.initComponents();

      try {
         userDefaults.load(new FileInputStream(USER_DEFAULTS));
         this.operationField.setText(getStringDefault(OPERATION_DISK, ""));
         this.resourceField.setText(getStringDefault(RESOURCE_DIR, ""));
         this.logField.setText(getStringDefault(LOG_DIR, ""));
         this.configurationField.setText(getStringDefault(CONFIG_DIR, ""));
         File file = new File(".");
         if (this.operationField.getText() != null && this.operationField.getText().length() != 0) {
            file = new File(this.operationField.getText());
         } else {
            this.operationField.setText(file.getCanonicalPath());
         }

         this.infer(file.getCanonicalFile());
      } catch (FileNotFoundException fileNotFoundException) {
         this.guess = true;
      } catch (Exception e) {
         e.printStackTrace();
         this.guess = true;
      }

      if (this.guess) {
         this.infer(new File("."));
         this.examine();
      }

   }

   private void initComponents() {
      this.guiBuild = new ButtonGroup();
      this.operationType = new ButtonGroup();
      this.coreBuild = new ButtonGroup();
      this.resourceField = new JTextField();
      this.logField = new JTextField();
      this.configurationField = new JTextField();
      this.operationField = new JTextField();
      this.resourceLabel = new JLabel();
      this.logLabel = new JLabel();
      this.configurationLabel = new JLabel();
      this.operationLabel = new JLabel();
      this.resourceBrowse = new JButton();
      this.logBrowse = new JButton();
      this.configurationBrowse = new JButton();
      this.operationBrowse = new JButton();
      this.goButton = new JButton();
      this.localCommsAddressLabel = new JLabel();
      this.localCommsAddressField = new JTextField();
      this.jPanel1 = new JPanel();
      this.operationPanel = new JPanel();
      this.liveOption = new JRadioButton();
      this.replayOption = new JRadioButton();
      this.optionsPanel = new JPanel();
      this.loadPrevious = new JCheckBox();
      this.localMode = new JCheckBox();
      this.corePanel = new JPanel();
      this.buildDebug = new JRadioButton();
      this.buildRelease = new JRadioButton();
      this.guiPanel = new JPanel();
      this.guiRelease = new JRadioButton();
      this.guiDebug = new JRadioButton();
      this.waitFor = new JCheckBox();
      this.threadDump = new JCheckBox();
      this.jPanel2 = new JPanel();
      this.themeSelector = new JComboBox();
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.setTitle("DanderSpritz Operation Center");
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      this.setLocationByPlatform(true);
      this.setName("startFrame");
      this.setResizable(false);
      this.resourceField.setToolTipText(prop.getProperty("tooltip.resource"));
      this.resourceField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent var1) {
            Start.this.enterPressed(var1);
         }
      });
      this.logField.setToolTipText(prop.getProperty("tooltip.log"));
      this.logField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent var1) {
            Start.this.enterPressed(var1);
         }
      });
      this.configurationField.setToolTipText(prop.getProperty("tooltip.config"));
      this.configurationField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent var1) {
            Start.this.enterPressed(var1);
         }
      });
      this.operationField.setToolTipText(prop.getProperty("tooltip.disk"));
      this.operationField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent var1) {
            Start.this.enterPressed(var1);
         }
      });
      this.resourceLabel.setText(prop.getProperty("label.resource"));
      this.logLabel.setText(prop.getProperty("label.log"));
      this.configurationLabel.setText(prop.getProperty("label.config"));
      this.operationLabel.setText(prop.getProperty("label.disk"));
      this.resourceBrowse.setText(prop.getProperty("label.browse"));
      this.resourceBrowse.setToolTipText(prop.getProperty("tooltip.resource.browse"));
      this.resourceBrowse.addActionListener(var1 -> Start.this.resourceBrowseActionPerformed(var1));
      this.logBrowse.setText(prop.getProperty("label.browse"));
      this.logBrowse.setToolTipText(prop.getProperty("tooltip.log.browse"));
      this.logBrowse.addActionListener(var1 -> Start.this.logBrowseActionPerformed(var1));
      this.configurationBrowse.setText(prop.getProperty("label.browse"));
      this.configurationBrowse.setToolTipText(prop.getProperty("tooltip.config.browse"));
      this.configurationBrowse.addActionListener(var1 -> Start.this.configurationBrowseActionPerformed(var1));
      this.operationBrowse.setText(prop.getProperty("label.browse"));
      this.operationBrowse.setToolTipText(prop.getProperty("tooltip.disk.browse"));
      this.operationBrowse.addActionListener(var1 -> Start.this.operationBrowseActionPerformed(var1));
      this.goButton.setText(prop.getProperty("label.start"));
      this.goButton.setToolTipText(prop.getProperty("tooltip.start"));
      this.goButton.addActionListener(var1 -> Start.this.goButtonActionPerformed(var1));
      this.localCommsAddressLabel.setText(prop.getProperty("label.comms"));
      this.localCommsAddressField.setText("z0.0.0.1");
      this.localCommsAddressField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent var1) {
            Start.this.enterPressed(var1);
         }
      });
      this.operationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), prop.getProperty("label.opMode")));
      this.operationType.add(this.liveOption);
      this.liveOption.setSelected(true);
      this.liveOption.setText(prop.getProperty("label.live"));
      this.liveOption.setToolTipText(prop.getProperty("tooltip.live"));
      this.liveOption.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.liveOption.setMargin(new Insets(0, 0, 0, 0));
      this.liveOption.addActionListener(var1 -> Start.this.liveOptionActionPerformed(var1));
      this.operationType.add(this.replayOption);
      this.replayOption.setText(prop.getProperty("label.replay"));
      this.replayOption.setToolTipText(prop.getProperty("tooltip.replay"));
      this.replayOption.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.replayOption.setMargin(new Insets(0, 0, 0, 0));
      this.replayOption.addActionListener(var1 -> Start.this.replayOptionActionPerformed(var1));
      GroupLayout var1 = new GroupLayout(this.operationPanel);
      this.operationPanel.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(var1.createParallelGroup(1).add(this.liveOption).add(this.replayOption)).addContainerGap(45, 32767)));
      var1.setVerticalGroup(var1.createParallelGroup(1).add(var1.createSequentialGroup().add(this.liveOption).addPreferredGap(0).add(this.replayOption).addContainerGap(-1, 32767)));
      this.optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), prop.getProperty("label.options")));
      this.loadPrevious.setText(prop.getProperty("label.loadPrevious"));
      this.loadPrevious.setToolTipText(prop.getProperty("tooltip.loadPrevious"));
      this.loadPrevious.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.loadPrevious.setMargin(new Insets(0, 0, 0, 0));
      this.localMode.setText(prop.getProperty("label.localMode"));
      this.localMode.setToolTipText(prop.getProperty("tooltip.localMode"));
      this.localMode.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.localMode.setMargin(new Insets(0, 0, 0, 0));
      this.localMode.setVisible(this.isShowLocal());
      GroupLayout var2 = new GroupLayout(this.optionsPanel);
      this.optionsPanel.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().addContainerGap().add(var2.createParallelGroup(1).add(this.loadPrevious).add(this.localMode)).addContainerGap(-1, 32767)));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(this.loadPrevious).addPreferredGap(0).add(this.localMode).addContainerGap(-1, 32767)));
      this.corePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), prop.getProperty("label.core")));
      this.corePanel.setVisible(this.isShowDebugCore());
      this.coreBuild.add(this.buildDebug);
      this.buildDebug.setText(prop.getProperty("label.debug"));
      this.buildDebug.setToolTipText(prop.getProperty("tooltip.debug"));
      this.buildDebug.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.buildDebug.setMargin(new Insets(0, 0, 0, 0));
      this.coreBuild.add(this.buildRelease);
      this.buildRelease.setSelected(true);
      this.buildRelease.setText(prop.getProperty("label.release"));
      this.buildRelease.setToolTipText(prop.getProperty("tooltip.release"));
      this.buildRelease.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.buildRelease.setMargin(new Insets(0, 0, 0, 0));
      GroupLayout var3 = new GroupLayout(this.corePanel);
      this.corePanel.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().addContainerGap().add(var3.createParallelGroup(1).add(this.buildRelease).add(this.buildDebug)).addContainerGap(-1, 32767)));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().add(this.buildRelease).addPreferredGap(0).add(this.buildDebug).addContainerGap(-1, 32767)));
      this.guiPanel.setBorder(BorderFactory.createTitledBorder(prop.getProperty("label.gui")));
      this.guiPanel.setVisible(this.isShowDebugGui());
      this.guiBuild.add(this.guiRelease);
      this.guiRelease.setText(prop.getProperty("label.release"));
      this.guiRelease.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.guiRelease.setMargin(new Insets(0, 0, 0, 0));
      this.guiBuild.add(this.guiDebug);
      this.guiDebug.setText(prop.getProperty("label.debug"));
      this.guiDebug.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.guiDebug.setMargin(new Insets(0, 0, 0, 0));
      GroupLayout var4 = new GroupLayout(this.guiPanel);
      this.guiPanel.setLayout(var4);
      var4.setHorizontalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().addContainerGap().add(var4.createParallelGroup(1).add(this.guiRelease).add(this.guiDebug)).addContainerGap(-1, 32767)));
      var4.setVerticalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().add(this.guiRelease).addPreferredGap(0).add(this.guiDebug).addContainerGap(-1, 32767)));
      this.waitFor.setText("Wait For Output");
      this.waitFor.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.waitFor.setMargin(new Insets(0, 0, 0, 0));
      this.waitFor.setVisible(false);
      this.threadDump.setText("Thread Dump");
      this.threadDump.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.threadDump.setMargin(new Insets(0, 0, 0, 0));
      this.threadDump.setVisible(this.isShowThreadDump());
      this.jPanel2.setBorder(BorderFactory.createTitledBorder("Theme"));
      this.themeSelector.setModel(new DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
      GroupLayout var5 = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(var5);
      var5.setHorizontalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().addContainerGap().add(this.themeSelector, 0, 179, 32767).addContainerGap()));
      var5.setVerticalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(this.themeSelector, -2, -1, -2).addContainerGap(25, 32767)));
      GroupLayout var6 = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(var6);
      var6.setHorizontalGroup(var6.createParallelGroup(1).add(var6.createSequentialGroup().addContainerGap().add(this.operationPanel, -2, -1, -2).addPreferredGap(0).add(this.optionsPanel, -2, -1, -2).addPreferredGap(0).add(this.corePanel, -2, -1, -2).addPreferredGap(0).add(this.guiPanel, -2, -1, -2).addPreferredGap(0).add(this.jPanel2, -1, -1, 32767).addPreferredGap(0).add(var6.createParallelGroup(1, false).add(this.threadDump, -1, -1, 32767).add(this.waitFor)).addContainerGap()));
      var6.setVerticalGroup(var6.createParallelGroup(1).add(var6.createSequentialGroup().addContainerGap().add(var6.createParallelGroup(1).add(var6.createSequentialGroup().add(this.threadDump).addPreferredGap(0).add(this.waitFor)).add(2, this.jPanel2, -1, -1, 32767).add(this.operationPanel, -1, -1, 32767).add(this.optionsPanel, -1, -1, 32767).add(this.corePanel, -1, -1, 32767).add(this.guiPanel, -1, -1, 32767)).addContainerGap()));
      GroupLayout var7 = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(var7);
      var7.setHorizontalGroup(var7.createParallelGroup(1).add(var7.createSequentialGroup().addContainerGap().add(var7.createParallelGroup(1).add(var7.createSequentialGroup().add(var7.createParallelGroup(1).add(var7.createSequentialGroup().add(var7.createParallelGroup(1).add(this.resourceLabel).add(this.logLabel).add(this.configurationLabel)).addPreferredGap(0).add(var7.createParallelGroup(1).add(this.configurationField, -1, 771, 32767).add(this.operationField, -1, 771, 32767).add(this.logField, -1, 771, 32767).add(2, this.resourceField, -1, 771, 32767).add(this.localCommsAddressField, -1, 771, 32767)).add(6, 6, 6)).add(var7.createSequentialGroup().add(this.operationLabel).addPreferredGap(0, 775, 32767))).addPreferredGap(0).add(var7.createParallelGroup(2).add(var7.createParallelGroup(1).add(this.resourceBrowse).add(this.logBrowse).add(this.configurationBrowse)).add(this.operationBrowse))).add(this.localCommsAddressLabel).add(var7.createSequentialGroup().add(this.jPanel1, -2, -1, -2).addPreferredGap(0, -1, 32767).add(this.goButton))).addContainerGap()));
      var7.setVerticalGroup(var7.createParallelGroup(1).add(var7.createSequentialGroup().addContainerGap().add(var7.createParallelGroup(3).add(this.operationLabel).add(this.operationBrowse).add(this.operationField, -2, -1, -2)).addPreferredGap(0).add(var7.createParallelGroup(1).add(var7.createParallelGroup(3).add(this.resourceLabel).add(this.resourceBrowse)).add(var7.createSequentialGroup().add(3, 3, 3).add(this.resourceField, -2, -1, -2))).addPreferredGap(0).add(var7.createParallelGroup(3).add(this.logLabel).add(this.logBrowse).add(this.logField, -2, -1, -2)).addPreferredGap(0).add(var7.createParallelGroup(3).add(this.configurationLabel).add(this.configurationBrowse).add(this.configurationField, -2, -1, -2)).addPreferredGap(0).add(var7.createParallelGroup(2).add(var7.createSequentialGroup().add(var7.createParallelGroup(3).add(this.localCommsAddressLabel).add(this.localCommsAddressField, -2, -1, -2)).addPreferredGap(0).add(this.jPanel1, -2, -1, -2)).add(this.goButton)).addContainerGap(-1, 32767)));
      this.pack();
   }

   private void replayOptionActionPerformed(ActionEvent var1) {
      if (this.replayOption.isSelected()) {
         this.loadPrevious.setSelected(true);
      }

      if (this.replayOption.isSelected()) {
         this.themeSelector.setModel(this.replayOperationThemes);
      } else {
         this.themeSelector.setModel(this.liveOperationThemes);
      }

   }

   private void liveOptionActionPerformed(ActionEvent var1) {
      if (this.liveOption.isSelected()) {
         this.loadPrevious.setSelected(false);
      }

      if (this.replayOption.isSelected()) {
         this.themeSelector.setModel(this.replayOperationThemes);
      } else {
         this.themeSelector.setModel(this.liveOperationThemes);
      }

   }

   private void operationBrowseActionPerformed(ActionEvent var1) {
      if (this.setDirectory(this.operationField, "Select the operations disk")) {
         this.infer(new File(this.operationField.getText()));
      }

   }

   private void goButtonActionPerformed(ActionEvent var1) {
      this.DanderSpritzBegin();
   }

   private void configurationBrowseActionPerformed(ActionEvent var1) {
      if (this.setDirectory(this.configurationField, "Select the configuration directory")) {
         this.infer(new File(this.configurationField.getText() + "/../"));
      }

   }

   private void logBrowseActionPerformed(ActionEvent var1) {
      if (this.setDirectory(this.logField, "Select the log directory")) {
         this.infer(new File(this.logField.getText() + "/../"));
      }

   }

   private void resourceBrowseActionPerformed(ActionEvent var1) {
      if (this.setDirectory(this.resourceField, "Select the resource directory")) {
         this.infer(new File(this.resourceField.getText() + "/../"));
      }

   }

   private void enterPressed(KeyEvent var1) {
      if (var1.getKeyCode() == '\n') {
         this.DanderSpritzBegin();
      } else {
         this.examine();
      }

   }

   boolean setDirectory(JTextField var1, String var2) {
      if (this.directoryFinder == null) {
         JOptionPane.showMessageDialog(this, "The File Selector dialog did not initialize.  You must enter your paths manually.", "File Selector not available", JOptionPane.WARNING_MESSAGE);
         return false;
      } else {
         this.directoryFinder.setDialogTitle(var2);
         if (var1.getText().trim().length() > 0) {
            File var3 = new File(var1.getText().trim());
            this.directoryFinder.setSelectedFile(var3);
            this.directoryFinder.setCurrentDirectory(var3.getParentFile());
         }

         if (this.directoryFinder.showDialog(this, "Select") == 0) {
            var1.setText(this.directoryFinder.getSelectedFile().getAbsolutePath());
            return true;
         } else {
            return false;
         }
      }
   }

   void infer(File file) {
      if (file != null) {
         try {
            if (this.operationField.getText().trim().length() == 0) {
               this.operationField.setText((new File(file.getAbsolutePath())).getCanonicalPath());
            }

            if (this.resourceField.getText().trim().length() == 0) {
               File resourcesDir = new File(file.getAbsolutePath(), "/Resources/");
               this.resourceField.setText(resourcesDir.getCanonicalPath());
               this.searchOutThemes(resourcesDir);
            }

            if (this.logField.getText().trim().length() == 0) {
               this.logField.setText((new File(file.getAbsolutePath(), "/Logs/")).getCanonicalPath());
            }

            if (this.configurationField.getText().trim().length() == 0) {
               this.configurationField.setText((new File(file.getAbsolutePath(), "/UserConfiguration/")).getCanonicalPath());
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }

         this.examine();
      }
   }

   void searchOutThemes(File f) {
      if (f != this.themeSearchRoot) {
         if (f == null) {
            this.themeSelector.setEnabled(false);
            this.themeSelector.setSelectedItem(0);
         } else if (!f.equals(this.themeSearchRoot)) {
            this.themeSearchRoot = f;
            this.liveOperationThemes.removeAllElements();
            this.replayOperationThemes.removeAllElements();
            String var2 = "Gui/Config/";
            Vector var3 = new Vector();
            var3.add(".");
            File[] files = f.listFiles();
            if (files != null) {
               for(File file: files) {
                  if (file.isDirectory()) {
                     var3.add(file.getName());
                  }
               }
            }

            Pattern var17 = Pattern.compile("systemStartup_([^.]+).xml");
            Pattern var18 = Pattern.compile("replay_([^.]+).xml");
            TreeSet var19 = new TreeSet();
            TreeSet var20 = new TreeSet();
            Iterator var9 = var3.iterator();

            while(true) {
               do {
                  String var10;
                  if (!var9.hasNext()) {
                     this.liveOperationThemes.addElement(DSZ_DEFAULT);
                     var9 = var19.iterator();

                     while(var9.hasNext()) {
                        var10 = (String)var9.next();
                        this.liveOperationThemes.addElement(var10);
                     }

                     this.replayOperationThemes.addElement(DSZ_DEFAULT);
                     var9 = var20.iterator();

                     while(var9.hasNext()) {
                        var10 = (String)var9.next();
                        this.replayOperationThemes.addElement(var10);
                     }

                     return;
                  }

                  var10 = (String)var9.next();
                  File var11 = new File(this.themeSearchRoot, String.format("%s/%s", var10, var2));
                  files = var11.listFiles();
               } while(files == null);

               File[] var12 = files;
               int var13 = files.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  File var15 = var12[var14];
                  if (var15.isFile()) {
                     Matcher var16 = var17.matcher(var15.getName());
                     if (var16.matches()) {
                        var19.add(var16.group(1));
                     }

                     var16 = var18.matcher(var15.getName());
                     if (var16.matches()) {
                        var20.add(var16.group(1));
                     }
                  }
               }
            }
         }
      }
   }

   public static void main(String[] args) {
      String lookAndFeel = "com.birosoft.liquid.LiquidLookAndFeel";
      System.setProperty("swing.defaultlaf", lookAndFeel);

      try {
         UIManager.setLookAndFeel(lookAndFeel);
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         Start start = new Start();
         String mode = getStringDefault(LIVE_KEYWORD);
         start.liveOperationThemes.setSelectedItem(DSZ_DEFAULT);
         int i;
         if (mode != null) {
            for(i = 0; i < start.liveOperationThemes.getSize(); ++i) {
               if (mode.equals(start.liveOperationThemes.getElementAt(i))) {
                  start.liveOperationThemes.setSelectedItem(mode);
                  break;
               }
            }
         }

         mode = getStringDefault(REPLAY_KEYWORD);
         start.replayOperationThemes.setSelectedItem(DSZ_DEFAULT);
         if (mode != null) {
            for(  i = 0; i < start.replayOperationThemes.getSize(); ++i) {
               if (mode.equals(start.replayOperationThemes.getElementAt(i))) {
                  start.replayOperationThemes.setSelectedItem(mode);
                  break;
               }
            }
         }

         if (getBooleanDefault(OPERATION_MODE, true)) {
            start.liveOption.setSelected(true);
            start.themeSelector.setModel(start.liveOperationThemes);
         } else {
            start.replayOption.setSelected(true);
            start.themeSelector.setModel(start.replayOperationThemes);
         }

         String resourceFieldText = start.resourceField.getText();
         if (resourceFieldText.endsWith(prop.getProperty(RES_DIR, "Dsz"))) {
            resourceFieldText = resourceFieldText.substring(0, resourceFieldText.lastIndexOf(prop.getProperty(RES_DIR, "Dsz")));
            start.resourceField.setText(resourceFieldText);
         }

         start.operationPanel.setVisible(start.isShowOpType());
         if (start.isShowDebugCore()) {
            start.buildRelease.setSelected(getBooleanDefault(BUILD_TYPE, true));
            start.buildDebug.setSelected(!getBooleanDefault(BUILD_TYPE, true));
         } else {
            start.buildRelease.setSelected(true);
            start.buildDebug.setSelected(false);
         }

         if (start.isShowDebugGui()) {
            start.guiRelease.setSelected(getBooleanDefault(GUI_TYPE, true));
            start.guiDebug.setSelected(!getBooleanDefault(GUI_TYPE, true));
         } else {
            start.guiRelease.setSelected(true);
            start.guiDebug.setSelected(false);
         }

         if (start.isShowLocal()) {
            start.localMode.setSelected(getBooleanDefault(LOCAL_MODE, false));
         } else {
            start.localMode.setSelected(false);
         }

         start.loadPrevious.setSelected(getBooleanDefault(LOAD_PREVIOUS, false));
         if (start.isShowThreadDump()) {
            start.threadDump.setSelected(getBooleanDefault(THREAD_DUMP, false));
         }

         start.waitFor.setSelected(getBooleanDefault(WAIT_FOR_OUTPUT, false));
         boolean loadingDsz = false;

         for (String arg : args) {
            String[] items = arg.split("=", 2);
            if (items.length == 0) {
               doHelp(start);
               System.exit(0);
            }

            String val = items[0].toLowerCase();
            if ("-core".equals(val)) {
               if (items.length != 2) {
                  doHelp(start);
                  return;
               }

               items[1] = items[1].toLowerCase();
               if (!"debug".equals(items[1])) {
                  if (!"release".equals(items[1])) {
                     doHelp(start);
                     return;
                  }

                  start.buildRelease.setSelected(true);
               } else {
                  start.buildDebug.setSelected(true);
               }
            } else if ("-gui".equals(val)) {
               if (items.length != 2) {
                  doHelp(start);
                  return;
               }

               items[1] = items[1].toLowerCase();
               if ("debug".equals(items[1])) {
                  start.guiDebug.setSelected(true);
               } else {
                  if (!"release".equals(items[1])) {
                     doHelp(start);
                     return;
                  }

                  start.guiRelease.setSelected(true);
               }
            } else if ("-debug".equals(val)) {
               start.buildDebug.setSelected(true);
            } else if ("-release".equals(val)) {
               start.buildRelease.setSelected(true);
            } else if ("-local".equals(val)) {
               start.localMode.setSelected(true);
            } else if ("-previous".equals(val)) {
               start.loadPrevious.setSelected(true);
            } else if ("-live".equals(val)) {
               start.liveOption.setSelected(true);
            } else if (!"-replay".equals(val)) {
               if ("-opsdisk".equals(val) && items.length == 2) {
                  start.operationField.setText(items[1]);
               } else if ("-resource".equals(val) && items.length == 2) {
                  start.resourceField.setText(items[1]);
               } else if ("-log".equals(val) && items.length == 2) {
                  start.logField.setText(items[1]);
               } else if ("-config".equals(val) && items.length == 2) {
                  start.configurationField.setText(items[1]);
               } else if ("-load".equals(val)) {
                  loadingDsz = true;
               } else {
                  doHelp(start);
                  System.exit(0);
               }
            } else {
               start.replayOption.setSelected(true);
            }
         }

         if (loadingDsz && start.isReady()) {
            start.DanderSpritzBegin();
         } else {
            start.setVisible(true);
         }
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         JOptionPane.showMessageDialog(null, "Start.jar requires Java SE6", "Incorrect Runtime", JOptionPane.ERROR_MESSAGE);
      }

   }

   public static String getStringDefault(String var0) {
      return getStringDefault(var0, "");
   }

   public static String getStringDefault(String var0, String var1) {
      try {
         return userDefaults.getProperty(var0, var1);
      } catch (Exception var3) {
         return var1;
      }
   }

   public static Boolean getBooleanDefault(String var0) {
      return getBooleanDefault(var0, Boolean.TRUE);
   }

   public static Boolean getBooleanDefault(String var0, Boolean var1) {
      try {
         return Boolean.parseBoolean(userDefaults.getProperty(var0, var1.toString()));
      } catch (Exception var3) {
         return var1;
      }
   }

   public static void setStringDefault(String var0, String var1) {
      if (var1 == null) {
         userDefaults.remove(var0);
      } else {
         userDefaults.setProperty(var0, var1);
      }

   }

   public static void setBooleanDefault(String var0, Boolean var1) {
      if (var1 == null) {
         setStringDefault(var0, null);
      } else {
         setStringDefault(var0, var1.toString());
      }

   }

   public static void doHelp(Start start) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(new StringWriter());
//      new StringBuilder();
      printWriter.println("Help for Start.jar");
      printWriter.println("Start.jar [-previous] [-live|-replay] [-opsdisk=DIR] [-resource=DIR] [-log=DIR] [-config=DIR] [-load]");
      printWriter.println("\t[-previous]:  Automatically load previous operations");
      printWriter.println("\t[-live]:  A live operation");
      printWriter.println("\t[-replay]:  A replay operation");
      printWriter.println("\t[-opsdisk=DIR]:  Set the operations disk value to the given DIR");
      printWriter.println("\t[-resource=DIR]:  Set the resource directory to the given DIR");
      printWriter.println("\t[-log=DIR]:  Set the log directory to the given DIR");
      printWriter.println("\t[-config=DIR]:  Set the user configuration directory to the given DIR");
      if (start.isShowDebugCore() || start.isShowLocal() || start.isShowDebugGui()) {
         printWriter.println("\nExtra Parameters:");
         if (start.isShowDebugCore()) {
            printWriter.println("\t[-core=<debug|release>]");
            printWriter.println("\t\t[debug]: Tells DanderSpritz to load the debug version of the core");
            printWriter.println("\t\t[release]: Tells DanderSpritz to load the release version of the core");
         }

         if (start.isShowDebugGui()) {
            printWriter.println("\t[-gui=<debug|release>]");
            printWriter.println("\t\t[-debug]: Tells DanderSpritz to load the debug version of the gui");
            printWriter.println("\t\t[-release]: Tells DanderSpritz to load the release version of the gui");
         }

         if (start.isShowLocal()) {
            printWriter.println("\t[-local]:  Turns on local mode");
         }
      }

      JTextArea jTextArea = new JTextArea();
      jTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
      jTextArea.setTabSize(4);
      JScrollPane jScrollPane = new JScrollPane(jTextArea);
      Dimension dimension = new Dimension(900, 350);
      jScrollPane.setMinimumSize(dimension);
      jScrollPane.setPreferredSize(dimension);
      jScrollPane.setSize(dimension);
      jScrollPane.setMaximumSize(dimension);
      jTextArea.setText(stringWriter.toString());
      JOptionPane.showMessageDialog(null, jScrollPane, "Start Help", JOptionPane.INFORMATION_MESSAGE);
   }

   private boolean evaluatePath(String path, boolean noCreate) {
      for (char c : INVALIDCHARACTERS) {
         if (path.indexOf(c) >= 0) {
            return false;
         }
      }

      File file = new File(path);
      if (file.exists() && file.isDirectory()) {
         return true;
      } else if (!file.exists()) {
         if (noCreate) {
            return false;
         } else {
            return file.mkdirs();
         }
      } else {
         return false;
      }
   }

   public boolean evaluate() {
      if (!this.evaluatePath(this.operationField.getText(), true)) {
         return this.error("Operation Disk location '" + this.operationField.getText() + "' does not exist or is not a directory");
      } else if (!this.evaluatePath(this.resourceField.getText(), true)) {
         return this.error("Resource location '" + this.resourceField.getText() + "' does not exist or is not a directory");
      } else if (!this.evaluatePath(this.logField.getText(), false)) {
         return this.error("Log directory '" + this.logField.getText() + "' is not a directory");
      } else if (!this.evaluatePath(this.configurationField.getText(), false)) {
         return this.error("Configuration directory '" + this.configurationField.getText() + "' is not a directory");
      } else {
         return !this.isValidId(this.localCommsAddressField.getText()) ? this.error(String.format("Comms Address '%s' is invalid", this.localCommsAddressField.getText())) : true;
      }
   }

   private boolean isValidId(String s) {
      for (Pattern var5 : patterns) {
         if (var5.matcher(s).matches()) {
            return true;
         }
      }

      return false;
   }

   public boolean error(String s) {
      JOptionPane.showMessageDialog(this, s, "Invalid parameters", JOptionPane.ERROR_MESSAGE);
      return false;
   }

   public boolean isReady() {
      return !this.guess && this.evaluate() && this.examine();
   }

   public boolean isDir(String dir) {
      if (dir == null) {
         return false;
      } else if (dir.length() == 0) {
         return false;
      } else {
         File file = new File(dir);
         return file.exists() ? file.isDirectory() : file.mkdirs();
      }
   }

   public void DanderSpritzBegin() {
      if (this.evaluate()) {
         (new Thread(() -> Start.this.beginImpl(), "Start DanderSpritz")).start();
      }
   }

   private void beginImpl() {
      File testfile = new File(this.configurationField.getText(), "testfile.dsz");
      File dir = testfile.getParentFile();
      boolean canWrite = false;
      dir.mkdirs();
      if (testfile.exists()) {
         if (testfile.delete()) {
            canWrite = true;
         }
      } else {
         try {
            FileOutputStream fileOutputStream = new FileOutputStream(testfile);
            fileOutputStream.write(90);
            fileOutputStream.close();
            testfile.delete();
            canWrite = true;
         } catch (Exception e) {
         }
      }

      if (!canWrite) {
         int selection = JOptionPane.showConfirmDialog(null, "Danderspritz is unable to write to the user configuration area.\nWithout the ability to write there, several plugins will not work.\nDanderspritz will now change the user configuration area to your temp directory,\nand you will lose any existing customization you have performed.\nSelect 'No' to ignore this problem, or 'Cancel' to change the directory.", "User Configuration is Read-Only", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
         switch(selection) {
         case 0:
            dir = new File(System.getProperty("java.io.tmpdir"), "UserConfiguration");
         case 1:
         default:
            break;
         case 2:
            return;
         }
      }

      EventQueue.invokeLater(() -> {
         Start.this.setVisible(false);
         Start.this.dispose();
      });
      String jmxremotePort = System.getProperty("com.sun.management.jmxremote.port");
      if (jmxremotePort != null) {
         File var5 = new File(this.logField.getText(), String.format("Dump-%s.txt", jmxremotePort));
         if (!var5.exists()) {
            try {
               FileOutputStream var6 = new FileOutputStream(var5);
               var6.close();
            } catch (Exception var25) {
               Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, var25);
            }
         }
      }

      setStringDefault(OPERATION_DISK, this.operationField.getText());
      setStringDefault(RESOURCE_DIR, this.resourceField.getText());
      setStringDefault(LOG_DIR, this.logField.getText());
      setStringDefault(CONFIG_DIR, this.configurationField.getText());
      setStringDefault(LOCAL_ADDRESS, this.localCommsAddressField.getText());
      if (this.liveOperationThemes.getSelectedItem() != null && !DSZ_DEFAULT.equals(this.liveOperationThemes.getSelectedItem())) {
         setStringDefault(LIVE_KEYWORD, this.liveOperationThemes.getSelectedItem().toString());
      } else {
         setStringDefault(LIVE_KEYWORD, null);
      }

      if (this.replayOperationThemes.getSelectedItem() != null && !DSZ_DEFAULT.equals(this.replayOperationThemes.getSelectedItem())) {
         setStringDefault(REPLAY_KEYWORD, this.replayOperationThemes.getSelectedItem().toString());
      } else {
         setStringDefault(REPLAY_KEYWORD, null);
      }

      setBooleanDefault(OPERATION_MODE, this.liveOption.isSelected());
      setBooleanDefault(BUILD_TYPE, this.buildRelease.isSelected());
      setBooleanDefault(GUI_TYPE, this.guiRelease.isSelected());
      setBooleanDefault(LOCAL_MODE, this.localMode.isSelected());
      setBooleanDefault(LOAD_PREVIOUS, this.loadPrevious.isSelected());
      setBooleanDefault(THREAD_DUMP, this.threadDump.isSelected());
      setBooleanDefault(WAIT_FOR_OUTPUT, this.waitFor.isSelected());

      try {
         userDefaults.store(new FileOutputStream("user.defaults"), "Autogenerated DanderSpritz configuration.  Do not edit manually");
      } catch (Exception e) {
      }

      ProcessBuilder processBuilder = new ProcessBuilder();
      Vector vector = new Vector();
      processBuilder.command(vector);
      String resourceFieldText = this.resourceField.getText();
      vector.add(prop.getProperty(JAVA_EXE, "java"));
      String[] vmargs = prop.getProperty(VMARGS, "").split("\\s");

      for(String arg :  vmargs) {
         if (arg.length() > 0) {
            vector.add(arg);
         }
      }

      if (this.guiDebug.isSelected()) {
         vmargs = prop.getProperty(DEBUGVMARGS, "").split("\\s");

         for(String arg :  vmargs) {
            if (arg.length() > 0) {
               vector.add(arg);
            }
         }
      }

      vector.add(String.format("-Djava.endorsed.dirs=%s/ExternalLibraries/%s/endorsed", resourceFieldText, "java-j2se_1.6-sun"));
      Vector jarsVector = new Vector();
      Vector<String> resDirVector = new Vector<>();
      this.addJars(jarsVector, new File(String.format("%s/ExternalLibraries/%s", resourceFieldText, "java-j2se_1.6-sun")));
      resDirVector.add("Ops");
      resDirVector.add(".");
      resDirVector.add(prop.getProperty(RES_DIR, "Dsz"));
      File[] resourceFiles = (new File(resourceFieldText)).listFiles();

      for( File f: resourceFiles) {
         if (f.isDirectory() && !resDirVector.contains(f.getName())) {
            resDirVector.add(f.getName());
         }
      }

      for (String s : resDirVector) {
         File guiDir = new File(String.format("%s/%s/Gui", resourceFieldText, s));
         if (guiDir.exists()) {
            File config = new File(guiDir, "Config");
            if (config.exists()) {
               jarsVector.add(config.getAbsolutePath());
            }

            File lib = new File(guiDir, String.format("%s/%s", "lib", "java-j2se_1.6-sun"));
            if (lib.exists()) {
               this.addJars(jarsVector, lib);
            }
         }
      }

      boolean win = System.getProperty("os.name").toLowerCase().startsWith(prop.getProperty("windows.start", "win"));

      URL[] urls = new URL[jarsVector.size()];

      for(int i = 0; i < jarsVector.size(); ++i) {
         try {
            urls[i] = (new File((String)jarsVector.get(i))).toURI().toURL();
         } catch (MalformedURLException e) {
            e.printStackTrace();
         }
      }

      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      boolean initClassLoader = false;
      if (classLoader instanceof URLClassLoader) {
         try {
            URLClassLoader loader = (URLClassLoader)classLoader;
            Class clazz = URLClassLoader.class;
            Field ucp = clazz.getDeclaredField("ucp");
            ucp.setAccessible(true);
            URLClassPath urlClassPath = (URLClassPath)ucp.get(loader);

            for (URL url : urls) {
               urlClassPath.addURL(url);
            }

            initClassLoader = true;
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (!initClassLoader) {
         classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
      }

      Vector<String> args = new Vector<String>();
      args.add(String.format("-logDir=%s", this.logField.getText()));
      args.add(String.format("-resourceDir=%s", resourceFieldText));
      args.add(String.format("-comms=%s", this.localCommsAddressField.getText()));
      args.add(String.format("-build=%s", prop.getProperty(String.format("%s.%s", win ? "windows" : "linux", this.buildRelease.isSelected() ? "build.release" : "build.debug"))));
      args.add(String.format("-local=%s", this.localMode.isSelected() ? "true" : "false"));
      args.add(String.format("-config=%s", dir.getAbsolutePath()));
      args.add(String.format("-loadPrevious=%s", this.loadPrevious.isSelected() ? "true" : "false"));
      args.add(String.format("-threadDump=%s", this.threadDump.isSelected() ? "true" : "false"));
      String operation;
      if (this.liveOption.isSelected()) {
         operation = prop.getProperty("live.operation");
      } else {
         operation = prop.getProperty("replay.operation");
      }

      if (win) {
         addLibraryPath(String.format("%s\\ExternalLibraries\\%s", resourceFieldText, prop.getProperty(String.format("%s.%s", "windows", TOOL_CHAIN_STR))));
      } else {
         addLibraryPath(String.format("%s/ExternalLibraries/%s", resourceFieldText, prop.getProperty(String.format("%s.%s", "linux", TOOL_CHAIN_STR))));
      }

      System.setProperty("windows.tool.chain", prop.getProperty(String.format("%s.%s", "windows", TOOL_CHAIN_STR)));
      System.setProperty("linux.tool.chain", prop.getProperty(String.format("%s.%s", "linux", TOOL_CHAIN_STR)));
      if (this.themeSelector.getSelectedItem() != null && !DSZ_DEFAULT.equals(this.themeSelector.getSelectedItem())) {
         System.setProperty(DSZ_KEYWORD, this.themeSelector.getSelectedItem().toString());
      }

      try {
         Thread.currentThread().setContextClassLoader((ClassLoader)classLoader);
         Class operationClazz = Class.forName(operation, true, classLoader);
         Class[] argTypes = new Class[] { String[].class };
         Method main = operationClazz.getMethod("main", argTypes);
         Class constantsClazz = Class.forName("ds.core.DSConstants", true, classLoader);
         Method setLoader = constantsClazz.getMethod("setLoader", ClassLoader.class);
         setLoader.invoke(null, classLoader);
         String[] mainArgs = new String[args.size()];
         mainArgs = args.toArray(mainArgs);
         main.invoke(null, (Object)mainArgs);
      } catch (Exception e) {
         e.printStackTrace();
         JOptionPane.showMessageDialog((Component)null, "Unable to start DanderSpritz.  The OpsDisk appears incomplete.", "Invalid OpsDisk", JOptionPane.ERROR_MESSAGE);
         this.examine();
         this.setVisible(true);
      }

   }

   private void addJars(List<String> list, File f) {
      if (f.isDirectory()) {
         File[] files = f.listFiles(jars);

         for (File file : files) {
            list.add(file.getAbsolutePath());
         }
      }
   }

   private void addJarsRecursively(List<String> list, File f) {
      if (f.isDirectory()) {
         this.addJars(list, f);
         File[] files = f.listFiles();

         for (File file : files) {
            if (file.isDirectory() && !".svn".equals(file.getName())) {
               this.addJarsRecursively(list, file);
            }
         }

      }
   }

   public boolean isShowOpType() {
      return this.getBooleanProperty("show.optype", true);
   }

   public boolean isShowDebugCore() {
      return this.getBooleanProperty("show.debug.core", false);
   }

   public boolean isShowDebugGui() {
      return this.getBooleanProperty("show.debug.gui", false);
   }

   public boolean isShowLocal() {
      return this.getBooleanProperty("show.local.mode", false);
   }

   public boolean isShowThreadDump() {
      return this.getBooleanProperty("show.thread.dump", false);
   }

   public boolean getBooleanProperty(String var1, Boolean var2) {
      try {
         return Boolean.parseBoolean(prop.getProperty(var1, var2.toString()));
      } catch (Throwable var4) {
         return var2;
      }
   }

   public static void addLibraryPath(String lib) {
      String path;

      try {
         path = (new File(lib)).getCanonicalPath();
      } catch (IOException e) {
         e.printStackTrace();
         return;
      }

      try {
         Field usr_paths = ClassLoader.class.getDeclaredField("usr_paths");
         usr_paths.setAccessible(true);
         String[] paths = (String[]) usr_paths.get(null);

         for(String p: paths) {
            if (p.equals(path)) {
               return;
            }
         }

         String[] paths_append  = new String[paths.length + 1];
         System.arraycopy(paths, 0, paths_append, 0, paths.length);
         paths_append[paths.length] = path;
         usr_paths.set(null, paths_append);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
      }

   }

   private boolean examine() {
      Vector<File> operationVector = new Vector<>();
      Vector<File> resourceVector = new Vector<>();
      Vector configVector = new Vector();
      Vector logVector = new Vector();
      operationVector.add(new File(String.format("%s", this.operationField.getText())));
      operationVector.add(new File(String.format("%s%s%s", this.operationField.getText(), File.separator, "Bin")));
      resourceVector.add(new File(String.format("%s", this.resourceField.getText())));
      // 我们是代码，木有core.jar
//      resourceVector.add(new File(String.format("%s/%s/%s/%s/%s", this.resourceField.getText(), File.separator, "Dsz/Gui/lib", "java-j2se_1.6-sun", "Core.jar")));
      boolean ret = this.examine(operationVector, this.operationField);

      if (!this.examine(resourceVector, this.resourceField)) {
         ret = false;
      } else {
         this.searchOutThemes(new File(this.resourceField.getText()));
      }

      if (!this.examine(configVector, this.configurationField)) {
         ret = false;
      }

      if (!this.examine(logVector, this.logField)) {
         ret = false;
      }

      if (!this.isValidId(this.localCommsAddressField.getText())) {
         this.setConfig(this.localCommsAddressField, false);
         ret = false;
      } else {
         this.setConfig(this.localCommsAddressField, true);
      }

      this.goButton.setEnabled(ret);
      return ret;
   }

   private void setConfig(JTextField var1, boolean valid) {
      if (valid) {
         var1.setBackground(Color.WHITE);
         var1.setForeground(Color.BLACK);
      } else {
         var1.setBackground(Color.GRAY);
         var1.setForeground(Color.WHITE);
      }

   }

   private boolean examine(List<File> list, JTextField textField) {
      boolean ret = true;

      for (char c : INVALIDCHARACTERS) {
         if (textField.getText().indexOf(c) >= 0) {
            ret = false;
            break;
         }
      }

      File f;
      for(Iterator var8 = list.iterator(); var8.hasNext(); ret = ret && f.exists()) {
         f = (File)var8.next();
      }


      this.setConfig(textField, ret);
      return ret;
   }
}
