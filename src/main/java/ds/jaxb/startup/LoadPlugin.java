package ds.jaxb.startup;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "LoadPlugin",
   propOrder = {"detached", "initArgs"}
)
public class LoadPlugin {
   @XmlElement(
      name = "Detached"
   )
   protected LoadPlugin.Detached detached;
   @XmlElement(
      name = "InitArgs"
   )
   protected List<String> initArgs;
   @XmlAttribute(
      name = "platform"
   )
   protected String platform;
   @XmlAttribute(
      name = "pluginClass"
   )
   protected String pluginClass;
   @XmlAttribute(
      name = "pluginId"
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger pluginId;
   @XmlAttribute(
      name = "pluginName"
   )
   protected String pluginName;
   @XmlAttribute(
      name = "visible"
   )
   protected Boolean visible;
   @XmlAttribute(
      name = "icon"
   )
   protected String icon;
   @XmlAttribute(
      name = "align"
   )
   protected String align;

   public LoadPlugin.Detached getDetached() {
      return this.detached;
   }

   public void setDetached(LoadPlugin.Detached var1) {
      this.detached = var1;
   }

   public List<String> getInitArgs() {
      if (this.initArgs == null) {
         this.initArgs = new ArrayList();
      }

      return this.initArgs;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(String var1) {
      this.platform = var1;
   }

   public String getPluginClass() {
      return this.pluginClass;
   }

   public void setPluginClass(String var1) {
      this.pluginClass = var1;
   }

   public BigInteger getPluginId() {
      return this.pluginId;
   }

   public void setPluginId(BigInteger var1) {
      this.pluginId = var1;
   }

   public String getPluginName() {
      return this.pluginName;
   }

   public void setPluginName(String var1) {
      this.pluginName = var1;
   }

   public boolean isVisible() {
      return this.visible == null ? true : this.visible;
   }

   public void setVisible(Boolean var1) {
      this.visible = var1;
   }

   public String getIcon() {
      return this.icon;
   }

   public void setIcon(String var1) {
      this.icon = var1;
   }

   public String getAlign() {
      return this.align == null ? "center" : this.align;
   }

   public void setAlign(String var1) {
      this.align = var1;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(
      name = "",
      propOrder = {"width", "height", "positionX", "positionY"}
   )
   public static class Detached {
      @XmlElement(
         name = "Width"
      )
      @XmlSchemaType(
         name = "positiveInteger"
      )
      protected BigInteger width;
      @XmlElement(
         name = "Height"
      )
      @XmlSchemaType(
         name = "positiveInteger"
      )
      protected BigInteger height;
      @XmlElement(
         name = "PositionX"
      )
      @XmlSchemaType(
         name = "positiveInteger"
      )
      protected BigInteger positionX;
      @XmlElement(
         name = "PositionY"
      )
      @XmlSchemaType(
         name = "positiveInteger"
      )
      protected BigInteger positionY;

      public BigInteger getWidth() {
         return this.width;
      }

      public void setWidth(BigInteger var1) {
         this.width = var1;
      }

      public BigInteger getHeight() {
         return this.height;
      }

      public void setHeight(BigInteger var1) {
         this.height = var1;
      }

      public BigInteger getPositionX() {
         return this.positionX;
      }

      public void setPositionX(BigInteger var1) {
         this.positionX = var1;
      }

      public BigInteger getPositionY() {
         return this.positionY;
      }

      public void setPositionY(BigInteger var1) {
         this.positionY = var1;
      }
   }
}
