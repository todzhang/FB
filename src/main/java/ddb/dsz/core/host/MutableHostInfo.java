package ddb.dsz.core.host;

import java.util.Calendar;

public interface MutableHostInfo extends HostInfo {
   void setLocal(boolean local);

   void setConnected(boolean connected);

   void setId(String id);

   void setVersion(String version);

   void setPlatform(String platform);

   void setImplantType(String implantType);

   void setArch(String arch);

   void setModified(Calendar modified);

   void setHostname(String hostname);
}
