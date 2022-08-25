package ddb.bcb;

public class BreadcrumbBarEvent {
   public static final int PATH_CHANGED = 0;
   public static final int MEMORY_CHANGED = 1;
   public static final int ITEM_LOADED = 2;
   private int type;
   private Object oldValue;
   private Object newValue;
   private Object src;

   public BreadcrumbBarEvent(Object var1, int var2, Object var3, Object var4) {
      this.src = var1;
      this.type = var2;
      this.oldValue = var3;
      this.newValue = var4;
   }

   public int getType() {
      return this.type;
   }

   public Object getNewValue() {
      return this.newValue;
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public Object getSource() {
      return this.src;
   }
}
