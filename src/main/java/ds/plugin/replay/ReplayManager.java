package ds.plugin.replay;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszDetachable;
import ddb.dsz.annotations.DszHideable;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.annotations.DszUserStartable;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.util.GeneralUtilities;
import ddb.util.Guid;
import ddb.util.tablefilter.FilteredTableModel;
import ddb.util.tablefilter.sample.ColumnHidingModel;
import java.awt.Component;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/administration.png")
@DszName("Replay Manager")
@DszDescription("List of previous operations")
@DszUserStartable(false)
@DszHideable(
   hide = false,
   unhide = false
)
@DszDetachable(false)
public class ReplayManager extends NoHostAbstractPlugin {
   public ReplayManager() {
      super.setVerifyClose(false);
      super.setCanClose(false);
      super.setUserClosable(false);
   }

   @Override
   protected int init2() {
      JTable var1 = new JTable();
      FilteredTableModel var2 = new FilteredTableModel(ReplayTableModel.getReplayModel());
      ColumnHidingModel var3 = new ColumnHidingModel(ReplayTableModelColumns.class);
      var1.setColumnModel(var3);
      var3.applyToTable(var1);
      var1.setModel(var2);
      JScrollPane var4 = new JScrollPane(var1);
      TableColumn var5 = var1.getColumnModel().getColumn(ReplayTableModelColumns.LOADED.ordinal());
      JLabel var6 = new JLabel("  " + ReplayTableModelColumns.LOADED.getName());
      var5.setWidth(var6.getPreferredSize().width);
      var5.setMaxWidth(var6.getPreferredSize().width);
      var5.setMinWidth(var6.getPreferredSize().width);
      var6.setText("0000-00-00 00:00:00.0000");
      var5 = var1.getColumnModel().getColumn(ReplayTableModelColumns.START.ordinal());
      var5.setWidth(var6.getPreferredSize().width);
      var5.setMaxWidth(var6.getPreferredSize().width);
      var5.setMinWidth(var6.getPreferredSize().width);
      var5 = var1.getColumnModel().getColumn(ReplayTableModelColumns.GUID.ordinal());
      var6.setText(Guid.NULL.toString() + "0000");
      var5.setWidth(var6.getPreferredSize().width);
      var5.setMaxWidth(var6.getPreferredSize().width);
      var5.setMinWidth(var6.getPreferredSize().width);
      var1.setDefaultRenderer(Calendar.class, new DszTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (var7 instanceof JLabel && value instanceof Calendar) {
               Calendar var8 = (Calendar) value;
               ((JLabel)JLabel.class.cast(var7)).setText(GeneralUtilities.CalendarToStringDisplay(var8));
            }

            return var7;
         }
      });
      super.setDisplay(var4);
      return 0;
   }

   @Override
   protected void fini2() {
      ReplayTableModel.getReplayModel().setStop();
   }
}
