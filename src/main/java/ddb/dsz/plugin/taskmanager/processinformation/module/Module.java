package ddb.dsz.plugin.taskmanager.processinformation.module;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.core.data.ObjectValue;
import java.util.Iterator;

public class Module {
   final Long baseAddress;
   final Long imageSize;
   final Long entryPoint;
   final String name;
   final String[] hashes;
   final CoreController core;

   public Module(ObjectValue module, CoreController cc) {
      this.baseAddress = module.getInteger("baseaddress");
      this.imageSize = module.getInteger("imagesize");
      this.entryPoint = module.getInteger("entrypoint");
      String n = module.getString("modulename");
      this.hashes = new String[Module.Hash.values().length];
      Iterator i$ = module.getObjects("checksum").iterator();

      while(i$.hasNext()) {
         ObjectValue ov = (ObjectValue)i$.next();
         String value = ov.getString("value");
         Module.Hash h = Module.Hash.getHash(ov.getString("type"));
         if (h != null) {
            this.hashes[h.ordinal()] = value;
         }
      }

      if (n != null && n.length() != 0) {
         this.name = n;
      } else {
         this.name = " ";
      }

      this.core = cc;
   }

   public Module(CoreController core, long baseAddress, long imageSize, long entryPoint, String name, String[] hashes) {
      this.core = core;
      this.baseAddress = baseAddress;
      this.imageSize = imageSize;
      this.entryPoint = entryPoint;
      this.name = name;
      this.hashes = hashes;
   }

   public Long getBaseAddress() {
      return this.baseAddress;
   }

   public Long getImageSize() {
      return this.imageSize;
   }

   public Long getEntryPoint() {
      return this.entryPoint;
   }

   public String getName() {
      return this.name;
   }

   public String getHash(Module.Hash h) {
      return this.hashes[h.ordinal()];
   }

   public static enum Hash {
      MD5("MD5"),
      SHA1("SHA1"),
      SHA256("SHA256"),
      SHA512("SHA512");

      public final String key;

      private Hash(String k) {
         this.key = k;
      }

      public static Module.Hash getHash(String str) {
         Module.Hash[] arr$ = values();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Module.Hash h = arr$[i$];
            if (h.key.equalsIgnoreCase(str)) {
               return h;
            }
         }

         return null;
      }
   }
}
