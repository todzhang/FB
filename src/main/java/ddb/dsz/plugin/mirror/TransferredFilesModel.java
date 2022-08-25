package ddb.dsz.plugin.mirror;

import ddb.util.FrequentlyAppendedTableModel;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

public class TransferredFilesModel extends FrequentlyAppendedTableModel<TransferredFiles, FileInformation> {
   Map<String, FileInformation> pathToFileInfo = Collections.synchronizedMap(LazyMap.decorate(new Hashtable(), new Transformer() {
      public Object transform(Object var1) {
         FileInformation var2 = new FileInformation();
         var2.setName(var1.toString());
         var2.setSize(0L);
         var2.setSoFar(0L);
         return var2;
      }
   }));

   public TransferredFilesModel() {
      super(TransferredFiles.class);
   }

   public Object getValueAt(int i, TransferredFiles e) {
      FileInformation var3 = (FileInformation)super.getRecord(i);
      if (var3 == null) {
         return null;
      } else {
         switch(e) {
         case FILENAME:
            return var3.getName();
         case SIZE:
            return var3.getSize();
         case TODATE:
            return var3.getSoFar();
         default:
            return null;
         }
      }
   }

   @Override
   public Class<?> getColumnClass(TransferredFiles e) {
      return e.getClazz();
   }

   @Override
   public String getColumnName(TransferredFiles e) {
      return e.getCaption();
   }

   public FileInformation getFileInformation(String var1) {
      return (FileInformation)this.pathToFileInfo.get(var1);
   }
}
