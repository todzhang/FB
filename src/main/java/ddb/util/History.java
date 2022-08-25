package ddb.util;

import java.awt.Toolkit;
import java.util.List;
import java.util.Vector;

public class History {
   private List<String> items;
   private int currentIndex;
   private int maxSize;
   public static final int UNLIMITED = 0;

   public History() {
      this(0);
   }

   public History(int limit) {
      this.items = new Vector(60, 60);
      this.currentIndex = 0;
      this.maxSize = limit;
   }

   public void setSizeLimit(int limit) {
      this.maxSize = limit;
      this.trim();
   }

   public void add(String item) {
      boolean trim = !this.items.remove(item);
      this.items.add(item);
      if (trim) {
         this.trim();
      }

      this.currentIndex = this.items.size();
   }

   public String prev() {
      if (this.currentIndex > 0) {
         --this.currentIndex;
      }

      return this.items.size() > 0 ? (String)this.items.get(this.currentIndex) : "";
   }

   public String next() {
      if (this.currentIndex < this.items.size()) {
         ++this.currentIndex;
      }

      return this.currentIndex < this.items.size() ? (String)this.items.get(this.currentIndex) : "";
   }

   public String first() {
      this.currentIndex = 0;
      Toolkit.getDefaultToolkit().beep();
      return this.items.size() > 0 ? (String)this.items.get(0) : "";
   }

   public String last() {
      this.currentIndex = this.items.size() - 1;
      return this.items.size() > 0 ? (String)this.items.get(this.currentIndex) : "";
   }

   public int getSize() {
      return this.items.size();
   }

   public String elementAt(int index) {
      return (String)this.items.get(index);
   }

   public void clear() {
      this.items.clear();
      this.currentIndex = 0;
   }

   public List<String> toList() {
      return new Vector(this.items);
   }

   private void trim() {
      if (this.maxSize != 0) {
         if (this.items.size() > this.maxSize) {
            int clear = this.items.size() - this.maxSize;
            this.items.subList(0, clear).clear();
            this.currentIndex -= clear;
         }

      }
   }
}
