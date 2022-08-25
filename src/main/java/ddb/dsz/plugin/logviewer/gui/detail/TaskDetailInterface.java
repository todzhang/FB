package ddb.dsz.plugin.logviewer.gui.detail;

import ddb.dsz.core.data.ObjectValue;

public interface TaskDetailInterface {
   void appendString(String var1);

   void registerVariable(ObjectValue var1);

   void clearVariables();

   void finished();
}
