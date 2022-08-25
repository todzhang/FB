package ddb.history;

import ddb.actions.history.HistoryDirection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class History<E> {
   public static final int UNLIMITED = 0;
   List<E> items;
   int index;
   int maximum;
   E lastSearchPattern;
   E lastSearchResult;
   int searchBegan;
   Comparator<E> compEquals;
   Comparator<E> compMatches;
   E defaultValue;

   public History() {
      this(new Comparator<E>() {
         @Override
         public int compare(E var1, E var2) {
            if (var1 == var2) {
               return 0;
            } else if (var1 != null && var2 != null) {
               return var1.equals(var2) ? 0 : 1;
            } else {
               return 1;
            }
         }
      }, new Comparator<E>() {
         public int compare(E var1, E var2) {
            if (var1 == var2) {
               return 0;
            } else if (var1 != null && var2 != null) {
               return var1.equals(var2) ? 0 : 1;
            } else {
               return 1;
            }
         }
      });
   }

   public History(Comparator<E> var1, Comparator<E> var2) {
      this(var1, var2, (E) null);
   }

   public History(Comparator<E> var1, Comparator<E> var2, E var3) {
      this(0, var1, var2, var3);
   }

   public History(int var1, Comparator<E> var2, Comparator<E> var3, E var4) {
      this.items = new ArrayList();
      this.index = 0;
      this.maximum = 5000;
      this.lastSearchPattern = null;
      this.lastSearchResult = null;
      this.searchBegan = -1;
      this.defaultValue = null;
      this.maximum = var1;
      this.compEquals = var2;
      this.compMatches = var3;
      this.defaultValue = var4;
   }

   private void trim() {
      if (this.maximum != 0) {
         if (this.items.size() > this.maximum) {
            this.items.subList(0, this.items.size() - this.maximum).clear();
         }

         this.index = this.items.size();
      }
   }

   private void clearSearch() {
      this.lastSearchPattern = null;
      this.lastSearchResult = null;
   }

   public void setMaximum(int var1) {
      this.maximum = var1;
      this.trim();
   }

   public void addHistoryItem(E var1) {
      this.items.add(var1);
      this.index = this.items.size();
      this.trim();
      this.clearSearch();
   }

   public E doHistoryAction(HistoryDirection var1, E var2) {
      if (this.items.size() == 0) {
         return this.defaultValue;
      } else {
         int var3 = this.index;
         switch(var1) {
         case FIRST:
            var3 = 0;
            break;
         case LAST:
            var3 = this.items.size() - 1;
            break;
         case NEXT:
            ++var3;
            break;
         case PREVIOUS:
            --var3;
            break;
         case SEARCH:
            if (this.lastSearchPattern != null && this.lastSearchResult != null && 0 == this.compEquals.compare(this.lastSearchResult, var2)) {
               --var3;
            } else {
               this.lastSearchPattern = var2;
               var3 = this.items.size() - 1;
            }

            while(var3 >= 0) {
               if (0 == this.compMatches.compare(this.items.get(var3), this.lastSearchPattern)) {
                  this.lastSearchResult = this.items.get(var3);
                  break;
               }

               --var3;
            }

            if (var3 == -1) {
               this.index = this.items.size();
               return this.lastSearchPattern;
            }
         }

         this.index = Math.min(Math.max(0, var3), this.items.size());
         return this.index != this.items.size() ? this.items.get(this.index) : this.defaultValue;
      }
   }

   public void clear() {
      this.index = 0;
      this.items.clear();
   }

   public List<E> toList() {
      return Collections.unmodifiableList(this.items);
   }

   public E get(int var1) {
      return var1 >= 0 && var1 < this.items.size() ? this.items.get(var1) : this.defaultValue;
   }

   public List<E> get(int var1, int var2) {
      return (List)(var1 < var2 && var1 >= 0 && var2 < this.items.size() ? this.items.subList(var1, var2) : new Vector());
   }

   public static History<String> getHistoryString() {
      return getHistoryString(0);
   }

   public static History<String> getHistoryString(int var0) {
      Comparator var1 = new Comparator<String>() {
         public int compare(String var1, String var2) {
            return var1 != null ? var1.compareTo(var2) : -1;
         }
      };
      Comparator var2 = new Comparator<String>() {
         public int compare(String var1, String var2) {
            if (var1 != null && var2 != null) {
               return var1.startsWith(var2) ? 0 : 1;
            } else {
               return 1;
            }
         }
      };
      return new History(var0, var1, var2, (Object)null);
   }
}
