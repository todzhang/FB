package ddb.util;

import java.util.*;
import java.util.function.UnaryOperator;

public class InsertionOrderedSet<E> implements Collection<E>, Set<E>, List<E> {
   private Class<? extends Set<E>> setImpl;
   private Class<? extends List<E>> listImpl;
   private Set<E> set;
   private List<E> list;

   public InsertionOrderedSet() {
      this(HashSet.class, ArrayList.class);
   }

   public InsertionOrderedSet(Class<? extends Set> setImpl, Class<? extends List> listImpl) {
      this.set = null;
      this.list = null;
      this.setImpl = (Class<? extends Set<E>>) setImpl;
      this.listImpl = (Class<? extends List<E>>) listImpl;
      this.reset();
   }

   public boolean reset() {
      if (this.setImpl != null && this.listImpl != null) {
         try {
            this.set = (Set)this.setImpl.newInstance();
            this.list = (List)this.listImpl.newInstance();
            return true;
         } catch (IllegalAccessException var2) {
            var2.printStackTrace();
            return false;
         } catch (InstantiationException var3) {
            var3.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isValid() {
      return this.set != null && this.list != null;
   }

   public void setSetClass(Class<? extends Set<E>> var1) {
      this.setImpl = var1;
   }

   public void setListClass(Class<? extends List<E>> var1) {
      this.listImpl = var1;
   }

   @Override
   public boolean add(E e) {
      if (this.set.add(e)) {
         if (this.list.add(e)) {
            return true;
         }

         this.set.remove(e);
      }

      return false;
   }

   @Override
   public void add(int index, E element) {
      if (this.set.add(element)) {
         this.list.add(index, element);
      }

   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = c.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (this.set.add((E) var4)) {
            var2.add(var4);
         }
      }

      this.list.addAll(var2);
      return !var2.isEmpty();
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> c) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = c.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         if (this.set.add((E) var5)) {
            var3.add(var5);
         }
      }

      this.list.addAll(index, var3);
      return !var3.isEmpty();
   }

   @Override
   public void clear() {
      this.set.clear();
      this.list.clear();
   }

   @Override
   public Spliterator<E> spliterator() {
      return Set.super.spliterator();
   }

   @Override
   public boolean contains(Object o) {
      return this.set.contains(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      Iterator var2 = c.iterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = var2.next();
      } while(this.contains(var3));

      return false;
   }

   @Override
   public E get(int index) {
      return this.list.get(index);
   }

   @Override
   public int indexOf(Object o) {
      return !this.set.contains(o) ? -1 : this.list.indexOf(o);
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   @Override
   public int lastIndexOf(Object o) {
      return !this.set.contains(o) ? -1 : this.list.lastIndexOf(o);
   }

   @Override
   public boolean remove(Object o) {
      return this.set.remove(o) ? this.list.remove(o) : false;
   }

   @Override
   public E remove(int index) {
      Object var2 = this.list.remove(index);
      this.set.remove(var2);
      return (E) var2;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean var2 = false;
      Iterator var3 = c.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (this.remove(var4)) {
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      boolean var2 = false;
      int var3 = 0;

      while(var3 < this.list.size()) {
         Object var4 = this.list.get(var3);
         if (c.contains(var4)) {
            ++var3;
         } else {
            this.remove(var4);
            var2 = true;
         }
      }

      return var2;
   }

   @Override
   public void replaceAll(UnaryOperator<E> operator) {
      List.super.replaceAll(operator);
   }

   @Override
   public void sort(Comparator<? super E> c) {
      List.super.sort(c);
   }

   @Override
   public int size() {
      return this.list.size();
   }

   @Override
   public String toString() {
      return this.list.toString();
   }

   @Override
   public Object[] toArray() {
      return this.list.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return this.list.toArray(a);
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex) {
      return Collections.unmodifiableList(this.list.subList(fromIndex, toIndex));
   }

   @Override
   public Iterator<E> iterator() {
      return this.list.iterator();
   }

   @Override
   public ListIterator<E> listIterator() {
      return this.list.listIterator();
   }

   @Override
   public E set(int index, E element) {
      if (index >= 0 && index < this.list.size()) {
         if (this.set.add(element)) {
            Object var3 = this.list.get(index);
            this.set.remove(var3);
            this.list.set(index, element);
            return (E) var3;
         } else {
            return null;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public ListIterator<E> listIterator(int index) {
      return this.list.listIterator(index);
   }
}
