package ds.jaxb.module;

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
   name = "Module",
   propOrder = {"name", "clazz", "description", "logo", "live", "replay", "userStartable", "hide", "unhide", "detach", "userClose", "canClose", "verifyClose", "initArgs"}
)
public class Module {
   @XmlElement(
      name = "Name",
      required = true
   )
   protected String name;
   @XmlElement(
      name = "Class",
      required = true
   )
   protected String clazz;
   @XmlElement(
      name = "Description",
      required = true
   )
   protected String description;
   @XmlElement(
      name = "Logo",
      required = true
   )
   protected String logo;
   @XmlElement(
      name = "Live"
   )
   protected boolean live;
   @XmlElement(
      name = "Replay"
   )
   protected boolean replay;
   @XmlElement(
      name = "UserStartable"
   )
   protected boolean userStartable;
   @XmlElement(
      name = "Hide"
   )
   protected boolean hide;
   @XmlElement(
      name = "Unhide"
   )
   protected boolean unhide;
   @XmlElement(
      name = "Detach"
   )
   protected boolean detach;
   @XmlElement(
      name = "UserClose"
   )
   protected boolean userClose;
   @XmlElement(
      name = "CanClose"
   )
   protected boolean canClose;
   @XmlElement(
      name = "VerifyClose"
   )
   protected boolean verifyClose;
   @XmlElement(
      name = "InitArgs"
   )
   protected List<String> initArgs;
   @XmlAttribute(
      name = "id"
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger id;
   @XmlAttribute(
      name = "macro"
   )
   protected Boolean macro;

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getClazz() {
      return this.clazz;
   }

   public void setClazz(String var1) {
      this.clazz = var1;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public String getLogo() {
      return this.logo;
   }

   public void setLogo(String var1) {
      this.logo = var1;
   }

   public boolean isLive() {
      return this.live;
   }

   public void setLive(boolean var1) {
      this.live = var1;
   }

   public boolean isReplay() {
      return this.replay;
   }

   public void setReplay(boolean var1) {
      this.replay = var1;
   }

   public boolean isUserStartable() {
      return this.userStartable;
   }

   public void setUserStartable(boolean var1) {
      this.userStartable = var1;
   }

   public boolean isHide() {
      return this.hide;
   }

   public void setHide(boolean var1) {
      this.hide = var1;
   }

   public boolean isUnhide() {
      return this.unhide;
   }

   public void setUnhide(boolean var1) {
      this.unhide = var1;
   }

   public boolean isDetach() {
      return this.detach;
   }

   public void setDetach(boolean var1) {
      this.detach = var1;
   }

   public boolean isUserClose() {
      return this.userClose;
   }

   public void setUserClose(boolean var1) {
      this.userClose = var1;
   }

   public boolean isCanClose() {
      return this.canClose;
   }

   public void setCanClose(boolean var1) {
      this.canClose = var1;
   }

   public boolean isVerifyClose() {
      return this.verifyClose;
   }

   public void setVerifyClose(boolean var1) {
      this.verifyClose = var1;
   }

   public List<String> getInitArgs() {
      if (this.initArgs == null) {
         this.initArgs = new ArrayList();
      }

      return this.initArgs;
   }

   public BigInteger getId() {
      return this.id == null ? new BigInteger("0") : this.id;
   }

   public void setId(BigInteger var1) {
      this.id = var1;
   }

   public boolean isMacro() {
      return this.macro == null ? false : this.macro;
   }

   public void setMacro(Boolean var1) {
      this.macro = var1;
   }
}
