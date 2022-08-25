package ddb.antialiasing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class AntialiasedJTree extends JTree {
   public void paintComponent(Graphics var1) {
      Graphics2D var2 = (Graphics2D)var1;
      var2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(var2);
   }

   public AntialiasedJTree() {
      this.init();
   }

   public AntialiasedJTree(Object[] var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTree(Hashtable<?, ?> var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTree(Vector<?> var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTree(TreeModel var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTree(TreeNode var1) {
      super(var1);
      this.init();
   }

   public AntialiasedJTree(TreeNode var1, boolean var2) {
      super(var1, var2);
      this.init();
   }

   private void init() {
      this.putClientProperty("substancelaf.colorizationFactor", new Double(1.0D));
   }
}
