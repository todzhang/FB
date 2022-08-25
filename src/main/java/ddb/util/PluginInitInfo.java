package ddb.util;

import java.awt.Dimension;
import java.awt.Point;
import java.math.BigInteger;
import java.util.List;

public class PluginInitInfo {
   private String className;
   private String instanceName;
   private BigInteger pluginId;
   private List<String> initArgs;
   private String align;
   private boolean visible;
   private boolean detached;
   private Dimension frameSize;
   private Point position;
   private String icon;
   private String identifier;

   public PluginInitInfo(String classNaame, BigInteger pluginId, String instanceName, List<String> initArgs, boolean visible, String align) {
      this.className = classNaame;
      this.pluginId = pluginId;
      this.instanceName = instanceName;
      this.initArgs = initArgs;
      this.visible = visible;
      this.align = align;
      this.detached = false;
      this.frameSize = null;
   }

   public String getClassName() {
      return this.className;
   }

   public BigInteger getPluginId() {
      return this.pluginId;
   }

   public List<String> getInitArgs() {
      return this.initArgs;
   }

   public String getInstanceName() {
      return this.instanceName;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public String getAlign() {
      return this.align;
   }

   public boolean isDetached() {
      return this.detached;
   }

   public void setDetached(boolean var1) {
      this.detached = var1;
   }

   public Dimension getFrameSize() {
      return this.frameSize;
   }

   public void setFrameSize(Dimension var1) {
      this.frameSize = var1;
   }

   public Point getFramePosition() {
      return this.position;
   }

   public void setFramePosition(Point var1) {
      this.position = var1;
   }

   public String getIcon() {
      return this.icon;
   }

   public void setIcon(String var1) {
      this.icon = var1;
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public void setIdentifier(String identifier) {
      this.identifier = identifier;
   }
}
