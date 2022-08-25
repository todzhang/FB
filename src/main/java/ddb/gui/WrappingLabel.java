package ddb.gui;

import ddb.util.StringUtils;
import ddb.web.util.HTMLAttributes;
import ddb.web.util.HTMLWriter;
import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class WrappingLabel extends JLabel {
   public static final String classVersion = "5.2";
   protected static final int DEFAULT_MAXIMUM_LABEL_WIDTH = 350;
   private static int defaultMaximumWidth = 350;
   private static boolean correctFont;
   protected HTMLAttributes attributes;
   protected HTMLWriter html;
   protected int maximumWidth;
   protected String text;
   protected Color foreground;

   public WrappingLabel() {
      this("", (Icon)null, 10);
   }

   public WrappingLabel(Icon image) {
      this("", image, 10);
   }

   public WrappingLabel(Icon image, int horizontalAlignment) {
      this("", image, horizontalAlignment);
   }

   public WrappingLabel(String text) {
      this(text, (Icon)null, 10);
   }

   public WrappingLabel(String text, int horizontalAlignment) {
      this(text, (Icon)null, horizontalAlignment);
   }

   public WrappingLabel(String text, Icon icon, int horizontalAlignment) {
      super("", icon, horizontalAlignment);
      this.attributes = new HTMLAttributes();
      this.html = new HTMLWriter();
      this.maximumWidth = defaultMaximumWidth;
      this.text = "";
      this.setText(text);
   }

   protected String convertToHTML(String text1) {
      if (text1 == null || text1.length() == 0 || StringUtils.startsWithIgnoreCase(text1, "<html>")) {
         if (StringUtils.indexOfIgnoreCase(text1, "<table>") != -1) {
            return text1;
         }

         if (text1.length() > 13) {
            text1 = text1.substring(6, text1.length() - 7);
         }
      }

      text1 = StringUtils.replaceAll(text1, "\n", "<br>");
      Font font = this.getFont();
      this.html.clear();
      this.html.beginHTML();
      this.attributes.putOnly("width", this.maximumWidth);
      this.html.beginTable(this.attributes);
      this.html.beginTableRow();
      this.html.beginTableData();
      if (correctFont) {
         this.attributes.put("face", font.getName());
         this.attributes.put("size", this.getHTMLFontSize(font));
         this.html.beginFont(this.attributes);
         if (font.isBold()) {
            this.html.beginBold();
         }

         if (font.isItalic()) {
            this.html.beginItalic();
         }
      }

      this.html.write(text1);
      if (correctFont) {
         if (font.isItalic()) {
            this.html.endItalic();
         }

         if (font.isBold()) {
            this.html.endBold();
         }

         this.html.endFont();
      }

      this.html.endTableData();
      this.html.endTableRow();
      this.html.endTable();
      this.html.endHTML();
      return this.html.toString();
   }

   public static int getDefaultMaximumWidth() {
      return defaultMaximumWidth;
   }

   protected int getHTMLFontSize(Font font) {
      int size = font.getSize();
      if (size <= 10) {
         return 1;
      } else if (size <= 12) {
         return 2;
      } else if (size <= 16) {
         return 3;
      } else if (size <= 20) {
         return 4;
      } else {
         return size <= 29 ? 5 : 6;
      }
   }

   public int getMaximumWidth() {
      return this.maximumWidth;
   }

   public String getUnconvertedText() {
      return this.text;
   }

   public static void setDefaultMaximumWidth(int maximumWidth) {
      defaultMaximumWidth = maximumWidth;
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (enabled) {
         super.setForeground(this.foreground);
      }

      if (!enabled) {
         LookAndFeel laf = UIManager.getLookAndFeel();
         UIDefaults uid = laf.getDefaults();
         Enumeration<?> e = uid.keys();
         super.setForeground((Color)uid.get("Label.disabledForeground"));
      }

      this.setText(this.text);
   }

   public void setFont(Font font) {
      super.setFont(font);
      this.setText(this.text);
   }

   public void setForeground(Color color) {
      super.setForeground(color);
      this.foreground = color;
      this.setText(this.text);
   }

   /** @deprecated */
   @Deprecated
   public void setMaxWidth(int maxWidth) {
      this.setMaximumWidth(maxWidth);
   }

   public void setMaximumWidth(int maximumWidth) {
      this.maximumWidth = maximumWidth;
      this.setText(this.text);
   }

   public void setText(String text) {
      this.text = text;
      if (text != null && text.length() != 0) {
         super.setText(this.convertToHTML(this.text));
      } else {
         super.setText(text);
      }

   }

   static {
      try {
         Class.forName("java.lang.CharSequence");
         correctFont = false;
      } catch (ClassNotFoundException var1) {
         correctFont = true;
      }

   }
}
