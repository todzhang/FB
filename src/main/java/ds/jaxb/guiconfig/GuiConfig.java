package ds.jaxb.guiconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "GuiConfig",
   propOrder = {"windowSize", "frameSize", "tabAlignment"}
)
public class GuiConfig {
   @XmlElement(
      name = "WindowSize",
      required = true
   )
   protected DszDimension windowSize;
   @XmlElement(
      name = "FrameSize",
      required = true
   )
   protected DszDimension frameSize;
   @XmlElement(
      name = "TabAlignment",
      required = true
   )
   protected GuiConfig.TabAlignment tabAlignment;

   public DszDimension getWindowSize() {
      return this.windowSize;
   }

   public void setWindowSize(DszDimension var1) {
      this.windowSize = var1;
   }

   public DszDimension getFrameSize() {
      return this.frameSize;
   }

   public void setFrameSize(DszDimension var1) {
      this.frameSize = var1;
   }

   public GuiConfig.TabAlignment getTabAlignment() {
      return this.tabAlignment;
   }

   public void setTabAlignment(GuiConfig.TabAlignment var1) {
      this.tabAlignment = var1;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlType(
      name = "",
      propOrder = {"main", "sub"}
   )
   public static class TabAlignment {
      @XmlElement(
         name = "Main",
         required = true
      )
      protected DszTabAlignment main;
      @XmlElement(
         name = "Sub",
         required = true
      )
      protected DszTabAlignment sub;

      public DszTabAlignment getMain() {
         return this.main;
      }

      public void setMain(DszTabAlignment var1) {
         this.main = var1;
      }

      public DszTabAlignment getSub() {
         return this.sub;
      }

      public void setSub(DszTabAlignment var1) {
         this.sub = var1;
      }
   }
}
