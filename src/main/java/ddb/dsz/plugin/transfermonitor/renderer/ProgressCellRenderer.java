package ddb.dsz.plugin.transfermonitor.renderer;

import ddb.dsz.plugin.transfermonitor.model.TransferDirection;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import ddb.dsz.plugin.transfermonitor.model.TransferState;
import ddb.gui.swing.DszTableCellRenderer;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.math.BigInteger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProgressCellRenderer extends DszTableCellRenderer implements TableCellRenderer {
   JProgressBar progress = new JProgressBar();

   public ProgressCellRenderer() {
      this.progress.setStringPainted(true);
      this.progress.putClientProperty("inverted", Boolean.TRUE);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Object var7 = this.progress;
      JLabel var8 = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (value instanceof TransferRecord) {
         TransferRecord var9 = (TransferRecord) value;
         var8.setHorizontalAlignment(4);
         if (var9.getState().equals(TransferState.FAILURE)) {
            var8.setText(String.format("%s %,d Bytes", var9.getDirection().getText(), var9.getTransfered()));
            var7 = var8;
         } else if (var9.getSize() == 0L) {
            var8.setText(String.format("%,d of ??? Bytes", var9.getTransfered()));
            var7 = var8;
         }

         if (var9.getSize().compareTo(var9.getTransfered()) == 0) {
            var8.setText(String.format("%s %,d Bytes", var9.getDirection().getText(), var9.getTransfered()));
            var7 = var8;
         } else if (var9.getSize().compareTo(var9.getTransfered()) >= 0 && var9.getSize() >= 0L && var9.getTransfered() >= 0L) {
            this.progress.setMaximum(var9.getSize().intValue());
            this.progress.setString(String.format("%s %,d of %,d Bytes", var9.getDirection().getText(), var9.getTransfered(), var9.getSize()));
            if (var9.getDirection() == TransferDirection.GET) {
               this.progress.setValue(var9.getTransfered().intValue());
            } else {
               this.progress.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
               this.progress.setValue(var9.getSize().intValue() - var9.getTransfered().intValue());
            }
         } else {
            var8.setText(String.format("%s %,d of %,d Bytes", var9.getDirection().getText(), var9.getTransfered(), var9.getSize()));
            var7 = var8;
         }
      } else {
         this.progress.setIndeterminate(true);
      }

      return (Component)var7;
   }

   private String stringify(BigInteger var1) {
      if (var1.compareTo(BigInteger.ZERO) == 0) {
         return "0";
      } else {
         String var2;
         for(var2 = ""; var1.compareTo(BigInteger.ZERO) > 0; var1 = var1.divide(BigInteger.valueOf(1000L))) {
            BigInteger var3 = var1.mod(BigInteger.valueOf(1000L));
            if (var2.length() > 0) {
               var2 = "," + var2;
            }

            String var4 = "";
            if (var1.compareTo(BigInteger.valueOf(1000L)) >= 0) {
               if (var3.compareTo(BigInteger.valueOf(100L)) < 0) {
                  var4 = var4 + "0";
               }

               if (var3.compareTo(BigInteger.valueOf(10L)) < 0) {
                  var4 = var4 + "0";
               }
            }

            var2 = var3 + var2;
            if (var1.compareTo(BigInteger.ZERO) > 0) {
               var2 = var4 + var2;
            }
         }

         return var2;
      }
   }
}
