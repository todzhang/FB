package ddb.console;

import ddb.GuiConstants;
import java.awt.Color;
import java.io.Serializable;
import java.util.Observable;
import java.util.Properties;

public class ColorTheme extends Observable implements Serializable {
   private String name;
   final Color[] themeSet;

   private ColorTheme() {
      this("Default");
   }

   public ColorTheme(String var1) {
      this.themeSet = new Color[ColorTheme.Location.values().length];
      this.name = var1 != null ? var1 : "";
      ColorTheme.Location[] var2 = ColorTheme.Location.values();

      for (Location var5 : var2) {
         this.themeSet[var5.ordinal()] = var5.getColor();
      }

   }

   public ColorTheme(Properties var1) {
      this.themeSet = new Color[ColorTheme.Location.values().length];
      this.name = var1.getProperty("Name");
      ColorTheme.Location[] var2 = ColorTheme.Location.values();

      for (Location var5 : var2) {
         String var6 = var1.getProperty(var5.getKey());
         if (var6 != null) {
            Color var7 = Color.decode(var6);
            this.themeSet[var5.ordinal()] = var7;
         }
      }

   }

   public ColorTheme(String var1, ColorTheme var2) {
      this.themeSet = new Color[ColorTheme.Location.values().length];
      this.name = var1 != null ? var1 : "";
      this.copyTheme(var2);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1 != null ? var1 : "";
      this.setChanged();
      this.notifyObservers();
   }

   public void setValue(ColorTheme.Location var1, Color var2) {
      if (var1 != null && var2 != null) {
         this.themeSet[var1.ordinal()] = var2;
         this.setChanged();
         this.notifyObservers();
      }
   }

   public Color getValue(ColorTheme.Location var1) {
      return var1 == null ? null : this.themeSet[var1.ordinal()];
   }

   public void copyTheme(ColorTheme var1) {
      ColorTheme.Location[] var2 = ColorTheme.Location.values();

      for (Location var5 : var2) {
         this.themeSet[var5.ordinal()] = var1.themeSet[var5.ordinal()];
      }

      this.setChanged();
      this.notifyObservers();
   }

   public Properties getProperties() {
      Properties var1 = new Properties();
      var1.setProperty("Name", this.name);
      ColorTheme.Location[] var2 = ColorTheme.Location.values();

      for (Location var5 : var2) {
         Color var6 = this.themeSet[var5.ordinal()];
         String var7 = "0x";
         int var8 = var6.getRed();
         var7 = var7 + this.parseHex(var8);
         var8 = var6.getGreen();
         var7 = var7 + this.parseHex(var8);
         var8 = var6.getBlue();
         var7 = var7 + this.parseHex(var8);
         var1.put(var5.getKey(), var7);
      }

      return var1;
   }

   private String parseHex(int var1) {
      return String.format("%02x", var1);
   }

   @Override
   public String toString() {
      return this.name;
   }

   public enum Location {
      NormalForeground("NormalFg", "Normal Foreground", GuiConstants.DefaultColor.DEFAULT_FOREGROUND),
      NoticeForeground("NoticeFg", "Notice Foreground", GuiConstants.DefaultColor.NOTICE),
      WarningForeground("WarningFg", "Warning Foreground", GuiConstants.DefaultColor.WARNING),
      ErrorForeground("ErrorFg", "Error Foreground", GuiConstants.DefaultColor.ERROR),
      NormalBackground("NormalBg", "Normal Background", GuiConstants.DefaultColor.DEFAULT_BACKGROUND),
      NoticeBackground("NoticeBg", "Notice Background", GuiConstants.DefaultColor.DEFAULT_BACKGROUND),
      WarningBackground("WarningBg", "Warning Background", GuiConstants.DefaultColor.DEFAULT_BACKGROUND),
      ErrorBackground("ErrorBg", "Error Background", GuiConstants.DefaultColor.DEFAULT_BACKGROUND);

      private final String key;
      private final Color color;
      private final String name;

      Location(String var3, String var4, GuiConstants.DefaultColor var5) {
         this.key = var3;
         this.name = var4;
         this.color = var5.getColor();
      }

      public String getKey() {
         return this.key;
      }

      public Color getColor() {
         return this.color;
      }

      public String getName() {
         return this.name;
      }

      public static ColorTheme.Location lookup(StyleTypes st, boolean var1) {
         switch(st) {
         case ERROR:
            return var1 ? ErrorBackground : ErrorForeground;
         case WARNING:
            return var1 ? WarningBackground : WarningForeground;
         case NOTICE:
            return var1 ? NoticeBackground : NoticeForeground;
         case BOLD:
         case DEFAULT:
         default:
            return var1 ? NormalBackground : NormalForeground;
         }
      }
   }
}
