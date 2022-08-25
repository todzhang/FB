package ddb.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class FindDialog extends BaseDialog {
   public static final String classVersion1 = "4.2";
   public static final long serialVersionUID = -7436375510393405983L;
   public static final String DEFAULT_TITLE = "Find";
   public static final int DISMISS_NEVER = 0;
   public static final int DISMISS_ALWAYS = 1;
   public static final int DISMISS_ON_FIRST_FIND = 2;
   public static final int DISMISS_ON_FIRST_MISS = 3;
   protected static final int MAX_DISMISS_POLICY = 3;
   protected int dismissPolicy;
   protected boolean neverFound;
   protected boolean resetSearchFromBeginning;
   protected Searchable search;
   protected boolean startedSearchAtBeginning;
   protected int testCounter;
   protected boolean wrappedOnce;
   protected boolean wrappingEnabled;
   protected JButton cancel;
   protected JPanel checkboxPanel;
   protected JLabel findLabel;
   protected JCheckBox matchCase;
   protected JCheckBox matchWholeWords;
   protected JButton next;
   protected JButton previous;
   protected JComboBox searchBox;
   protected JCheckBox searchFromBeginning;
   protected JTextField searchTextField;
   protected FindReplaceSettings settings;
   private Thread searchThread;
   private boolean userStopped;

   public FindDialog() {
      this((Searchable)null);
   }

   public FindDialog(Searchable s) {
      this((Component)null, s);
   }

   public FindDialog(Component owner, Searchable s) {
      this(owner, s, (String)null);
   }

   public FindDialog(Component owner, Searchable s, String title) {
      this(owner, s, title, (FindReplaceSettings)null);
   }

   public FindDialog(Component owner, Searchable s, String title, FindReplaceSettings model) {
      super(owner, title == null ? "Find" : title);
      this.dismissPolicy = 0;
      this.neverFound = true;
      this.testCounter = 1;
      this.wrappedOnce = false;
      this.wrappingEnabled = true;
      this.userStopped = false;
      if (s == null) {
         s = new Searchable() {
            public boolean find(String what, boolean forward, boolean searchAtBeginning, boolean caseMatch, boolean wholeWordsMatch) {
               FindDialog var10000 = FindDialog.this;
               var10000.testCounter *= -1;
               return FindDialog.this.testCounter > 0;
            }

            public void stopFind() {
               throw new UnsupportedOperationException("Not supported yet.");
            }
         };
      }

      this.search = s;
      if (model == null) {
         this.settings = DefaultFindReplaceSettings.getInstance();
      } else {
         this.settings = model;
      }

      this.create();
   }

   protected void create() {
      Container contentPane = this.getContentPane();
      contentPane.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = 18;
      gbc.fill = 0;
      gbc.gridheight = 1;
      gbc.gridwidth = 1;
      gbc.gridx = gbc.gridy = 0;
      gbc.insets = new Insets(10, 10, 1, 0);
      gbc.weightx = 0.0D;
      gbc.weighty = 0.0D;
      this.findLabel = new JLabel("Find what:");
      contentPane.add(this.findLabel, gbc);
      gbc.anchor = 11;
      gbc.fill = 0;
      gbc.gridheight = 3;
      gbc.gridwidth = 1;
      gbc.gridx = 3;
      gbc.gridy = 0;
      gbc.insets = new Insets(10, 10, 5, 5);
      gbc.weightx = 0.0D;
      gbc.weighty = 1.0D;
      JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 5));
      contentPane.add(buttonPanel, gbc);
      gbc.anchor = 18;
      gbc.fill = 2;
      gbc.gridheight = 1;
      gbc.gridwidth = 3;
      ++gbc.gridy;
      gbc.gridx = 0;
      gbc.insets = new Insets(1, 10, 10, 10);
      gbc.weightx = 1.0D;
      gbc.weighty = 0.0D;
      this.searchBox = new JComboBox();
      this.searchBox.setMaximumRowCount(5);
      this.searchBox.setEditable(true);
      this.searchBox.setModel(this.settings.getFindModel());
      contentPane.add(this.searchBox, gbc);
      this.searchTextField = getJTextField(this.searchBox);
      gbc.anchor = 18;
      gbc.fill = 0;
      gbc.gridheight = 1;
      gbc.gridwidth = 2;
      gbc.gridx = 0;
      ++gbc.gridy;
      gbc.insets = new Insets(2, 10, 2, 5);
      gbc.weightx = 0.0D;
      gbc.weighty = 0.0D;
      this.checkboxPanel = new JPanel(new GridLayout(3, 1));
      contentPane.add(this.checkboxPanel, gbc);
      this.searchFromBeginning = new JCheckBox("Search from beginning");
      this.setSearchFromBeginningVisible(true);
      this.searchFromBeginning.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            FindDialog.this.startedSearchAtBeginning = FindDialog.this.searchFromBeginning.isSelected();
         }
      });
      this.matchCase = new JCheckBox("Match case");
      this.setMatchCaseVisible(true);
      this.matchCase.setSelected(this.settings.getMatchCase());
      this.matchWholeWords = new JCheckBox("Match whole words only");
      this.setMatchWholeWordsVisible(true);
      this.matchWholeWords.setSelected(this.settings.getMatchWholeWord());
      this.next = new JButton("Next");
      buttonPanel.add(this.next);
      this.next.setEnabled(false);
      this.next.setActionCommand("next");
      final ActionListener searchAction = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            final boolean forward = e.getActionCommand().equals("next");
            FindDialog.this.next.setEnabled(false);
            FindDialog.this.previous.setEnabled(false);
            FindDialog.this.searchBox.setEnabled(false);
            FindDialog.this.searchThread = new Thread() {
               public void run() {
                  FindDialog.this.findIt(forward);
                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        FindDialog.this.next.setEnabled(true);
                        FindDialog.this.previous.setEnabled(true);
                        FindDialog.this.searchBox.setEnabled(true);
                     }
                  });
               }
            };
            FindDialog.this.searchThread.start();
         }
      };
      this.next.addActionListener(searchAction);
      this.previous = new JButton("Previous");
      buttonPanel.add(this.previous);
      this.previous.setEnabled(false);
      this.previous.setActionCommand("previous");
      this.previous.addActionListener(searchAction);
      this.cancel = new JButton("Cancel");
      buttonPanel.add(this.cancel);
      this.cancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (FindDialog.this.searchThread != null && FindDialog.this.searchThread.isAlive()) {
               FindDialog.this.search.stopFind();
               FindDialog.this.userStopped = true;
            } else {
               FindDialog.this.setVisible(false);
            }

         }
      });
      this.searchBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            boolean b = FindDialog.this.searchTextField.getText().length() >= 1;
            FindDialog.this.next.setEnabled(b);
            FindDialog.this.previous.setEnabled(b);
         }
      });
      this.searchTextField.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent e) {
            boolean b = FindDialog.this.searchTextField.getText().length() > 0;
            FindDialog.this.next.setEnabled(b);
            FindDialog.this.previous.setEnabled(b);
         }
      });
      this.searchTextField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String s = FindDialog.this.searchTextField.getText();
            if (s.length() > 0) {
               FindDialog.this.searchBox.setSelectedItem(s);
               ActionEvent ae = new ActionEvent(FindDialog.this.searchTextField, 0, "next");
               searchAction.actionPerformed(ae);
            }

         }
      });
      this.addWindowListener(new WindowAdapter() {
         public void windowActivated(WindowEvent e) {
            FindDialog.this.searchTextField.requestFocus();
            FindDialog.this.searchTextField.selectAll();
         }

         public void windowOpened(WindowEvent e) {
            FindDialog.this.searchTextField.requestFocus();
            FindDialog.this.searchTextField.selectAll();
         }
      });
      this.setMinimumSize(400, 180);
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         public void windowOpened(WindowEvent e) {
            FindDialog.this.searchBox.requestFocus();
         }

         public void windowClosing(WindowEvent e) {
            FindDialog.this.cancel.doClick();
         }
      });
      this.setSearchFromBeginningSelected(true);
      this.startedSearchAtBeginning = true;
      this.resetFlagsForNewSearch();
      final List<Component> items = new Vector();
      items.add(this.searchBox);
      items.add(this.next);
      items.add(this.previous);
      items.add(this.cancel);
      items.add(this.searchFromBeginning);
      items.add(this.matchCase);
      items.add(this.matchWholeWords);
      FocusTraversalPolicy ftp = new FocusTraversalPolicy() {
         public Component getComponentAfter(Container aContainer, Component aComponent) {
            int index = items.indexOf(aComponent);
            if (index == -1) {
               return (Component)items.get(0);
            } else {
               return index < items.size() - 1 ? (Component)items.get(index + 1) : (Component)items.get(0);
            }
         }

         public Component getComponentBefore(Container aContainer, Component aComponent) {
            int index = items.indexOf(aComponent);
            if (index == -1) {
               return (Component)items.get(0);
            } else {
               return index == 0 ? (Component)items.get(items.size() - 1) : (Component)items.get(index - 1);
            }
         }

         public Component getFirstComponent(Container aContainer) {
            return (Component)items.get(0);
         }

         public Component getLastComponent(Container aContainer) {
            return (Component)items.get(items.size() - 1);
         }

         public Component getDefaultComponent(Container aContainer) {
            return (Component)items.get(0);
         }
      };
      this.searchBox.setFocusCycleRoot(true);
      this.searchBox.setFocusTraversalPolicy(ftp);
      this.next.setFocusTraversalPolicy(ftp);
      this.previous.setFocusTraversalPolicy(ftp);
      this.cancel.setFocusTraversalPolicy(ftp);
      this.searchFromBeginning.setFocusTraversalPolicy(ftp);
      this.matchCase.setFocusTraversalPolicy(ftp);
      this.matchWholeWords.setFocusTraversalPolicy(ftp);
      this.findLabel.setLabelFor(this.searchBox);
      setAccessible(buttonPanel, "Option panel", "Panel containing search options");
      setAccessible(this.checkboxPanel, "Option panel", "Panel containing search options");
      setAccessible(this.cancel, "Cancel button", "Dismisses the dialog");
      setAccessible(this.checkboxPanel, "Option panel", "Panel containing search options");
      setAccessible(this.matchCase, "Match case option", "Determines whether the search is case-sensitive");
      setAccessible(this.matchWholeWords, "Match whole words option", "Determines whether the search is for whole words only");
      setAccessible(this.next, "Next button", "Searches forward for the next match");
      setAccessible(this.previous, "Previous button", "Searches backward for the next match");
      setAccessible(this.searchBox, "Search text combo box", "Contains the text to search for");
      setAccessible(this.searchFromBeginning, "Search from beginning option", "Determines whether the search starts at the beginning of the text");
      this.restoreSettings();
      this.pack();
   }

   protected void findIt(boolean forward) {
      final String what = this.getSearchString();
      this.updateSettings();
      boolean b = false;

      try {
         b = this.search.find(what, forward, this.isSearchFromBeginningSelected(), this.isMatchCaseSelected(), this.isMatchWholeWordsSelected());
      } catch (Exception var14) {
         JOptionPane.showMessageDialog(this, "An error occured while searching!\n\nThe text may have been changing during the search.", "Warning", 0);
         return;
      }

      if (this.userStopped) {
         this.userStopped = false;
         JOptionPane.showMessageDialog(this, "Search stopped by user.");
      } else {
         if (b) {
            this.neverFound = false;

            try {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     FindDialog.this.setSearchFromBeginningSelected(false);
                  }
               });
            } catch (Exception var13) {
            }
         } else {
            String msg;
            if (!this.startedSearchAtBeginning && !this.wrappedOnce && this.wrappingEnabled) {
               msg = (forward ? "End" : "Beginning") + " of the search reached.\n\nDo you want to wrap around and continue?";
               int answer = JOptionPane.showConfirmDialog(this, msg, "Continue", 0);
               if (answer == 0) {
                  this.wrappedOnce = true;

                  try {
                     EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                           FindDialog.this.setSearchFromBeginningSelected(true);
                        }
                     });
                  } catch (Exception var11) {
                  }

                  this.findIt(forward);
               } else {
                  try {
                     EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                           FindDialog.this.resetFlagsForNewSearch();
                        }
                     });
                  } catch (Exception var10) {
                  }
               }
            } else {
               msg = this.neverFound ? "Search string not found!" : "Finished searching.";
               JOptionPane.showMessageDialog(this, msg, "Finished Search", 1);

               try {
                  EventQueue.invokeAndWait(new Runnable() {
                     public void run() {
                        FindDialog.this.resetFlagsForNewSearch();
                     }
                  });
               } catch (Exception var12) {
               }
            }
         }

         if (this.dismissPolicy == 1) {
            this.setVisible(false);
         } else if (b) {
            if (this.dismissPolicy == 2) {
               this.setVisible(false);
            }
         } else if (this.dismissPolicy == 3) {
            this.setVisible(false);
         }

         if (!b) {
            try {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     FindDialog.this.searchTextField.requestFocus();
                     FindDialog.this.searchTextField.selectAll();
                  }
               });
            } catch (Exception var9) {
            }
         }

         if (this.searchBox.getItemCount() == 0 || !this.searchBox.getItemAt(0).toString().equals(what)) {
            try {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     FindDialog.this.searchBox.removeItem(what);
                     FindDialog.this.searchBox.insertItemAt(what, 0);
                     FindDialog.this.searchBox.setSelectedIndex(0);
                  }
               });
            } catch (Exception var8) {
            }
         }

         if (this.searchBox.getItemCount() > this.searchBox.getMaximumRowCount()) {
            try {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     FindDialog.this.searchBox.removeItemAt(FindDialog.this.searchBox.getMaximumRowCount());
                  }
               });
            } catch (Exception var7) {
            }
         }

      }
   }

   public int getAutoDismissPolicy() {
      return this.dismissPolicy;
   }

   public String getSearchFieldLabel() {
      return this.findLabel.getText();
   }

   public String getSearchString() {
      return (String)this.searchBox.getSelectedItem();
   }

   protected static JTextField getJTextField(JComboBox comboBox) {
      Component[] children = comboBox.getComponents();

      for(int i = 0; i < children.length; ++i) {
         if (children[i] instanceof JTextField) {
            return (JTextField)children[i];
         }
      }

      throw new InternalError(FindDialog.class.getName() + " cannot be used with this JVM because the JComboBox does not" + " have a JTextField child.  Please report this error to the" + " JBlocks team.");
   }

   public boolean isMatchCaseSelected() {
      return this.matchCase.isSelected();
   }

   public boolean isMatchWholeWordsSelected() {
      return this.matchWholeWords.isSelected();
   }

   public boolean isSearchFromBeginningSelected() {
      return this.searchFromBeginning.isSelected();
   }

   public boolean isWrappingEnabled() {
      return this.wrappingEnabled;
   }

   protected void resetFlagsForNewSearch() {
      this.neverFound = true;
      this.wrappedOnce = false;
      this.setSearchFromBeginningSelected(this.startedSearchAtBeginning);
      this.startedSearchAtBeginning = this.isSearchFromBeginningSelected();
   }

   protected void restoreSettings() {
      this.matchCase.setSelected(this.settings.getMatchCase());
      this.matchWholeWords.setSelected(this.settings.getMatchWholeWord());
      this.searchBox.setModel(this.settings.getFindModel());
      if (this.settings.getFindModel().getSize() > 0) {
         this.next.setEnabled(true);
         this.previous.setEnabled(true);
      }

   }

   public void setAutoDismissPolicy(int policy) {
      if (policy >= 0 && policy <= 3) {
         this.dismissPolicy = policy;
      } else {
         System.out.println("Invalid policy value. Policy change not accepted.");
      }
   }

   public void setMatchCaseSelected(boolean b) {
      this.matchCase.setSelected(b);
   }

   public void setMatchWholeWordsSelected(boolean b) {
      this.matchWholeWords.setSelected(b);
   }

   public void setSearchFromBeginningSelected(boolean b) {
      this.searchFromBeginning.setSelected(b);
   }

   public void setMatchCaseVisible(boolean b) {
      this.checkboxPanel.remove(this.matchCase);
      if (b) {
         this.checkboxPanel.add(this.matchCase);
      }

      this.checkboxPanel.repaint();
   }

   public void setMatchWholeWordsVisible(boolean b) {
      this.checkboxPanel.remove(this.matchWholeWords);
      if (b) {
         this.checkboxPanel.add(this.matchWholeWords);
      }

      this.checkboxPanel.repaint();
   }

   public void setSearchFromBeginningVisible(boolean b) {
      this.checkboxPanel.remove(this.searchFromBeginning);
      if (b) {
         this.checkboxPanel.add(this.searchFromBeginning);
      }

      this.checkboxPanel.repaint();
   }

   public void setSearchFieldLabel(String s) {
      this.findLabel.setText(s);
   }

   public void setSearchString(String s) {
      this.searchBox.setSelectedItem(s);
   }

   public void setWrappingEnabled(boolean b) {
      this.wrappingEnabled = b;
   }

   protected void updateSettings() {
      this.settings.setMatchCase(this.matchCase.isSelected());
      this.settings.setMatchWholeWord(this.matchWholeWords.isSelected());
      this.settings.setFindModel(this.searchBox.getModel());
   }

   public void setVisible(boolean val) {
      if (val) {
         this.restoreSettings();
      }

      super.setVisible(val);
   }

   public static void show(JTextComponent textComponent) {
      show((Component)null, "Find", textComponent);
   }

   public static FindDialog show(Component parent, JTextComponent textComponent) {
      return show(parent, "Find", textComponent);
   }

   public static FindDialog show(String title, JTextComponent textComponent) {
      return show((Component)null, title, textComponent);
   }

   public static FindDialog show(Component parent, String title, JTextComponent textComponent) {
      return show(parent, title, textComponent, (FindReplaceSettings)null);
   }

   public static FindDialog show(Component parent, String title, JTextComponent textComponent, FindReplaceSettings model) {
      TextComponentSearcher searcher = new TextComponentSearcher(textComponent);
      FindDialog dialog = new FindDialog(parent, searcher, title, model);
      dialog.setVisible(true);
      return dialog;
   }
}
