package ddb.dsz.plugin.scripteditor;

import ddb.dsz.annotations.DszDescription;
import ddb.dsz.annotations.DszLive;
import ddb.dsz.annotations.DszLogo;
import ddb.dsz.annotations.DszName;
import ddb.dsz.core.command.CommandEvent;
import ddb.dsz.core.command.IdCallback;
import ddb.dsz.core.controller.DispatcherException;
import ddb.dsz.core.data.ClosureFactory;
import ddb.dsz.core.data.ClosureOrder;
import ddb.dsz.core.data.DataEvent;
import ddb.dsz.core.data.DataTransformer;
import ddb.dsz.core.data.ObjectValue;
import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.core.task.TaskState;
import ddb.dsz.core.task.TaskDataAccess.DataType;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.dsz.plugin.scripteditor.jaxb.filelist.FileEntry;
import ddb.dsz.plugin.scripteditor.jaxb.filelist.FileList;
import ddb.dsz.plugin.scripteditor.jaxb.filelist.ObjectFactory;
import ddb.dsz.plugin.scripteditor.jaxb.keywords.Keyword;
import ddb.dsz.plugin.scripteditor.jaxb.keywords.Keywords;
import ddb.dsz.plugin.scripteditor.jaxb.styles.Style;
import ddb.dsz.plugin.scripteditor.jaxb.styles.Styles;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import ddb.util.JaxbCache;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoableEdit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Closure;
import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.SyntaxDocument;
import org.syntax.jedit.SyntaxStyle;
import org.syntax.jedit.TextAreaDefaults;
import org.syntax.jedit.tokenmarker.DssTokenMarker;

@DszLive(
   live = true,
   replay = true
)
@DszLogo("images/abiword_48.png")
@DszName("Script Editor")
@DszDescription("Provides a GUI interface for editing scripts")
public class ScriptEditor extends NoHostAbstractPlugin {
   public static final String SyntaxStyle = "/ScriptEditor/styles.xml";
   public static final String DssKeywords = "/ScriptEditor/dssKeywords.xml";
   public static final String MRU = "ScriptEditor_mru.xml";
   public static final String OPEN_FILES = "ScriptEditor_open.xml";
   public static final String JAXB = "ddb.dsz.plugin.scripteditor.jaxb";
   public int MRU_MAX = 5;
   CaretListener caretListener = new CaretListener() {
      public void caretUpdate(CaretEvent var1) {
         ScriptEditor.EditorPane var2 = ScriptEditor.this.currentPane;
         if (var2 != null) {
            int var3 = var2.textArea.getSelectionStart();
            int var4 = var2.textArea.getSelectionEnd();
            boolean var8 = false;
            int var7 = 0;
            boolean var6 = false;
            int var5 = 0;
            int var11 = var2.textArea.getLineOfOffset(var3);
            int var12 = var3 - var2.textArea.getLineStartOffset(var11);
            String var9 = var2.textArea.getLineText(var11);

            for(int var10 = 0; var10 < var12 && var10 < var9.length(); ++var10) {
               switch(var9.charAt(var10)) {
               case '\t':
                  var5 += 4;
                  break;
               default:
                  ++var5;
               }
            }

            if (var3 != var4) {
               var7 = var4 - var3;
            }

            if (var7 > 0) {
               ScriptEditor.this.setStatus(String.format("Ln %-10d Col %-10d Ch %-10d Length %-10d", var11, var5, var12, var7));
            } else {
               ScriptEditor.this.setStatus(String.format("Ln %-10d Col %-10d Ch %-10d", var11, var5, var12));
            }
         }

      }
   };
   private Commands[][] buildCommands;
   private Commands[][] editCommands;
   private Commands[][] fileCommands;
   private Commands[][] testCommands;
   private Commands[][] toolbarCommands;
   JFileChooser chooser;
   File listenFile;
   ErrorTableModel model;
   JSplitPane splitPane;
   JToolBar toolBar;
   JTabbedPane tabbedFiles;
   List<ScriptEditor.EditorPane> fileList;
   ScriptEditor.EditorPane currentPane;
   int fileCounter;
   String pendingRun;
   boolean compileRunning;
   private TextAreaDefaults textAreaDefaults;
   private Map<Object, Commands> buttonToCommand;
   ActionListener buttonClicked;
   List<FileEntry> mrulist;
   List<FileEntry> persistFiles;
   DataTransformer transformer;
   Task lastRun;
   int line;
   File loadFile;

   public ErrorTableModel getModel() {
      return this.model;
   }

   public ScriptEditor() {
      this.buildCommands = new Commands[][]{{Commands.Compile}};
      this.editCommands = new Commands[][]{{Commands.Cut, Commands.Copy, Commands.Paste}, {Commands.Undo, Commands.Redo, Commands.Reload}};
      this.fileCommands = new Commands[][]{{Commands.New, Commands.Open}, {Commands.Save, Commands.SaveAs, Commands.SaveAll}, {Commands.Close, Commands.CloseAll}, {Commands.RecentFiles}};
      this.testCommands = new Commands[0][];
      this.toolbarCommands = new Commands[][]{{Commands.New, Commands.Open, Commands.Save, Commands.SaveAs, Commands.Close}, {Commands.Cut, Commands.Copy, Commands.Paste, Commands.Undo, Commands.Redo, Commands.Reload}, {Commands.Compile}};
      this.chooser = null;
      this.listenFile = null;
      this.currentPane = null;
      this.fileCounter = 0;
      this.pendingRun = null;
      this.compileRunning = false;
      this.mrulist = new Vector();
      this.persistFiles = null;
      this.transformer = DataTransformer.newInstance();
      this.lastRun = null;
      this.line = -1;
      super.setName("Script Editor");
   }

