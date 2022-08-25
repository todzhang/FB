package ddb.targetmodel.filemodel;

import java.util.EventListener;

public interface FileSystemListener extends EventListener {
   void fileChanged(FileObject var1);
}
