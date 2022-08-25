package ddb.dsz.plugin.logviewer;

import ddb.detach.TabbableOption;
import ddb.dsz.core.controller.CoreController;
import ddb.util.checkedtablemodel.CheckableFilterList;
import ddb.util.checkedtablemodel.CheckedTableSelection;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class LogViewerOptions extends TabbableOption {
   int initialized = 0;
   int finalized = 0;
   CoreController core;
   JPanel panel;
   Collection<String> shown = new Vector();
   Collection<String> hidden = new Vector();
   private CheckableFilterList<String> commands;

   @Override
   public JComponent getDisplay() {
      return this.panel;
   }

   public LogViewerOptions() {
      super(LogViewer.class);
      this.setName("LogViewer");
      this.panel = new JPanel(new BorderLayout());
      this.commands = new CheckableFilterList("By default,show these commands", new CheckedTableSelection<String>() {
         public void selected(String item, boolean selected) {
            Collection giving;
            Collection taking;
            if (!selected) {
               giving = LogViewerOptions.this.hidden;
               taking = LogViewerOptions.this.shown;
            } else {
               taking = LogViewerOptions.this.hidden;
               giving = LogViewerOptions.this.shown;
            }

            taking.remove(item);
            if (!giving.contains(item)) {
               giving.add(item.toString());
            }

         }
      }, String.CASE_INSENSITIVE_ORDER);
      this.panel.add(this.commands, "Center");
      JButton temp = new JButton("Commit");
      temp.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            LogViewerOptions.this.commit();
         }
      });
      this.panel.add(temp, "South");
   }

   void commit() {
      this.core.setOption(LogViewer.class, "Shown", this.shown);
      this.core.setOption(LogViewer.class, "Hidden", this.hidden);
      this.core.commitSettings();
   }

   @Override
   public void fini() {
      synchronized(this) {
         ++this.finalized;
         if (this.finalized >= this.initialized) {
            ;
         }
      }
   }

   public void init(CoreController cc) {
      synchronized(this) {
         ++this.initialized;
         if (this.initialized > 1) {
            return;
         }
      }

      this.core = cc;
      Object obj1 = this.core.getOption(LogViewer.class, "Shown");
      Object obj2 = this.core.getOption(LogViewer.class, "Hidden");
      Collection hiddenCommands;
      Iterator i$;
      Object o;
      if (obj1 != null && obj1 instanceof Collection) {
         hiddenCommands = (Collection)Collection.class.cast(obj1);
         i$ = hiddenCommands.iterator();

         while(i$.hasNext()) {
            o = i$.next();
            if (this.commands.addElement(o.toString().toLowerCase(), true)) {
               this.shown.add(o.toString().toLowerCase());
            }
         }
      }

      if (obj2 != null && obj2 instanceof Collection) {
         hiddenCommands = (Collection)Collection.class.cast(obj2);
         i$ = hiddenCommands.iterator();

         while(i$.hasNext()) {
            o = i$.next();
            String s = o.toString().toLowerCase();
            if (this.commands.addElement(s, false)) {
               this.hidden.add(s);
            }
         }

         hiddenCommands.removeAll(this.shown);
      }

   }

   public void addCommand(String s) {
      if (s != null) {
         s = s.toLowerCase();
         if (!this.shown.contains(s) && !this.hidden.contains(s)) {
            if (this.commands.addElement(s, true)) {
               this.shown.add(s);
            }

         }
      }
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      return false;
   }

   @Override
   public JComponent getDefaultElement() {
      return null;
   }
}
