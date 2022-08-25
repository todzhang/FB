package ddb.dsz.library.console;

import ddb.GuiConstants;
import ddb.antialiasing.AntialiasedTextPane;
import ddb.console.ColorTheme;
import ddb.console.OptionPane;
import ddb.console.Themable;
import ddb.console.ColorTheme.Location;
import ddb.dsz.core.controller.CoreController;
import ddb.events.AutoScroll;
import ddb.imagemanager.ImageManager;
import ddb.listeners.RightClickListener;
import ddb.predicate.PredicateClosure;
import ddb.predicate.PredicateClosureImpl;
import ddb.util.XmlCache;
import ddb.writequeue.AbstractWritable;
import ddb.writequeue.Writable;
import ddb.writequeue.WriteQueue;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.functors.SwitchClosure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class ConsoleOutputPane extends JScrollPane implements Observer, Themable {
   private static final String DEFAULTNODE = "Default";
   private static final String GOODNODE = "Good";
   private static final String ERRORNODE = "Error";
   private static final String WARNINGNODE = "Warning";
   private static final String NODE = "Node";
   PredicateClosure[] xmlClosures;
   Closure appendText;
   Transformer textToDoc;
   Closure handleDisplay;
   public static final int DEFAULT_MAX_CHARS = 400000;
   protected Style boldStyle;
   protected int currentFontSize;
   private AttributeSet currentStyle;
   protected Style defaultStyle;
   protected final Object DOC_LOCK;
   protected Style docProvidedStyle;
   protected Style errorStyle;
   protected Style noticeStyle;
   protected ConsoleStyledDocument outputDoc;
   protected boolean wordWrap;
   private ColorTheme currentTheme;
   private final List<Themable> connectedThemables;
   PredicateClosure[] items;
   WriteQueue<Writable> writer2;
   private StringBuilder sb;
   boolean shouldStop;
   protected AntialiasedTextPane textPane;
   protected Style warningStyle;
   protected CoreController core;
   private AutoScroll auto;
   protected ToggleButtonModel inputPaused;
   private int MaximumCharacters;

   public void disableDisplay() {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
         }
      });
   }

   public void enableDisplay() {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
         }
      });
   }

   public void appendDisplay(String input) {
      this.handleDisplay.execute(input);
   }

   public ConsoleOutputPane(CoreController core) {
      this(core, 3000);
   }

   public ConsoleOutputPane(CoreController core, int var2) {
      this.xmlClosures = new PredicateClosure[]{new ConsoleOutputPane.XmlDocumentClosure("Default", ConsoleOutputPane.OutputLevel.DEFAULT), new ConsoleOutputPane.XmlDocumentClosure("Good", ConsoleOutputPane.OutputLevel.NOTICE), new ConsoleOutputPane.XmlDocumentClosure("Error", ConsoleOutputPane.OutputLevel.ERROR), new ConsoleOutputPane.XmlDocumentClosure("Warning", ConsoleOutputPane.OutputLevel.WARNING), new PredicateClosure() {
         @Override
         public boolean evaluate(Object var1) {
            if (!(var1 instanceof Node)) {
               return false;
            } else {
               for(Node var2 = (Node)var1; var2 != null; var2 = var2.getNextSibling()) {
                  if (var2.getNodeName() != null) {
                     return var2.getNodeName().equals("Node");
                  }
               }

               return false;
            }
         }

         @Override
         public void execute(Object var1) {
            Node var2 = (Node)var1;
            if (var2.getNodeName() != null) {
               if (var2.getNodeValue() == null) {
                  for(Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
                     ConsoleOutputPane.OutputLevel var4 = ConsoleOutputPane.OutputLevel.DEFAULT;
                     if (var2.getAttributes() != null) {
                        Node var5 = var2.getAttributes().getNamedItem("type");
                        if (var5 != null) {
                           String var6 = var5.getNodeValue();
                           if (var6 != null) {
                              if (var6.equals("Default")) {
                                 var4 = ConsoleOutputPane.OutputLevel.DEFAULT;
                              } else if (var6.equals("Good")) {
                                 var4 = ConsoleOutputPane.OutputLevel.NOTICE;
                              } else if (var6.equals("Warning")) {
                                 var4 = ConsoleOutputPane.OutputLevel.WARNING;
                              } else if (var6.equals("Error")) {
                                 var4 = ConsoleOutputPane.OutputLevel.ERROR;
                              }
                           }
                        }
                     }

                     ConsoleOutputPane.this.appendOutputMessage(var3.getNodeValue(), var4);
                  }
               } else {
                  ConsoleOutputPane.OutputLevel var7 = ConsoleOutputPane.OutputLevel.DEFAULT;
                  Node var8 = var2.getAttributes().getNamedItem("type");
                  if (var8 == null) {
                     var7 = ConsoleOutputPane.OutputLevel.DEFAULT;
                  } else if (var8.equals("Default")) {
                     var7 = ConsoleOutputPane.OutputLevel.DEFAULT;
                  } else if (var8.equals("Good")) {
                     var7 = ConsoleOutputPane.OutputLevel.NOTICE;
                  } else if (var8.equals("Warning")) {
                     var7 = ConsoleOutputPane.OutputLevel.WARNING;
                  } else if (var8.equals("Error")) {
                     var7 = ConsoleOutputPane.OutputLevel.ERROR;
                  }

                  ConsoleOutputPane.this.appendOutputMessage(var2.getNodeValue(), var7);
               }

            }
         }
      }};
      this.appendText = new Closure() {
         @Override
         public void execute(Object var1) {
            if (var1 != null) {
               ConsoleOutputPane.this.appendOutputMessage(var1.toString(), ConsoleOutputPane.OutputLevel.DEFAULT);
            }

         }
      };
      this.textToDoc = new Transformer() {
         @Override
         public Object transform(Object var1) {
            String var2 = "";
            if (var1 != null) {
               var2 = var1.toString();
            }

            DocumentBuilder var3 = XmlCache.getBuilder();
            if (var3 == null) {
               return var2;
            } else {
               String var5;
               try {
                  Document var4 = var3.parse(new InputSource(new StringReader(var2)));
                  Document var11 = var4;
                  return var11;
               } catch (Exception var9) {
                  var5 = var2;
               } finally {
                  XmlCache.releaseBuilder(var3);
               }

               return var5;
            }
         }
      };
      this.handleDisplay = new Closure() {
         @Override
         public void execute(Object var1) {
            Object var2 = ConsoleOutputPane.this.textToDoc.transform(var1);
            if (var2 != null && var2 instanceof Document) {
               Document var3 = (Document)var2;
               Object var4 = var3.getDocumentElement();
               if ("Xml".equals(((Node)var4).getNodeName())) {
                  var4 = ((Node)var4).getFirstChild();
               }

               boolean var5;
               for(var5 = false; var4 != null; var4 = ((Node)var4).getNextSibling()) {
                  for(int var6 = 0; var6 < ConsoleOutputPane.this.xmlClosures.length && ((Node)var4).getNodeType() == 1; ++var6) {
                     if (ConsoleOutputPane.this.xmlClosures[var6].evaluate(var4)) {
                        ConsoleOutputPane.this.xmlClosures[var6].execute(var4);
                        var5 = true;
                        break;
                     }
                  }
               }

               if (var5) {
                  return;
               }
            }

            ConsoleOutputPane.this.appendText.execute(var1);
         }
      };
      this.DOC_LOCK = new Object();
      this.wordWrap = false;
      this.currentTheme = null;
      this.connectedThemables = new Vector();
      this.items = new PredicateClosure[]{new PredicateClosureImpl(InstanceofPredicate.getInstance(ConsoleOutputPane.ReapplyStyles.class), new Closure() {
         @Override
         public void execute(Object var1) {
            Element var2 = ConsoleOutputPane.this.outputDoc.getDefaultRootElement();
            Style var3 = ((ConsoleOutputPane.ReapplyStyles)ConsoleOutputPane.ReapplyStyles.class.cast(var1)).getStyle();
            int var4 = var2.getElementCount();

            for(int var5 = 0; var5 < var4; ++var5) {
               Element var6 = var2.getElement(var5);
               AttributeSet var7 = var6.getAttributes();
               String var8 = (String)var7.getAttribute(StyleConstants.NameAttribute);
               int var9;
               if (var3.getName().equals(var8)) {
                  var9 = var6.getStartOffset();
                  int var10 = var6.getEndOffset();
                  ConsoleOutputPane.this.outputDoc.setParagraphAttributes(var9, var10 - var9, var3, true);
               }

               for(var9 = 0; var9 < var6.getElementCount(); ++var9) {
                  Element var13 = var6.getElement(var9);
                  var7 = var13.getAttributes();
                  var8 = (String)var7.getAttribute(StyleConstants.NameAttribute);
                  if (var3.getName().equals(var8)) {
                     int var11 = var13.getStartOffset();
                     int var12 = var13.getEndOffset();
                     ConsoleOutputPane.this.outputDoc.setCharacterAttributes(var11, var12 - var11, var3, true);
                  }
               }
            }

         }
      }), new PredicateClosureImpl(InstanceofPredicate.getInstance(ConsoleOutputPane.TextBlock.class), new Closure() {
         @Override
         public void execute(Object var1) {
            ConsoleOutputPane.TextBlock var2 = (ConsoleOutputPane.TextBlock)ConsoleOutputPane.TextBlock.class.cast(var1);
            ConsoleOutputPane.this.appendTextToPane(var2.getText(), var2.getStyle());
         }
      }), new PredicateClosureImpl(InstanceofPredicate.getInstance(ConsoleOutputPane.TextClear.class), new Closure() {
         @Override
         public void execute(Object var1) {
            ConsoleOutputPane.this.outputDoc.clear();
         }
      })};
      this.writer2 = new WriteQueue(new SwitchClosure(this.items, this.items, ClosureUtils.nopClosure()), false);
      this.sb = new StringBuilder();
      this.shouldStop = false;
      this.inputPaused = new ToggleButtonModel();
      this.MaximumCharacters = Integer.MAX_VALUE;
      this.core = core;
      OptionPane.getInstance().addDirectory(core.getUserConfigDirectory() + "/Console");
      String[] var3 = core.getResourcePackages();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         OptionPane.getInstance().addDirectory(String.format("%s/%s", core.getResourceDirectory(), var6));
      }

      this.inputPaused.setSelected(this.writer2.isPaused());
      this.inputPaused.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            ConsoleOutputPane.this.writer2.setPaused(ConsoleOutputPane.this.inputPaused.isSelected());
         }
      });
      this.MaximumCharacters = var2 * 100;
      if (this.MaximumCharacters < 0) {
         this.MaximumCharacters = Integer.MAX_VALUE;
      }

      this.textPane = new AntialiasedTextPane() {
         public boolean getScrollableTracksViewportWidth() {
            return ConsoleOutputPane.this.wordWrap;
         }
      };
      this.textPane.addCaretListener(new CaretListener() {
         @Override
         public void caretUpdate(CaretEvent var1) {
            while(ConsoleOutputPane.this.textPane.getSelectionEnd() - 1 > ConsoleOutputPane.this.textPane.getSelectionStart()) {
               try {
                  String var2 = ConsoleOutputPane.this.textPane.getText(ConsoleOutputPane.this.textPane.getSelectionEnd() - 1, 1);
                  if (!var2.equals("\r") && !var2.equals("\n")) {
                     break;
                  }

                  ConsoleOutputPane.this.textPane.setSelectionEnd(ConsoleOutputPane.this.textPane.getSelectionEnd() - 1);
               } catch (BadLocationException var3) {
                  break;
               }
            }

            if (ConsoleOutputPane.this.textPane.getSelectionEnd() != ConsoleOutputPane.this.textPane.getSelectionStart()) {
               ConsoleOutputPane.this.textPane.copy();
            }

         }
      });
      this.outputDoc = new ConsoleStyledDocument();
      this.outputDoc.setMaximumCharacters(this.MaximumCharacters);
      this.textPane.setDocument(this.outputDoc);
      this.textPane.setEditable(false);
      this.textPane.setCaret(new DefaultCaret() {
         @Override
         protected void adjustVisibility(Rectangle var1) {
         }

         @Override
         public void setSelectionVisible(boolean var1) {
            super.setSelectionVisible(true);
         }
      });
      super.setViewportView(this.textPane);
      this.setAutoscrolls(true);
      this.setVerticalScrollBarPolicy(22);
      this.currentFontSize = GuiConstants.FIXED_WIDTH_FONT.size;
      this.docProvidedStyle = this.outputDoc.getStyle("default");
      this.defaultStyle = this.outputDoc.addStyle("defaultStyle", this.docProvidedStyle);
      this.errorStyle = this.outputDoc.addStyle("error", this.defaultStyle);
      this.warningStyle = this.outputDoc.addStyle("warning", this.defaultStyle);
      this.noticeStyle = this.outputDoc.addStyle("notice", this.defaultStyle);
      this.boldStyle = this.outputDoc.addStyle("bold", this.defaultStyle);
      StyleConstants.setBold(this.boldStyle, true);
      this.outputDoc.setAsynchronousLoadPriority(3);
      JPopupMenu var7 = new JPopupMenu();
      this.addMenuItem("Copy", "images/editcopy.png", new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            ConsoleOutputPane.this.copy();
         }
      }, var7);
      this.addMouseListener(new RightClickListener(var7));
      JCheckBoxMenuItem var8 = new JCheckBoxMenuItem("Pause Input");
      var8.setModel(this.inputPaused);
      var7.add(var8);
      this.addMenuItem("Change Theme", (String)null, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            ColorTheme var2 = OptionPane.getInstance().displayThemeSelectionDialog(ConsoleOutputPane.this.currentTheme);
            if (var2 != null) {
               ConsoleOutputPane.this.setTheme(var2);
            }
         }
      }, var7);
      if (core != null) {
         core.setupKeyBindings(this);
      }

      this.setCurrentFontSize(this.currentFontSize);
      this.auto = new AutoScroll(this);
      this.setBackground(Color.BLACK);
      if (this.currentTheme == null) {
         this.currentTheme = new ColorTheme("Default");
      }

   }

   public void setAutoScroll(boolean var1) {
      this.auto.setScroll(var1);
   }

   public boolean getAutoScroll() {
      return this.auto.getScroll();
   }

   private void addMenuItem(String var1, String var2, ActionListener var3, JPopupMenu var4) {
      var4.add(this.createMenuItem(var1, var2, var3));
   }

   private JMenuItem createMenuItem(String var1, String var2, ActionListener var3) {
      JMenuItem var4 = new JMenuItem(var1);
      if (var2 != null) {
         var4.setIcon(ImageManager.getIcon(var2, ImageManager.SIZE16));
      }

      var4.addActionListener(var3);
      return var4;
   }

   public void setMaximumCharacters(int var1) {
   }

   public void addFocusRelayListener(FocusListener var1) {
      synchronized(this) {
         super.addFocusListener(var1);
         this.textPane.addFocusListener(var1);
      }
   }

   @Override
   public void addMouseListener(MouseListener var1) {
      this.textPane.addMouseListener(var1);
   }

   public void appendOutputMessage(String var1, ConsoleOutputPane.OutputLevel var2) {
      switch(var2) {
      case DEFAULT:
         this.appendStyledStringToOutput(var1, this.defaultStyle);
         break;
      case ERROR:
         this.appendStyledStringToOutput(var1, this.errorStyle);
         break;
      case WARNING:
         this.appendStyledStringToOutput(var1, this.warningStyle);
         break;
      case NOTICE:
         this.appendStyledStringToOutput(var1, this.noticeStyle);
         break;
      case BOLD:
         this.appendStyledStringToOutput(var1, this.boldStyle);
         break;
      default:
         this.appendStyledStringToOutput(var1, this.defaultStyle);
      }

   }

   public void appendStyledStringToOutput(String var1, AttributeSet var2) {
      int var3 = var1.length() - this.MaximumCharacters;
      if (var3 > 0) {
         this.offer(new ConsoleOutputPane.TextClear());
         var1 = var1.substring(var3, var1.length());
      }

      this.offer(new ConsoleOutputPane.TextBlock(var1, var2));
   }

   void appendTextToPane(String var1, AttributeSet var2) {
      if (var1 != null) {
         int var3 = var1.length();
         if (var3 != 0) {
            try {
               this.outputDoc.appendString(var1, var2);
            } catch (BadLocationException var5) {
               var5.printStackTrace();
            }

         }
      }
   }

   public void appendToOutput(String var1) {
      this.appendOutputMessage(var1, ConsoleOutputPane.OutputLevel.DEFAULT);
   }

   public void clearAndReplace(String var1) {
      this.offer(new ConsoleOutputPane.TextClear());
      this.appendOutputMessage(var1, ConsoleOutputPane.OutputLevel.DEFAULT);
   }

   private void offer(Writable var1) {
      this.writer2.enqueue(var1);
   }

   public void copy() {
      this.textPane.copy();
   }

   public void decreaseFontSize() {
      this.setFontSize(--this.currentFontSize);
   }

   void flush() {
      if (this.sb.length() != 0) {
         this.appendTextToPane(this.sb.toString(), this.currentStyle);
         this.sb.setLength(0);
         this.currentStyle = null;
      }
   }

   public int getCurrentFontSize() {
      return this.currentFontSize;
   }

   private Style getStyle(ConsoleOutputPane.StyleTypes var1) {
      Style var2 = null;
      switch(var1) {
      case DEFAULT:
         var2 = this.defaultStyle;
         break;
      case ERROR:
         var2 = this.errorStyle;
         break;
      case WARNING:
         var2 = this.warningStyle;
         break;
      case NOTICE:
         var2 = this.noticeStyle;
         break;
      case BOLD:
         var2 = this.boldStyle;
      }

      return var2;
   }

   public void increaseFontSize() {
      this.setFontSize(++this.currentFontSize);
   }

   public void reapplyStyles(Style var1) {
      this.offer(new ConsoleOutputPane.ReapplyStyles(var1));
   }

   public void removeFocusRelayListener(FocusListener var1) {
      synchronized(this) {
         super.removeFocusListener(var1);
         this.textPane.removeFocusListener(var1);
      }
   }

   @Override
   public void removeMouseListener(MouseListener var1) {
      this.textPane.removeMouseListener(var1);
   }

   public void resetFontSizeToDefault() {
      this.currentFontSize = GuiConstants.FIXED_WIDTH_FONT.size;
      this.setFontSize(this.currentFontSize);
   }

   private void setBackgroundColor(Color var1) {
      this.textPane.setBackground(var1);
      this.setBackground(var1);
      this.textPane.setSelectionColor(this.textPane.getForeground());
   }

   public void setCurrentFontSize(int var1) {
      this.currentFontSize = var1;
      this.setFontSize(this.currentFontSize);
   }

   public void setFontSize(int var1) {
      synchronized(this.DOC_LOCK) {
         StyleConstants.setFontSize(this.docProvidedStyle, var1);
         Font var3 = this.outputDoc.getFont(this.docProvidedStyle);
         JLabel var4 = new JLabel("    ");
         var4.setFont(var3);
         int var5 = var4.getPreferredSize().width;
         Vector var6 = new Vector();

         for(int var7 = 0; var7 < 20; ++var7) {
            var6.add(new TabStop((float)(var5 * var7)));
         }

         TabStop[] var13 = new TabStop[var6.size()];
         var13 = (TabStop[])var6.toArray(var13);
         TabSet var8 = new TabSet(var13);
         Enumeration var9 = this.outputDoc.getStyleNames();

         while(var9.hasMoreElements()) {
            String var10 = var9.nextElement().toString();
            StyleConstants.setTabSet(this.outputDoc.getStyle(var10), var8);
         }

      }
   }

   private void setForegroundColor(Color var1) {
      this.textPane.setForeground(var1);
      this.textPane.setCaretColor(var1);
      this.textPane.setSelectionColor(this.textPane.getForeground());
      this.textPane.setSelectedTextColor(this.textPane.getBackground());
   }

   public void setTextBackgroundColor(ConsoleOutputPane.StyleTypes var1, Color var2) {
      Style var3 = this.getStyle(var1);
      if (var3 != null) {
         StyleConstants.setBackground(var3, var2);
         if (var1 == ConsoleOutputPane.StyleTypes.DEFAULT) {
            this.setBackgroundColor(var2);
            StyleConstants.setBackground(this.boldStyle, var2);
            this.reapplyStyles(this.boldStyle);
         }

         this.reapplyStyles(var3);
      }
   }

   @Override
   public synchronized void setTheme(ColorTheme colorTheme) {
      if (colorTheme != null) {
         if (this.currentTheme != null) {
            this.currentTheme.deleteObserver(this);
         }

         this.currentTheme = colorTheme;
         this.currentTheme.addObserver(this);
         this.applyTheme(colorTheme);
         synchronized(this.connectedThemables) {
            Iterator var3 = this.connectedThemables.iterator();

            while(var3.hasNext()) {
               Themable var4 = (Themable)var3.next();
               var4.setTheme(colorTheme);
            }

         }
      }
   }

   public void addConnectedThemable(Themable var1) {
      synchronized(this.connectedThemables) {
         this.connectedThemables.add(var1);
      }
   }

   public void removeConnectedThemable(Themable var1) {
      synchronized(this.connectedThemables) {
         this.connectedThemables.remove(var1);
      }
   }

   public ColorTheme getTheme() {
      return this.currentTheme;
   }

   private void applyTheme(ColorTheme var1) {
      this.setTextColor(ConsoleOutputPane.StyleTypes.ERROR, var1.getValue(Location.ErrorForeground));
      this.setTextBackgroundColor(ConsoleOutputPane.StyleTypes.ERROR, var1.getValue(Location.ErrorBackground));
      this.setTextColor(ConsoleOutputPane.StyleTypes.DEFAULT, var1.getValue(Location.NormalForeground));
      this.setTextBackgroundColor(ConsoleOutputPane.StyleTypes.DEFAULT, var1.getValue(Location.NormalBackground));
      this.setTextColor(ConsoleOutputPane.StyleTypes.BOLD, var1.getValue(Location.NormalForeground));
      this.setTextBackgroundColor(ConsoleOutputPane.StyleTypes.BOLD, var1.getValue(Location.NormalBackground));
      this.setTextColor(ConsoleOutputPane.StyleTypes.NOTICE, var1.getValue(Location.NoticeForeground));
      this.setTextBackgroundColor(ConsoleOutputPane.StyleTypes.NOTICE, var1.getValue(Location.NoticeBackground));
      this.setTextColor(ConsoleOutputPane.StyleTypes.WARNING, var1.getValue(Location.WarningForeground));
      this.setTextBackgroundColor(ConsoleOutputPane.StyleTypes.WARNING, var1.getValue(Location.WarningBackground));
      this.setBackground(var1.getValue(Location.NormalBackground));
      this.getViewport().setBackground(var1.getValue(Location.NormalBackground));
   }

   @Override
   public void update(Observable observable, Object arg) {
      if (observable instanceof ColorTheme) {
         this.applyTheme((ColorTheme)ColorTheme.class.cast(observable));
      }

   }

   public void setTextColor(ConsoleOutputPane.StyleTypes styleTypes, Color color) {
      Style var3 = this.getStyle(styleTypes);
      if (var3 != null) {
         StyleConstants.setForeground(var3, color);
         if (styleTypes == ConsoleOutputPane.StyleTypes.DEFAULT) {
            this.setForegroundColor(color);
            StyleConstants.setForeground(this.boldStyle, color);
            this.reapplyStyles(this.boldStyle);
         }

         this.reapplyStyles(var3);
      }
   }

   public void stop() {
      this.shouldStop = true;
      this.writer2.stop();
   }

   public JTextComponent getTextPane() {
      return this.textPane;
   }

   public void passInMouseListener(MouseListener var1) {
      this.textPane.addMouseListener(var1);
   }

   public void setWordWrap(boolean wordWrap) {
      this.wordWrap = wordWrap;
      this.textPane.revalidate();
   }

   public boolean getWordWrap() {
      return this.wordWrap;
   }

   public void setPaused(boolean paused) {
      this.inputPaused.setSelected(paused);
      ActionListener[] var2 = this.inputPaused.getActionListeners();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ActionListener var5 = var2[var4];
         var5.actionPerformed(new ActionEvent(this, 0, ""));
      }

   }

   public boolean isPaused() {
      return this.inputPaused.isSelected();
   }

   public ButtonModel getPausedModel() {
      return this.inputPaused;
   }

   public enum StyleTypes {
      DEFAULT,
      ERROR,
      WARNING,
      NOTICE,
      BOLD;
   }

   private class XmlDocumentClosure implements PredicateClosure {
      String nodeName;
      ConsoleOutputPane.OutputLevel style;

      public XmlDocumentClosure(String var2, ConsoleOutputPane.OutputLevel var3) {
         this.nodeName = var2;
         this.style = var3;
      }

      public boolean evaluate(Object var1) {
         if (!(var1 instanceof Node)) {
            return false;
         } else {
            for(Node var2 = (Node)var1; var2 != null; var2 = var2.getNextSibling()) {
               if (var2.getNodeName() != null) {
                  return var2.getNodeName().equals(this.nodeName);
               }
            }

            return false;
         }
      }

      public void execute(Object var1) {
         Node var2 = (Node)var1;
         if (var2.getNodeName() != null) {
            if (var2.getNodeValue() == null) {
               for(Node var3 = var2.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
                  ConsoleOutputPane.this.appendOutputMessage(var3.getNodeValue(), this.style);
               }
            } else {
               ConsoleOutputPane.this.appendOutputMessage(var2.getNodeValue(), this.style);
            }

         }
      }
   }

   class TextClear extends AbstractWritable {
      @Override
      public boolean resets() {
         return true;
      }
   }

   class TextBlock extends AbstractWritable {
      StringBuilder sb = new StringBuilder();
      AttributeSet style;

      public TextBlock(String var2, AttributeSet var3) {
         this.sb.append(var2);
         this.style = var3;
      }

      public String getText() {
         return this.sb.toString();
      }

      public AttributeSet getStyle() {
         return this.style;
      }

      @Override
      public boolean resets() {
         return this.sb.length() >= ConsoleOutputPane.this.MaximumCharacters;
      }

      @Override
      public boolean combine(Writable var1) {
         if (var1 instanceof ConsoleOutputPane.TextBlock) {
            ConsoleOutputPane.TextBlock var2 = (ConsoleOutputPane.TextBlock)ConsoleOutputPane.TextBlock.class.cast(var1);
            if (var2.getStyle().equals(this.getStyle())) {
               this.sb.append(var2.getText());
               return true;
            }
         }

         return false;
      }
   }

   class ReapplyStyles extends AbstractWritable {
      Style style;

      public ReapplyStyles(Style var2) {
         this.style = var2;
      }

      public Style getStyle() {
         return this.style;
      }
   }

   public enum OutputLevel {
      DEFAULT,
      NOTICE,
      WARNING,
      ERROR,
      BOLD;
   }
}
