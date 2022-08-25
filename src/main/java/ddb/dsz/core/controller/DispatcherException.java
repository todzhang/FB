package ddb.dsz.core.controller;

import java.net.SocketException;

public class DispatcherException extends SocketException {
   public DispatcherException() {
   }

   public DispatcherException(String msg) {
      super(msg);
   }
}
