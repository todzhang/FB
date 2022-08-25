package ddb.dsz.plugin.requesthandler.requests;

import ddb.dsz.core.internalcommand.InternalCommandCallback;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.peer.PeerTag;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class RequestedOperation {
   public static final BigInteger NO_ID = BigInteger.valueOf(-1L);
   protected BigInteger id;
   private String key;
   private Map<String, String> data;
   private PeerTag tag;
   private String source;
   private TaskId taskId;
   private InternalCommandCallback callback;
   private boolean local;

   public RequestedOperation() {
      this.id = NO_ID;
      this.data = new HashMap();
      this.taskId = TaskId.NULL;
      this.callback = null;
      this.local = false;
   }

   public final PeerTag getTag() {
      return this.tag;
   }

   public final void setTaskId(TaskId var1) {
      this.taskId = var1;
   }

   public final TaskId getTaskId() {
      return this.taskId;
   }

   public final void setTag(PeerTag var1) {
      this.tag = var1;
   }

   public final String getKey() {
      return this.key;
   }

   public final void setKey(String var1) {
      this.key = var1;
   }

   public final String getData(String var1) {
      return (String)this.data.get(var1);
   }

   public final Set<String> getDataKeys() {
      return this.data.keySet();
   }

   public final void setData(String var1, String var2) {
      this.data.put(var1, var2);
   }

   public final BigInteger getId() {
      return this.id;
   }

   public final void setId(BigInteger var1) {
      this.id = var1;
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.data.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.append(String.format("\t\t%s=%s\n", var3, this.data.get(var3)));
      }

      return String.format("RequestedOperation\n\tKey=%s\n\tMap=\n%s", this.key, var1.toString());
   }

   public final String getSource() {
      return this.source;
   }

   public final void setSource(String var1) {
      this.source = var1;
   }

   public void setCallback(InternalCommandCallback var1) {
      this.callback = var1;
   }

   public InternalCommandCallback getCallback() {
      return this.callback;
   }

   public boolean isLocal() {
      return this.local;
   }

   public void setLocal() {
      this.local = true;
   }
}
