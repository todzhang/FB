package ddb.gui.debugview;

import ddb.util.FrequentlyAppendedTableModel;

public class MessageRecordDisplayModel extends FrequentlyAppendedTableModel<OutputMessageColumns, MessageRecord> {
   DebugView.MessageState state;

   public MessageRecordDisplayModel() {
      super(OutputMessageColumns.class);
      this.state = DebugView.MessageState.SHOW;
   }

   public Object getValueAt(int i, OutputMessageColumns e) {
      MessageRecord mr = (MessageRecord)super.getRecord(i);
      if (mr == null) {
         return null;
      } else {
         switch(e) {
         case MESSAGE:
            return mr.getMessage();
         case PRIORITY:
            return mr.getPriority();
         case SECTION:
            return mr.getSection();
         case THREAD:
            return mr.getThread();
         case TIME:
            return mr.getTime();
         default:
            return null;
         }
      }
   }

   @Override
   public String getColumnName(OutputMessageColumns e) {
      return e.getColumnName();
   }

   @Override
   public Class<?> getColumnClass(OutputMessageColumns e) {
      return e.getClazz();
   }
}
