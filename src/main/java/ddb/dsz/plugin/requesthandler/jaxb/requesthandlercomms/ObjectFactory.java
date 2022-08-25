package ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
   private static final QName _Close_QNAME = new QName("", "Close");
   private static final QName _Cancel_QNAME = new QName("", "Cancel");
   private static final QName _Response_QNAME = new QName("", "Response");
   private static final QName _AutoApprove_QNAME = new QName("", "AutoApprove");
   private static final QName _Request_QNAME = new QName("", "Request");
   private static final QName _ExecutionMethod_QNAME = new QName("", "ExecutionMethod");

   public IntegerValue createIntegerValue() {
      return new IntegerValue();
   }

   public StringValue createStringValue() {
      return new StringValue();
   }

   public NewRequestType createNewRequestType() {
      return new NewRequestType();
   }

   public ExecutedRequestType createExecutedRequestType() {
      return new ExecutedRequestType();
   }

   public DeniedRequestType createDeniedRequestType() {
      return new DeniedRequestType();
   }

   public Argument createArgument() {
      return new Argument();
   }

   public KeyValuePair createKeyValuePair() {
      return new KeyValuePair();
   }

   public OperationOptionType createOperationOptionType() {
      return new OperationOptionType();
   }

   public CancelType createCancelType() {
      return new CancelType();
   }

   public ExecutionMethodType createExecutionMethodType() {
      return new ExecutionMethodType();
   }

   public ActionType createActionType() {
      return new ActionType();
   }

   public Format createFormat() {
      return new Format();
   }

   public ParentType createParentType() {
      return new ParentType();
   }

   public CloseType createCloseType() {
      return new CloseType();
   }

   public BooleanValue createBooleanValue() {
      return new BooleanValue();
   }

   public ResponseType createResponseType() {
      return new ResponseType();
   }

   public ObjectValueType createObjectValueType() {
      return new ObjectValueType();
   }

   public TaskDataRequestType createTaskDataRequestType() {
      return new TaskDataRequestType();
   }

   public RequestCompletedType createRequestCompletedType() {
      return new RequestCompletedType();
   }

   public AutoApproveType createAutoApproveType() {
      return new AutoApproveType();
   }

   public TaskDataType createTaskDataType() {
      return new TaskDataType();
   }

   public CancelledRequestType createCancelledRequestType() {
      return new CancelledRequestType();
   }

   public RequestType createRequestType() {
      return new RequestType();
   }

   @XmlElementDecl(
      namespace = "",
      name = "Close"
   )
   public JAXBElement<CloseType> createClose(CloseType var1) {
      return new JAXBElement(_Close_QNAME, CloseType.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "Cancel"
   )
   public JAXBElement<CancelType> createCancel(CancelType var1) {
      return new JAXBElement(_Cancel_QNAME, CancelType.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "Response"
   )
   public JAXBElement<ResponseType> createResponse(ResponseType var1) {
      return new JAXBElement(_Response_QNAME, ResponseType.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "AutoApprove"
   )
   public JAXBElement<AutoApproveType> createAutoApprove(AutoApproveType var1) {
      return new JAXBElement(_AutoApprove_QNAME, AutoApproveType.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "Request"
   )
   public JAXBElement<RequestType> createRequest(RequestType var1) {
      return new JAXBElement(_Request_QNAME, RequestType.class, (Class)null, var1);
   }

   @XmlElementDecl(
      namespace = "",
      name = "ExecutionMethod"
   )
   public JAXBElement<ExecutionMethodType> createExecutionMethod(ExecutionMethodType var1) {
      return new JAXBElement(_ExecutionMethod_QNAME, ExecutionMethodType.class, (Class)null, var1);
   }
}
