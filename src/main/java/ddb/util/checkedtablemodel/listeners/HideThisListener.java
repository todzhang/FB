package ddb.util.checkedtablemodel.listeners;

import ddb.util.checkedtablemodel.CheckableFilterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HideThisListener implements ActionListener {
   private CheckableFilterList parent;

   public HideThisListener(CheckableFilterList var1) {
      this.parent = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.parent.hideThis();
   }
}
