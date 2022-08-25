package ddb.dsz.plugin.mirror.jaxb.mirrorcomms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _MirrorRequest_QNAME = new QName("", "MirrorRequest");
   private static final QName _MirrorFileStatus_QNAME = new QName("", "MirrorFileStatus");
   private static final QName _MirrorNext_QNAME = new QName("", "MirrorNext");
   private static final QName _MirrorPong_QNAME = new QName("", "MirrorPong");
   private static final QName _MirrorPing_QNAME = new QName("", "MirrorPing");
   private static final QName _MirrorTransfer_QNAME = new QName("", "MirrorTransfer");

   public MirrorNext createMirrorNext() {
      return new MirrorNext();
   }

   public MirrorTransfer createMirrorTransfer() {
      return new MirrorTransfer();
   }

   public MirrorPong createMirrorPong() {
      return new MirrorPong();
   }

   public MirrorPing createMirrorPing() {
      return new MirrorPing();
   }

   public MirrorFileStatus createMirrorFileStatus() {
      return new MirrorFileStatus();
   }

   public MirrorRequest createMirrorRequest() {
      return new MirrorRequest();
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorRequest"
   )
   public JAXBElement<MirrorRequest> createMirrorRequest(MirrorRequest var1) {
      return new JAXBElement(_MirrorRequest_QNAME, MirrorRequest.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorFileStatus"
   )
   public JAXBElement<MirrorFileStatus> createMirrorFileStatus(MirrorFileStatus var1) {
      return new JAXBElement(_MirrorFileStatus_QNAME, MirrorFileStatus.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorNext"
   )
   public JAXBElement<MirrorNext> createMirrorNext(MirrorNext var1) {
      return new JAXBElement(_MirrorNext_QNAME, MirrorNext.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorPong"
   )
   public JAXBElement<MirrorPong> createMirrorPong(MirrorPong var1) {
      return new JAXBElement(_MirrorPong_QNAME, MirrorPong.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorPing"
   )
   public JAXBElement<MirrorPing> createMirrorPing(MirrorPing var1) {
      return new JAXBElement(_MirrorPing_QNAME, MirrorPing.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "MirrorTransfer"
   )
   public JAXBElement<MirrorTransfer> createMirrorTransfer(MirrorTransfer var1) {
      return new JAXBElement(_MirrorTransfer_QNAME, MirrorTransfer.class, (Class)null, var1);
   }
}
