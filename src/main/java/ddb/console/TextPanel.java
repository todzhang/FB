package ddb.console;

import ddb.antialiasing.AntialiasedTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class TextPanel extends JPanel {
   ColorTheme currentTheme;
   private boolean bOverrideWarningBg = false;
   private boolean bOverrideErrorBg = false;
   private boolean bOverrideNoticeBg = false;
   private JButton defaultBackground;
   private JButton defaultForeground;
   private JLabel defaultLabel;
   private JTextField defaultText;
   private JButton errorBackground;
   private JButton errorForeground;
   private JLabel errorLabel;
   private JTextField errorText;
   private JButton goodBackground;
   private JButton goodForeground;
   private JLabel goodLabel;
   private JTextField goodText;
   private JButton jButton1;
   private JButton warningBackground;
   private JButton warningForeground;
   private JLabel warningLabel;
   private JTextField warningText;

   public TextPanel() {
      this.initComponents();
      this.currentTheme = new ColorTheme("Default");
      this.applyTheme();
   }

   private void applyTheme() {
      this.applyTheme(this.defaultText, ColorTheme.Location.NormalForeground, ColorTheme.Location.NormalBackground);
      this.applyTheme(this.warningText, ColorTheme.Location.WarningForeground, ColorTheme.Location.WarningBackground);
      this.applyTheme(this.errorText, ColorTheme.Location.ErrorForeground, ColorTheme.Location.ErrorBackground);
      this.applyTheme(this.goodText, ColorTheme.Location.NoticeForeground, ColorTheme.Location.NoticeBackground);
   }

   private void applyTheme(JTextField var1, ColorTheme.Location var2, ColorTheme.Location var3) {
      var1.setForeground(this.currentTheme.getValue(var2));
      var1.setCaretColor(this.currentTheme.getValue(var2));
      var1.setBackground(this.currentTheme.getValue(var3));
   }

   private void initComponents() {
      this.defaultLabel = new JLabel();
      this.goodLabel = new JLabel();
      this.warningLabel = new JLabel();
      this.errorLabel = new JLabel();
      this.defaultText = new AntialiasedTextField(false);
      this.goodText = new AntialiasedTextField(false);
      this.warningText = new AntialiasedTextField(false);
      this.errorText = new AntialiasedTextField(false);
      this.defaultForeground = new JButton();
      this.defaultBackground = new JButton();
      this.goodBackground = new JButton();
      this.goodForeground = new JButton();
      this.warningBackground = new JButton();
      this.warningForeground = new JButton();
      this.errorBackground = new JButton();
      this.errorForeground = new JButton();
      this.jButton1 = new JButton();
      this.defaultLabel.setText("Default Text:");
      this.goodLabel.setText("Good Text:");
      this.warningLabel.setText("Warning Text:");
      this.errorLabel.setText("Error Text:");
      this.defaultText.setText("This is default text");
      this.goodText.setText("This is good/notice text");
      this.warningText.setText("This is warning text");
      this.errorText.setText("This is error text");
      this.defaultForeground.setText("Foreground");
      this.defaultForeground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.defaultForegroundActionPerformed(var1);
         }
      });
      this.defaultBackground.setText("Background");
      this.defaultBackground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.defaultBackgroundActionPerformed(var1);
         }
      });
      this.goodBackground.setText("Background");
      this.goodBackground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.goodBackgroundActionPerformed(var1);
         }
      });
      this.goodForeground.setText("Foreground");
      this.goodForeground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.goodForegroundActionPerformed(var1);
         }
      });
      this.warningBackground.setText("Background");
      this.warningBackground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.warningBackgroundActionPerformed(var1);
         }
      });
      this.warningForeground.setText("Foreground");
      this.warningForeground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.warningForegroundActionPerformed(var1);
         }
      });
      this.errorBackground.setText("Background");
      this.errorBackground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.errorBackgroundActionPerformed(var1);
         }
      });
      this.errorForeground.setText("Foreground");
      this.errorForeground.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            TextPanel.this.errorForegroundActionPerformed(var1);
         }
      });
      this.jButton1.setText("Save Settings");
      GroupLayout var1 = new GroupLayout(this);
      this.setLayout(var1);
      var1.setHorizontalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.defaultLabel).addComponent(this.goodLabel).addComponent(this.warningLabel).addComponent(this.errorLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.defaultText, -1, 277, 32767).addComponent(this.goodText, -1, 277, 32767).addComponent(this.warningText, -1, 277, 32767).addComponent(this.errorText, -1, 277, 32767)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.TRAILING).addComponent(this.defaultForeground).addComponent(this.goodForeground).addComponent(this.warningForeground, Alignment.LEADING).addComponent(this.errorForeground)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.TRAILING).addComponent(this.defaultBackground).addComponent(this.goodBackground).addComponent(this.warningBackground).addComponent(this.errorBackground))).addComponent(this.jButton1, Alignment.TRAILING)).addContainerGap()));
      var1.setVerticalGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addContainerGap().addGroup(var1.createParallelGroup(Alignment.LEADING).addGroup(var1.createSequentialGroup().addComponent(this.defaultForeground).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.goodForeground).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.warningForeground).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.errorForeground)).addGroup(var1.createSequentialGroup().addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.defaultLabel).addComponent(this.defaultText, -2, -1, -2).addComponent(this.defaultBackground)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.goodLabel).addComponent(this.goodText, -2, -1, -2).addComponent(this.goodBackground)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.warningLabel).addComponent(this.warningText, -2, -1, -2).addComponent(this.warningBackground)).addPreferredGap(ComponentPlacement.RELATED).addGroup(var1.createParallelGroup(Alignment.LEADING).addComponent(this.errorLabel).addGroup(var1.createParallelGroup(Alignment.BASELINE).addComponent(this.errorText, -2, -1, -2).addComponent(this.errorBackground))))).addPreferredGap(ComponentPlacement.RELATED, 145, 32767).addComponent(this.jButton1).addContainerGap()));
   }

   private void defaultForegroundActionPerformed(ActionEvent var1) {
      this.chooseColor(ColorTheme.Location.NormalForeground);
   }

   private void defaultBackgroundActionPerformed(ActionEvent var1) {
      Color var2 = this.chooseColor(ColorTheme.Location.NormalBackground);
      if (!this.bOverrideErrorBg) {
         this.currentTheme.setValue(ColorTheme.Location.ErrorBackground, var2);
      }

      if (!this.bOverrideWarningBg) {
         this.currentTheme.setValue(ColorTheme.Location.WarningBackground, var2);
      }

      if (!this.bOverrideNoticeBg) {
         this.currentTheme.setValue(ColorTheme.Location.NoticeBackground, var2);
      }

      this.applyTheme();
   }

   private void goodForegroundActionPerformed(ActionEvent var1) {
      this.chooseColor(ColorTheme.Location.NoticeForeground);
   }

   private void goodBackgroundActionPerformed(ActionEvent var1) {
      if (this.chooseColor(ColorTheme.Location.NoticeBackground) != null) {
         this.bOverrideNoticeBg = true;
      }

   }

   private void warningForegroundActionPerformed(ActionEvent var1) {
      this.chooseColor(ColorTheme.Location.WarningForeground);
   }

   private void warningBackgroundActionPerformed(ActionEvent var1) {
      if (this.chooseColor(ColorTheme.Location.WarningBackground) != null) {
         this.bOverrideWarningBg = true;
      }

   }

   private void errorForegroundActionPerformed(ActionEvent var1) {
      this.chooseColor(ColorTheme.Location.ErrorForeground);
   }

   private void errorBackgroundActionPerformed(ActionEvent var1) {
      if (this.chooseColor(ColorTheme.Location.ErrorBackground) != null) {
         this.bOverrideErrorBg = true;
      }

   }

   private Color chooseColor(ColorTheme.Location var1) {
      Color var2 = this.currentTheme.getValue(var1);
      Color var3 = JColorChooser.showDialog((Component)null, "Choose " + var1.getName() + " color", var2);
      if (var3 != null && !var3.equals(var2)) {
         this.currentTheme.setValue(var1, var3);
         this.applyTheme();
         return var3;
      } else {
         return null;
      }
   }
}
