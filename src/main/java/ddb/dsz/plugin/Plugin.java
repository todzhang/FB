package ddb.dsz.plugin;

import ddb.detach.Tabbable;
import ddb.detach.TabbableOption;
import ddb.dsz.core.command.CommandEventListener;
import ddb.dsz.core.connection.ConnectionChangeListener;
import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import java.util.List;
import javax.swing.JComponent;

public interface Plugin extends Tabbable, CommandEventListener, ConnectionChangeListener, InternalCommandHandler {
   int init(CoreController core, JComponent parentDisplay, List<String> args);

   void fini();

   void setUserClosable(boolean userClosable);

   void setCanClose(boolean canClose);

   @Override
   boolean isClosable();

   @Override
   boolean isUserClosable();

   void setShortDescription(String shortDescription);

   void setLogo(String logo);

   void setHideable(boolean hideable);

   void setUnhideable(boolean unhideable);

   void setDetachable(boolean detachable);

   void setVerifyClose(boolean verifyClose);

   void receivedFocus();

   TabbableOption getRegularOptions();

   TabbableOption getStaticOptions();

   String getClazz();

   void setIdentifier(String identifier);

   String getIdentifier();

   void setShowStatus(boolean showStatus);

   boolean isShowStatus();

   HostInfo getTarget();

   void setTarget(HostInfo hostInfo);

   boolean canSetTarget();
}
