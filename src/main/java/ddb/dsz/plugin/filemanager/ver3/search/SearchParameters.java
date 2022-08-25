package ddb.dsz.plugin.filemanager.ver3.search;

import ddb.dsz.core.host.HostInfo;
import ddb.imagemanager.ImageManager;
import ddb.targetmodel.filemodel.FileObject;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.layout.GroupLayout;

public class SearchParameters extends JPanel {
   DefaultComboBoxModel types = new DefaultComboBoxModel();
   DefaultListCellRenderer typesRenderer = new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var6 instanceof JLabel && var2 != null) {
            JLabel var7 = (JLabel)var6;
            if (var2.equals(Boolean.TRUE)) {
               var7.setText("(All Files and Folders");
               var7.setIcon((Icon)null);
            }

            if (var2 instanceof MimeTypeMap.MimeType) {
               var7.setText(((MimeTypeMap.MimeType)MimeTypeMap.MimeType.class.cast(var2)).getName());
               var7.setIcon(ImageManager.getIcon(((MimeTypeMap.MimeType)MimeTypeMap.MimeType.class.cast(var2)).getIcon(), ImageManager.SIZE16));
            }
         }

         return var6;
      }
   };
   DefaultComboBoxModel sizeTypes = new DefaultComboBoxModel();
   DefaultComboBoxModel sizeChoices = new DefaultComboBoxModel();
   DefaultListCellRenderer sizeRenderer = new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var6 instanceof JLabel && var2 != null) {
            JLabel var7 = (JLabel)var6;
            if (var2 instanceof Boolean) {
               if (Boolean.TRUE.equals(var2)) {
                  var7.setText("at least");
               } else {
                  var7.setText("at most");
               }
            }

            if (var2 instanceof SearchParameters.SizeMultiplier) {
               var7.setText(((SearchParameters.SizeMultiplier)SearchParameters.SizeMultiplier.class.cast(var2)).getSuffix());
            }
         }

         return var6;
      }
   };
   DefaultComboBoxModel fileSearchRoot = new DefaultComboBoxModel();
   DefaultListCellRenderer fileSearchRenderer = new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var6 instanceof JLabel && var2 != null) {
            JLabel var7 = (JLabel)var6;
            if (var2 instanceof Boolean) {
               var7.setText("Target Computer");
               var7.setIcon((Icon)null);
            }

            if (var2 instanceof FileObject) {
               String var8 = ((FileObject)var2).getPath() + "/" + ((FileObject)var2).getName();
               var8 = var8.substring(0, 5).equals("null/") ? var8.substring(5) : var8;
               if (var8.length() > 34) {
                  String var9 = var8.substring(var8.length() - 28, var8.length());
                  int var10 = var9.indexOf("/") > -1 ? var9.indexOf("/") + 1 : 0;
                  var9 = var9.substring(var10);
                  var8 = var8.substring(0, 3) + ".../" + var9;
               }

               var7.setText(var8);
               var7.setIcon(ImageManager.getIcon(((FileObject)FileObject.class.cast(var2)).getIcon(), ImageManager.SIZE16));
            }
         }

         return var6;
      }
   };
   Closure startSearching = ClosureUtils.nopClosure();
   Closure stopSearching = ClosureUtils.nopClosure();
   private ButtonGroup DateOptions;
   private JCheckBox caseCheckBox;
   private JRadioButton dateBetween;
   private JRadioButton dateLastDays;
   private JRadioButton dateLastMonths;
   private JPanel dateOptions;
   private JComboBox dateType;
   private JComboBox hostListing;
   private JCheckBox isDate;
   private JCheckBox isSize;
   private JCheckBox isType;
   private JCheckBox jCheckBox1;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JSpinner lastDays;
   private JSpinner lastMonths;
   private JButton searchCancel;
   private JButton searchGo;
   private JComboBox searchMethod;
   private JPanel searchOptions;
   private JTextField searchPattern;
   private JComboBox searchRoot;
   private ButtonGroup searchType;
   private JComboBox sizeMultiplier;
   private JPanel sizeOptions;
   private JComboBox sizeScopeCombo;
   private JSpinner sizeSize;
   private JSpinner startDate;
   private JSpinner stopDate;
   private JComboBox typeCombo;
   private JPanel typeOptions;
   private BindingGroup bindingGroup;

   public SearchParameters(List<HostInfo> var1, HostInfo var2) {
      this.initComponents();
      this.isDateStateChanged((ChangeEvent)null);
      this.isTypeStateChanged((ChangeEvent)null);
      this.isSizeStateChanged((ChangeEvent)null);
      this.types.addElement(Boolean.TRUE);
      Vector var3 = new Vector();
      var3.addAll(MimeTypeMap.getMimeTypes());
      Collections.sort(var3);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         MimeTypeMap.MimeType var5 = (MimeTypeMap.MimeType)var4.next();
         this.types.addElement(var5);
      }

      this.hostListing.setModel(new DefaultComboBoxModel(var1.toArray()));
      this.hostListing.setSelectedItem(var2);
      this.hostListing.setRenderer(new DefaultListCellRenderer() {
         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            Component var6 = super.getListCellRendererComponent(var1, var2, var3, var4, var5);
            if (var6 instanceof JLabel && var2 instanceof HostInfo) {
               JLabel var7 = (JLabel)var6;
               HostInfo var8 = (HostInfo)var2;
               var7.setText(String.format("%s:  %s/%s/%s", var8.getId(), var8.getArch(), var8.getPlatform(), var8.getVersion()));
            }

            return var6;
         }
      });
      this.types.setSelectedItem(Boolean.TRUE);
      this.sizeTypes.addElement(Boolean.TRUE);
      this.sizeTypes.addElement(Boolean.FALSE);
      this.sizeTypes.setSelectedItem(Boolean.TRUE);
      SearchParameters.SizeMultiplier[] var8 = SearchParameters.SizeMultiplier.values();
      int var10 = var8.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         SearchParameters.SizeMultiplier var7 = var8[var6];
         this.sizeChoices.addElement(var7);
      }

      this.sizeChoices.setSelectedItem(SearchParameters.SizeMultiplier.Kilobytes);
      this.fileSearchRoot.addElement(Boolean.TRUE);
      this.fileSearchRoot.setSelectedItem(Boolean.TRUE);
      Calendar var9 = Calendar.getInstance();
      this.stopDate.setValue(var9.getTime());
      var9.add(5, -14);
      this.startDate.setValue(var9.getTime());
   }

   public void setSearchActions(Closure var1, Closure var2) {
      this.startSearching = var1;
      this.stopSearching = var2;
   }

   public void searchingStarted() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            SearchParameters.this.searchGo.setEnabled(false);
            SearchParameters.this.searchCancel.setEnabled(true);
         }
      });
   }

   public void searchingStopped() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            SearchParameters.this.searchGo.setEnabled(true);
            SearchParameters.this.searchCancel.setEnabled(false);
         }
      });
   }

   public HostInfo getHost() {
      return (HostInfo)this.hostListing.getSelectedItem();
   }

   public void addSearchRoot(FileObject var1, boolean var2) {
      if (var1 != null) {
         boolean var3 = false;

         for(int var4 = 0; var4 < this.fileSearchRoot.getSize() && !var3; ++var4) {
            Object var5 = this.fileSearchRoot.getElementAt(var4);
            if (var5 instanceof FileObject && ((FileObject)FileObject.class.cast(var5)).getId() == var1.getId()) {
               var3 = true;
               var1 = (FileObject)var5;
            }
         }

         if (!var3) {
            this.fileSearchRoot.addElement(var1);
         }

         if (var2) {
            this.fileSearchRoot.setSelectedItem(var1);
         }

      }
   }

   boolean configure(Searcher var1) {
      var1.name = this.searchPattern.getText();
      if (this.searchRoot.getSelectedItem() instanceof FileObject) {
         var1.searchRoot = (FileObject)FileObject.class.cast(this.searchRoot.getSelectedItem());
      } else {
         var1.searchRoot = null;
      }

      if (this.isDate.isSelected()) {
         var1.dateType = (DateSearchType)this.dateType.getSelectedItem();
         if (this.dateLastMonths.isSelected()) {
            var1.lastDays = (Integer)Integer.class.cast(this.lastMonths.getValue()) * 30;
         } else if (this.dateLastDays.isSelected()) {
            var1.lastDays = (Integer)Integer.class.cast(this.lastDays.getValue());
         } else if (this.dateBetween.isSelected()) {
            var1.spanStart = Calendar.getInstance();
            var1.spanStop = Calendar.getInstance();
            var1.spanStart.setTime((Date)this.startDate.getValue());
            var1.spanStop.setTime((Date)this.stopDate.getValue());
         }
      }

      if (this.caseCheckBox.isSelected()) {
         var1.isCaseSensitive = true;
      } else {
         var1.isCaseSensitive = false;
      }

      if (this.searchMethod.getSelectedItem() == Searcher.SearchType.GLOB) {
         var1.searchType = Searcher.SearchType.GLOB;
      } else if (this.searchMethod.getSelectedItem() == Searcher.SearchType.REGEX) {
         try {
            if (var1.isCaseSensitive) {
               var1.regexPattern = Pattern.compile(this.searchPattern.getText());
            } else {
               var1.regexPattern = Pattern.compile(this.searchPattern.getText().toLowerCase());
            }
         } catch (Exception var3) {
            return false;
         }

         var1.searchType = Searcher.SearchType.REGEX;
      }

      if (this.isType.isSelected()) {
         Object var2 = this.typeCombo.getSelectedItem();
         if (var2 instanceof MimeTypeMap.MimeType) {
            var1.mimeType = (MimeTypeMap.MimeType)var2;
         }
      }

      if (this.isSize.isSelected()) {
         SearchParameters.SizeMultiplier var4 = (SearchParameters.SizeMultiplier)this.sizeMultiplier.getSelectedItem();
         if (this.sizeScopeCombo.getSelectedItem().equals(Boolean.TRUE)) {
            var1.minimumSize = var4.getSize() * (long)(Integer)Integer.class.cast(this.sizeSize.getValue());
         } else {
            var1.maximumSize = var4.getSize() * (long)(Integer)Integer.class.cast(this.sizeSize.getValue());
         }
      }

      return true;
   }

   private void initComponents() {
      this.bindingGroup = new BindingGroup();
      this.DateOptions = new ButtonGroup();
      this.searchType = new ButtonGroup();
      this.jCheckBox1 = new JCheckBox();
      this.jLabel1 = new JLabel();
      this.searchPattern = new JTextField();
      this.jLabel2 = new JLabel();
      this.searchRoot = new JComboBox();
      this.searchGo = new JButton();
      this.searchCancel = new JButton();
      this.searchOptions = new JPanel();
      this.isDate = new JCheckBox();
      this.dateOptions = new JPanel();
      this.dateType = new JComboBox();
      this.dateLastMonths = new JRadioButton();
      this.lastMonths = new JSpinner();
      this.dateLastDays = new JRadioButton();
      this.dateBetween = new JRadioButton();
      this.lastDays = new JSpinner();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.startDate = new JSpinner();
      this.jLabel5 = new JLabel();
      this.stopDate = new JSpinner();
      this.isType = new JCheckBox();
      this.typeOptions = new JPanel();
      this.typeCombo = new JComboBox();
      this.isSize = new JCheckBox();
      this.sizeOptions = new JPanel();
      this.sizeScopeCombo = new JComboBox();
      this.sizeSize = new JSpinner();
      this.sizeMultiplier = new JComboBox();
      this.jLabel6 = new JLabel();
      this.hostListing = new JComboBox();
      this.jLabel7 = new JLabel();
      this.searchMethod = new JComboBox();
      this.caseCheckBox = new JCheckBox();
      this.jCheckBox1.setText("jCheckBox1");
      this.jLabel1.setText("Search for files or folders:");
      this.searchPattern.setToolTipText("A string the file name contains (.xml, etc)");
      this.searchPattern.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent var1) {
            SearchParameters.this.searchPatternKeyReleased(var1);
         }
      });
      this.jLabel2.setText("Look In:");
      this.searchRoot.setModel(this.fileSearchRoot);
      this.searchRoot.setRenderer(this.fileSearchRenderer);
      this.searchGo.setText("Search Now");
      this.searchGo.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            SearchParameters.this.searchGoActionPerformed(var1);
         }
      });
      this.searchCancel.setText("Stop Search");
      this.searchCancel.setEnabled(false);
      this.searchCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            SearchParameters.this.searchCancelActionPerformed(var1);
         }
      });
      this.searchOptions.setBorder(BorderFactory.createTitledBorder("Search Options"));
      this.isDate.setText("Date");
      this.isDate.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            SearchParameters.this.isDateStateChanged(var1);
         }
      });
      this.dateOptions.setBorder(BorderFactory.createEtchedBorder());
      this.dateType.setModel(DateSearchType.getModel());
      this.dateType.setSelectedItem(DateSearchType.Modified);
      this.DateOptions.add(this.dateLastMonths);
      this.dateLastMonths.setSelected(true);
      this.dateLastMonths.setText("in the last");
      this.lastMonths.setModel(new SpinnerNumberModel(1, 1, (Comparable)null, 1));
      AutoBinding var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.dateLastMonths, ELProperty.create("${selected}"), this.lastMonths, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.DateOptions.add(this.dateLastDays);
      this.dateLastDays.setText("in the last");
      this.DateOptions.add(this.dateBetween);
      this.dateBetween.setText("between");
      this.lastDays.setModel(new SpinnerNumberModel(1, 1, (Comparable)null, 1));
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.dateLastDays, ELProperty.create("${selected}"), this.lastDays, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.jLabel3.setText("months");
      this.jLabel4.setText("days");
      this.startDate.setModel(new SpinnerDateModel());
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.dateBetween, ELProperty.create("${selected}"), this.startDate, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      this.jLabel5.setText("and");
      this.stopDate.setModel(new SpinnerDateModel());
      var1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this.dateBetween, ELProperty.create("${selected}"), this.stopDate, BeanProperty.create("enabled"));
      this.bindingGroup.addBinding(var1);
      GroupLayout var2 = new GroupLayout(this.dateOptions);
      this.dateOptions.setLayout(var2);
      var2.setHorizontalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().addContainerGap().add(var2.createParallelGroup(2).add(1, this.dateType, 0, -1, 32767).add(1, var2.createSequentialGroup().add(this.dateLastMonths).addPreferredGap(0).add(this.lastMonths).addPreferredGap(0).add(this.jLabel3).add(11, 11, 11)).add(1, var2.createSequentialGroup().add(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(var2.createParallelGroup(1).add(this.dateBetween).add(var2.createSequentialGroup().add(21, 21, 21).add(this.jLabel5))).add(8, 8, 8)).add(var2.createSequentialGroup().add(this.dateLastDays, -1, -1, 32767).addPreferredGap(0))).add(var2.createParallelGroup(1).add(var2.createSequentialGroup().add(this.lastDays).addPreferredGap(0).add(this.jLabel4).add(25, 25, 25)).add(this.startDate).add(this.stopDate)))).add(13, 13, 13)));
      var2.setVerticalGroup(var2.createParallelGroup(1).add(var2.createSequentialGroup().addContainerGap().add(this.dateType, -2, -1, -2).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.dateLastMonths).add(this.lastMonths, -2, -1, -2).add(this.jLabel3)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.dateLastDays).add(this.lastDays, -2, -1, -2).add(this.jLabel4)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.dateBetween).add(this.startDate, -2, -1, -2)).addPreferredGap(0).add(var2.createParallelGroup(3).add(this.stopDate, -2, -1, -2).add(this.jLabel5)).addContainerGap(-1, 32767)));
      this.isType.setText("Type");
      this.isType.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            SearchParameters.this.isTypeStateChanged(var1);
         }
      });
      this.typeOptions.setBorder(BorderFactory.createEtchedBorder());
      this.typeCombo.setModel(this.types);
      this.typeCombo.setRenderer(this.typesRenderer);
      GroupLayout var3 = new GroupLayout(this.typeOptions);
      this.typeOptions.setLayout(var3);
      var3.setHorizontalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().addContainerGap().add(this.typeCombo, 0, -1, 32767).add(21, 21, 21)));
      var3.setVerticalGroup(var3.createParallelGroup(1).add(var3.createSequentialGroup().addContainerGap().add(this.typeCombo, -2, -1, -2).addContainerGap(-1, 32767)));
      this.isSize.setText("Size");
      this.isSize.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
            SearchParameters.this.isSizeStateChanged(var1);
         }
      });
      this.sizeOptions.setBorder(BorderFactory.createEtchedBorder());
      this.sizeScopeCombo.setModel(this.sizeTypes);
      this.sizeScopeCombo.setRenderer(this.sizeRenderer);
      this.sizeSize.setModel(new SpinnerNumberModel(0, 0, (Comparable)null, 1));
      this.sizeMultiplier.setModel(this.sizeChoices);
      this.sizeMultiplier.setRenderer(this.sizeRenderer);
      GroupLayout var4 = new GroupLayout(this.sizeOptions);
      this.sizeOptions.setLayout(var4);
      var4.setHorizontalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().addContainerGap().add(this.sizeScopeCombo, 0, -1, 32767).addPreferredGap(0).add(this.sizeSize).addPreferredGap(0).add(this.sizeMultiplier, 0, -1, 32767).addContainerGap()));
      var4.setVerticalGroup(var4.createParallelGroup(1).add(var4.createSequentialGroup().addContainerGap().add(var4.createParallelGroup(3).add(this.sizeScopeCombo, -2, -1, -2).add(this.sizeSize, -2, -1, -2).add(this.sizeMultiplier, -2, -1, -2)).addContainerGap(-1, 32767)));
      GroupLayout var5 = new GroupLayout(this.searchOptions);
      this.searchOptions.setLayout(var5);
      var5.setHorizontalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(var5.createParallelGroup(1).add(var5.createSequentialGroup().addContainerGap().add(this.isType)).add(var5.createSequentialGroup().addContainerGap().add(this.isDate)).add(var5.createSequentialGroup().addContainerGap().add(this.isSize)).add(2, var5.createSequentialGroup().add(27, 27, 27).add(var5.createParallelGroup(2).add(1, this.dateOptions, -1, -1, 32767).add(1, this.typeOptions, -1, -1, 32767).add(1, this.sizeOptions, -1, -1, 32767)))).addContainerGap()));
      var5.setVerticalGroup(var5.createParallelGroup(1).add(var5.createSequentialGroup().add(this.isDate).addPreferredGap(0).add(this.dateOptions, -2, -1, -2).addPreferredGap(0).add(this.isType).addPreferredGap(0).add(this.typeOptions, -2, -1, -2).addPreferredGap(0).add(this.isSize).addPreferredGap(0).add(this.sizeOptions, -2, -1, -2).addContainerGap()));
      this.jLabel6.setText("Host:");
      this.hostListing.setModel(new DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
      this.jLabel7.setText("Search method:");
      this.searchMethod.setModel(new DefaultComboBoxModel(Searcher.SearchType.values()));
      this.caseCheckBox.setText("Case Sensitive");
      GroupLayout var6 = new GroupLayout(this);
      this.setLayout(var6);
      var6.setHorizontalGroup(var6.createParallelGroup(1).add(var6.createSequentialGroup().addContainerGap().add(var6.createParallelGroup(1).add(this.searchOptions, -1, -1, 32767).add(this.jLabel1, -1, -1, 32767).add(this.searchRoot, 0, -1, 32767).add(this.searchPattern).add(this.hostListing, 0, -1, 32767).add(var6.createSequentialGroup().add(var6.createParallelGroup(1).add(this.jLabel2).add(this.jLabel6).add(var6.createSequentialGroup().add(this.searchGo).addPreferredGap(0).add(this.searchCancel)).add(this.jLabel7)).add(0, 0, 32767)).add(var6.createSequentialGroup().add(this.searchMethod, -2, -1, -2).add(42, 42, 42).add(this.caseCheckBox, -1, -1, 32767))).addContainerGap()));
      var6.setVerticalGroup(var6.createParallelGroup(1).add(var6.createSequentialGroup().addContainerGap().add(this.jLabel1).addPreferredGap(0).add(this.searchPattern, -2, -1, -2).addPreferredGap(0).add(this.jLabel7).addPreferredGap(0).add(var6.createParallelGroup(3).add(this.searchMethod, -2, -1, -2).add(this.caseCheckBox)).addPreferredGap(0).add(this.jLabel2).addPreferredGap(0).add(this.searchRoot, -2, -1, -2).addPreferredGap(0).add(this.jLabel6).add(3, 3, 3).add(this.hostListing, -2, -1, -2).addPreferredGap(1).add(var6.createParallelGroup(3).add(this.searchGo).add(this.searchCancel)).addPreferredGap(0).add(this.searchOptions, -2, -1, -2).addContainerGap(-1, 32767)));
      this.bindingGroup.bind();
   }

   private void isDateStateChanged(ChangeEvent var1) {
      this.dateOptions.setVisible(this.isDate.isSelected());
   }

   private void isTypeStateChanged(ChangeEvent var1) {
      this.typeOptions.setVisible(this.isType.isSelected());
   }

   private void isSizeStateChanged(ChangeEvent var1) {
      this.sizeOptions.setVisible(this.isSize.isSelected());
   }

   private void searchGoActionPerformed(ActionEvent var1) {
      this.startSearching.execute(this);
   }

   private void searchCancelActionPerformed(ActionEvent var1) {
      this.stopSearching.execute(this);
   }

   private void searchPatternKeyReleased(KeyEvent var1) {
      if (var1.getKeyCode() == 10) {
         this.startSearching.execute(this);
      }

   }

   static enum SizeMultiplier {
      Bytes(1L, "B"),
      Kilobytes(Bytes, "KB"),
      Megabytes(Kilobytes, "MB"),
      Gigabytes(Megabytes, "GB"),
      Terabytes(Gigabytes, "TB"),
      Petabytes(Terabytes, "PB");

      long size;
      String suffix;

      public long getSize() {
         return this.size;
      }

      public String getSuffix() {
         return this.suffix;
      }

      private SizeMultiplier(long var3, String var5) {
         this.size = var3;
         this.suffix = var5;
      }

      private SizeMultiplier(SearchParameters.SizeMultiplier var3, String var4) {
         this.size = 1024L * var3.size;
         this.suffix = var4;
      }
   }
}