   @Override
   protected int init2() {
      super.setCareAboutLocalEvents(true);
      if (this.transformer == null) {
         return -1;
      } else {
         this.transformer.addClosure(ClosureFactory.newVariableClosure(this.core, "script", "Dsz", var1 -> {
            if (var1 != null && var1 instanceof DataEvent) {
               DataEvent var2 = (DataEvent)var1;
               Iterator var3 = var2.getData().getObjects("syntaxerror").iterator();

               ObjectValue var4;
               ErrorEntry var5;
               while(var3.hasNext()) {
                  var4 = (ObjectValue)var3.next();
                  var5 = new ErrorEntry();
                  var5.setLine(var4.getInteger("line"));
                  var5.setText(var4.getString("error"));
                  ScriptEditor.this.model.addError(var5);
               }

               var3 = var2.getData().getObjects("Error").iterator();

               while(var3.hasNext()) {
                  var4 = (ObjectValue)var3.next();
                  var5 = new ErrorEntry();
                  var5.setText(var4.getString("error"));
                  ScriptEditor.this.model.addError(var5);
               }

            }
         }));
         this.transformer.addClosure(var1 -> {
            if (var1 != null && var1 instanceof TaskDataAccess) {
               TaskDataAccess var2 = (TaskDataAccess)var1;
               Task var3 = var2.getTask();
               if (var3 != null) {
                  if (TaskState.SUCCEEDED.equals(var3.getState())) {
                     ScriptEditor.this.model.setState(ErrorTableModel.EvaluationState.SUCCESS);
                  } else if (TaskState.FAILED.equals(var3.getState())) {
                     ScriptEditor.this.model.setState(ErrorTableModel.EvaluationState.FAILURE);
                  }

                  ScriptEditor.this.compileRunning = false;
                  ScriptEditor.this.updateToolbar();
               }

            }
         }, ClosureOrder.MIDDLE, DataType.STATE);
         this.fileList = new ArrayList();
         this.buttonClicked = actionEvent -> {
            Commands var2 = (Commands)ScriptEditor.this.buttonToCommand.get(actionEvent.getSource());
            if (var2 != null) {
               ScriptEditor.this.performCommand(var2);
            }

         };
         this.buttonToCommand = new Hashtable();
         JPanel var1 = new JPanel(new BorderLayout());
         JPanel var2 = new JPanel(new BorderLayout());
         this.tabbedFiles = new JTabbedPane();
         this.menuBar = new JMenuBar();
         this.toolBar = new JToolBar();
         this.buildMenu(this.fileCommands, "File");
         this.buildMenu(this.editCommands, "Edit");
         this.buildMenu(this.buildCommands, "Build");
         this.buildMenu(this.testCommands, "Test");
         this.buildToolBar(this.toolbarCommands);
         this.tabbedFiles.addChangeListener(changeEvent -> {
            ScriptEditor.this.currentPane = ScriptEditor.this.getEditorPaneByComponent(ScriptEditor.this.tabbedFiles.getSelectedComponent());
            ScriptEditor.this.updateToolbar();
         });
         DropTarget var3 = new DropTarget(this.tabbedFiles, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent var1) {
               try {
                  Transferable var2 = var1.getTransferable();
                  var1.acceptDrop(var1.getDropAction());
                  List var3 = (List)var2.getTransferData(DataFlavor.javaFileListFlavor);
                  Iterator var4 = var3.iterator();

                  while(var4.hasNext()) {
                     Object var5 = var4.next();
                     if (var5 instanceof File) {
                        ScriptEditor.this.loadFile((File)File.class.cast(var5), 0);
                     }
                  }
               } catch (Exception var6) {
                  ScriptEditor.this.core.logEvent(Level.WARNING, "DragAndDrop failure", var6);
               }

            }
         });
         this.tabbedFiles.setDropTarget(var3);
         var2.add(this.toolBar, "North");
         var2.add(this.tabbedFiles, "Center");
         this.model = new ErrorTableModel();
         JTable var4 = new JTable(this.model);
         var4.getColumnModel().getColumn(ErrorTableColumns.LINE.ordinal()).setMaxWidth(60);
         var4.getColumnModel().getColumn(ErrorTableColumns.FILE.ordinal()).setPreferredWidth(160);
         var4.getColumnModel().getColumn(ErrorTableColumns.FILE.ordinal()).setMaxWidth(200);
         var4.setDefaultRenderer(File.class, new DszTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
               Component var7 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               if (var7 instanceof JLabel && value instanceof File) {
                  JLabel var8 = (JLabel)var7;
                  File var9 = (File) value;
                  var8.setText(var9.getName());
               }

               return var7;
            }
         });
         var4.addMouseListener(new LineFocusMouseListener(this, var4));
         JScrollPane var5 = new JScrollPane(var4);
         var5.setMinimumSize(new Dimension(0, 0));
         this.splitPane = new JSplitPane(0, var2, var5);
         this.splitPane.setOneTouchExpandable(true);
         this.splitPane.setContinuousLayout(true);
         this.splitPane.setResizeWeight(1.0D);
         this.splitPane.addComponentListener(new ComponentAdapter() {
            boolean shown = false;

            @Override
            public void componentResized(ComponentEvent var1) {
               if (!this.shown) {
                  ScriptEditor.this.splitPane.setDividerLocation(0.75D);
                  ScriptEditor.this.splitPane.setLastDividerLocation(ScriptEditor.this.splitPane.getDividerLocation());
                  ScriptEditor.this.splitPane.setDividerLocation(1.0D);
                  ScriptEditor.this.splitPane.removeComponentListener(this);
                  this.shown = true;
               }

            }
         });
         var1.add(this.splitPane, "Center");
         super.setDisplay(var1);
         this.loadStyle();
         this.loadKeywords();
         this.core.submit(() -> {
            ScriptEditor.this.loadFile(ScriptEditor.this.loadFile, ScriptEditor.this.line);
         });
         this.mrulist.addAll(this.loadFileListFromDisk("ScriptEditor_mru.xml"));
         List var6 = this.loadFileListFromDisk("ScriptEditor_open.xml");
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            FileEntry var8 = (FileEntry)var7.next();
            this.LoadFileEntry(var8);
         }

         return 0;
      }
   }

   @Override
   protected void commandEnded(CommandEvent var1) {
      super.commandEnded(var1);
      if (this.lastRun != null && this.lastRun.equals(this.core.getTaskById(var1.getId()))) {
         this.compileRunning = false;
         if (this.lastRun.getState().equals(TaskState.SUCCEEDED)) {
            this.model.setState(ErrorTableModel.EvaluationState.SUCCESS);
         } else {
            this.model.setState(ErrorTableModel.EvaluationState.FAILURE);
         }

         this.updateToolbar();
      }

   }

   void LoadFileEntry(FileEntry var1) {
      ScriptEditor.EditorPane var2 = this.loadFile(new File(var1.getName()), 0);
      if (var2 != null) {
         JEditTextArea var3 = var2.getTextArea();
         if (var3 != null) {
            var3.setSelectionEnd(var1.getSelectionStop());
            var3.setSelectionStart(var1.getSelectionStart());
         }
      }

   }

   @Override
   protected boolean parseArgument2(String var1, String var2) {
      if (var1.equalsIgnoreCase("-file") && var2 != null) {
         this.loadFile = new File(var2);
         return true;
      } else if (var1.equalsIgnoreCase("-line") && var2 != null) {
         try {
            this.line = Integer.parseInt(var2);
            return true;
         } catch (NumberFormatException var4) {
            this.core.logEvent(Level.WARNING, String.format("Invalid format: '%s'", var2), var4);
            return false;
         }
      } else if (var1.equalsIgnoreCase("-max") && var2 != null) {
         try {
            this.MRU_MAX = Integer.parseInt(var2);
            return true;
         } catch (NumberFormatException var5) {
            this.core.logEvent(Level.WARNING, String.format("Invalid format: '%s'", var2), var5);
            return false;
         }
      } else {
         return false;
      }
   }

   private void setupActionMap() {
      Commands[] var1 = Commands.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         final Commands var4 = var1[var3];
         if (var4.stroke != null) {
            this.textAreaDefaults.inputHandler.addKeyBinding(var4.stroke, actionEvent -> {
               if (ScriptEditor.this.validCommand(var4)) {
                  ScriptEditor.this.performCommand(var4);
               }

            });
         }
      }

   }

   ScriptEditor.EditorPane getEditorPaneByComponent(Component var1) {
      if (var1 == null) {
         return null;
      } else {
         synchronized(this) {
            Iterator var3 = this.fileList.iterator();

            ScriptEditor.EditorPane var4;
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               var4 = (ScriptEditor.EditorPane)var3.next();
            } while(var4.getTextArea() != var1);

            return var4;
         }
      }
   }

   private synchronized ScriptEditor.EditorPane makeNewEditorPane(File var1) {
      ScriptEditor.EditorPane var2 = new ScriptEditor.EditorPane(new JEditTextArea(this.textAreaDefaults), var1, var1 == null ? ++this.fileCounter : 0);
      this.tabbedFiles.add("NewFile", var2.getTextArea());
      this.fileList.add(var2);
      this.editorModified(var2);
      var2.getTextArea().addCaretListener(this.caretListener);
      return var2;
   }

   @Override
   protected void fini2() {
      this.persistFiles = new Vector();
      this.closeAll(false);
      this.commitFileListToDisk(this.mrulist, "ScriptEditor_mru.xml");
      this.commitFileListToDisk(this.persistFiles, "ScriptEditor_open.xml");
   }

   private List<FileEntry> loadFileListFromDisk(String var1) {
      var1 = String.format("%s/%s", this.core.getUserConfigDirectory(), var1);

      try {
         JAXBContext var2 = JaxbCache.getContext(ObjectFactory.class);
         Unmarshaller var3 = var2.createUnmarshaller();
         Object var4 = var3.unmarshal(new File(var1));
         if (var4 instanceof JAXBElement) {
            var4 = ((JAXBElement)JAXBElement.class.cast(var4)).getValue();
         }

         FileList var5 = null;
         if (var4 instanceof FileList) {
            var5 = (FileList)FileList.class.cast(var4);
         }

         Vector var6 = new Vector();
         Iterator var7 = var5.getFileEntry().iterator();

         while(var7.hasNext()) {
            Object var8 = var7.next();
            if (var8 instanceof FileEntry) {
               var6.add(FileEntry.class.cast(var8));
            }
         }

         return var6;
      } catch (Exception var9) {
         return new Vector();
      }
   }

   private void commitFileListToDisk(List<FileEntry> var1, String var2) {
      var2 = String.format("%s/%s", this.core.getUserConfigDirectory(), var2);
      FileList var3 = new FileList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         FileEntry var5 = (FileEntry)var4.next();
         var3.getFileEntry().add(var5);
      }

      try {
         JAXBContext var9 = JaxbCache.getContext(ObjectFactory.class);
         Marshaller var10 = var9.createMarshaller();
         ObjectFactory var6 = new ObjectFactory();
         var10.setProperty("jaxb.formatted.output", Boolean.TRUE);
         FileWriter var7 = new FileWriter(var2);
         var10.marshal(var6.createFileList(var3), var7);
         var7.close();
      } catch (Exception var8) {
      }

   }

   private void buildMenu(Commands[][] var1, String var2) {
      if (var1.length != 0) {
         boolean var3 = false;
         final JMenu var4 = new JMenu(var2);
         var4.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(MenuEvent var1) {
            }

            @Override
            public void menuDeselected(MenuEvent var1) {
            }

            @Override
            public void menuSelected(MenuEvent var1) {
               Component[] var2 = var4.getMenuComponents();
               if (var2 != null) {
                  Component[] var3 = var2;
                  int var4x = var2.length;

                  for(int var5 = 0; var5 < var4x; ++var5) {
                     Component var6 = var3[var5];
                     Commands var7 = (Commands)ScriptEditor.this.buttonToCommand.get(var6);
                     if (var7 != null) {
                        var6.setEnabled(ScriptEditor.this.validCommand(var7));
                     }
                  }

               }
            }
         });

         for(int var5 = 0; var5 < var1.length; ++var5) {
            if (var3) {
               var4.addSeparator();
               var3 = false;
            }

            for(int var6 = 0; var6 < var1[var5].length; ++var6) {
               final Commands var7 = var1[var5][var6];
               Object var8;
               if (var7.submenu) {
                  final JMenu var9 = new JMenu(var7.text);
                  var8 = var9;
                  var4.addMenuListener(new MenuListener() {
                     @Override
                     public void menuCanceled(MenuEvent var1) {
                     }

                     @Override
                     public void menuDeselected(MenuEvent var1) {
                     }

                     @Override
                     public void menuSelected(MenuEvent var1) {
                        ScriptEditor.this.setupMenu(var7, var9);
                     }
                  });
               } else {
                  var8 = new NoAcceleratorMenuItem(var7.text);
                  ((JMenuItem)var8).addActionListener(this.buttonClicked);
                  ((JMenuItem)var8).setAccelerator(var7.stroke);
               }

               this.buttonToCommand.put(var8, var7);
               if (var7.icon != null) {
                  ((JMenuItem)var8).setIcon(ImageManager.getIcon(var7.icon, ImageManager.SIZE16));
               }

               ((JMenuItem)var8).setMnemonic(var7.mnemonic);
               ((JMenuItem)var8).setToolTipText(var7.description);
               var4.add((JMenuItem)var8);
               var3 = true;
            }
         }

         this.menuBar.add(var4);
      }
   }

   void setupMenu(Commands var1, JMenu var2) {
      if (var1 == Commands.RecentFiles) {
         var2.removeAll();
         var2.setEnabled(false);
         Iterator var3 = this.mrulist.iterator();

         while(var3.hasNext()) {
            final FileEntry var4 = (FileEntry)var3.next();
            JMenuItem var5 = new JMenuItem(var4.getName());
            var5.addActionListener(var11 -> ScriptEditor.this.LoadFileEntry(var4));
            var2.add(var5);
         }
      }

   }

   private void buildToolBar(Commands[][] var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var2) {
            this.toolBar.addSeparator();
            var2 = false;
         }

         for(int var4 = 0; var4 < var1[var3].length; ++var4) {
            Commands var5 = var1[var3][var4];
            JButton var6 = new JButton();
            if (var5.icon != null) {
               var6.setIcon(ImageManager.getIcon(var5.icon, ImageManager.SIZE22));
               var6.setToolTipText(var5.description);
               var6.addActionListener(this.buttonClicked);
               this.buttonToCommand.put(var6, var5);
               this.toolBar.add(var6);
               var2 = true;
            }
         }
      }

      this.updateToolbar();
   }

   public boolean closeAll(boolean var1) {
      while(true) {
         if (this.fileList.size() > 0) {
            if (this.closeFile(var1)) {
               continue;
            }

            return false;
         }

         return true;
      }
   }

   private boolean closeFileHelper(ScriptEditor.EditorPane var1, boolean var2) {
      if (var1 == null) {
         return false;
      } else {
         if (var1.isModified()) {
            int var3 = JOptionPane.showConfirmDialog(this.parentDisplay, "Would you like to save the file?", String.format("%s Modified", var1.getFileName()), var2 ? 1 : 0);
            switch(var3) {
            case 0:
               if (!this.saveHelper(var1)) {
                  return false;
               }
            case 1:
            default:
               break;
            case 2:
               return false;
            }
         }

         synchronized(this) {
            FileEntry var4 = null;

            int var5;
            for(var5 = 0; var5 < this.mrulist.size(); ++var5) {
               if ((new File(((FileEntry)this.mrulist.get(var5)).getName())).equals(var1.getFile())) {
                  var4 = (FileEntry)this.mrulist.get(var5);
                  this.mrulist.remove(var5);
               }
            }

            if (var4 == null) {
               var4 = new FileEntry();
               var4.setName(var1.getFile().getAbsolutePath());
            }

            try {
               var4.setName((new File(var4.getName())).getCanonicalPath());
            } catch (Exception var7) {
            }

            var4.setSelectionStart(var1.getTextArea().getSelectionStart());
            var4.setSelectionStop(var1.getTextArea().getSelectionEnd());
            var4.setTopLine(var1.getTextArea().getFirstLine());
            this.mrulist.add(0, var4);
            if (this.persistFiles != null) {
               this.persistFiles.add(var4);
            }

            while(this.mrulist.size() > this.MRU_MAX) {
               this.mrulist.remove(this.MRU_MAX);
            }

            var5 = this.fileList.indexOf(var1);
            this.fileList.remove(var1);
            this.tabbedFiles.remove(var1.getTextArea());
            if (var5 >= this.fileList.size()) {
               var5 = this.fileList.size() - 1;
            }

            if (var1 == this.currentPane) {
               if (var5 >= 0) {
                  this.currentPane = (ScriptEditor.EditorPane)this.fileList.get(var5);
                  this.setSelectedPane(this.currentPane);
               } else {
                  this.currentPane = null;
               }
            }

            return true;
         }
      }
   }

   public boolean closeFile(boolean var1) {
      return this.closeFileHelper(this.currentPane, var1);
   }

   private boolean compileHelper(ScriptEditor.EditorPane editorPane) {
      if (editorPane == null) {
         return false;
      } else {
         synchronized(this) {
            if (this.compileRunning) {
               return false;
            }

            this.compileRunning = true;
         }

         this.model.setState(ErrorTableModel.EvaluationState.RUNNING);
         if (!this.save()) {
            this.compileRunning = false;
            this.model.setState(ErrorTableModel.EvaluationState.FAILURE);
            return false;
         } else if (editorPane.getFile() == null) {
            this.compileRunning = false;
            this.model.setState(ErrorTableModel.EvaluationState.FAILURE);
            return false;
         } else {
            this.transformer.removeAllTasks();
            this.model.clear();
            this.pendingRun = null;
            this.listenFile = editorPane.getFile();
            String cmdstr = String.format("script \"%s\" -verify", this.listenFile.getAbsolutePath());
            this.updateToolbar();

            try {
               this.core.startCommand(cmdstr, (taskId, var21) -> {
                  ScriptEditor.this.lastRun = ScriptEditor.this.core.getTaskById(taskId);
                  ScriptEditor.this.transformer.addTask(ScriptEditor.this.core.getTaskById(taskId));
               }, null, this.core.getHostById("127.0.0.1"));
               this.splitPane.setDividerLocation(0.7D);
               return true;
            } catch (DispatcherException e) {
               this.core.logEvent(Level.SEVERE, e.getMessage(), e);
               this.compileRunning = false;
               return false;
            }
         }
      }
   }

   void maybeRun() {
      String var1 = this.pendingRun;
      this.pendingRun = null;
      if (var1 != null) {
         this.core.internalCommand((InternalCommandCallback)null, new String[]{"terminal-focus", var1});
      }

      this.updateToolbar();
   }

   private boolean runHelper(ScriptEditor.EditorPane var1) {
      if (this.compileHelper(var1)) {
         this.pendingRun = String.format("script \"%s\"", var1.getFile().getAbsolutePath());
         return true;
      } else {
         return false;
      }
   }

   public boolean compile() {
      return this.compileHelper(this.currentPane);
   }

   public boolean copy() {
      if (this.currentPane != null) {
         this.currentPane.copy();
      }

      return true;
   }

   public synchronized boolean cut() {
      if (this.currentPane != null) {
         this.currentPane.cut();
      }

      return true;
   }

   void focusOn(ErrorEntry var1) {
      if (var1.getFile() != null && var1.getLine() != null) {
         synchronized(this) {
            Iterator var3 = this.fileList.iterator();

            ScriptEditor.EditorPane var4;
            do {
               if (!var3.hasNext()) {
                  this.loadFile(var1.getFile(), var1.getLine().intValue());
                  return;
               }

               var4 = (ScriptEditor.EditorPane)var3.next();
            } while(!var1.getFile().equals(var4.getFile()));

            this.setLine(var4, var1.getLine().intValue());
            this.setSelectedPane(var4);
         }
      }
   }

   synchronized JFileChooser getChooser() {
      if (this.chooser == null) {
         this.chooser = new JFileChooser();
         this.chooser.setAcceptAllFileFilterUsed(true);
         this.chooser.setDragEnabled(true);
         this.chooser.setFileFilter(new FileFilter() {
            public boolean accept(File var1) {
               if (var1.isDirectory()) {
                  return true;
               } else {
                  String var2 = var1.getAbsolutePath();
                  return var2.toLowerCase().matches("^.*\\.ds[si]$");
               }
            }

            public String getDescription() {
               return "DanderSpritz Scripts";
            }
         });
         this.chooser.setCurrentDirectory(new File(String.format("%s/%s/Scripts", this.core.getResourceDirectory(), this.core.getDefaultPackage())));
      }

      return this.chooser;
   }

   synchronized void setSelectedPane(ScriptEditor.EditorPane var1) {
      this.tabbedFiles.setSelectedComponent(var1.getTextArea());
      this.currentPane = var1;
      this.updateToolbar();
   }

   boolean loadInto(File var1, ScriptEditor.EditorPane var2) {
      StringBuilder var3 = new StringBuilder();

      try {
         FileReader var4 = new FileReader(var1);
         char[] var5 = new char[1028];
         int var6 = 0;

         while(true) {
            if (var6 == -1) {
               var4.close();
               break;
            }

            var6 = var4.read(var5);
            if (var6 > 0) {
               var3.append(var5, 0, var6);
            }
         }
      } catch (FileNotFoundException var7) {
         this.core.logEvent(Level.WARNING, "Unable to find file", var7);
         return false;
      } catch (IOException var8) {
         this.core.logEvent(Level.WARNING, "Unable to read file", var8);
         return false;
      }

      if (var1.getName().toLowerCase().endsWith(".dsi") || var1.getName().toLowerCase().endsWith(".dss")) {
         var2.getTextArea().setTokenMarker(new DssTokenMarker());
      }

      var2.getTextArea().setText(var3.toString().replaceAll("\r\n", "\n"));
      var2.setModified(false);
      var2.getTextArea().requestFocusInWindow();
      this.setLine(var2, 0);
      return true;
   }

   ScriptEditor.EditorPane loadFile(File var1, int var2) {
      if (var1 == null) {
         return null;
      } else if (!var1.exists()) {
         return null;
      } else {
         ScriptEditor.EditorPane var3;
         synchronized(this) {
            Iterator var5 = this.fileList.iterator();

            while(var5.hasNext()) {
               ScriptEditor.EditorPane var6 = (ScriptEditor.EditorPane)var5.next();
               if (var6.getFile() != null && var6.getFile().equals(var1)) {
                  this.setSelectedPane(var6);
                  return var6;
               }
            }

            var3 = this.makeNewEditorPane(var1);
            if (var3 == null) {
               return null;
            }
         }

         this.loadInto(var1, var3);
         Iterator var4 = this.mrulist.iterator();

         while(var4.hasNext()) {
            FileEntry var9 = (FileEntry)var4.next();
            if (var3.getFile().equals(new File(var9.getName()))) {
               this.mrulist.remove(var9);
               break;
            }
         }

         this.setSelectedPane(var3);
         this.setLine(var3, var2);
         EventQueue.invokeLater(new FocusOnTextArea(var3.getTextArea()));
         return var3;
      }
   }

   protected void loadKeywords() {
      try {
         JAXBContext var1 = JaxbCache.getContext(ddb.dsz.plugin.scripteditor.jaxb.keywords.ObjectFactory.class);
         Unmarshaller var2 = var1.createUnmarshaller();
         Object var3 = var2.unmarshal(ScriptEditor.class.getResource(String.format("/ScriptEditor/dssKeywords.xml")));
         Keywords var4 = null;
         if (var3 instanceof JAXBElement) {
            JAXBElement var5 = (JAXBElement)JAXBElement.class.cast(var3);
            var4 = (Keywords)Keywords.class.cast(var5.getValue());
         } else {
            var4 = (Keywords)Keywords.class.cast(var3);
         }

         if (var4 == null) {
            return;
         }

         Iterator var8 = var4.getKeyword().iterator();

         while(var8.hasNext()) {
            Keyword var6 = (Keyword)var8.next();
            DssTokenMarker.getKeywords().add(var6.getValue(), Byte.decode(var6.getType().toString()));
         }
      } catch (JAXBException var7) {
         this.core.logEvent(Level.INFO, var7.getMessage(), var7);
      }

   }

   protected void loadStyle() {
      this.textAreaDefaults = TextAreaDefaults.getDefaults();
      this.setupActionMap();

      try {
         JAXBContext var1 = JaxbCache.getContext(ddb.dsz.plugin.scripteditor.jaxb.styles.ObjectFactory.class);
         Unmarshaller var2 = var1.createUnmarshaller();
         Object var3 = var2.unmarshal(ScriptEditor.class.getResource(String.format("/ScriptEditor/styles.xml")));
         Styles var4 = null;
         if (var3 instanceof JAXBElement) {
            JAXBElement var5 = (JAXBElement)JAXBElement.class.cast(var3);
            var4 = (Styles)Styles.class.cast(var5.getValue());
         } else {
            var4 = (Styles)Styles.class.cast(var3);
         }

         if (var4 != null) {
            Iterator var10 = var4.getStyle().iterator();

            while(true) {
               while(var10.hasNext()) {
                  Style var6 = (Style)var10.next();
                  int var7 = var6.getId().intValue();
                  if (var7 > 0 && var7 < this.textAreaDefaults.styles.length) {
                     Color var8 = Color.decode(Integer.decode(var6.getForeground()).toString());
                     if (var8 == null) {
                        System.err.println("Invalid style color");
                     } else {
                        this.textAreaDefaults.styles[var7] = new SyntaxStyle(var8, var6.isItalics(), var6.isBold());
                     }
                  } else {
                     System.err.println(String.format("Invalid style id %d ", var7));
                  }
               }

               return;
            }
         }
      } catch (JAXBException var9) {
         this.core.logEvent(Level.INFO, var9.getMessage(), var9);
      }
   }

   public boolean newFile() {
      ScriptEditor.EditorPane var1 = this.makeNewEditorPane((File)null);
      if (var1 != null) {
         this.setSelectedPane(var1);
         EventQueue.invokeLater(new FocusOnTextArea(var1.getTextArea()));
         return true;
      } else {
         return false;
      }
   }

   public boolean open() {
      JFileChooser var1 = this.getChooser();
      File var2;
      synchronized(var1) {
         if (var1.showOpenDialog(this.parentDisplay) != 0) {
            return false;
         }

         var2 = var1.getSelectedFile();
      }

      boolean var3 = true;
      if (var2 != null && this.loadFile(var2, 0) == null) {
         var3 = false;
      }

      return var3;
   }

   public synchronized boolean paste() {
      if (this.currentPane != null) {
         this.currentPane.paste();
      }

      return true;
   }

   public synchronized boolean redo() {
      if (this.currentPane != null) {
         this.currentPane.redo();
      }

      return true;
   }

   private boolean reloadHelper(ScriptEditor.EditorPane var1) {
      if (var1 == null) {
         return false;
      } else if (var1.getFile() == null) {
         return false;
      } else {
         if (JOptionPane.showConfirmDialog(this.parentDisplay, "Are you sure you want to reload?") == 0) {
            this.loadInto(var1.getFile(), var1);
         }

         return true;
      }
   }

   public synchronized boolean reload() {
      return this.reloadHelper(this.currentPane);
   }

   public synchronized boolean save() {
      return this.saveHelper(this.currentPane);
   }

   private boolean saveHelper(ScriptEditor.EditorPane var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.getFile() != null ? this.saveTo(var1, var1.getFile()) : this.saveAsHelper(var1);
      }
   }

   public synchronized boolean saveAll() {
      Iterator var1 = this.fileList.iterator();

      ScriptEditor.EditorPane var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ScriptEditor.EditorPane)var1.next();
      } while(this.saveHelper(var2));

      return false;
   }

   public boolean saveAs() {
      return this.saveAsHelper(this.currentPane);
   }

   private boolean saveAsHelper(ScriptEditor.EditorPane var1) {
      if (var1 == null) {
         return false;
      } else {
         JFileChooser var2 = this.getChooser();
         if (var1.getFile() != null) {
            var2.setSelectedFile(var1.getFile());
         }

         return 0 == var2.showSaveDialog(this.parentDisplay) ? this.saveTo(var1, var2.getSelectedFile()) : false;
      }
   }

   private boolean saveTo(ScriptEditor.EditorPane var1, File var2) {
      try {
         PrintStream var3 = new PrintStream(var2);

         for(int var4 = 0; var4 < var1.getTextArea().getLineCount(); ++var4) {
            var3.println(var1.getTextArea().getLineText(var4).replaceAll("[\r\n]", ""));
         }

         var3.close();
         var1.setFile(var2);
         var1.setModified(false);
         return true;
      } catch (FileNotFoundException var5) {
         JOptionPane.showMessageDialog(this.parentDisplay, String.format("Cannot create the %s file.\r\nMake sure that the path and filename are correct.", var2.getName()), "Error saving file", 0);
         return this.saveAsHelper(var1);
      } catch (IOException var6) {
         this.core.logEvent(Level.SEVERE, "Unable to save file", var6);
         return false;
      }
   }

   private void setLine(ScriptEditor.EditorPane var1, int var2) {
      if (var1 != null) {
         int var3;
         for(var3 = 0; var2 > 1; --var2) {
            int var4 = var1.getTextArea().getText().indexOf("\n", var3);
            if (var4 == -1) {
               break;
            }

            var3 = var4 + 1;
         }

         if (var3 < var1.getTextArea().getText().length()) {
            var1.getTextArea().setCaretPosition(var3);
         } else {
            var1.getTextArea().setCaretPosition(var1.getTextArea().getText().length());
         }

      }
   }

   public synchronized boolean undo() {
      if (this.currentPane != null) {
         this.currentPane.undo();
      }

      return true;
   }

   public synchronized void editorModified(ScriptEditor.EditorPane var1) {
      if (var1 != null) {
         int var2 = this.tabbedFiles.indexOfComponent(var1.getTextArea());
         if (var2 == -1) {
            System.err.println("Editor not found");
         } else {
            this.tabbedFiles.setTitleAt(var2, var1.getTitle());
            this.updateToolbar();
         }
      }
   }

   protected boolean performCommand(Commands var1) {
      switch(var1) {
      case Close:
         return this.closeFileHelper(this.currentPane, true);
      case CloseAll:
         return this.closeAll(true);
      case Compile:
         return this.compileHelper(this.currentPane);
      case Copy:
         return this.copy();
      case Cut:
         return this.cut();
      case New:
         return this.newFile();
      case Open:
         return this.open();
      case Paste:
         return this.paste();
      case Redo:
         return this.redo();
      case Reload:
         return this.reload();
      case Save:
         return this.save();
      case SaveAll:
         return this.saveAll();
      case SaveAs:
         return this.saveAs();
      case Undo:
         return this.undo();
      case PreviousTab:
         return this.tabAdjust(-1);
      case NextTab:
         return this.tabAdjust(1);
      default:
         return false;
      }
   }

   private boolean tabAdjust(int var1) {
      synchronized(this) {
         if (this.tabbedFiles.getTabCount() == 0) {
            return false;
         } else {
            int var3 = this.tabbedFiles.getSelectedIndex();
            int var4 = this.tabbedFiles.getTabCount();
            var3 += var1;
            if (var3 >= var4) {
               var3 -= var4;
            }

            if (var3 < 0) {
               var3 += var4;
            }

            this.tabbedFiles.setSelectedIndex(var3);
            return true;
         }
      }
   }

   protected synchronized boolean validCommand(Commands var1) {
      switch(var1) {
      case Close:
      case Copy:
      case Cut:
      case Paste:
      case Save:
      case SaveAs:
      case PreviousTab:
      case NextTab:
         return this.currentPane != null;
      case CloseAll:
      case SaveAll:
         return this.fileList.size() > 0;
      case Compile:
         if (this.currentPane == null) {
            return false;
         } else if (this.compileRunning) {
            return false;
         } else if (this.currentPane.getFile() == null) {
            return true;
         } else if (this.currentPane.getFile().getName().toLowerCase().endsWith(".dss")) {
            return true;
         } else {
            if (this.currentPane.getFile().getName().toLowerCase().endsWith(".dsi")) {
               return true;
            }

            return false;
         }
      case New:
      case Open:
         return true;
      case Redo:
         if (this.currentPane == null) {
            return false;
         }

         return this.currentPane.isRedoAvailable();
      case Reload:
         if (this.currentPane == null) {
            return false;
         }

         return this.currentPane.isModified() && this.currentPane.getFile() != null;
      case Undo:
         if (this.currentPane == null) {
            return false;
         }

         return this.currentPane.isUndoAvailable();
      case RecentFiles:
         return !this.mrulist.isEmpty();
      default:
         return false;
      }
   }

   protected void updateToolbar() {
      Component[] var1 = this.toolBar.getComponents();
      if (var1 != null) {
         Component[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            Commands var6 = (Commands)this.buttonToCommand.get(var5);
            if (var6 != null) {
               var5.setEnabled(this.validCommand(var6));
            }
         }

      }
   }

   private final class EditorPane implements DocumentListener, UndoableEditListener {
      private static final int MAX_UNDO_SIZE = 200;
      JEditTextArea textArea;
      SyntaxDocument document;
      boolean modified;
      File file;
      int id;
      LinkedList<UndoableEdit> undos;
      LinkedList<UndoableEdit> redos;
      boolean undoEventHandle = false;

      public File getFile() {
         return this.file;
      }

      public void setFile(File var1) {
         this.file = var1;
      }

      public JEditTextArea getTextArea() {
         return this.textArea;
      }

      EditorPane(JEditTextArea var2, File var3, int var4) {
         this.setTextArea(var2);
         this.file = var3;
         this.undos = new LinkedList();
         this.redos = new LinkedList();
         this.modified = false;
         this.id = var4;
      }

      public synchronized void setTextArea(JEditTextArea var1) {
         if (this.document != null) {
            this.document.removeDocumentListener(this);
            this.document.removeUndoableEditListener(this);
         }

         this.textArea = var1;
         this.document = new DssDocument();
         this.textArea.setDocument(this.document);
         this.document.getDocumentProperties().put("tabSize", 4);
         this.document.addDocumentListener(this);
         this.document.addUndoableEditListener(this);
      }

      void setModified(boolean var1) {
         synchronized(this) {
            this.modified = var1;
            if (!var1) {
               this.redos.clear();
               this.undos.clear();
            }
         }

         ScriptEditor.this.editorModified(this);
      }

      boolean isModified() {
         return this.modified;
      }

      String getTitle() {
         return String.format("%s%s", this.getFileName(), this.modified ? "*" : "");
      }

      String getFileName() {
         return this.file == null ? String.format("NewFile%d", this.id) : this.file.getName();
      }

      public String toString() {
         return this.getTitle();
      }

      public void changedUpdate(DocumentEvent var1) {
         this.handleEvent(var1);
      }

      public void insertUpdate(DocumentEvent var1) {
         this.handleEvent(var1);
      }

      public void removeUpdate(DocumentEvent var1) {
         this.handleEvent(var1);
      }

      public synchronized void undoableEditHappened(UndoableEditEvent var1) {
         UndoableEdit var2 = var1.getEdit();
         if (var2.canUndo()) {
            this.undos.addLast(var1.getEdit());

            while(this.undos.size() > 200) {
               this.undos.removeFirst();
            }
         } else {
            this.undos.clear();
         }

         ScriptEditor.this.updateToolbar();
      }

      private void handleEvent(DocumentEvent var1) {
         if (var1.getDocument().equals(this.document)) {
            this.setModified(true);
            if (!this.undoEventHandle) {
               this.redos.clear();
            }
         }

      }

      public void cut() {
         this.textArea.cut();
      }

      public void copy() {
         this.textArea.copy();
      }

      public void paste() {
         this.textArea.paste();
      }

      public void undo() {
         synchronized(this) {
            label76: {
               this.undoEventHandle = true;

               try {
                  if (!this.undos.isEmpty()) {
                     UndoableEdit var2 = (UndoableEdit)this.undos.removeLast();
                     if (var2.canUndo()) {
                        var2.undo();
                        this.redos.addLast(var2);
                     }
                     break label76;
                  }
               } finally {
                  this.undoEventHandle = false;
               }

               return;
            }
         }

         ScriptEditor.this.updateToolbar();
      }

      public void redo() {
         synchronized(this) {
            label76: {
               this.undoEventHandle = true;

               try {
                  if (!this.redos.isEmpty()) {
                     UndoableEdit var2 = (UndoableEdit)this.redos.removeLast();
                     if (var2.canRedo()) {
                        var2.redo();
                        this.undos.addLast(var2);
                     }
                     break label76;
                  }
               } finally {
                  this.undoEventHandle = false;
               }

               return;
            }
         }

         ScriptEditor.this.updateToolbar();
      }

      public synchronized boolean isRedoAvailable() {
         return this.redos.size() > 0;
      }

      public synchronized boolean isUndoAvailable() {
         return this.undos.size() > 0;
      }
   }
}
