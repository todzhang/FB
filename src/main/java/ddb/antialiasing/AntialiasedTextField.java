package ddb.antialiasing;

import ddb.console.ColorTheme;
import ddb.console.OptionPane;
import ddb.console.Themable;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JTextField;
import javax.swing.text.Document;

public class AntialiasedTextField extends JTextField implements Observer, Themable {
   private ColorTheme theme;

   public AntialiasedTextField(Document var1, String var2, int var3) {
      this(var1, var2, var3, true);
   }

   public AntialiasedTextField(String var1, int var2) {
      this(var1, var2, true);
   }

   public AntialiasedTextField(int var1) {
      this(var1, true);
   }

   public AntialiasedTextField(String var1) {
      this(var1, true);
   }

   public AntialiasedTextField() {
      this(true);
   }

   public AntialiasedTextField(Document var1, String var2, int var3, boolean var4) {
      super(var1, var2, var3);
      this.theme = null;
      this.init(var4);
   }

   public AntialiasedTextField(String var1, int var2, boolean var3) {
      super(var1, var2);
      this.theme = null;
      this.init(var3);
   }

   public AntialiasedTextField(int var1, boolean var2) {
      super(var1);
      this.theme = null;
      this.init(var2);
   }

   public AntialiasedTextField(String var1, boolean var2) {
      super(var1);
      this.theme = null;
      this.init(var2);
   }

   public AntialiasedTextField(boolean var1) {
      this.theme = null;
      this.init(var1);
   }

   private void init(boolean var1) {
      this.putClientProperty("substancelaf.colorizationFactor", new Double(1.0D));
      if (var1) {
         this.setTheme(OptionPane.getInstance().getSharedTheme());
      }

   }

   @Override
   public void setTheme(ColorTheme colorTheme) {
      if (this.theme != null) {
         this.theme.deleteObserver(this);
      }

      this.theme = colorTheme;
      if (this.theme != null) {
         this.theme.addObserver(this);
         this.update(colorTheme, (Object)null);
      }

   }

   @Override
   public void paintComponent(Graphics var1) {
      Graphics2D var2 = (Graphics2D)var1;
      var2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(var2);
   }

   @Override
   public void update(Observable var1, Object var2) {
      if (var1 instanceof ColorTheme) {
         ColorTheme var3 = (ColorTheme)var1;
         this.setForeground(var3.getValue(ColorTheme.Location.NormalForeground));
         this.setCaretColor(var3.getValue(ColorTheme.Location.NormalForeground));
         this.setBackground(var3.getValue(ColorTheme.Location.NormalBackground));
      }

   }
}
