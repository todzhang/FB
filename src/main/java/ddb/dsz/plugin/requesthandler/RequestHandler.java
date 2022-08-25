package ddb.dsz.plugin.requesthandler;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.host.HostInfo;
import ddb.dsz.core.internalcommand.InternalCommandHandler;
import ddb.dsz.core.operation.Operation;
import ddb.dsz.core.task.TaskId;
import ddb.dsz.plugin.NoHostAbstractPlugin;
import ddb.dsz.plugin.peer.PeerReceiver;
import ddb.dsz.plugin.peer.PeerTag;
import ddb.dsz.plugin.requesthandler.closures.ClosureData;
import ddb.dsz.plugin.requesthandler.closures.RequestHandlerClosure;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ExecutionMethodType;
import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ObjectFactory;
import ddb.dsz.plugin.requesthandler.model.RequestStatus;
import ddb.dsz.plugin.requesthandler.model.RequestTableColumns;
import ddb.dsz.plugin.requesthandler.model.RequestTableModel;
import ddb.dsz.plugin.requesthandler.renderers.DescriptionRenderer;
import ddb.dsz.plugin.requesthandler.renderers.StatusRenderer;
import ddb.dsz.plugin.requesthandler.requests.RequestedOperation;
import ddb.dsz.plugin.requesthandler.tranformers.InternalTranslator;
import ddb.imagemanager.ImageManager;
import ddb.util.Guid;
import ddb.util.JaxbCache;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.Closure;

public abstract class RequestHandler extends NoHostAbstractPlugin implements InternalCommandHandler, PeerReceiver {
   public static final String REQUEST_XML = "Requests/config.xml";
   public static final String EXECMETHODS = "/RequestHandler/execution.xml";
   protected static ObjectFactory objFact = new ObjectFactory();
   private JTable requestTable;
   protected RequestTableModel requests;
   protected ExecutionMethodType execMethod = null;
   protected Closure messageHandler = RequestHandlerClosure.getInstance(this);
   Marshaller marsh;
   Unmarshaller unmarsh;
   protected JPanel mainPanel = new JPanel(new BorderLayout());
   InternalTranslator translator = InternalTranslator.getInstance();
   protected int focusedRow = -1;

   @Override
   protected final int init2() {
      this.core.addPeerReceiver(this);

      try {
         JAXBContext var1 = JaxbCache.getContext(ObjectFactory.class);
         this.marsh = var1.createMarshaller();
         this.unmarsh = var1.createUnmarshaller();
      } catch (JAXBException var2) {
         this.core.logEvent(Level.SEVERE, var2.getMessage(), var2);
         return -1;
      }

      this.requests = new RequestTableModel(this, this.getExecutor(this.core));
      this.requestTable = new JTable(this.requests);
      JScrollPane var3 = new JScrollPane(this.requestTable);
      this.setWidth(RequestTableColumns.STATUS, true);
      this.requestTable.getTableHeader().setReorderingAllowed(false);
      this.requestTable.setDefaultRenderer(RequestStatus.class, new StatusRenderer(this.core));
      this.requestTable.setDefaultRenderer(RequestedOperation.class, new DescriptionRenderer());
      this.requestTable.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            this.maybeDoPopup(var1);
            if (var1.getClickCount() == 2) {
               int var2 = RequestHandler.this.requestTable.rowAtPoint(var1.getPoint());
               if (var2 < 0 || var2 >= RequestHandler.this.requests.getRowCount()) {
                  return;
               }

               Object var3 = RequestHandler.this.requests.getValueAt(var2, RequestTableColumns.DESCRIPTION);
               Object var4 = RequestHandler.this.requests.getValueAt(var2, RequestTableColumns.STATUS);
               if (var3 instanceof RequestedOperation && var4 instanceof RequestStatus) {
                  RequestInformation var5 = new RequestInformation((RequestedOperation)RequestedOperation.class.cast(var3), RequestHandler.this, RequestHandler.this.core.isLiveOperation(), (RequestStatus)RequestStatus.class.cast(var4));
                  var5.setVisible(true);
               }
            }

         }

