package ddb.detach;

import ddb.imagemanager.ImageManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

public abstract class AbstractTabbable implements Tabbable {
   protected static final PropertyChangeListener updateTabRenderer = propertyChangeEvent -> {
      if (propertyChangeEvent.getSource() instanceof AbstractTabbable) {
         (AbstractTabbable.class.cast(propertyChangeEvent.getSource())).setDisplayedName();
      }

   };
   protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
   protected Color nameColor;
   private JComponent subDisplay;
   private JPanel mainDisplay;
   protected EventListenerList allListeners;
   protected Tabbable.TabState state;
   protected Tabbable.TabState previous;
   protected Alignment alignment;
   protected Workbench workbench;
   private JPanel _titleBar;
   private JLabel _title;
   private boolean verifyClose;
   private boolean showButtons;
   private boolean selected;
   protected final MutableTabbableStatus status;
   protected TabbableFrame frame;
   protected Dimension frameSize;
   protected Point frameSite;
   private String name;
   private String description;
   private String logo;
   private boolean hideable;
   private boolean unhideable;
   private boolean detachable;
   protected JPanel tabRenderDisplay;
   private JLabel tabNameDisplay;
   private JLabel tabIconDisplay;
   private Font baseFont;
   int colorCycle;
   protected final Runnable FireContentsChanged;
   protected final Runnable FireContentsChangedRequestFocus;

