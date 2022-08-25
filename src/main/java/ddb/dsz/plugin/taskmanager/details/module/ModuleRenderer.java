package ddb.dsz.plugin.taskmanager.details.module;

import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ModuleRenderer extends DefaultListCellRenderer {
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      boolean bold = false;
      if (value instanceof Module) {
         Module m = (Module)value;
         if (m.getName() != null && m.getName().trim().length() != 0) {
            value = getLastPathComponent(m.getName());
         } else {
            if (m.getBaseAddress() > 2147483647L) {
               value = String.format("??? 0x%016x", m.getBaseAddress());
            } else {
               value = String.format("??? 0x%08x", m.getBaseAddress());
            }

            bold = true;
         }
      }

      if (bold) {
         super.setFont(super.getFont().deriveFont(1));
      } else {
         super.setFont(super.getFont().deriveFont(0));
      }

      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (bold) {
         c.setFont(c.getFont().deriveFont(1));
      } else {
         c.setFont(c.getFont().deriveFont(0));
      }

      return c;
   }

   public static String getLastPathComponent(String name) {
      while(name.indexOf("/") != -1 || name.indexOf("\\") != -1) {
         name = name.replaceFirst(".*[/\\\\]", "");
      }

      return name;
   }

   public static String getAllButLastPathComponent(String name) {
      int index = -1;
      if (name.lastIndexOf("/") > index) {
         index = name.lastIndexOf("/");
      }

      if (name.lastIndexOf("\\") > index) {
         index = name.lastIndexOf("\\");
      }

      return name.substring(0, index).replaceAll("\\\\", "\\\\\\\\");
   }
}
