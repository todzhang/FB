package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.console.ColorTheme;
import ddb.console.OptionPane;
import ddb.detach.Alignment;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.dsz.core.task.impl.StringAccess;
import ddb.dsz.plugin.logviewer.gui.LogViewerDetachable;
import ddb.dsz.plugin.logviewer.gui.target.TargetLogspace;
import ddb.gui.FindDialog;
import ddb.gui.Searchable;
import ddb.imagemanager.ImageManager;
import ddb.util.BlockingInputStream;
import ddb.util.FileManips;
import ddb.util.Pair;
import ddb.util.UtilityConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.Executor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Transformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class TaskDetail2 extends LogViewerDetachable implements Observer {
   private static final int SIZE_PER_PAGE = 1000000;
   public static final String TASKING = "Tasking";
   public static final String SEPERATOR = "\n----------------------------------------------------------\n";
   public static final String SUFFIX = "dsz";
   public static final String SEP = "/";
   public static final String PREFIX = "LogViewer";
   private final List<TaskDetail2.PageData2> pages2 = new Vector();
   private final List<File> filesToDestroy = new Vector();
   private final Executor dataCreator = UtilityConstants.createSingleThreadExecutorService("TaskDetail2");
   private Object currentPageObject = null;
   private Thread workerThread = null;
   private Closure printError = new Closure() {
      public void execute(final Object arg0) {
         if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  execute(arg0);
               }
            });
         } else {
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  if (arg0 instanceof TransformerException) {
                     TransformerException te = (TransformerException)arg0;
                     TaskDetail2.this.errorOutput.append(String.format("Transformer Error: %s\n", te.getMessageAndLocation()));
                  } else if (arg0 instanceof SAXParseException) {
                     SAXParseException se = (SAXParseException)arg0;
                     TaskDetail2.this.errorOutput.append(String.format("Data Error: %s (%d:%d)\n", se.getMessage(), se.getLineNumber(), se.getColumnNumber()));
                  } else if (arg0 instanceof Exception) {
                     Exception e = (Exception)arg0;
                     e.printStackTrace();
                     TaskDetail2.this.errorOutput.append(e.getMessage() + "\n");
                     TaskDetail2.this.setDisplaySetting();
                  }

               }
            });
         }
      }
   };
   private Transformer parsePage;
   private Closure parseVars;
   private static final String NO_ERRORS = "No errors";
   private final DataTransformer transformer;
   private JList pageList;
   private DefaultListModel pageModel;
   private final Object PAGE_LOCK = new Object();
   private TaskDisplay page;
   private FindDialog finder;
   ColorTheme _currentTheme;
   JTextArea errorOutput = new JTextArea();
   boolean discardText = false;
   JTextField parameters = new JTextField();
   JPanel innerDisplay = new JPanel(new BorderLayout());
   Task task;
   CoreController core;
   private boolean developer = false;
   private JSplitPane splitter;
   private JPanel headerPanel = new JPanel();
   JMenuBar headerMenu = new JMenuBar();
   JToolBar headerToolBar = new JToolBar(0);
   JTextField headerCommand = new JTextField();
   JCheckBoxMenuItem WordWrap;
   JCheckBoxMenuItem AutoScroll;
   String displayTransform;
   String storageTransform;
   String pageTitle;
   boolean forcedFocus = false;
   JLabel taskStatus = new JLabel("");
   private int findPage = 0;
   private int currentPage = 0;
   private File tempDir;
   TaskDetail2.SearchDelegate delegate = new TaskDetail2.SearchDelegate();

   public TaskDetail2(Task task, CoreController cc, TargetLogspace parent, boolean developer, String displayTransform, String storageTransform) {
      this.transformer = DataTransformer.newInstance(String.format("LogViewer:  %d - %s", task.getId().getId(), task.getFullCommandLine()), true);
      this.tempDir = new File(cc.getUserConfigDirectory(), "LogViewer");
      this.tempDir.mkdirs();
      this.task = task;
      this.core = cc;
      this.developer = developer;
      super.setAlignment(Alignment.RIGHT);
      super.setVerifyClose(false);
      super.setLogo("images/document2.png", ImageManager.SIZE16);
      this.displayTransform = displayTransform;
      this.storageTransform = storageTransform;
      this.headerCommand.setEditable(false);
      this.headerCommand.setMargin(new Insets(0, 0, 0, 0));
      JPanel topper = new JPanel(new BorderLayout());
      topper.add(this.headerCommand, "North");
      this.pageModel = new DefaultListModel();
      this.pageList = new JList(this.pageModel);
      this.pageList.setPrototypeCellValue("Page XXXXXX");
      this.pageList.setCellRenderer(new DefaultListCellRenderer() {
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel && value instanceof Integer) {
               ((JLabel)JLabel.class.cast(c)).setText("Page " + Integer.class.cast(value));
            }

            return c;
         }
      });
      this.page = new TaskDisplay(this.core, developer);
      this.page.variableDisplay.go.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskDetail2.this.parseVariables();
         }
      });
      this.pageList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               Integer i = (Integer)TaskDetail2.this.pageList.getSelectedValue();
               synchronized(TaskDetail2.this.PAGE_LOCK) {
                  if (i != null) {
                     TaskDetail2.this.currentPage = i;
                  } else {
                     TaskDetail2.this.currentPage = 1;
                  }

                  TaskDetail2.this.displayPage(TaskDetail2.this.currentPage);
               }
            }

         }
      });
      JPanel main = new JPanel(new BorderLayout());
      main.add(new JScrollPane(this.pageList), "West");
      main.add(this.page, "Center");
      topper.add(main, "Center");
      if (this.task == null) {
         this.setName("Unknown Task");
      } else {
         this.setName(this.task.getId() + " " + this.task.getCommandName());
      }

      this.splitter = new JSplitPane(0);
      this.splitter.setOpaque(false);
      this.splitter.setLeftComponent(topper);
      this.splitter.setRightComponent(new JScrollPane(this.errorOutput));
      this.splitter.setOneTouchExpandable(true);
      this.display.setLayout(new BorderLayout());
      this.display.add(this.splitter, "Center");
      this.splitter.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            TaskDetail2.this.setDisplaySetting();
         }
      });
      this._currentTheme = OptionPane.getInstance().getSharedTheme();
      this.ConfigurePane(this._currentTheme);
      this.headerCommand.setText(task.getTypedCommand());
      this.createHeader();
      StringBuilder sb = new StringBuilder();
      this.parameters.setText(sb.toString());
      this.parameters.setToolTipText("Put parameters here, in this format:  name=value  No spaces are allowed");
      this.finder = new FindDialog(this.getDisplay(), this.delegate);
      this.core.execute(new Runnable() {
         public void run() {
            TaskDetail2.this.setup();
         }
      });
   }

   private void setDisplaySetting() {
      if (this.errorOutput.getText().length() != 0 && !this.errorOutput.getText().equals("No errors")) {
         if ((double)this.splitter.getDividerLocation() * 1.0D >= (double)this.splitter.getSize().height * 0.8D) {
            this.splitter.setDividerLocation(0.8D);
         }
      } else {
         this.splitter.setDividerLocation(1.0D);
      }

   }

   public void setMaxCharacters(int max) {
   }

   private void createHeader() {
      GridBagConstraints gbc = new GridBagConstraints();
      GridBagLayout gbl = new GridBagLayout();
      this.headerCommand.setFont(this.headerCommand.getFont().deriveFont(1));
      JPanel spacer = new JPanel();
      this.headerPanel.setLayout(gbl);
      gbc.fill = 1;
      gbc.weightx = 0.0D;
      gbc.anchor = 17;
      gbc.gridx = -1;
      gbc.gridy = 0;
      this.headerPanel.add(this.headerMenu);
      gbl.addLayoutComponent(this.headerMenu, gbc);
      this.headerPanel.add(this.headerToolBar);
      gbl.addLayoutComponent(this.headerToolBar, gbc);
      gbc.weightx = 10.0D;
      this.headerPanel.add(spacer);
      gbl.addLayoutComponent(spacer, gbc);
      this.headerToolBar.setFloatable(false);
      JMenu menu = new JMenu("Options");
      this.headerMenu.add(menu);
      JMenuItem find = new JMenuItem("Find", ImageManager.getIcon("images/zoom.png", this.core.getLabelImageSize()));
      menu.add(find);
      menu.addSeparator();
      this.WordWrap = new JCheckBoxMenuItem("Word Wrap");
      menu.add(this.WordWrap);
      this.WordWrap.setSelected(this.page.getWordWrap());
      this.WordWrap.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskDetail2.this.page.setWordWrap(!TaskDetail2.this.page.getWordWrap());
            TaskDetail2.this.WordWrap.setSelected(TaskDetail2.this.page.getWordWrap());
         }
      });
      this.AutoScroll = new JCheckBoxMenuItem("Auto Scroll");
      menu.add(this.AutoScroll);
      this.AutoScroll.setSelected(this.page.getAutoScroll());
      this.AutoScroll.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TaskDetail2.this.page.setAutoScroll(!TaskDetail2.this.page.getAutoScroll());
            TaskDetail2.this.AutoScroll.setSelected(TaskDetail2.this.page.getAutoScroll());
         }
      });
      find.addActionListener(new TaskDetail2.FindAction());
      JButton findButton = new JButton("");
      findButton.addActionListener(new TaskDetail2.FindAction());
      findButton.setToolTipText("Find");
      findButton.setIcon(ImageManager.getIcon("images/zoom.png", this.core.getLabelImageSize()));
      this.headerToolBar.add(findButton);
      JButton printButton = new JButton("");
      printButton.setToolTipText("Print");
      printButton.setIcon(ImageManager.getIcon("images/print_printer.png", this.core.getLabelImageSize()));
      JButton increaseFont;
      if (this.developer) {
         increaseFont = new JButton("");
         this.headerToolBar.add(increaseFont);
         increaseFont.setToolTipText("Reload");
         increaseFont.setIcon(ImageManager.getIcon("images/reload.png", this.core.getLabelImageSize()));
         increaseFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               TaskDetail2.this.setup();
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     TaskDetail2.this.setDisplaySetting();
                  }
               });
            }
         });
      }

      this.headerToolBar.addSeparator();
      this.headerToolBar.add(new JLabel("Font Size:"));
      increaseFont = new JButton("");
      increaseFont.addActionListener(new TaskDetail2.IncreaseAction());
      increaseFont.setToolTipText("Increase font size");
      increaseFont.setIcon(ImageManager.getIcon("images/blue-plus.png", this.core.getLabelImageSize()));
      this.headerToolBar.add(increaseFont);
      JButton decreaseFont = new JButton("");
      decreaseFont.addActionListener(new TaskDetail2.DecreaseAction());
      decreaseFont.setToolTipText("Decrease font size");
      decreaseFont.setIcon(ImageManager.getIcon("images/blue-minus.png", this.core.getLabelImageSize()));
      this.headerToolBar.add(decreaseFont);
      this.headerToolBar.addSeparator();
      this.headerToolBar.add(new JLabel("Task:"));
      this.headerToolBar.add(this.taskStatus);
   }

   private void setup() {
      this.task.unsubscribe(this);
      this.pages2.clear();
      this.pages2.add(new TaskDetail2.PageData2());
      this.workerThread = null;
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            TaskDetail2.this.pageModel.clear();
            TaskDetail2.this.pageModel.addElement(1);
            TaskDetail2.this.pageList.setSelectedValue(1, true);
            TaskDetail2.this.errorOutput.setText("");
         }
      });
      this.parsePage = new Transformer() {
         StringBuilder temp = new StringBuilder();
         Closure worker;

         {
            this.worker = ClosureFactory.newDisplayClosure(TaskDetail2.this.core, TaskDetail2.this.task, new Closure() {
               public void execute(Object arg0) {
                  temp.append(arg0.toString());
               }
            }, TaskDetail2.this.printError);
         }

         public Object transform(Object o) {
            this.temp.setLength(0);
            this.worker.execute(o);
            String ret = this.temp.toString();
            this.temp.setLength(0);
            return ret;
         }
      };
      if (this.developer) {
         this.parseVars = ClosureFactory.newVariableClosure(this.core, this.task, new Closure() {
            public void execute(Object arg0) {
               TaskDetail2.this.page.appendVariable(((DataEvent)DataEvent.class.cast(arg0)).getData());
            }
         }, this.printError);
         this.transformer.removeTask(this.task);
         this.transformer.addTask(this.task);
         this.parseVariables();
      }

      Integer selectedValue = (Integer)this.pageList.getSelectedValue();
      if (selectedValue != null) {
         this.currentPage = selectedValue;
      }

      this.displayPage(this.currentPage);
      this.task.subscribe(this, true);
   }

   private void displayPage(int pageNum) {
      if (this.pages2.size() != 0) {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDetail2.this.pageList.setEnabled(false);
               TaskDetail2.this.disablePage();
            }
         });
         synchronized(this.dataCreator) {
            this.currentPageObject = new Object();
            this.page.clear(false);
            if (pageNum == 1) {
               TaskDetail2.PageData2 pageData = (TaskDetail2.PageData2)this.pages2.get(0);
               if (pageData.data.size() == 0 || ((TaskDataAccess)((Pair)pageData.data.get(0)).getFirst()).getType() != DataType.TASKING) {
                  pageData.data.add(0, new Pair(this.task.getTaskingAccess(), (Object)null));
               }
            }

            this.dataCreator.execute(new TaskDetail2.PageLoader((TaskDetail2.PageData2)this.pages2.get(pageNum - 1), this.currentPageObject, pageNum - 1));
            this.dataCreator.execute(new Runnable() {
               public void run() {
                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        TaskDetail2.this.pageList.setEnabled(true);
                        TaskDetail2.this.enablePage();
                     }
                  });
               }
            });
         }
      }
   }

   private void parseVariables() {
      this.page.variableDisplay.clearVariables();
      int iterations = (Integer)this.page.variableDisplay.maximumVars.getValue();
      if (this.task.getDataCount() < iterations) {
         iterations = this.task.getDataCount();
      }

      if (!this.task.isAlive()) {
         this.parseVars.execute(this.task.getTaskingAccess());
      }

      for(int i = 0; i < iterations; ++i) {
         this.parseVars.execute(this.task.getDataAccess(i));
      }

   }

   public boolean allowClosing() {
      return true;
   }

   @Override
   public JMenuBar getMenuBar() {
      return null;
   }

   private void ConfigurePane(ColorTheme theme) {
      this.page.configureDisplay(theme);
   }

   @Override
   public void close() {
      this.task.unsubscribe(this);
      if (this.transformer != null) {
         this.transformer.stop();
      }

      this.destroyFiles();
   }

   @Override
   public JComponent getHeader() {
      return this.headerPanel;
   }

   private void destroyFiles() {
      this.workerThread = null;
      Thread th = new Thread(new Runnable() {
         public void run() {
            Collections.reverse(TaskDetail2.this.filesToDestroy);
            Iterator i$ = TaskDetail2.this.filesToDestroy.iterator();

            while(i$.hasNext()) {
               File f = (File)i$.next();

               try {
                  f.delete();
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }

         }
      });
      th.setName("Temp File Destroyer");
      th.setPriority(1);
      th.start();
   }

   public void update(Observable o, Object arg) {
      EventQueue.invokeLater(new TaskDetail2.UpdateTaskState(this.task));
      if (arg instanceof TaskDataAccess) {
         TaskDataAccess tda = (TaskDataAccess)arg;
         if (tda.getType() == DataType.DATA) {
            synchronized(this.dataCreator) {
               try {
                  TaskDetail2.PageData2 data = (TaskDetail2.PageData2)this.pages2.get(this.pages2.size() - 1);
                  long size = 0L;

                  Pair item;
                  for(Iterator i$ = data.data.iterator(); i$.hasNext(); size += ((TaskDataAccess)item.getFirst()).getSize()) {
                     item = (Pair)i$.next();
                  }

                  if (size > 500000L) {
                     data = new TaskDetail2.PageData2();
                     this.pages2.add(data);
                     final int temp = this.pages2.size();
                     EventQueue.invokeLater(new Runnable() {
                        public void run() {
                           TaskDetail2.this.pageModel.addElement(temp);
                        }
                     });
                  }

                  Pair<TaskDataAccess, File> newPair = new Pair(tda, (Object)null);
                  data.data.add(newPair);
                  if (this.currentPage == this.pages2.size()) {
                     this.displayPage(this.currentPage);
                  }
               } catch (Exception var11) {
                  var11.printStackTrace();
               }

            }
         }
      }
   }

   public void disablePage() {
      if (EventQueue.isDispatchThread()) {
         this.pageList.setEnabled(false);
         this.page.setEnabled(false);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDetail2.this.disablePage();
            }
         });
      }

   }

   public void enablePage() {
      if (EventQueue.isDispatchThread()) {
         this.pageList.setEnabled(true);
         this.page.setEnabled(true);
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               TaskDetail2.this.enablePage();
            }
         });
      }

   }

   private String getText(Pair<TaskDataAccess, File> pair) {
      String text = this.readFromFile((File)pair.getSecond());
      return text != null ? text : this.fullTranslate(pair);
   }

   private String readFromFile(Reader reader) {
      if (reader == null) {
         return null;
      } else {
         try {
            StringBuilder retVal = new StringBuilder();
            char[] buffer = new char[4096];

            while(reader.ready()) {
               int read = reader.read(buffer);
               retVal.append(buffer, 0, read);
            }

            reader.close();
            return retVal.toString();
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   private String readFromFile(File input) {
      if (input == null) {
         return null;
      } else {
         try {
            return this.readFromFile((Reader)FileManips.createFileReader(input));
         } catch (Exception var3) {
            var3.printStackTrace();
            return null;
         }
      }
   }

   private String fullTranslate(Pair<TaskDataAccess, File> data) {
      if (data != null && this.parsePage != null) {
         TaskDataAccess tda = (TaskDataAccess)data.getFirst();
         if (tda.getType() == DataType.TASKING && tda.getTask().isAlive() && this.workerThread == null) {
            TaskDetail2.TaskingDisplay taskingDisplay = new TaskDetail2.TaskingDisplay(data);
            Thread th = this.core.newThread("TaskDetail Live Tasking Transformer", taskingDisplay);
            taskingDisplay.setParent(th);
            this.workerThread = th;
            th.start();
            return "";
         } else {
            String text = (String)this.parsePage.transform(data.getFirst());

            try {
               File cacheDirectory = new File(this.core.getUserConfigDirectory(), "LogViewer");
               cacheDirectory.mkdirs();
               this.filesToDestroy.add(cacheDirectory);
               File cacheFile = File.createTempFile("log", ".txt", cacheDirectory);
               this.filesToDestroy.add(cacheFile);
               OutputStreamWriter osw = FileManips.createFileWriter(cacheFile, false);
               osw.write(text);
               osw.close();
               data.setSecond(cacheFile);
            } catch (Exception var7) {
               var7.printStackTrace();
            }

            return text;
         }
      } else {
         return null;
      }
   }

   private class TaskingDisplay implements Runnable {
      Thread parent = null;
      private final Pair<TaskDataAccess, File> data;

      public TaskingDisplay(Pair<TaskDataAccess, File> data) {
         this.data = data;
      }

      public void setParent(Thread p) {
         this.parent = p;
      }

      public void run() {
         TaskDataAccess tda = (TaskDataAccess)this.data.getFirst();

         try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            try {
               DefaultHandler handler = new DefaultHandler() {
                  boolean bStarted = false;
                  int level = 0;
                  StringBuilder sb = new StringBuilder();

                  private void checkForExit() {
                     if (TaskingDisplay.this.parent != TaskDetail2.this.workerThread) {
                        throw new IllegalStateException("No longer caring about the tasking file");
                     }
                  }

                  public void endDocument() throws SAXException {
                     this.checkForExit();
                     super.endDocument();
                  }

                  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                     this.checkForExit();
                     super.startElement(uri, localName, qName, attributes);
                     if (this.bStarted) {
                        this.sb.append("<" + qName + " ");

                        for(int i = 0; i < attributes.getLength(); ++i) {
                           this.sb.append(attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\" ");
                        }

                        this.sb.append(">");
                        ++this.level;
                     }

                     if (qName.equals("CommandTasking")) {
                        this.bStarted = true;
                     }

                  }

                  public void endElement(String uri, String localName, String qName) throws SAXException {
                     this.checkForExit();
                     super.endElement(uri, localName, qName);
                     if (this.bStarted) {
                        --this.level;
                        this.sb.append("</" + qName + ">");
                        if (this.level == 0) {
                           String text = String.format("<DataLog><CommandTasking>%s</CommandTasking></DataLog>", this.sb.toString());
                           String newText = (String)TaskDetail2.this.parsePage.transform(new StringAccess((TaskDataAccess)TaskingDisplay.this.data.getFirst(), text));
                           this.checkForExit();
                           this.write(newText);
                           this.sb.setLength(0);
                        } else if (this.level < 0) {
                           throw new IllegalStateException("Done stuff!");
                        }
                     }

                  }

                  public void characters(char[] ch, int start, int length) throws SAXException {
                     this.checkForExit();
                     super.characters(ch, start, length);
                     if (this.bStarted) {
                        this.sb.append(ch, start, length);
                     }

                  }

                  private void write(String text) {
                     List<String> textItems = null;
                     if (text == null) {
                        textItems = Collections.EMPTY_LIST;
                     } else if (text.startsWith("<Node")) {
                        textItems = new Vector();

                        while(true) {
                           int nextString = text.indexOf("<Node", 1);
                           if (nextString < 0) {
                              ((List)textItems).add(text);
                              break;
                           }

                           ((List)textItems).add(text.substring(0, nextString));
                           text = text.substring(nextString);
                        }
                     } else {
                        textItems = Collections.singletonList(text);
                     }

                     List<String> finalTextItems = textItems;
                     EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           Iterator i$ = ((List) finalTextItems).iterator();

                           while(i$.hasNext()) {
                              String item = (String)i$.next();
                              TaskDetail2.this.page.appendDisplay(item);
                           }

                        }
                     });
                  }
               };
               parser.parse(new BlockingInputStream(tda.getStream()), handler);
            } catch (IllegalStateException var5) {
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }

   private class SearchDelegate implements Searchable {
      private boolean stopSearch;
      private boolean found;
      private int startFindPage;
      private int currentFindPage;

      private SearchDelegate() {
         this.found = false;
         this.startFindPage = -1;
         this.currentFindPage = -1;
      }

      public void reset() {
         this.startFindPage = this.currentFindPage = -1;
      }

      public boolean find(final String what, final boolean forward, final boolean fromBeginning, final boolean matchCase, final boolean wholeWords) {
         if (this.startFindPage == -1) {
            this.startFindPage = TaskDetail2.this.currentPage - 1;
         }

         if (this.currentFindPage == -1) {
            this.currentFindPage = TaskDetail2.this.currentPage - 1;
         }

         this.stopSearch = false;
         new StringBuffer();
         Object var7 = null;

         try {
            EventQueue.invokeAndWait(new Runnable() {
               public void run() {
                  TaskDetail2.this.disablePage();
                  SearchDelegate.this.found = TaskDetail2.this.page.find(what, forward, fromBeginning, matchCase, wholeWords);
               }
            });
         } catch (Exception var12) {
         }

         TaskDetail2.this.enablePage();
         if (this.found) {
            return true;
         } else {
            do {
               if (forward) {
                  ++this.currentFindPage;
                  if (this.currentFindPage >= TaskDetail2.this.pages2.size()) {
                     if (!fromBeginning) {
                        break;
                     }

                     this.currentFindPage = 0;
                  }
               } else {
                  --this.currentFindPage;
                  if (this.currentFindPage < 0) {
                     this.currentFindPage = TaskDetail2.this.pages2.size() - 1;
                  }
               }

               if (this.currentFindPage == this.startFindPage) {
                  break;
               }

               TaskDetail2.PageSearcher ps = TaskDetail2.this.new PageSearcher((TaskDetail2.PageData2)TaskDetail2.this.pages2.get(this.currentFindPage), TaskDetail2.this.page.getSelectedPaneIndex() == 0);
               if (ps.find(what, forward, fromBeginning, matchCase, wholeWords)) {
                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        TaskDetail2.this.pageList.setSelectedValue(SearchDelegate.this.currentFindPage + 1, true);
                     }
                  });

                  try {
                     Thread.sleep(2000L);
                  } catch (Exception var10) {
                  }

                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        TaskDetail2.this.page.find(what, forward, true, matchCase, wholeWords);
                     }
                  });
                  return true;
               }
            } while(!this.stopSearch);

            try {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     TaskDetail2.this.currentPage = TaskDetail2.this.findPage;
                     TaskDetail2.this.pageList.setSelectedValue(TaskDetail2.this.currentPage, true);
                  }
               });
            } catch (Exception var11) {
            }

            return false;
         }
      }

      public void stopFind() {
         this.stopSearch = true;
      }

      // $FF: synthetic method
      SearchDelegate(Object x1) {
         this();
      }
   }

   private class PageSearcher implements Searchable {
      final TaskDetail2.PageData2 pageData;
      final boolean bText;
      boolean bStop = false;

      public PageSearcher(TaskDetail2.PageData2 pageData, boolean bText) {
         this.pageData = pageData;
         this.bText = bText;
      }

      public boolean find(String what, boolean forward, boolean fromBeginning, boolean matchCase, boolean wholeWords) {
         if (what == null) {
            return false;
         } else {
            List<Pair<TaskDataAccess, File>> data = new ArrayList(this.pageData.data);
            Iterator i$ = data.iterator();

            String searchString;
            do {
               if (!i$.hasNext()) {
                  return false;
               }

               Pair<TaskDataAccess, File> item = (Pair)i$.next();
               if (this.bStop) {
                  return false;
               }

               searchString = this.bText ? TaskDetail2.this.getText(item) : TaskDetail2.this.readFromFile(((TaskDataAccess)item.getFirst()).getReader());
               if (searchString == null) {
                  return false;
               }

               if (!matchCase) {
                  searchString = searchString.toLowerCase();
                  what = what.toLowerCase();
               }
            } while(!searchString.contains(what));

            return true;
         }
      }

      public void stopFind() {
         this.bStop = true;
      }
   }

   private final class UpdateTaskState implements Runnable {
      Task task;

      public UpdateTaskState(Task t) {
         this.task = t;
      }

      public void run() {
         TaskDetail2.this.taskStatus.setIcon(ImageManager.getIcon(this.task.getState().getIcon(), TaskDetail2.this.core.getLabelImageSize()));
         TaskDetail2.this.taskStatus.setText(this.task.getState().getText());
      }
   }

   private final class DecreaseAction implements ActionListener {
      private DecreaseAction() {
      }

      public void actionPerformed(ActionEvent e) {
         TaskDetail2.this.page.decreaseFontSize();
      }

      // $FF: synthetic method
      DecreaseAction(Object x1) {
         this();
      }
   }

   private final class IncreaseAction implements ActionListener {
      private IncreaseAction() {
      }

      public void actionPerformed(ActionEvent e) {
         TaskDetail2.this.page.increaseFontSize();
      }

      // $FF: synthetic method
      IncreaseAction(Object x1) {
         this();
      }
   }

   private final class FindAction implements ActionListener {
      private FindAction() {
      }

      public void actionPerformed(ActionEvent e) {
         TaskDetail2.this.core.submit(new Runnable() {
            public void run() {
               TaskDetail2.this.findPage = TaskDetail2.this.currentPage;
               TaskDetail2.this.delegate.reset();
               TaskDetail2.this.finder.setVisible(true);
            }
         });
      }

      // $FF: synthetic method
      FindAction(Object x1) {
         this();
      }
   }

   public class PageLoader implements Runnable {
      TaskDetail2.PageData2 pageData;
      Object currentPageLock;
      int pageIndex = -1;

      public PageLoader(TaskDetail2.PageData2 page, Object currentPageLock, int pageIndex) {
         this.pageData = page;
         this.currentPageLock = currentPageLock;
         this.pageIndex = pageIndex;
      }

      public void run() {
         int currentLength = 0;

         for(int i = 0; i < this.pageData.data.size(); ++i) {
            synchronized(TaskDetail2.this.dataCreator) {
               if (TaskDetail2.this.currentPageObject != this.currentPageLock && TaskDetail2.this.currentPageObject != null) {
                  return;
               }
            }

            String text;
            try {
               text = TaskDetail2.this.getText((Pair)this.pageData.data.get(i));
            } catch (Exception var11) {
               continue;
            }

            if (text != null) {
               if (this.pageData.start >= 0 && i + 1 == this.pageData.data.size()) {
                  text = text.substring(this.pageData.start);
               }

               TaskDetail2.PageData2 newItem;
               int index;
               if (this.pageData.length >= 0 && i + 1 == this.pageData.data.size()) {
                  text = text.substring(0, this.pageData.length);
               } else if (text.length() + currentLength > 1000000) {
                  if (this.pageData.data.size() > i + 1) {
                     newItem = TaskDetail2.this.new PageData2();
                     List<Pair<TaskDataAccess, File>> subList = this.pageData.data.subList(1, this.pageData.data.size());
                     newItem.data.addAll(subList);
                     TaskDetail2.this.pages2.add(this.pageIndex + 1, newItem);
                     subList.clear();
                     final int size = TaskDetail2.this.pages2.size();
                     EventQueue.invokeLater(new Runnable() {
                        public void run() {
                           TaskDetail2.this.pageModel.addElement(size);
                        }
                     });
                  }

                  String searchText = "\n";
                  if (text.startsWith("<Node")) {
                     searchText = "<Node";
                  }

                  index = text.lastIndexOf(searchText, 1000000);
                  if (index == -1) {
                     index = text.indexOf(searchText, 1000000);
                  }

                  if (index == -1) {
                     index = 1000000;
                  }

                  this.pageData.length = index;
                  TaskDetail2.PageData2 newItemx = TaskDetail2.this.new PageData2();
                  newItemx.data.add(this.pageData.data.get(i));
                  newItemx.start = this.pageData.start + this.pageData.length;
                  TaskDetail2.this.pages2.add(this.pageIndex + 1, newItemx);
                  final int sizex = TaskDetail2.this.pages2.size();
                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        TaskDetail2.this.pageModel.addElement(sizex);
                     }
                  });
                  text = text.substring(0, index);
               }

               newItem = null;
               final Object textItems;
               if (text.startsWith("<Node")) {
                  textItems = new Vector();

                  while(true) {
                     index = text.indexOf("<Node", 1);
                     if (index < 0) {
                        ((List)textItems).add(text);
                        break;
                     }

                     ((List)textItems).add(text.substring(0, index));
                     text = text.substring(index);
                  }
               } else {
                  textItems = Collections.singletonList(text);
               }

               final String xml = TaskDetail2.this.readFromFile(((TaskDataAccess)((Pair)this.pageData.data.get(i)).getFirst()).getReader());
               synchronized(TaskDetail2.this.dataCreator) {
                  if (TaskDetail2.this.currentPageObject != this.currentPageLock || TaskDetail2.this.currentPageObject == null) {
                     return;
                  }

                  Runnable r = new Runnable() {
                     public void run() {
                        Iterator i$ = ((List)textItems).iterator();

                        while(i$.hasNext()) {
                           String item = (String)i$.next();
                           TaskDetail2.this.page.appendDisplay(item);
                        }

                        TaskDetail2.this.page.appendXml(xml);
                     }
                  };
                  if (EventQueue.isDispatchThread()) {
                     r.run();
                  } else {
                     EventQueue.invokeLater(r);
                  }
               }
            }
         }

      }
   }

   public class PageData2 {
      final List<Pair<TaskDataAccess, File>> data = new ArrayList();
      int start = 0;
      int length = -1;
   }
}