   @Override
   public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.changeSupport.addPropertyChangeListener(propertyChangeListener);
   }

   @Override
   public void addPropertyChangeListener(String var1, PropertyChangeListener propertyChangeListener) {
      this.changeSupport.addPropertyChangeListener(var1, propertyChangeListener);
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.changeSupport.removePropertyChangeListener(propertyChangeListener);
   }

   @Override
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener propertyChangeListener) {
      this.changeSupport.removePropertyChangeListener(propertyName, propertyChangeListener);
   }

   protected abstract JComponent getTabbableSpecificRenderComponent();

   @Override
   public JComponent getTabComponent() {
      return this.tabRenderDisplay;
   }

   public void fini() {
      this.status.fini();
      if (this.frame != null) {
         this.frame.dispose();
         this.frame = null;
      }

   }

   @Override
   public boolean isDetachable() {
      return this.detachable;
   }

   @Override
   public boolean isHideable() {
      return this.hideable;
   }

   @Override
   public boolean isUnhideable() {
      return this.unhideable;
   }

   @Override
   public void setFrame(TabbableFrame tabbableFrame) {
      TabbableFrame var2 = this.frame;
      this.frame = tabbableFrame;
      this.changeSupport.firePropertyChange("TABBABLE_FRAME", var2, tabbableFrame);
   }

   @Override
   public TabbableFrame getFrame() {
      return this.frame;
   }

   @Override
   public synchronized Tabbable.TabState getCurrentState() {
      return this.state;
   }

   @Override
   public synchronized Tabbable.TabState getPreviousState() {
      return this.previous;
   }

   @Override
   public String getShortDescription() {
      return this.description;
   }

   protected void setShortDescription(String shortDescription) {
      String var2 = this.description;
      this.description = shortDescription;
      this.changeSupport.firePropertyChange("TABBABLE_SHORT_DESCRIPTION", var2, shortDescription);
   }

   @Override
   public void setLogo(String logo, Dimension dimension) {
      String var3 = this.logo;
      this.logo = logo;
      this.tabIconDisplay.setIcon(ImageManager.getIcon(logo, dimension));
      this.changeSupport.firePropertyChange("TABBABLE_LOGO", var3, logo);
   }

   @Override
   public void setLogo(Icon icon) {
      this.tabIconDisplay.setIcon(icon);
   }

   protected void setHideable(boolean hideable) {
      boolean var2 = this.hideable;
      this.hideable = hideable;
      this.changeSupport.firePropertyChange("TABBABLE_HIDEABLE", var2, hideable);
   }

   protected void setUnhideable(boolean unhideable) {
      boolean var2 = this.unhideable;
      this.unhideable = unhideable;
      this.changeSupport.firePropertyChange("TABBABLE_UNHIDEABLE", var2, unhideable);
   }

   protected void setDetachable(boolean detachable) {
      boolean var2 = this.detachable;
      this.detachable = detachable;
      this.changeSupport.firePropertyChange("TABBABLE_DETACHABLE", var2, detachable);
   }

   @Override
   public String getLogo() {
      return this.logo;
   }

   @Override
   public JComponent getHeader() {
      return null;
   }

   protected void setShowButtons(boolean showButtons) {
      boolean var2 = this.showButtons;
      this.showButtons = showButtons;
      this.changeSupport.firePropertyChange("TABBABLE_SHOW_BUTTONS", var2, showButtons);
   }

   @Override
   public boolean ShowButtons() {
      return this.showButtons;
   }

   public void setVerifyClose(boolean verifyClose) {
      boolean var2 = this.verifyClose;
      this.verifyClose = verifyClose;
      this.changeSupport.firePropertyChange("TABBABLE_VERIFY_CLOSE", var2, verifyClose);
   }

   @Override
   public boolean isVerifyClose() {
      return this.verifyClose;
   }

   protected void registerDisplay(JComponent subDisplay) {
      JComponent var2 = this.subDisplay;
      this.subDisplay = subDisplay;
      this.mainDisplay.add(this.subDisplay, "Center");
      this.changeSupport.firePropertyChange("TABBABLE_DISPLAY", var2, subDisplay);
   }

   @Override
   public JComponent getDisplay() {
      return this.mainDisplay;
   }

   protected void fireContentsChanged() {
      this.changeSupport.firePropertyChange("TABBABLE_CONTENT_CHANGED", false, true);
   }

   protected void fireContentsChangedRequestFocus() {
      this.changeSupport.firePropertyChange("TABBABLE_CONTENT_CHANGED_REQUEST_FOCUS", false, true);
   }

   @Override
   public synchronized boolean isDetached() {
      return this.state == Tabbable.TabState.DETACHED;
   }

   @Override
   public synchronized boolean isHidden() {
      return this.state == Tabbable.TabState.HIDDEN;
   }

   @Override
   public synchronized boolean isTabbed() {
      return this.state == Tabbable.TabState.TABBED;
   }

   @Override
   public synchronized boolean wasDetached() {
      return this.previous == Tabbable.TabState.DETACHED;
   }

   @Override
   public synchronized boolean wasHidden() {
      return this.previous == Tabbable.TabState.HIDDEN;
   }

   @Override
   public synchronized boolean wasTabbed() {
      return this.previous == Tabbable.TabState.TABBED;
   }

   @Override
   public boolean isClosable() {
      return true;
   }

   @Override
   public boolean isUserClosable() {
      return true;
   }

   @Override
   public Alignment getAlignment() {
      return this.alignment;
   }

   @Override
   public void setAlignment(Alignment alignment) {
      Alignment var2 = this.alignment;
      this.alignment = alignment;
      this.changeSupport.firePropertyChange("TABBABLE_ALIGNMENT", var2, alignment);
   }

   @Override
   public void setDetached() {
      this.setState(Tabbable.TabState.DETACHED);
   }

   @Override
   public void setHidden() {
      this.setState(Tabbable.TabState.HIDDEN);
   }

   @Override
   public void setTabbed() {
      this.setState(Tabbable.TabState.TABBED);
   }

   @Override
   public synchronized void setState(Tabbable.TabState tabState) {
      this.previous = this.state;
      this.state = tabState;
      if (tabState == Tabbable.TabState.DETACHED) {
         this.generateFloatingTitle();
      } else if (tabState == Tabbable.TabState.TABBED) {
         this.generateDockedTitle();
      }

      if (!Tabbable.TabState.TABBED.equals(tabState)) {
         this.setSelected(false);
      }

      this.changeSupport.firePropertyChange("TABBABLE_STATE", this.previous, tabState);
   }

   @Override
   public JMenuBar getMenuBar() {
      return null;
   }

   @Override
   public TabbableStatus getStatus() {
      return this.status;
   }

   protected void setStatus(String status) {
      this.status.setDetails(status);
      this.status.notifyObservers();
   }

   @Override
   public Dimension getPreferredSize() {
      return this.subDisplay.getPreferredSize();
   }

   public AbstractTabbable() {
      this.nameColor = Color.BLACK;
      this.allListeners = new EventListenerList();
      this.state = Tabbable.TabState.TABBED;
      this.previous = Tabbable.TabState.TABBED;
      this.alignment = Alignment.CENTER;
      this._titleBar = null;
      this.verifyClose = true;
      this.showButtons = true;
      this.selected = false;
      this.frame = null;
      this.frameSize = null;
      this.frameSite = null;
      this.tabRenderDisplay = new JPanel();
      this.tabNameDisplay = new JLabel();
      this.tabIconDisplay = new JLabel();
      this.baseFont = UIManager.getFont("Label.font");
      this.colorCycle = 0;
      this.FireContentsChanged = new Runnable() {
         @Override
         public void run() {
            AbstractTabbable.this.fireContentsChanged();
         }
      };
      this.FireContentsChangedRequestFocus = new Runnable() {
         @Override
         public void run() {
            AbstractTabbable.this.fireContentsChangedRequestFocus();
         }
      };
      this.status = new TabbableStatusImpl(this);
      JComponent var1 = this.getTabbableSpecificRenderComponent();
      GridBagLayout var2 = new GridBagLayout();
      GridBagConstraints var3 = new GridBagConstraints();
      this.tabRenderDisplay.setLayout(var2);
      byte var4 = 1;
      if (var1 != null) {
         var4 = 2;
      }

      var3.gridx = 0;
      var3.gridy = 0;
      var3.gridheight = var4;
      this.tabRenderDisplay.add(this.tabIconDisplay, var3);
      var2.addLayoutComponent(this.tabIconDisplay, var3);
      var3.gridx = 1;
      var3.gridheight = 1;
      this.tabRenderDisplay.add(this.tabNameDisplay, var3);
      var2.addLayoutComponent(this.tabNameDisplay, var3);
      if (var1 != null) {
         var3.gridy = 1;
         this.tabRenderDisplay.add(var1, var3);
         var2.addLayoutComponent(var1, var3);
      }

      this.tabRenderDisplay.setOpaque(false);
      this.mainDisplay = new JPanel();
      this.mainDisplay.setLayout(new BorderLayout());
      this._title = new JLabel();
      this._title.setFont(this._title.getFont().deriveFont(12).deriveFont(1));
      this.addPropertyChangeListener("TABBABLE_NAME", updateTabRenderer);
      this.addPropertyChangeListener("TABBABLE_NAME_COLOR_CHANGED", updateTabRenderer);
   }

   @Override
   public void setWorkbench(Workbench workbench) {
      Workbench var2 = this.workbench;
      this.workbench = workbench;
      this.changeSupport.firePropertyChange("TABBABLE_WORKBENCH", var2, workbench);
   }

   @Override
   public Workbench getWorkbench() {
      return this.workbench;
   }

   @Override
   public void generateDockedTitle() {
      if (this._titleBar != null) {
         this.mainDisplay.remove(this._titleBar);
      }

      if (!this.ShowButtons() && this.getMenuBar() == null) {
         this._titleBar = null;
      } else {
         this._titleBar = new JPanel(new BorderLayout());
         JMenuBar var1 = this.getMenuBar();
         if (var1 != null) {
            this._titleBar.add(var1, "West");
         }

         JComponent var2 = this.getHeader();
         if (var2 != null) {
            this._titleBar.add(var2, "Center");
         }

         if (this.ShowButtons()) {
            this._titleBar.add(this.generateButtonPanel(false), "East");
         }

         this.mainDisplay.add(this._titleBar, "North");
      }
   }

   @Override
   public void generateFloatingTitle() {
      if (this._titleBar != null) {
         this.mainDisplay.remove(this._titleBar);
      }

      this._titleBar = new JPanel();
      GridBagLayout var1 = new GridBagLayout();
      GridBagConstraints var2 = new GridBagConstraints();
      this._titleBar.setLayout(var1);
      var2.gridx = 0;
      var2.gridy = 0;
      var2.weightx = 10.0D;
      var2.anchor = 17;
      var2.gridx = 1;
      var2.weightx = 0.0D;
      var2.anchor = 13;
      JPanel var3 = this.generateButtonPanel(true);
      this._titleBar.add(var3);
      var1.addLayoutComponent(var3, var2);
      JMenuBar var4 = this.getMenuBar();
      JComponent var5 = this.getHeader();
      if (var4 != null && var5 != null) {
         var2.gridy = 0;
         var2.gridx = 0;
         var2.gridwidth = 1;
         var2.weightx = 1.0D;
         var2.anchor = 17;
         this._titleBar.add(var4);
         var1.addLayoutComponent(var4, var2);
         ++var2.gridy;
         var2.gridx = 0;
         var2.gridwidth = 2;
         var2.weightx = 1.0D;
         var2.fill = 1;
         var2.anchor = 10;
         this._titleBar.add(var5);
         var1.addLayoutComponent(var5, var2);
      } else if (var4 != null) {
         var2.gridy = 0;
         var2.gridx = 0;
         var2.gridwidth = 1;
         var2.weightx = 1.0D;
         var2.anchor = 17;
         this._titleBar.add(var4);
         var1.addLayoutComponent(var4, var2);
      } else if (var5 != null) {
         var2.gridy = 0;
         var2.gridx = 0;
         var2.gridwidth = 1;
         var2.weightx = 1.0D;
         var2.anchor = 17;
         this._titleBar.add(var5);
         var1.addLayoutComponent(var5, var2);
      } else {
         JPanel var6 = this._titleBar;
         this._titleBar = new JPanel(new BorderLayout());
         this._titleBar.add(var6, "East");
      }

      this.mainDisplay.add(this._titleBar, "North");
   }

   private JPanel generateButtonPanel(boolean var1) {
      JPanel var2 = new JPanel(new FlowLayout(2, 0, 0));
      if (!var1) {
         var2.add(this.generateButton("Minimize", "Minimize this plugin", "InternalFrame.iconifyIcon", this.isDetached(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               AbstractTabbable.this.frame.setExtendedState(1);
            }
         }));
      }

      var2.add(this.generateButton("Attachment", "Attach/Detatch this plugin", "InternalFrame.minimizeIcon", this.isDetachable() || this.isDetached(), new DetachTabbableDisplayAction(this, this.workbench)));
      if (!var1) {
         var2.add(this.generateButton("Maximize", "Maximize this plugin", "InternalFrame.maximizeIcon", this.isDetached(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               if (AbstractTabbable.this.frame != null) {
                  if ((AbstractTabbable.this.frame.getExtendedState() & 6) == 6) {
                     AbstractTabbable.this.frame.setExtendedState(0);
                  } else {
                     AbstractTabbable.this.frame.setExtendedState(6);
                  }
               }

            }
         }));
         var2.add(this.generateButton("Close", "Close this plugin", "InternalFrame.closeIcon", this.isClosable(), new CloseTabbableAction(this, this.workbench)));
      }

      return var2;
   }

   private JButton generateButton(String var1, String var2, String var3, boolean var4, ActionListener actionListener) {
      AbstractTabbable.NoFocusButton var6 = new AbstractTabbable.NoFocusButton(var1);
      var6.setIcon(UIManager.getIcon(var3));
      if (var2.length() > 0) {
         var6.setToolTipText(var2);
      }

      var6.addActionListener(actionListener);
      var6.setEnabled(var4);
      return var6;
   }

   @Override
   public void setName(String name) {
      String var2 = this.name;
      this.name = name;
      this.changeSupport.firePropertyChange("TABBABLE_NAME", var2, name);
      this.setDisplayedName();
   }

   public String getName() {
      return this.name;
   }

   protected void setDisplayedName() {
      EventQueue.invokeLater(new AbstractTabbable.SetDisplayedName(this.getDockedTitle()));
   }

   @Override
   public String getDockedTitle() {
      return this.name;
   }

   @Override
   public String getDetachedTitle() {
      return this.name;
   }

   @Override
   public void close() {
   }

   @Override
   public JComponent getDefaultElement() {
      return null;
   }

   @Override
   public void hideFrame() {
      if (this.frame != null) {
         TabbableFrame var1 = this.frame;
         this.frame = null;
         this.frameSize = var1.getSize();
         this.frameSite = var1.getLocation();
         var1.dispose();
      }
   }

   public int compareTo(Tabbable var1) {
      if (this == var1) {
         return 0;
      } else if (var1 == null) {
         return 1;
      } else {
         int var2 = this.alignment.compareTo(var1.getAlignment());
         return var2;
      }
   }

   @Override
   public void setSelected(boolean selected) {
      boolean var2 = this.selected;
      this.selected = selected;
      this.changeSupport.firePropertyChange("TABBABLE_SELECTED", var2, selected);
   }

   protected boolean isSelected() {
      return this.selected && this.workbench.isSelected();
   }

   @Override
   public Point getFrameLocation() {
      return this.frame != null ? this.frame.getLocation() : this.frameSite;
   }

   @Override
   public Dimension getFrameSize() {
      return this.frame != null ? this.frame.getSize() : this.frameSize;
   }

   public String toString() {
      return String.format("Tabbable (%s)", this.name);
   }

   @Override
   public void setDisplayColor(Color color) {
      Color var2 = this.nameColor;
      this.nameColor = color;
      this.changeSupport.firePropertyChange("TABBABLE_NAME_COLOR_CHANGED", var2, color);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            if (AbstractTabbable.this.baseFont != null) {
               if (AbstractTabbable.this.nameColor.equals(Color.BLACK)) {
                  AbstractTabbable.this.tabNameDisplay.setFont(AbstractTabbable.this.baseFont);
               } else {
                  AbstractTabbable.this.tabNameDisplay.setFont(AbstractTabbable.this.baseFont.deriveFont(1));
               }
            }

            AbstractTabbable.this.tabNameDisplay.setForeground(AbstractTabbable.this.nameColor);
         }
      });
   }

   public class SetDisplayedName implements Runnable {
      String text;

      public SetDisplayedName(String var2) {
         this.text = var2;
      }

      @Override
      public void run() {
         AbstractTabbable.this.tabNameDisplay.setText(this.text);
      }
   }

   private class NoFocusButton extends JButton {
      private String uiKey;

      public NoFocusButton(String uiKey) {
         this.setFocusPainted(false);
         this.setMargin(new Insets(0, 0, 0, 0));
         this.setOpaque(true);
         this.uiKey = uiKey;
      }

      @Override
      public boolean isFocusable() {
         return false;
      }

      @Override
      public void requestFocus() {
      }

      @Override
      public AccessibleContext getAccessibleContext() {
         AccessibleContext var1 = super.getAccessibleContext();
         if (this.uiKey != null) {
            var1.setAccessibleName(UIManager.getString(this.uiKey));
            this.uiKey = null;
         }

         return var1;
      }
   }
}
