package org.syntax.jedit;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.swing.KeyStroke;

public class DefaultInputHandler extends InputHandler {
   private Hashtable<KeyStroke, Object> bindings;
   private Hashtable<KeyStroke, Object> currentBindings;

   public DefaultInputHandler() {
      this.bindings = this.currentBindings = new Hashtable();
   }

   public void addDefaultKeyBindings() {
      this.addKeyBinding("BACK_SPACE", BACKSPACE);
      this.addKeyBinding("C+BACK_SPACE", BACKSPACE_WORD);
      this.addKeyBinding("DELETE", DELETE);
      this.addKeyBinding("C+DELETE", DELETE_WORD);
      this.addKeyBinding("ENTER", INSERT_BREAK);
      this.addKeyBinding("TAB", INSERT_TAB);
      this.addKeyBinding("S+TAB", UNSERT_TAB);
      this.addKeyBinding("INSERT", OVERWRITE);
      this.addKeyBinding("C+\\", TOGGLE_RECT);
      this.addKeyBinding("HOME", HOME);
      this.addKeyBinding("END", END);
      this.addKeyBinding("C+A", SELECT_ALL);
      this.addKeyBinding("S+HOME", SELECT_HOME);
      this.addKeyBinding("S+END", SELECT_END);
      this.addKeyBinding("C+HOME", DOCUMENT_HOME);
      this.addKeyBinding("C+END", DOCUMENT_END);
      this.addKeyBinding("CS+HOME", SELECT_DOC_HOME);
      this.addKeyBinding("CS+END", SELECT_DOC_END);
      this.addKeyBinding("PAGE_UP", PREV_PAGE);
      this.addKeyBinding("PAGE_DOWN", NEXT_PAGE);
      this.addKeyBinding("S+PAGE_UP", SELECT_PREV_PAGE);
      this.addKeyBinding("S+PAGE_DOWN", SELECT_NEXT_PAGE);
      this.addKeyBinding("LEFT", PREV_CHAR);
      this.addKeyBinding("S+LEFT", SELECT_PREV_CHAR);
      this.addKeyBinding("C+LEFT", PREV_WORD);
      this.addKeyBinding("CS+LEFT", SELECT_PREV_WORD);
      this.addKeyBinding("RIGHT", NEXT_CHAR);
      this.addKeyBinding("S+RIGHT", SELECT_NEXT_CHAR);
      this.addKeyBinding("C+RIGHT", NEXT_WORD);
      this.addKeyBinding("CS+RIGHT", SELECT_NEXT_WORD);
      this.addKeyBinding("UP", PREV_LINE);
      this.addKeyBinding("S+UP", SELECT_PREV_LINE);
      this.addKeyBinding("DOWN", NEXT_LINE);
      this.addKeyBinding("S+DOWN", SELECT_NEXT_LINE);
      this.addKeyBinding("C+ENTER", REPEAT);
      this.addKeyBinding("C+C", CLIP_COPY);
      this.addKeyBinding("C+V", CLIP_PASTE);
      this.addKeyBinding("C+X", CLIP_CUT);
   }

   public void addKeyBinding(String var1, ActionListener var2) {
      Hashtable var3 = this.bindings;
      StringTokenizer var4 = new StringTokenizer(var1);

      while(var4.hasMoreTokens()) {
         KeyStroke var5 = parseKeyStroke(var4.nextToken());
         if (var5 == null) {
            return;
         }

         if (var4.hasMoreTokens()) {
            Object var6 = var3.get(var5);
            if (var6 instanceof Hashtable) {
               var3 = (Hashtable)var6;
            } else {
               Hashtable var7 = new Hashtable();
               var3.put(var5, var7);
               var3 = (Hashtable)var7;
            }
         } else {
            var3.put(var5, var2);
         }
      }

   }

   public void addKeyBinding(KeyStroke var1, ActionListener var2) {
      Hashtable var3 = this.bindings;
      if (var1 != null) {
         var3.put(var1, var2);
      }
   }

