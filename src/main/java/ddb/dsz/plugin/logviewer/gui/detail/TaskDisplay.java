package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.console.ColorTheme;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.library.console.ConsoleOutputPane;
import ddb.gui.Searchable;
import ddb.gui.TextComponentSearcher;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import org.jdesktop.layout.GroupLayout;

public class TaskDisplay extends JPanel implements Searchable {
   TaskVariables variableDisplay;
   ConsoleOutputPane textDisplay;
   boolean noVariables = false;
   boolean hasVariables = false;
   boolean hasText = false;
   TextComponentSearcher xmlSearcher;
   TextComponentSearcher textSearcher;
   private JPanel containsDisplay;
   private JPanel containsVariables;
   private JPanel containsXml;
   private JTabbedPane displayManager;
   private JScrollPane jScrollPane1;
   private JTextArea xmlDisplay;

   public TaskDisplay(CoreController core, boolean showVariables) {
      this.initComponents();
      this.variableDisplay = new TaskVariables();
      this.textDisplay = new ConsoleOutputPane(core, Integer.MAX_VALUE);
      this.containsDisplay.setLayout(new BorderLayout());
      this.containsVariables.setLayout(new BorderLayout());
      this.containsDisplay.add(this.textDisplay);
      this.containsVariables.add(this.variableDisplay);
      if (!showVariables) {
         this.displayManager.remove(this.containsVariables);
         this.noVariables = true;
      }

      this.textSearcher = new TextComponentSearcher(this.textDisplay.getTextPane());
      this.xmlSearcher = new TextComponentSearcher(this.xmlDisplay);
      this.textDisplay.setAutoScroll(false);
      this.textDisplay.setWordWrap(false);
   }

   private void initComponents() {
      this.displayManager = new JTabbedPane();
      this.containsDisplay = new JPanel();
      this.containsXml = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.xmlDisplay = new JTextArea();
      this.containsVariables = new JPanel();
      GroupLayout containsDisplayLayout = new GroupLayout(this.containsDisplay);
      this.containsDisplay.setLayout(containsDisplayLayout);
      containsDisplayLayout.setHorizontalGroup(containsDisplayLayout.createParallelGroup(1).add(0, 395, 32767));
      containsDisplayLayout.setVerticalGroup(containsDisplayLayout.createParallelGroup(1).add(0, 272, 32767));
      this.displayManager.addTab("Display", this.containsDisplay);
      this.xmlDisplay.setColumns(20);
      this.xmlDisplay.setEditable(false);
      this.xmlDisplay.setFont(this.xmlDisplay.getFont());
      this.xmlDisplay.setRows(5);
      this.jScrollPane1.setViewportView(this.xmlDisplay);
      GroupLayout containsXmlLayout = new GroupLayout(this.containsXml);
      this.containsXml.setLayout(containsXmlLayout);
      containsXmlLayout.setHorizontalGroup(containsXmlLayout.createParallelGroup(1).add(this.jScrollPane1, -1, 395, 32767));
      containsXmlLayout.setVerticalGroup(containsXmlLayout.createParallelGroup(1).add(this.jScrollPane1, -1, 272, 32767));
      this.displayManager.addTab("Xml", this.containsXml);
      GroupLayout containsVariablesLayout = new GroupLayout(this.containsVariables);
      this.containsVariables.setLayout(containsVariablesLayout);
      containsVariablesLayout.setHorizontalGroup(containsVariablesLayout.createParallelGroup(1).add(0, 395, 32767));
      containsVariablesLayout.setVerticalGroup(containsVariablesLayout.createParallelGroup(1).add(0, 272, 32767));
      this.displayManager.addTab("Variables", this.containsVariables);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(1).add(this.displayManager, -1, 400, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(1).add(this.displayManager, -1, 300, 32767));
   }

   public void appendDisplay(String string) {
      if (string != null && string.length() != 0) {
         this.textDisplay.appendDisplay(string);
         this.hasText = true;
      }
   }

   public void disableDisplay() {
      this.textDisplay.disableDisplay();
   }

   public void enableDisplay() {
      this.textDisplay.enableDisplay();
   }

   public void appendXml(final String string) {
      if (string.length() > 1000000) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDisplay.this.xmlDisplay.append("XML File too large to display\n");
               TaskDisplay.this.xmlDisplay.append("------------------------------------------------------------\n");
            }
         });
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDisplay.this.xmlDisplay.append(string);
               TaskDisplay.this.xmlDisplay.append("\n");
               TaskDisplay.this.xmlDisplay.append("------------------------------------------------------------\n");
            }
         });
      }

   }

   public void appendVariable(final ObjectValue objVal) {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            TaskDisplay.this.variableDisplay.addData(objVal);
            TaskDisplay.this.hasVariables = true;
         }
      });
   }

   public void configureDisplay(ColorTheme theme) {
      this.textDisplay.setTheme(theme);
   }

   public void clear(final boolean incVars) {
      if (EventQueue.isDispatchThread()) {
         this.textDisplay.clearAndReplace("");
         this.xmlDisplay.setText("");
         if (incVars) {
            this.variableDisplay.clearVariables();
         }
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDisplay.this.clear(incVars);
            }
         });
      }

   }

   public boolean hideUnused() {
      if (!EventQueue.isDispatchThread()) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDisplay.this.hideUnused();
            }
         });
         return true;
      } else {
         int index = 0;
         boolean empty = false;
         if (!this.noVariables && this.hasVariables) {
            empty = false;
         }

         if (!this.hasText) {
            this.textDisplay.appendDisplay("<Warning>There is no text for this item.</Warning>");
            index = 1;
         } else {
            empty = false;
         }

         if (this.xmlDisplay.getText().length() == 0) {
            this.xmlDisplay.append("<!-- There is not XML for this item. -->");
            index = 0;
         } else {
            empty = false;
         }

         this.displayManager.setSelectedIndex(index);
         return empty;
      }
   }

   public boolean find(String what, boolean forward, boolean fromBeginning, boolean matchCase, boolean wholeWords) {
      TextComponentSearcher search;
      if (this.displayManager.getSelectedIndex() == 0) {
         search = this.textSearcher;
      } else {
         if (this.displayManager.getSelectedIndex() != 1) {
            return false;
         }

         search = this.xmlSearcher;
      }

      return search.find(what, forward, fromBeginning, matchCase, wholeWords);
   }

   public int getSelectedPaneIndex() {
      return this.displayManager.getSelectedIndex();
   }

   public void increaseFontSize() {
      this.textDisplay.increaseFontSize();
      Font f = this.xmlDisplay.getFont();
      f = f.deriveFont(f.getSize2D() + 1.0F);
      this.xmlDisplay.setFont(f);
   }

   public void decreaseFontSize() {
      this.textDisplay.decreaseFontSize();
      Font f = this.xmlDisplay.getFont();
      f = f.deriveFont(f.getSize2D() - 1.0F);
      this.xmlDisplay.setFont(f);
   }

   public void setWordWrap(boolean wrap) {
      this.textDisplay.setWordWrap(wrap);
   }

   public boolean getWordWrap() {
      return this.textDisplay.getWordWrap();
   }

   public void setAutoScroll(boolean scroll) {
      this.textDisplay.setAutoScroll(scroll);
   }

   public boolean getAutoScroll() {
      return this.textDisplay.getAutoScroll();
   }

   public void stopFind() {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
