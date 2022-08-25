package ddb.dsz.plugin.scripteditor;

import org.syntax.jedit.JEditTextArea;

final class FocusOnTextArea implements Runnable {
   JEditTextArea area;

   public FocusOnTextArea(JEditTextArea var1) {
      this.area = var1;
   }

   public void run() {
      this.area.requestFocusInWindow();
   }
}
