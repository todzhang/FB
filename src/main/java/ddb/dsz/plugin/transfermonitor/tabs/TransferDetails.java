package ddb.dsz.plugin.transfermonitor.tabs;

import ddb.detach.AbstractTabbable;
import ddb.detach.Alignment;
import ddb.detach.Workbench;
import ddb.detach.Workbench.WorkbenchAction;
import ddb.dsz.plugin.transfermonitor.TransferTabbable;
import ddb.dsz.plugin.transfermonitor.displays.HexDisplay;
import ddb.dsz.plugin.transfermonitor.displays.TextDisplay;
import ddb.dsz.plugin.transfermonitor.model.TransferRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class TransferDetails extends AbstractTabbable {
   TransferRecord record;
   JPanel blank = new JPanel();
   Workbench bench = new TransferWorkbench();

   public TransferDetails(TransferRecord var1, File var2) throws IOException {
      super.setAlignment(Alignment.RIGHT);
      super.setVerifyClose(false);
      super.setDetachable(true);
      super.setShowButtons(true);
      this.record = var1;
      TransferTabbable var3 = this.binaryDisplay(var2);
      TransferTabbable var4 = this.textDisplay(var2);
      this.bench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var3});
      this.bench.enqueAction(WorkbenchAction.ADDNEWTAB, new Object[]{var4});
      super.registerDisplay(this.bench);
   }

   @Override
   protected JComponent getTabbableSpecificRenderComponent() {
      return null;
   }

   @Override
   public boolean isClosable() {
      return true;
   }

   @Override
   public JComponent getHeader() {
      return this.blank;
   }

   public String getName() {
      return this.record.getRemote();
   }

   @Override
   public String getDetachedTitle() {
      return this.getName();
   }

   @Override
   public String getDockedTitle() {
      return this.getName();
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }

   public JComponent initialFocus() {
      return null;
   }

   private TransferTabbable binaryDisplay(final File var1) throws IOException {
      final HexDisplay var2 = new HexDisplay();
      SwingWorker var3 = new SwingWorker<byte[], byte[]>() {
         protected byte[] doInBackground() throws Exception {
            if (var1.length() <= 0L) {
               return "Unable to open file".getBytes();
            } else {
               FileInputStream var1x = new FileInputStream(var1);
               int var2x = var1x.available();
               byte[] var3 = new byte[var2x];
               int var4 = 0;

               for(int var5 = 0; -1 != var5 && var4 != var2x; var5 = var1x.read(var3, var4, var2x - var4)) {
                  var4 += var5;
               }

               return var3;
            }
         }

         protected void done() {
            try {
               var2.setData((byte[])this.get());
            } catch (Exception var2x) {
               var2.setData(String.format("Unable to get file: " + var2x.getMessage()).getBytes());
            }

         }
      };
      var3.execute();
      return var2;
   }

   private TransferTabbable textDisplay(File var1) throws IOException {
      return new TextDisplay(var1);
   }

   @Override
   public JLabel getTabComponent() {
      return new JLabel(this.record.getRemote());
   }
}