   public void removeKeyBinding(String var1) {
      throw new InternalError("Not yet implemented");
   }

   public void removeAllKeyBindings() {
      this.bindings.clear();
   }

   public InputHandler copy() {
      return new DefaultInputHandler(this);
   }

   public void keyPressed(KeyEvent var1) {
      int var2 = var1.getKeyCode();
      int var3 = var1.getModifiers();
      if (var2 != 17 && var2 != 16 && var2 != 18 && var2 != 157) {
         if ((var3 & -2) != 0 || var1.isActionKey() || var2 == 8 || var2 == 127 || var2 == 10 || var2 == 9 || var2 == 27) {
            if (this.grabAction != null) {
               this.handleGrabAction(var1);
               return;
            }

            KeyStroke var4 = KeyStroke.getKeyStroke(var2, var3);
            Object var5 = this.currentBindings.get(var4);
            if (var5 == null) {
               if (this.currentBindings != this.bindings) {
                  Toolkit.getDefaultToolkit().beep();
                  this.repeatCount = 0;
                  this.repeat = false;
                  var1.consume();
               }

               this.currentBindings = this.bindings;
               return;
            }

            if (var5 instanceof ActionListener) {
               this.currentBindings = this.bindings;
               this.executeAction((ActionListener)var5, var1.getSource(), (String)null);
               var1.consume();
               return;
            }

            if (var5 instanceof Hashtable) {
               this.currentBindings = (Hashtable)var5;
               var1.consume();
               return;
            }
         }

      }
   }

   public void keyTyped(KeyEvent var1) {
      int var2 = var1.getModifiers();
      char var3 = var1.getKeyChar();
      if (var3 != '\uffff' && (var2 & 8) == 0 && var3 >= ' ' && var3 != 127) {
         KeyStroke var4 = KeyStroke.getKeyStroke(Character.toUpperCase(var3));
         Object var5 = this.currentBindings.get(var4);
         if (var5 instanceof Hashtable) {
            this.currentBindings = (Hashtable)var5;
            return;
         }

         if (var5 instanceof ActionListener) {
            this.currentBindings = this.bindings;
            this.executeAction((ActionListener)var5, var1.getSource(), String.valueOf(var3));
            return;
         }

         this.currentBindings = this.bindings;
         if (this.grabAction != null) {
            this.handleGrabAction(var1);
            return;
         }

         if (this.repeat && Character.isDigit(var3)) {
            this.repeatCount *= 10;
            this.repeatCount += var3 - 48;
            return;
         }

         this.executeAction(INSERT_CHAR, var1.getSource(), String.valueOf(var1.getKeyChar()));
         this.repeatCount = 0;
         this.repeat = false;
      }

   }

   public static KeyStroke parseKeyStroke(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = 0;
         int var2 = var0.indexOf(43);
         if (var2 != -1) {
            for(int var3 = 0; var3 < var2; ++var3) {
               switch(Character.toUpperCase(var0.charAt(var3))) {
               case 'A':
                  var1 |= 8;
                  break;
               case 'C':
                  var1 |= 2;
                  break;
               case 'M':
                  var1 |= 4;
                  break;
               case 'S':
                  var1 |= 1;
               }
            }
         }

         String var7 = var0.substring(var2 + 1);
         if (var7.length() == 1) {
            char var8 = Character.toUpperCase(var7.charAt(0));
            return var1 == 0 ? KeyStroke.getKeyStroke(var8) : KeyStroke.getKeyStroke(var8, var1);
         } else if (var7.length() == 0) {
            System.err.println("Invalid key stroke: " + var0);
            return null;
         } else {
            int var4;
            try {
               var4 = KeyEvent.class.getField("VK_".concat(var7)).getInt((Object)null);
            } catch (Exception var6) {
               System.err.println("Invalid key stroke: " + var0);
               return null;
            }

            return KeyStroke.getKeyStroke(var4, var1);
         }
      }
   }

   private DefaultInputHandler(DefaultInputHandler var1) {
      this.bindings = this.currentBindings = var1.bindings;
   }
}
