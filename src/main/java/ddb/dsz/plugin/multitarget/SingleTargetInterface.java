package ddb.dsz.plugin.multitarget;

import ddb.detach.Tabbable;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.host.HostInfo;
import javax.swing.JComponent;

public interface SingleTargetInterface extends Tabbable, CommandEventListener {
   void setDisplay(JComponent var1);

   HostInfo getTarget();

   int compareTo(SingleTargetInterface var1);

   void fini();
}
