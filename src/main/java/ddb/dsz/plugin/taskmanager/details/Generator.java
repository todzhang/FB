package ddb.dsz.plugin.taskmanager.details;

import ddb.dsz.core.controller.CoreController;
import ddb.dsz.plugin.taskmanager.details.basicinfo.BasicInfoDisplay;
import ddb.dsz.plugin.taskmanager.details.basicinfo.WindowsBasicInfoDisplay;
import ddb.dsz.plugin.taskmanager.details.basicinfo.identity.IdentityDisplay;
import ddb.dsz.plugin.taskmanager.details.basicinfo.identity.WindowsIdentityDisplay;
import ddb.dsz.plugin.taskmanager.details.group.GroupDisplay;
import ddb.dsz.plugin.taskmanager.details.group.GroupsDisplay;
import ddb.dsz.plugin.taskmanager.details.group.WindowsGroupDisplay;
import ddb.dsz.plugin.taskmanager.details.handle.HandleInfoColumns;
import ddb.dsz.plugin.taskmanager.details.handle.HandleInfoModel;
import ddb.dsz.plugin.taskmanager.details.handle.HandleTypeRenderer;
import ddb.dsz.plugin.taskmanager.details.module.ModuleDisplay;
import ddb.dsz.plugin.taskmanager.details.module.ModulesDisplay;
import ddb.dsz.plugin.taskmanager.details.privilege.PrivilegeDisplay;
import ddb.dsz.plugin.taskmanager.details.privilege.PrivilegesDisplay;
import ddb.dsz.plugin.taskmanager.details.privilege.WindowsPrivilegeDisplay;
import ddb.dsz.plugin.taskmanager.processinformation.ProcessInformation;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.BasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.WindowsBasicInfo;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.Identity;
import ddb.dsz.plugin.taskmanager.processinformation.basicinfo.identity.WindowsIdentity;
import ddb.dsz.plugin.taskmanager.processinformation.group.Group;
import ddb.dsz.plugin.taskmanager.processinformation.group.WindowsGroup;
import ddb.dsz.plugin.taskmanager.processinformation.handle.Handle;
import ddb.dsz.plugin.taskmanager.processinformation.module.Module;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.Privilege;
import ddb.dsz.plugin.taskmanager.processinformation.privilege.WindowsPrivilege;
import ddb.gui.swing.DszTableCellRenderer;
import ddb.imagemanager.ImageManager;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class Generator {
   private Generator() {
   }

   public static BasicInfoDisplay makeBasicInfoDisplay(CoreController core, BasicInfo basicInfo) {
      return (BasicInfoDisplay)(basicInfo instanceof WindowsBasicInfo ? new WindowsBasicInfoDisplay(core, (WindowsBasicInfo)WindowsBasicInfo.class.cast(basicInfo)) : new BasicInfoDisplay(core, basicInfo));
   }

   public static IdentityDisplay makeIdentityDisplay(CoreController core, Identity identity) {
      return (IdentityDisplay)(identity instanceof WindowsIdentity ? new WindowsIdentityDisplay(core, (WindowsIdentity)WindowsIdentity.class.cast(identity)) : new IdentityDisplay(core, identity));
   }

   public static GroupsDisplay makeGroupsDisplay(CoreController core, List<Group> groups) {
      return new GroupsDisplay(core, groups);
   }

   public static GroupDisplay makeGroupDisplay(CoreController core, Group group) {
      return (GroupDisplay)(group instanceof WindowsGroup ? new WindowsGroupDisplay(core, (WindowsGroup)WindowsGroup.class.cast(group)) : new GroupDisplay(core, group));
   }

   public static ModulesDisplay makeModulesDisplay(CoreController core, ProcessInformation procInfo) {
      return new ModulesDisplay(core, procInfo);
   }

   public static PrivilegesDisplay makePrivilegesDisplay(CoreController core, ProcessInformation procInfo) {
      return new PrivilegesDisplay(core, procInfo);
   }

   public static PrivilegeDisplay makePrivilegeDisplay(CoreController core, Privilege privilege) {
      return (PrivilegeDisplay)(privilege instanceof WindowsPrivilege ? new WindowsPrivilegeDisplay(core, (WindowsPrivilege)WindowsPrivilege.class.cast(privilege)) : new PrivilegeDisplay(core, privilege));
   }

   public static ModuleDisplay makeModuleDisplay(CoreController core, Module module) {
      return new ModuleDisplay(core, module);
   }

   public static JComponent makeHandleInfoDisplay(CoreController core, ProcessInformation procInfo) {
      JTable table = new JTable(new HandleInfoModel(core, procInfo));
      table.setDefaultRenderer(String.class, new DszTableCellRenderer());
      table.setDefaultRenderer(Long.class, new DszTableCellRenderer());
      table.setDefaultRenderer(Handle.HandleType.class, new HandleTypeRenderer());
      setColumnWidth(table, HandleInfoColumns.ID, "999999", false, true);
      setColumnWidth(table, HandleInfoColumns.TYPE, "AVeryLongStringName", false, true);
      return new JScrollPane(table);
   }

   private static void setColumnWidth(JTable jtable, Enum<?> col, String string, boolean icon, boolean binding) {
      TableColumn column = jtable.getColumnModel().getColumn(col.ordinal());
      JLabel label = new JLabel(string);
      if (icon) {
         label.setIcon(ImageManager.getIcon("images/taskmanager.png", ImageManager.SIZE16));
      }

      column.setPreferredWidth(label.getPreferredSize().width + 10);
      if (binding) {
         column.setMaxWidth(label.getPreferredSize().width + 15);
         column.setMinWidth(label.getPreferredSize().width + 5);
      }

   }
}
