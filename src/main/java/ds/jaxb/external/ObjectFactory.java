package ds.jaxb.external;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _RemoteIdentification_QNAME = new QName("", "RemoteIdentification");
   private static final QName _RemotePong_QNAME = new QName("", "RemotePong");
   private static final QName _RemotePing_QNAME = new QName("", "RemotePing");
   private static final QName _RemoteMessage_QNAME = new QName("", "RemoteMessage");

   public RemoteMessage createRemoteMessage() {
      return new RemoteMessage();
   }

   public RemotePong createRemotePong() {
      return new RemotePong();
   }

   public RemotePing createRemotePing() {
      return new RemotePing();
   }

   public RemoteIdentification createRemoteIdentification() {
      return new RemoteIdentification();
   }

   @XmlElementDecl(
      namespace = "",
      name = "RemoteIdentification"
   )
   public JAXBElement<RemoteIdentification> createRemoteIdentification(RemoteIdentification var1) {
      return new JAXBElement(_RemoteIdentification_QNAME, RemoteIdentification.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "RemotePong"
   )
   public JAXBElement<RemotePong> createRemotePong(RemotePong var1) {
      return new JAXBElement(_RemotePong_QNAME, RemotePong.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "RemotePing"
   )
   public JAXBElement<RemotePing> createRemotePing(RemotePing var1) {
      return new JAXBElement(_RemotePing_QNAME, RemotePing.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "RemoteMessage"
   )
   public JAXBElement<RemoteMessage> createRemoteMessage(RemoteMessage var1) {
      return new JAXBElement(_RemoteMessage_QNAME, RemoteMessage.class, (Class)null, var1);
   }
}
