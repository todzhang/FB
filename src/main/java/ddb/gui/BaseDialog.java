package ddb.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class BaseDialog extends JDialog {
   public static final String classVersion = "5.0.1";
   protected static final Point INITIAL_LOCATION = new Point(-1, -1);
   protected static Insets buttonInsets = new Insets(3, 10, 3, 10);
   protected static Image defaultImage = null;
   protected static Frame defaultInvisibleFrame = JOptionPane.getFrameForComponent((Component)null);
   protected JPanel actionArea;
   protected JPanel bottomPanel;
   protected Vector<JButton> buttons;
   protected JPanel buttonAreaExternalPanel;
   protected JPanel buttonAreaInternalPanel;
   protected boolean childWasShowing;
   protected Component componentOwner;
   protected JButton defaultButton;
   protected JButton helpButton;
   protected Dimension minimumSize;
   protected JComponent newContentPane;
   protected Container originalContentPane;
   protected Dimension preferredSize;
   protected JSeparator separatorBar;
   protected WindowListener windowListener;

   public BaseDialog() {
      this((Component)null, "", false);
   }

   public BaseDialog(String title) {
      this((Component)null, title, false);
   }

   public BaseDialog(String title, boolean modal) {
      this((Component)null, title, modal);
   }

   public BaseDialog(Component owner) {
      this(owner, "", false);
   }

   public BaseDialog(Component owner, String title) {
      this(owner, title, false);
   }

   public BaseDialog(Component parent, String title, boolean modal) {
      super(JOptionPane.getFrameForComponent(parent), title == null ? "" : title, modal);
      this.buttons = new Vector();
      this.childWasShowing = false;
      this.componentOwner = null;
      this.defaultButton = null;
      this.helpButton = null;
      this.minimumSize = null;
      this.preferredSize = null;
      this.windowListener = null;
      this.componentOwner = parent;
      this.create();
   }

   public JButton addActionAreaButton(JButton b) {
      if (this.helpButton != null) {
         this.buttonAreaInternalPanel.remove(this.helpButton);
      }

      this.separatorBar.setVisible(true);
      this.actionArea.setVisible(true);
      b.setMargin(buttonInsets);
      this.buttonAreaInternalPanel.add(b);
      if (this.helpButton != null) {
         this.buttonAreaInternalPanel.add(this.helpButton);
      }

      this.buttons.addElement(b);
      return b;
   }

   public JButton addActionAreaButton(String label) {
      return this.addActionAreaButton(new JButton(label));
   }

   public JButton addHelpButton(JButton b) {
      this.removeActionAreaButton(this.helpButton);
      this.helpButton = b;
      return this.addActionAreaButton(b);
   }

   private void create() {
      if (this.componentOwner != null && this.componentOwner instanceof Dialog) {
         this.componentOwner.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
               if (BaseDialog.this.isShowing()) {
                  BaseDialog.this.setVisible(false);
                  BaseDialog.this.childWasShowing = true;
               }

            }

            public void componentShown(ComponentEvent e) {
               if (BaseDialog.this.childWasShowing) {
                  BaseDialog.this.setVisible(true);
               }

               BaseDialog.this.childWasShowing = false;
            }
         });
      }

      this.originalContentPane = super.getContentPane();
      this.originalContentPane.setLayout(new BorderLayout());
      String title = this.getTitle();
      if (title != null && title.length() > 0) {
         this.setTitle(title);
      }

      this.setContentPane(new JPanel(new BorderLayout()));
      this.bottomPanel = new JPanel(new BorderLayout());
      this.originalContentPane.add(this.bottomPanel, "South");
      this.actionArea = new JPanel(new BorderLayout());
      this.actionArea.setVisible(false);
      this.separatorBar = new JSeparator();
      this.separatorBar.setBorder(new EmptyBorder(0, 0, 10, 0));
      this.separatorBar.setVisible(false);
      this.setActionAreaSeparatorVisible(true);
      this.buttonAreaExternalPanel = new JPanel(new FlowLayout());
      this.buttonAreaExternalPanel.setBorder(new EmptyBorder(3, 7, 1, 7));
      this.actionArea.add(this.buttonAreaExternalPanel, "Center");
      this.buttonAreaInternalPanel = new JPanel(new HorizontalLayout(10));
      this.buttonAreaExternalPanel.add(this.buttonAreaInternalPanel);
      this.bottomPanel.add(this.actionArea, "North");
      this.windowListener = new WindowAdapter() {
         public void windowActivated(WindowEvent we) {
            if (BaseDialog.this.defaultButton != null) {
               BaseDialog.this.defaultButton.requestFocus();
               BaseDialog.this.getRootPane().setDefaultButton(BaseDialog.this.defaultButton);
            }

         }
      };
      this.addWindowListener(this.windowListener);
      this.setLocation(INITIAL_LOCATION);
      this.pack();
      setAccessible(this.buttonAreaInternalPanel, "Action Area", "The area containing the action buttons for this dialog");
   }

   public JButton[] getActionAreaButtons() {
      Component[] c = this.buttonAreaInternalPanel.getComponents();
      Vector<JButton> localButtons = new Vector();

      for(int i = 0; c != null && i < c.length; ++i) {
         if (c[i] instanceof JButton) {
            localButtons.addElement((JButton)c[i]);
         }
      }

      if (localButtons.size() == 0) {
         return null;
      } else {
         JButton[] b = new JButton[localButtons.size()];

         for(int j = 0; j < b.length; ++j) {
            b[j] = (JButton)localButtons.elementAt(j);
         }

         return b;
      }
   }

   public JButton getActionAreaButton(String label) {
      JButton[] b = this.getActionAreaButtons();
      if (b == null) {
         return null;
      } else {
         for(int i = 0; i < b.length; ++i) {
            if (b[i].getText().equals(label)) {
               return b[i];
            }
         }

         return null;
      }
   }

   public Container getContentPane() {
      return this.newContentPane;
   }

   public Dimension getMinimumSize() {
      return this.minimumSize == null ? super.getMinimumSize() : this.minimumSize;
   }

   public Dimension getPreferredSize() {
      Dimension pref = this.preferredSize == null ? super.getPreferredSize() : this.preferredSize;
      Dimension min = this.getMinimumSize();
      if (min == null) {
         return pref;
      } else {
         int width = pref.getWidth() < min.getWidth() ? (int)min.getWidth() : (int)pref.getWidth();
         int height = pref.getHeight() < min.getHeight() ? (int)min.getHeight() : (int)pref.getHeight();
         return new Dimension(width, height);
      }
   }

   public void removeActionAreaButton(String label) {
      JButton b = this.getActionAreaButton(label);
      if (b != null) {
         this.removeActionAreaButton(b);
      }
   }

   public void removeActionAreaButton(JButton b) {
      if (b != null) {
         this.buttonAreaInternalPanel.remove(b);
         this.buttonAreaInternalPanel.invalidate();
         this.buttonAreaInternalPanel.validate();
         if (b == this.helpButton) {
            this.helpButton = null;
         }

      }
   }

   public void removeActionAreaButtons() {
      JButton[] localButtons = this.getActionAreaButtons();

      for(int i = 0; localButtons != null && i < localButtons.length; ++i) {
         this.removeActionAreaButton(localButtons[i]);
      }

   }

   public void removeHelpButton() {
      this.removeActionAreaButton(this.helpButton);
      this.helpButton = null;
   }

   public static void setAccessible(JComponent component, String name, String description) {
      component.getAccessibleContext().setAccessibleName(name);
      component.getAccessibleContext().setAccessibleDescription(description);
   }

   public void setActionAreaSeparatorVisible(boolean b) {
      if (b) {
         this.actionArea.add(this.separatorBar, "North");
      } else {
         this.actionArea.remove(this.separatorBar);
      }

      this.actionArea.invalidate();
      this.actionArea.validate();
   }

   public void setContentPane(JComponent newContentPane) {
      if (this.newContentPane != null) {
         this.originalContentPane.remove(this.newContentPane);
      }

      this.newContentPane = newContentPane;
      this.originalContentPane.add(newContentPane);
   }

   public void setDefaultButton(JButton b) {
      if (b == null) {
         this.getRootPane().setDefaultButton((JButton)null);
         this.defaultButton = b;
      } else {
         this.defaultButton = this.buttons.contains(b) ? b : null;
         this.getRootPane().setDefaultButton(this.defaultButton);
      }
   }

   public void setMargin(int margin) {
      Border border = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
      this.newContentPane.setBorder(border);
   }

   public void setMinimumSize(Dimension size) {
      this.minimumSize = size;
   }

   public void setMinimumSize(int width, int height) {
      this.setMinimumSize(new Dimension(width, height));
   }

   public void setPreferredSize(Dimension size) {
      this.preferredSize = size;
   }

   public void setTitle(String title) {
      if (title != null && !title.equals("")) {
         super.setTitle(title);
      }
   }

   public void setTitle(String title, boolean b) {
      if (b) {
         this.setTitle(title);
      } else {
         super.setTitle(title);
      }

   }

   public static JDialog showDialog(Object message, String title) {
      return showDialog(message, title);
   }
}
