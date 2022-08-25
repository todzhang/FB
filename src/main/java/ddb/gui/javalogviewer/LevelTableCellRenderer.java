package ddb.gui.javalogviewer;

import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.JLabel;
import javax.swing.JTable;

public class LevelTableCellRenderer extends DszTableCellRenderer {
   public static final String SEVERE_ICON = "images/error.png";
   public static final String WARNING_ICON = "images/important.png";
   public static final String INFO_ICON = "images/mimetypes.png";
   public static final String CONFIG_ICON = "images/exec.png";
   public static final String FINE_ICON = "images/kedit.png";
   public static final String FINER_ICON = "images/kate.png";
   public static final String FINEST_ICON = "images/korganizer.png";
   JLabel severe = new JLabel("SEVERE");
   JLabel warning = new JLabel("WARNING");
   JLabel info = new JLabel("INFO");
   JLabel config = new JLabel("CONFIG");
   JLabel fine = new JLabel("FINE");
   JLabel finer = new JLabel("FINER");
   JLabel finest = new JLabel("FINEST");
   int maximumValue = 127;

   private Color brighter(Color var1, int var2) {
      return var2 == 0 ? var1 : this.brighter(var1.brighter(), var2 - 1);
   }

   private Color darker(Color var1, int var2) {
      return var2 == 0 ? var1 : this.darker(var1.darker(), var2 - 1);
   }

   public LevelTableCellRenderer(Dimension var1) {
      this.severe.setBackground(this.brighter(Color.RED, 0));
      this.warning.setBackground(this.darker(Color.YELLOW, 0));
      this.severe.setIcon(ImageManager.getIcon("images/error.png", var1));
      this.warning.setIcon(ImageManager.getIcon("images/important.png", var1));
      this.config.setIcon(ImageManager.getIcon("images/exec.png", var1));
      this.info.setIcon(ImageManager.getIcon("images/mimetypes.png", var1));
      this.fine.setIcon(ImageManager.getIcon("images/kedit.png", var1));
      this.finer.setIcon(ImageManager.getIcon("images/kate.png", var1));
      this.finest.setIcon(ImageManager.getIcon("images/korganizer.png", var1));
      this.severe.setOpaque(true);
      this.warning.setOpaque(true);
      this.info.setOpaque(true);
      this.config.setOpaque(true);
      this.fine.setOpaque(true);
      this.finer.setOpaque(true);
      this.finest.setOpaque(true);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof LogRecord) {
         value = ((LogRecord)LogRecord.class.cast(value)).getLevel();
      }

      if (value.equals(Level.SEVERE)) {
         return this.severe;
      } else if (value.equals(Level.WARNING)) {
         return this.warning;
      } else if (value.equals(Level.INFO)) {
         return this.info;
      } else if (value.equals(Level.CONFIG)) {
         return this.config;
      } else if (value.equals(Level.FINE)) {
         return this.fine;
      } else if (value.equals(Level.FINER)) {
         return this.finer;
      } else {
         return (Component)(value.equals(Level.FINEST) ? this.finest : super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
      }
   }
}
