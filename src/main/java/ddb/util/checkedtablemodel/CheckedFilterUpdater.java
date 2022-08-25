package ddb.util.checkedtablemodel;

import ddb.util.predicate.MustMatchOnePredicate;

public class CheckedFilterUpdater<T> implements CheckedTableSelection<T> {
   private final MustMatchOnePredicate predicate;
   private final FilterWatcher filter;

   public CheckedFilterUpdater(MustMatchOnePredicate var1, FilterWatcher var2) {
      this.predicate = var1;
      this.filter = var2;
   }

   public void selected(T var1, boolean var2) {
      if (var2) {
         this.predicate.add(var1);
      } else {
         this.predicate.remove(var1);
      }

      this.filter.predicateChanged();
   }

   public String toString() {
      return "Checked Filter Updater";
   }
}
