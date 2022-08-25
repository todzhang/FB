package ddb.targetmodel.filemodel.listeners;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.task.Task;
import ddb.dsz.core.task.TaskDataAccess;
import ddb.dsz.core.task.TaskId;
import ddb.predicate.PredicateClosure;
import ddb.predicate.PredicateClosureImpl;
import ddb.targetmodel.filemodel.FileObject;
import ddb.targetmodel.filemodel.FileObjectFields;
import ddb.targetmodel.filemodel.FileSystemModel;
import ddb.targetmodel.filemodel.history.ActionType;
import ddb.targetmodel.filemodel.history.CommandType;
import ddb.util.GeneralUtilities;
import ddb.util.Guid;
import ddb.util.UtilityConstants;
import ddb.util.XmlCache;
import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.SAXParser;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DirCrawler implements Observer {
   private final CoreController core;
   private final FileSystemModel model;
   private static final int THREAD_COUNT = 10;
   private static final BlockingQueue<Runnable> QUEUE = new PriorityBlockingQueue();
   private static final Executor exec;
   private static final SAXException EarlyFinish;
   private static final Object TASKING_LOCK;
   private static final List<TaskId> handledTasks;
   private int pendingDirDataSections = 0;
   Map<Guid, DirCrawler.TaskingInfo> TaskingMap = new HashMap();

   public DirCrawler(CoreController var1, FileSystemModel var2) {
      this.core = var1;
      this.model = var2;
   }

   public void addTask(Task var1) {
      synchronized(handledTasks) {
         TaskId var3 = var1.getId();
         int var4 = Collections.binarySearch(handledTasks, var3);
         if (var4 >= 0) {
            return;
         }

         ++var4;
         var4 = -var4;
         handledTasks.add(var4, var3);
      }

      var1.subscribe(this, true);
   }

   private void dataChanged() {
      this.model.setPendingData(this.pendingDirDataSections);
   }

   public void update(Observable var1, Object var2) {
      synchronized(this) {
         ++this.pendingDirDataSections;
      }

      exec.execute(new DirCrawler.DelegateWrapper((TaskDataAccess)var2));
      this.dataChanged();
   }

   private DirCrawler.TaskingInfo getTaskingInformation(Task var1) {
      synchronized(TASKING_LOCK) {
         DirCrawler.TaskingInfo var3 = (DirCrawler.TaskingInfo)this.TaskingMap.get(var1.getTaskId());
         if (var3 != null) {
            return var3;
         } else {
            DirCrawler.TaskingHandler var4 = new DirCrawler.TaskingHandler();
            Reader var5 = var1.getTaskingAccess().getReader();

            DirCrawler.TaskingInfo var7;
            try {
               try {
                  SAXParser var6 = XmlCache.getSAXParser();
                  if (var6 != null) {
                     var6.parse(new InputSource(var5), var4);
                     return null;
                  }

                  var7 = null;
                  return var7;
               } catch (Exception var21) {
                  if (var21 != EarlyFinish) {
                     var21.printStackTrace();
                     return null;
                  }
               }

               var7 = var4.tasking;
            } finally {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (IOException var20) {
                  }
               }

            }

            return var7;
         }
      }
   }

   private void handleTaskDataAccess(TaskDataAccess var1) {
      if (var1 != null) {
         switch(var1.getType()) {
         case DATA:
            Task var2 = var1.getTask();
            DirCrawler.DataHandler var3 = new DirCrawler.DataHandler(this.getTaskingInformation(var2), var2);
            Reader var4 = var1.getReader();
            SAXParser var5 = XmlCache.getSAXParser();

            try {
               if (var5 == null || var4 == null) {
                  return;
               }

               var5.parse(new InputSource(var4), var3);
               return;
            } catch (Exception var18) {
               if (var18 != EarlyFinish) {
                  var18.printStackTrace();
                  return;
               }
            } finally {
               try {
                  if (var4 != null) {
                     var4.close();
                  }
               } catch (Exception var17) {
               }

               XmlCache.releaseParser(var5);
            }

            return;
         default:
         }
      }
   }

   static {
      exec = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.MINUTES, QUEUE, UtilityConstants.createThreadFactory("DirCrawler"));
      EarlyFinish = new SAXException();
      TASKING_LOCK = new Object();
      handledTasks = new Vector();
   }

   private class DataHandler extends DefaultHandler {
      final DirCrawler.TaskingInfo tasking;
      private final Task task;
      private StringBuilder sb = new StringBuilder();
      private FileObject currentDirectory = null;
      private FileObject currentFile = null;
      private Calendar timestamp;
      private final PredicateClosure[] StartElementClosure;
      private PredicateClosure[] EndElementClosure;
      private String currentHash = null;
      private final Predicate HasDirectory = new Predicate() {
         public boolean evaluate(Object var1) {
            return DataHandler.this.currentDirectory != null;
         }
      };
      private final Predicate HasFile = new Predicate() {
         public boolean evaluate(Object var1) {
            return DataHandler.this.currentFile != null;
         }
      };

      public DataHandler(DirCrawler.TaskingInfo var2, Task var3) {
         this.tasking = var2;
         this.task = var3;
         this.StartElementClosure = new PredicateClosure[]{new PredicateClosureImpl(PredicateUtils.equalPredicate("Directory"), new Closure() {
            public void execute(Object var1) {
               Attributes var2 = (Attributes)var1;
               String var3 = var2.getValue("path");
               DataHandler.this.timestamp = GeneralUtilities.stringToCalendar(var2.getValue("lptimestamp"), (Calendar)null);
               DataHandler.this.currentDirectory = DirCrawler.this.model.getNodeForPath(var3, true, CommandType.DIR, DataHandler.this.task.getId(), DataHandler.this.timestamp, true, (Closure)null);
               DirCrawler.this.model.addHistoryItem(DataHandler.this.currentDirectory, DataHandler.this.task.getId(), ActionType.INFO, CommandType.DIR, (Calendar)null);
               if (DataHandler.this.currentDirectory != null) {
                  DataHandler.this.currentDirectory.setDirectory();
                  if (DataHandler.this.tasking.fullList) {
                     DataHandler.this.currentDirectory.setDataElement((FileObjectFields)FileObjectFields.Dir_LastFull, DataHandler.this.timestamp);
                  } else {
                     DataHandler.this.currentDirectory.setDataElement((FileObjectFields)FileObjectFields.Dir_LastPartial, DataHandler.this.timestamp);
                  }
               }

            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasDirectory, PredicateUtils.equalPredicate("File")), new Closure() {
            public void execute(Object var1) {
               Attributes var2 = (Attributes)var1;
               String var3 = var2.getValue("name");
               String var4 = var2.getValue("altName");
               if (var3 == null) {
                  DataHandler.this.currentFile = null;
               } else if (var3.equals("..")) {
                  DataHandler.this.currentFile = null;
               } else if (var3.equals(".")) {
                  DataHandler.this.currentFile = DataHandler.this.currentDirectory;
               } else {
                  DataHandler.this.currentFile = DirCrawler.this.model.getChildNode(DataHandler.this.currentDirectory, var3, true, CommandType.DIR, DataHandler.this.task.getId(), DataHandler.this.timestamp, true, (Closure)null);
                  DirCrawler.this.model.addHistoryItem(DataHandler.this.currentFile, DataHandler.this.task.getId(), ActionType.INFO, CommandType.DIR, (Calendar)null);
                  if (DataHandler.this.currentFile != null) {
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_Name, var3);
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_Size, Long.parseLong(var2.getValue("size")));
                     if (var4 != null && var4.length() > 0) {
                        DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_AlternateName, var4);
                     }
                  }

               }
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(PredicateUtils.notPredicate(this.HasDirectory), PredicateUtils.equalPredicate("File")), new Closure() {
            public void execute(Object var1) {
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeArchive")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Archive, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeDirectory")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDirectory();
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeSymbolicLink")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_SymbolicLink, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeAFUnixFamilySocket")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_UnixFamilySocket, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeBlockSpecialFile")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_BlockSpecialFile, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeCharacterSpecialFile")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_CharacterSpecialFile, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeNamedPipeFile")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_NamedPipe, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeCompressed")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Compressed, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeEncrypted")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Encrypted, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeHidden")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Hidden, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeOffline")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Offline, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeReadonly")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_ReadOnly, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeSystem")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_System, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeTemporary")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Temporary, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeReparsePoint")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_ReparsePoint, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeSparseFile")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_SparseFile, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeVirtual")), new Closure() {
            public void execute(Object var1) {
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeNotIndexed")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_NotContentIndexed, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("FileAttributeDevice")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Attr_Device, Boolean.TRUE);
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("Hash")), new Closure() {
            public void execute(Object var1) {
               Attributes var2 = (Attributes)var1;
               DataHandler.this.currentHash = var2.getValue("type");
            }
         })};
         this.EndElementClosure = new PredicateClosure[]{new PredicateClosureImpl(PredicateUtils.equalPredicate("Directory"), new Closure() {
            public void execute(Object var1) {
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("File")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.save();
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("Modified")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_Modified, GeneralUtilities.stringToCalendar(var1.toString(), (Calendar)null));
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("Created")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_Created, GeneralUtilities.stringToCalendar(var1.toString(), (Calendar)null));
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("Accessed")), new Closure() {
            public void execute(Object var1) {
               DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.File_Accessed, GeneralUtilities.stringToCalendar(var1.toString(), (Calendar)null));
            }
         }), new PredicateClosureImpl(PredicateUtils.andPredicate(this.HasFile, PredicateUtils.equalPredicate("Hash")), new Closure() {
            public void execute(Object var1) {
               if (DataHandler.this.currentHash != null) {
                  if (DataHandler.this.currentHash.equalsIgnoreCase("SHA1")) {
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Hash_Sha1, var1.toString());
                  } else if (DataHandler.this.currentHash.equalsIgnoreCase("MD5")) {
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Hash_Md5, var1.toString());
                  } else if (DataHandler.this.currentHash.equalsIgnoreCase("SHA256")) {
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Hash_Sha256, var1.toString());
                  } else if (DataHandler.this.currentHash.equalsIgnoreCase("SHA512")) {
                     DataHandler.this.currentFile.setDataElement((FileObjectFields)FileObjectFields.Hash_Sha512, var1.toString());
                  }

               }
            }
         })};
      }

      public void startDocument() throws SAXException {
         super.startDocument();
      }

      public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
         super.startElement(var1, var2, var3, var4);
         this.sb.setLength(0);
         PredicateClosure[] var5 = this.StartElementClosure;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            PredicateClosure var8 = var5[var7];
            if (var8.evaluate(var3)) {
               var8.execute(var4);
               break;
            }
         }

      }

      public void endElement(String var1, String var2, String var3) throws SAXException {
         super.endElement(var1, var2, var3);
         if (!var3.equals("CommandData") && !var3.equals("TaskResult")) {
            PredicateClosure[] var4 = this.EndElementClosure;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               PredicateClosure var7 = var4[var6];
               if (var7.evaluate(var3)) {
                  var7.execute(this.sb.toString());
                  break;
               }
            }

            this.sb.setLength(0);
         } else {
            throw DirCrawler.EarlyFinish;
         }
      }

      public void characters(char[] var1, int var2, int var3) throws SAXException {
         super.characters(var1, var2, var3);
         this.sb.append(new String(var1, var2, var3));
      }

      private void save() {
         if (this.currentFile != null) {
            this.currentFile.save();
            this.currentFile = null;
         }

      }
   }

   private class TaskingHandler extends DefaultHandler {
      public final DirCrawler.TaskingInfo tasking;
      private String currentElement;
      private StringBuilder sb;

      private TaskingHandler() {
         this.tasking = DirCrawler.this.new TaskingInfo();
         this.currentElement = "";
         this.sb = new StringBuilder();
      }

      public void startDocument() throws SAXException {
         super.startDocument();
      }

      public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
         super.startElement(var1, var2, var3, var4);
         this.currentElement = var3;
         if (var3.equals("SearchAge")) {
            this.tasking.fullList = false;
            throw DirCrawler.EarlyFinish;
         } else if (var3.equals("SearchAfterDate")) {
            this.tasking.fullList = false;
            throw DirCrawler.EarlyFinish;
         } else if (var3.equals("SearchBeforeDate")) {
            this.tasking.fullList = false;
            throw DirCrawler.EarlyFinish;
         } else if (var3.equals("SearchMaxMatches")) {
            this.tasking.fullList = false;
            throw DirCrawler.EarlyFinish;
         }
      }

      public void endElement(String var1, String var2, String var3) throws SAXException {
         super.endElement(var1, var2, var3);
         if (this.currentElement.equals("SearchMask") && !this.sb.toString().equals("*")) {
            this.tasking.fullList = false;
         }

         this.sb.setLength(0);
         if (!this.tasking.fullList || var3.equals("TaskingInfo") || var3.equals("CommandTasking")) {
            throw DirCrawler.EarlyFinish;
         }
      }

      public void characters(char[] var1, int var2, int var3) throws SAXException {
         super.characters(var1, var2, var3);
         this.sb.append(new String(var1, var2, var3));
      }

      // $FF: synthetic method
      TaskingHandler(Object var2) {
         this();
      }
   }

   private class DelegateWrapper implements Runnable, Comparable<DirCrawler.DelegateWrapper> {
      TaskDataAccess tda;

      public DelegateWrapper(TaskDataAccess var2) {
         this.tda = var2;
      }

      public void run() {
         try {
            DirCrawler.this.handleTaskDataAccess(this.tda);
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         synchronized(DirCrawler.this) {
            DirCrawler.this.pendingDirDataSections--;
         }

         DirCrawler.this.dataChanged();
         this.tda = null;
      }

      public int compareTo(DirCrawler.DelegateWrapper var1) {
         if (this == var1) {
            return 0;
         } else if (var1 == null) {
            return -1;
         } else if (this.tda == null && var1.tda == null) {
            return 0;
         } else if (this.tda == null) {
            return -1;
         } else {
            return var1.tda == null ? 1 : this.tda.getOrdinal() - var1.tda.getOrdinal();
         }
      }
   }

   private class TaskingInfo {
      boolean fullList;

      private TaskingInfo() {
         this.fullList = true;
      }

      // $FF: synthetic method
      TaskingInfo(Object var2) {
         this();
      }
   }
}
