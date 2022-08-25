package ddb.bcb;

import ddb.bcb.ui.BasicBreadcrumbBarUI;
import ddb.bcb.ui.BreadcrumbBarUI;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BreadcrumbBar extends JComponent {
   public static final String TAG_BCB = "bcbmemory";
   public static final String TAG_PATH = "bcbpath";
   public static final String TAG_ITEM = "bcbitem";
   public static final String TAG_COMPONENT = "bcbcomponent";
   private static final long serialVersionUID = 3258407339731400502L;
   protected BreadcrumbStack stack;
   protected Icon icon;
   protected String separator;
   protected JButton okButton;
   protected BreadcrumbBarCallBack callback;
   protected Color underlineColor;
   protected Color boxColor;
   private List<BreadcrumbBarListener> listeners;
   private boolean noEvent;
   private MouseListener popupListener;
   private List<BreadcrumbItem[]> memory;
   private int currentMemoryIndex;
   private static final String uiClassID = "BreadcrumbBarUI";

   public void setPopupListener(MouseListener var1) {
      this.popupListener = var1;
   }

   public MouseListener getPopupListener() {
      return this.popupListener;
   }

   public void addListener(BreadcrumbBarListener var1) {
      if (this.listeners == null) {
         this.listeners = new ArrayList();
      }

      if (!this.listeners.contains(var1)) {
         this.listeners.add(var1);
      }

   }

   public void removeListener(BreadcrumbBarListener var1) {
      if (this.listeners != null) {
         this.listeners.remove(var1);
      }

   }

   public void fireBreadcrumbBarEvent(BreadcrumbBarEvent var1) {
      if (!this.noEvent && this.listeners != null && !this.listeners.isEmpty()) {
         for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
            ((BreadcrumbBarListener)this.listeners.get(var2)).breadcrumbBarEvent(var1);
         }

      }
   }

   public BreadcrumbBar() {
      this((Icon)null, (String)null, (JButton)null, (BreadcrumbBarCallBack)null);
   }

   public BreadcrumbBar(Icon var1, String var2, JButton var3, BreadcrumbBarCallBack var4) {
      this.callback = null;
      this.underlineColor = null;
      this.boxColor = null;
      this.listeners = null;
      this.noEvent = false;
      this.popupListener = null;
      this.memory = null;
      this.currentMemoryIndex = -1;
      this.icon = var1;
      this.stack = new BreadcrumbStack();
      this.separator = var2;
      this.okButton = var3;
      this.callback = var4;
      this.updateUI();
   }

   public BreadcrumbBar(Icon var1, String var2, BreadcrumbBarCallBack var3) {
      this(var1, var2, (JButton)null, var3);
   }

   public BreadcrumbBar(String var1, JButton var2, BreadcrumbBarCallBack var3) {
      this((Icon)null, var1, var2, var3);
   }

   public BreadcrumbBar(String var1, BreadcrumbBarCallBack var2) {
      this((Icon)null, var1, (JButton)null, var2);
   }

   public void setUnderlineColor(Color var1) {
      this.underlineColor = var1;
   }

   public void setBoxColor(Color var1) {
      this.boxColor = var1;
   }

   public Color getBoxColor() {
      return this.boxColor;
   }

   public String getSeparator() {
      return this.separator;
   }

   public void setPath(BreadcrumbItem[] var1) {
      BreadcrumbItem[] var2 = this.getPath();
      this.stack.clear();
      this.noEvent = true;
      BreadcrumbItemChoices var3 = null;
      var3 = this.callback.getChoices((BreadcrumbItem[])null);
      this.pushChoices(var3, false);

      for(int var4 = 0; var1 != null && var4 < var1.length; ++var4) {
         BreadcrumbItem var5 = var1[var4];
         this.pushChoice(var5, false);
         BreadcrumbItem[] var6 = new BreadcrumbItem[var4 + 1];
         System.arraycopy(var1, 0, var6, 0, var4 + 1);
         var3 = this.callback.getChoices(var6);
         if (var3 == null && var4 + 1 < var1.length) {
            var3 = new BreadcrumbItemChoices(new BreadcrumbItem[]{var5});
         }

         if (var3 != null) {
            this.pushChoices(var3, false);
         }
      }

      this.getUI().updateComponents();
      this.noEvent = false;
      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 0, var2, var1));
   }

   public BreadcrumbItem[] getPath() {
      return this.getPath(-1);
   }

   public BreadcrumbItem[] getPath(int var1) {
      assert var1 <= this.stack.size();

      Vector var2 = new Vector();
      ListIterator var3 = this.stack.listIterator(0);

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (var4 instanceof BreadcrumbItem) {
            var2.add((BreadcrumbItem)var4);
         }

         if (this.stack.indexOf(var4) == var1) {
            break;
         }
      }

      BreadcrumbItem[] var5 = new BreadcrumbItem[var2.size()];

      for(int var6 = 0; !var2.isEmpty(); var5[var6++] = (BreadcrumbItem)var2.remove(0)) {
      }

      return var5;
   }

   public BreadcrumbItem getLastItem() {
      if (this.stack.isEmpty()) {
         return null;
      } else {
         Object var1 = this.stack.get(this.stack.size() - 1);
         return var1 instanceof BreadcrumbItem ? (BreadcrumbItem)var1 : null;
      }
   }

   public BreadcrumbItem getItem(int var1) {
      if (this.stack.isEmpty()) {
         return null;
      } else if (var1 >= 0 && var1 < this.stack.size()) {
         Object var2 = this.stack.get(var1);
         return var2 instanceof BreadcrumbItem ? (BreadcrumbItem)var2 : null;
      } else {
         return null;
      }
   }

   public String getRootedPath() {
      return this.getRootedPath(-1);
   }

   public String getRootedPath(int var1) {
      assert var1 <= this.stack.size();

      StringBuffer var2 = new StringBuffer();
      ListIterator var3 = this.stack.listIterator(0);

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (var4 instanceof BreadcrumbItemChoices) {
            var2.append(this.separator);
         } else if (var4 instanceof BreadcrumbItem) {
            var2.append(((BreadcrumbItem)var4).getName());
         }

         if (this.stack.indexOf(var4) == var1) {
            break;
         }
      }

      return var2.toString();
   }

   public Object pushChoices(BreadcrumbItemChoices var1) {
      return this.pushChoices(var1, true);
   }

   public Object pushChoices(BreadcrumbItemChoices var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         if (this.okButton != null) {
            this.okButton.setEnabled(true);
         }

         if (this.stack.size() % 2 == 1) {
            this.stack.pop();
         }

         var1.setIndex(this.stack.size());
         Object var3 = this.stack.push(var1);
         if (var2) {
            this.getUI().updateComponents();
         }

         return var3;
      }
   }

   public Object setChoices(int var1, BreadcrumbItemChoices var2, boolean var3) {
      if (var2 == null) {
         return null;
      } else if (var1 <= this.stack.size() && var1 % 2 != 1) {
         var2.setIndex(var1);
         this.stack.set(var1, var2);
         if (var3) {
            this.getUI().updateComponents();
         }

         return var2;
      } else {
         return null;
      }
   }

   public static BreadcrumbItemChoices makeChoices(String[] var0) {
      if (var0 == null) {
         return null;
      } else {
         BreadcrumbItem[] var1 = new BreadcrumbItem[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new BreadcrumbItem(new String[]{var0[var2]});
         }

         return new BreadcrumbItemChoices(var1);
      }
   }

   public static BreadcrumbItemChoices makeChoices(Vector<String> var0) {
      if (var0 == null) {
         return null;
      } else {
         BreadcrumbItem[] var1 = new BreadcrumbItem[var0.size()];

         for(int var2 = 0; var2 < var0.size(); ++var2) {
            var1[var2] = new BreadcrumbItem(new String[]{(String)var0.get(var2)});
         }

         return new BreadcrumbItemChoices(var1);
      }
   }

   public Object pushChoice(BreadcrumbItem var1) {
      return this.pushChoice(var1, true);
   }

   public Object pushChoice(BreadcrumbItem var1, boolean var2) {
      assert var1 != null;

      if (this.okButton != null) {
         this.okButton.setEnabled(true);
      }

      BreadcrumbItem[] var3 = this.getPath();
      if (!this.stack.isEmpty() && this.stack.size() % 2 == 0) {
         this.stack.pop();
      }

      var1.setIndex(this.stack.size());
      Object var4 = this.stack.push(var1);
      if (var2) {
         this.getUI().updateComponents();
      }

      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 0, var3, this.getPath()));
      return var4;
   }

   public Object pop() {
      if (this.stack.size() <= 1 && this.okButton != null) {
         this.okButton.setEnabled(false);
      }

      return this.stack.pop();
   }

   public int getItemCount() {
      return this.stack.size();
   }

   public String toString() {
      return Arrays.asList(this.getPath()).toString();
   }

   public void previous() {
      if (this.hasPrevious()) {
         this.setPath((BreadcrumbItem[])this.memory.get(--this.currentMemoryIndex));
         this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
      }
   }

   public void next() {
      if (this.hasNext()) {
         this.setPath((BreadcrumbItem[])this.memory.get(++this.currentMemoryIndex));
         this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
      }
   }

   public void setMemoryIndex(int var1) {
      if (this.memory != null && var1 >= 0 && var1 < this.memory.size()) {
         this.currentMemoryIndex = var1;
         this.setPath((BreadcrumbItem[])this.memory.get(this.currentMemoryIndex));
      }

      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
   }

   public List<BreadcrumbItem[]> getMemory() {
      return Collections.unmodifiableList(this.memory);
   }

   public int getMemoryIndex() {
      return this.currentMemoryIndex;
   }

   public boolean hasNext() {
      return this.memory != null && this.memory.size() > this.currentMemoryIndex + 1;
   }

   public boolean hasPrevious() {
      return this.memory != null && this.currentMemoryIndex > 0;
   }

   public void addPathToMemory(BreadcrumbItem[] var1) {
      if (this.memory == null) {
         this.memory = new ArrayList();
      }

      if (this.currentMemoryIndex != this.memory.size()) {
      }

      this.memory.add(var1);
      ++this.currentMemoryIndex;
      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
   }

   public void addCurrentPathToMemory() {
      this.addPathToMemory(this.getPath());
   }

   public void clearMemory() {
      if (this.memory != null) {
         this.memory.clear();
      }

      this.currentMemoryIndex = -1;
      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
   }

   private static void putOpenTag(PrintWriter var0, String var1, boolean var2) {
      var0.write("<" + var1 + ">");
      if (var2) {
         var0.write(System.getProperty("line.separator"));
      }

   }

   private static void putCloseTag(PrintWriter var0, String var1, boolean var2) {
      var0.write("</" + var1 + ">");
      if (var2) {
         var0.write(System.getProperty("line.separator"));
      }

   }

   public void saveMemory(OutputStream var1) {
      PrintWriter var2 = null;

      try {
         var2 = new PrintWriter(var1);
         putOpenTag(var2, "bcbmemory", true);
         if (this.memory != null) {
            Iterator var3 = this.memory.iterator();

            while(var3.hasNext()) {
               BreadcrumbItem[] var4 = (BreadcrumbItem[])var3.next();
               var2.write("\t");
               putOpenTag(var2, "bcbpath", true);
               BreadcrumbItem[] var5 = var4;
               int var6 = var4.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  BreadcrumbItem var8 = var5[var7];
                  var2.write("\t\t");
                  putOpenTag(var2, "bcbitem", true);
                  String[] var9 = var8.getValue();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     String var12 = var9[var11];
                     var2.write("\t\t\t");
                     putOpenTag(var2, "bcbcomponent", false);
                     var2.write(var12);
                     putCloseTag(var2, "bcbcomponent", true);
                  }

                  var2.write("\t\t");
                  putCloseTag(var2, "bcbitem", true);
               }

               var2.write("\t");
               putCloseTag(var2, "bcbpath", true);
            }
         }

         putCloseTag(var2, "bcbmemory", true);
         var2.write(System.getProperty("line.separator"));
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

   }

   public void loadMemory(InputStream var1) {
      try {
         SAXParser var2 = SAXParserFactory.newInstance().newSAXParser();
         DefaultHandler var3 = new DefaultHandler() {
            List<BreadcrumbItem> currPath;
            List<String> currComponents;
            String currValue;

            public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
               this.currValue = null;
               if ("bcbpath".equals(var3)) {
                  this.currPath = new LinkedList();
               } else {
                  if ("bcbitem".equals(var3)) {
                     this.currComponents = new LinkedList();
                  }

               }
            }

            public void endElement(String var1, String var2, String var3) throws SAXException {
               if ("bcbpath".equals(var3)) {
                  BreadcrumbBar.this.memory.add(this.currPath.toArray(new BreadcrumbItem[0]));
               } else if ("bcbitem".equals(var3)) {
                  BreadcrumbItem var4 = new BreadcrumbItem((String[])this.currComponents.toArray(new String[0]));
                  BreadcrumbBar.this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(BreadcrumbBar.this, 2, (Object)null, var4));
                  this.currPath.add(var4);
               } else if ("bcbcomponent".equals(var3)) {
                  this.currComponents.add(this.currValue);
               }
            }

            public void characters(char[] var1, int var2, int var3) throws SAXException {
               String var4 = new String(var1, var2, var3);
               if (this.currValue == null) {
                  this.currValue = var4;
               } else {
                  this.currValue = this.currValue + var4;
               }

            }
         };
         var2.parse(var1, var3);
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }

      this.fireBreadcrumbBarEvent(new BreadcrumbBarEvent(this, 1, (Object)null, (Object)null));
   }

   public BreadcrumbBarCallBack getCallback() {
      return this.callback;
   }

   public void setUI(BreadcrumbBarUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      if (UIManager.get(this.getUIClassID()) != null) {
         this.setUI((BreadcrumbBarUI)UIManager.getUI(this));
      } else {
         this.setUI(new BasicBreadcrumbBarUI());
      }

   }

   public BreadcrumbBarUI getUI() {
      return (BreadcrumbBarUI)this.ui;
   }

   public String getUIClassID() {
      return "BreadcrumbBarUI";
   }

   public BreadcrumbStack getStack() {
      return this.stack;
   }

   public Icon getIcon() {
      return this.icon;
   }

   public JButton getOkButton() {
      return this.okButton;
   }

   public Color getUnderlineColor() {
      return this.underlineColor;
   }

   public interface BreadcrumbBarElement {
      String getText();

      int getIndex();
   }
}
