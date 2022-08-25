package ddb.detach;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

public interface Tabbable extends Comparable<Tabbable> {
   String TABBABLE_FRAME = "TABBABLE_FRAME";
   String TABBABLE_SHORT_DESCRIPTION = "TABBABLE_SHORT_DESCRIPTION";
   String TABBABLE_LOGO = "TABBABLE_LOGO";
   String TABBABLE_HIDEABLE = "TABBABLE_HIDEABLE";
   String TABBABLE_UNHIDEABLE = "TABBABLE_UNHIDEABLE";
   String TABBABLE_DETACHABLE = "TABBABLE_DETACHABLE";
   String TABBABLE_DISPLAY = "TABBABLE_DISPLAY";
   String TABBABLE_SHOW_BUTTONS = "TABBABLE_SHOW_BUTTONS";
   String TABBABLE_VERIFY_CLOSE = "TABBABLE_VERIFY_CLOSE";
   String TABBABLE_SELECTED = "TABBABLE_SELECTED";
   String TABBABLE_NAME = "TABBABLE_NAME";
   String TABBABLE_WORKBENCH = "TABBABLE_WORKBENCH";
   String TABBABLE_STATE = "TABBABLE_STATE";
   String TABBABLE_ALIGNMENT = "TABBABLE_ALIGNMENT";
   String TABBABLE_CONTENT_CHANGED = "TABBABLE_CONTENT_CHANGED";
   String TABBABLE_CONTENT_CHANGED_REQUEST_FOCUS = "TABBABLE_CONTENT_CHANGED_REQUEST_FOCUS";
   String TABBABLE_NAME_COLOR_CHANGED = "TABBABLE_NAME_COLOR_CHANGED";

   JComponent getDisplay();

   String getLogo();

   String getShortDescription();

   boolean isDetached();

   boolean isHidden();

   boolean isTabbed();

   boolean wasDetached();

   boolean wasHidden();

   boolean wasTabbed();

   Tabbable.TabState getCurrentState();

   Tabbable.TabState getPreviousState();

   TabbableFrame getFrame();

   boolean isClosable();

   boolean isUserClosable();

   Alignment getAlignment();

   void setAlignment(Alignment alignment);

   void setDetached();

   void hideFrame();

   void setHidden();

   void setTabbed();

   void setState(Tabbable.TabState tabState);

   JMenuBar getMenuBar();

   TabbableStatus getStatus();

   Dimension getPreferredSize();

   boolean allowNewInstance(Class<?> clazz);

   String getName();

   void setLogo(String logo, Dimension dimension);

   void setLogo(Icon icon);

   void setName(String name);

   String getDockedTitle();

   JComponent getTabComponent();

   String getDetachedTitle();

   void generateDockedTitle();

   void generateFloatingTitle();

   void setFrame(TabbableFrame tabbableFrame);

   boolean ShowButtons();

   boolean isVerifyClose();

   JComponent getHeader();

   boolean isHideable();

   boolean isUnhideable();

   boolean isDetachable();

   void setWorkbench(Workbench workbench);

   Workbench getWorkbench();

   void close();

   JComponent getDefaultElement();

   void setSelected(boolean selected);

   Point getFrameLocation();

   Dimension getFrameSize();

   void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

   void addPropertyChangeListener(String propertyName, PropertyChangeListener propertyChangeListener);

   void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

   void removePropertyChangeListener(String propertyName, PropertyChangeListener propertyChangeListener);

   void setDisplayColor(Color color);

   public enum TabState {
      HIDDEN,
      TABBED,
      DETACHED,
      UNKNOWN;
   }
}
