package ddb.dsz.plugin.about.jaxb.version;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "VersionType",
   propOrder = {"value"}
)
public class VersionType {
   @XmlValue
   protected String value;
   @XmlAttribute(
      name = "major"
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger major;
   @XmlAttribute(
      name = "minor"
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger minor;
   @XmlAttribute(
      name = "fix"
   )
   @XmlSchemaType(
      name = "nonNegativeInteger"
   )
   protected BigInteger fix;
   @XmlAttribute(
      name = "build"
   )
   protected String build;

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public BigInteger getMajor() {
      return this.major;
   }

   public void setMajor(BigInteger major) {
      this.major = major;
   }

   public BigInteger getMinor() {
      return this.minor;
   }

   public void setMinor(BigInteger minor) {
      this.minor = minor;
   }

   public BigInteger getFix() {
      return this.fix;
   }

   public void setFix(BigInteger fix) {
      this.fix = fix;
   }

   public String getBuild() {
      return this.build;
   }

   public void setBuild(String build) {
      this.build = build;
   }
}
