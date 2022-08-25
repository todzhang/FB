package ddb.util.checkedtablemodel;

class DataEntry<T> {
   private Boolean selected;
   private String caption;
   private String tooltip;
   private T tag;

   public DataEntry(Boolean var1, String var2, String var3, T var4) {
      this.selected = var1;
      this.caption = var2;
      this.tooltip = var3;
      this.tag = var4;
   }

   public final Boolean isSelected() {
      return this.selected;
   }

   public final void setSelected(Boolean var1) {
      this.selected = var1;
   }

   public final String getCaption() {
      if (this.caption != null) {
         return this.caption;
      } else {
         return this.tag != null ? this.tag.toString() : "No caption";
      }
   }

   public final T getTag() {
      return this.tag != null ? this.tag : null;
   }

   public final String getTooltip() {
      return this.tooltip;
   }
}
