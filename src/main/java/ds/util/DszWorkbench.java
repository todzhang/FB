package ds.util;

import ddb.detach.Workbench;
import ds.core.DSConstants;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.JComponent;

public abstract class DszWorkbench extends Workbench {
   public DszWorkbench(Logger logger) {
      super(logger);
      this.init();
   }

   public DszWorkbench(Logger logger, int tabPlacement) {
      super(logger, tabPlacement);
      this.init();
   }

   public DszWorkbench(Logger logger, int tabPlacement, int tabLayoutPolicy) {
      super(logger, tabPlacement, tabLayoutPolicy);
      this.init();
   }

   public DszWorkbench(int tabPlacement) {
      super(tabPlacement);
      this.init();
   }

   public DszWorkbench(int tabPlacement, int tabLayoutPolicy) {
      super(tabPlacement, tabLayoutPolicy);
      this.init();
   }

   public DszWorkbench() {
      this.init();
   }

   private void init() {
   }

   @Override
   protected final Dimension getDefaultSize() {
      return new Dimension(DSConstants.FRAME_WIDTH, DSConstants.FRAME_HEIGHT);
   }

   public JComponent getDefaultElement() {
      return null;
   }
}