         @Override
         public void mousePressed(MouseEvent var1) {
            this.maybeDoPopup(var1);
         }

         @Override
         public void mouseReleased(MouseEvent var1) {
            this.maybeDoPopup(var1);
         }

         private void maybeDoPopup(MouseEvent var1) {
            if (var1.isPopupTrigger()) {
               int var2 = RequestHandler.this.requestTable.rowAtPoint(var1.getPoint());
               if (var2 < 0 || var2 >= RequestHandler.this.requests.getRowCount()) {
                  return;
               }

               RequestHandler.this.focusedRow = var2;
               RequestHandler.this.buildMenu().show(RequestHandler.this.requestTable, var1.getX(), var1.getY());
            }

         }
      });
      this.mainPanel.add(var3, "Center");
      super.setDisplay(this.mainPanel);
      this.setCanClose(false);
      return this.init3();
   }

   protected int init3() {
      return 0;
   }

   @Override
   protected final void fini2() {
      this.core.removePeerReceiver(this);
      this.fini3();
   }

   protected void fini3() {
   }

   public final CoreController getCoreController() {
      return this.core;
   }

   private void setWidth(RequestTableColumns var1, boolean var2) {
      JLabel var3 = new JLabel(var1.getName() + "   ");
      if (var2) {
         var3.setIcon(ImageManager.getIcon(super.getLogo(), this.core.getLabelImageSize()));
      }

      TableColumn var4 = this.requestTable.getColumnModel().getColumn(var1.ordinal());
      if (var4 != null) {
         var4.setMaxWidth(var3.getPreferredSize().width);
         var4.setMinWidth(var3.getPreferredSize().width);
      }

   }

   public void cancelledRequest(BigInteger var1, Object var2) {
      this.requests.cancelledRequest(var1, var2);
   }

   public void deniedRequest(BigInteger var1, Object var2) {
      this.requests.deniedRequest(var1);
   }

   public void executedRequest(BigInteger var1, Object var2) {
      this.requests.executedRequest(var1);
   }

   public void handleNewRequest(RequestedOperation var1, PeerTag var2) {
      this.requests.addRequest(var1);
   }

   @Override
   protected boolean parseArgument2(String var1, String var2) {
      if (var1.equalsIgnoreCase("max")) {
         try {
            this.requests.setMaximum(Integer.valueOf(var2));
            return true;
         } catch (NumberFormatException var4) {
         }
      }

      return this.parseArgument3(var1, var2);
   }

   protected boolean parseArgument3(String var1, String var2) {
      return false;
   }

   public void handleRequest(RequestedOperation var1, PeerTag var2) {
   }

   public void executeRequest(RequestedOperation var1) {
   }

   public void handleCancel(BigInteger var1, PeerTag var2) {
   }

   protected abstract Closure getExecutor(CoreController var1);

   protected final void publish(JAXBElement<?> var1, PeerTag var2) {
      if (var1 != null) {
         try {
            StringWriter var3 = new StringWriter();
            synchronized(this.marsh) {
               this.marsh.marshal(var1, var3);
            }

            this.core.sendMessageToPeer(var3.toString(), var2);
         } catch (JAXBException var7) {
            this.core.logEvent(Level.WARNING, var7.getMessage(), var7);
         }

      }
   }

   @Override
   public void newConnection(PeerTag peerTag) {
   }

   @Override
   public void closedConnection(PeerTag peerTag) {
   }

   @Override
   public void receivedMessage(String message, PeerTag peerTag) {
      this.core.logEvent(Level.FINEST, "Received Message:\r\n" + message);

      try {
         this.messageHandler.execute(new ClosureData(this.unmarsh.unmarshal(new StringReader(message)), peerTag));
      } catch (UnmarshalException var4) {
         var4.printStackTrace();
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

   }

   public void sendPong(PeerTag var1) {
   }

   protected RequestedOperation generateOperation(List<String> var1) {
      return (RequestedOperation)RequestedOperation.class.cast(this.translator.transform(var1));
   }

   protected JPopupMenu buildMenu() {
      JPopupMenu var1 = new JPopupMenu();
      final ArrayList var2 = new ArrayList();
      final ArrayList var3 = new ArrayList();
      boolean var4 = false;
      int[] var5 = this.requestTable.getSelectedRows();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var5[var7];
         RequestedOperation var9 = (RequestedOperation)RequestedOperation.class.cast(this.requests.getValueAt(var8, RequestTableColumns.DESCRIPTION.ordinal()));
         var2.add(var9);
         var3.add(var9.getId());
         if (this.requests.getValueAt(var8, RequestTableColumns.STATUS.ordinal()) == RequestStatus.PENDING) {
            var4 = true;
         }
      }

      JMenuItem var12;
      if (this.allowEnabled() && var4) {
         var12 = new JMenuItem("Approve");
         var12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Iterator var2 = var3.iterator();

               while(var2.hasNext()) {
                  BigInteger var3x = (BigInteger)var2.next();
                  RequestHandler.this.approve(var3x);
               }

            }
         });
         var1.add(var12);
         if (var2.size() == 1) {
            JMenu var13 = new JMenu("Approve As");
            HostInfo var14 = this.core.getHostById(((RequestedOperation)var2.get(0)).getData("host"));
            if (var14 != null) {
               List var15 = this.core.getUserAliases(var14);
               if (var15.size() > 0) {
                  var1.add(var13);
                  Iterator var16 = var15.iterator();

                  while(var16.hasNext()) {
                     final String var10 = (String)var16.next();
                     JMenuItem var11 = new JMenuItem(var10);
                     var11.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent var1) {
                           Iterator var2x = var2.iterator();

                           while(var2x.hasNext()) {
                              RequestedOperation var3 = (RequestedOperation)var2x.next();
                              var3.setData("useralias", var10);
                              RequestHandler.this.approve(var3.getId());
                           }

                        }
                     });
                     var13.add(var11);
                  }
               }
            }
         }
      }

      if (this.forceEnabled()) {
         var12 = new JMenuItem("Start now");
         var12.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Iterator var2 = var3.iterator();

               while(var2.hasNext()) {
                  BigInteger var3x = (BigInteger)var2.next();
                  RequestHandler.this.force(var3x);
               }

            }
         });
         var1.add(var12);
      }

      var12 = new JMenuItem(this.getCancelName());
      var12.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            Iterator var2 = var3.iterator();

            while(var2.hasNext()) {
               BigInteger var3x = (BigInteger)var2.next();
               RequestHandler.this.cancel(var3x);
            }

         }
      });
      var1.add(var12);
      return var1;
   }

   @Override
   public boolean allowNewInstance(Class<?> clazz) {
      if (!super.allowNewInstance(clazz)) {
         return false;
      } else {
         return !RequestHandler.class.isAssignableFrom(clazz);
      }
   }

   protected abstract String getCancelName();

   protected boolean allowEnabled() {
      return false;
   }

   protected boolean forceEnabled() {
      return false;
   }

   protected void approve(BigInteger var1) {
   }

   protected void force(BigInteger var1) {
   }

   public void sendData(PeerTag var1, Guid var2, BigInteger var3, boolean var4) {
   }

   protected abstract void cancel(BigInteger var1);

   public void fireExecutedRequest() {
   }

   public void fireDeniedRequest() {
   }

   public void fireCancelledRequest() {
   }

   public void fireNewRequest() {
   }

   public void fireUpdatedRequest() {
   }

   public void requestData(TaskId taskId) {
   }

   public Operation getOperationById(BigInteger var1) {
      return this.core.getOperationById(var1);
   }

   public Operation getOperationById(Guid guid) {
      return this.core.getOperationById(guid);
   }

   public static void main(String[] args) throws Exception {
      Class var1 = Class.forName("ds.plugin.live.DSClientApp");
      Class var2 = Class.forName("ds.plugin.replay.OpReplayDriver");
      Method var3 = var1.getMethod("main", args.getClass());
      var3.invoke((Object)null, args);
   }
}
