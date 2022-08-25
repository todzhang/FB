package ddb.dsz.plugin.monitor;

import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.task.Task;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckNode extends DefaultMutableTreeNode {
   protected boolean isChangable;
   protected boolean isSelected;
   protected CheckNode.SelectionMode selectionMode;
   protected CheckNode.SelectionType selectionType;

   public CheckNode() {
      this((Object)null);
   }

   public CheckNode(Object var1) {
      this(var1, true, true);
   }

   public CheckNode(Object var1, boolean var2, boolean var3) {
      super(var1, var3);
      this.selectionMode = CheckNode.SelectionMode.SingleSelection;
      this.selectionType = CheckNode.SelectionType.Always;
      this.isSelected = var2;
      this.isChangable = true;
      this.setSelectionMode(CheckNode.SelectionMode.SingleSelection);
   }

   public boolean isSelected() {
      Object var1 = this.getUserObject();
      if (!(var1 instanceof Task) && !(var1 instanceof HostInfo)) {
         return this.isSelected;
      } else {
         return this.isSelected && ((CheckNode)CheckNode.class.cast(this.parent)).isSelected();
      }
   }

   public boolean isChangable() {
      return this.isChangable;
   }

   public void setSelectionMode(CheckNode.SelectionMode var1) {
      this.selectionMode = var1;
   }

   public void setSelectionType(CheckNode.SelectionType var1) {
      this.selectionType = var1;
   }

   public CheckNode.SelectionMode getSelectionMode() {
      return this.selectionMode;
   }

   public void setChangable(boolean var1) {
      this.isChangable = var1;
   }

   public Collection<CheckNode> setSelected(boolean var1) {
      HashSet var2 = new HashSet();
      if (!this.isChangable) {
         return var2;
      } else {
         this.isSelected = var1;
         switch(this.selectionType) {
         case OnFalse:
            if (var1) {
               return var2;
            }
            break;
         case OnTrue:
            if (!var1) {
               return var2;
            }
         }

         switch(this.selectionMode) {
         case DigInSelection:
            if (this.children != null) {
               Enumeration var3 = this.children.elements();

               while(var3.hasMoreElements()) {
                  CheckNode var4 = (CheckNode)var3.nextElement();
                  var2.addAll(var4.setSelected(var1));
               }
            }
            break;
         case DigOutSelection:
            if (this.parent != null && this.parent instanceof CheckNode) {
               var2.addAll(((CheckNode)CheckNode.class.cast(this.parent)).setSelected(var1));
            }
         }

         var2.addAll(this.enumerateChildren());
         return var2;
      }
   }

   public void setUserObject(Object var1) {
      if (var1 instanceof Boolean) {
         this.setSelected((Boolean)Boolean.class.cast(var1));
      } else {
         super.setUserObject(var1);
      }

   }

   private Collection<CheckNode> enumerateChildren() {
      HashSet var1 = new HashSet();
      if (this.children != null) {
         Enumeration var2 = this.children.elements();

         while(var2.hasMoreElements()) {
            CheckNode var3 = (CheckNode)var2.nextElement();
            var1.addAll(var3.enumerateChildren());
         }
      }

      var1.add(this);
      return var1;
   }

   public static enum SelectionType {
      Always,
      OnTrue,
      OnFalse;
   }

   public static enum SelectionMode {
      SingleSelection,
      DigInSelection,
      DigOutSelection;
   }
}
