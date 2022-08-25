package ddb.targetmodel.filemodel;

import java.util.HashMap;
import java.util.Map;

public enum FileObjectFields {
   File_Parent("Parent", LongData.class),
   File_Name("Name", StringData.class),
   File_TranslatedName("Translated_Name", StringData.class),
   File_AlternateName("Alt_Name", StringData.class),
   File_Size("Size", LongData.class),
   File_Created("Created", CalendarData.class),
   File_Modified("Modified", CalendarData.class),
   File_Accessed("Accessed", CalendarData.class),
   File_Children("Children", LongData.class),
   Dir_Id("DirId", LongData.class),
   Dir_LastPartial("Last_Partial", CalendarData.class),
   Dir_LastFull("Last_Full", CalendarData.class),
   Dir_AccessDenied("Access_Denied", BoolData.class),
   Drive_Id("DriveId", LongData.class),
   Drive_Source("Source", StringData.class),
   Drive_Type("Type", LongData.class),
   Drive_Serial("Serial", StringData.class),
   Drive_FileSystem("FileSystem", StringData.class),
   Drive_Options("Options", StringData.class),
   Attr_Archive("Archive", BoolData.class),
   Attr_Compressed("Compressed", BoolData.class),
   Attr_Encrypted("Encrypted", BoolData.class),
   Attr_Hidden("Hidden", BoolData.class),
   Attr_Offline("Offline", BoolData.class),
   Attr_ReadOnly("ReadOnly", BoolData.class),
   Attr_ReparsePoint("ReparsePoint", BoolData.class),
   Attr_SparseFile("SparseFile", BoolData.class),
   Attr_System("System", BoolData.class),
   Attr_Temporary("Temporary", BoolData.class),
   Attr_NotContentIndexed("NotContentIndexed", BoolData.class),
   Attr_Device("Device", BoolData.class),
   Attr_OwnerRead("OwnerRead", BoolData.class),
   Attr_OwnerWrite("OwnerWrite", BoolData.class),
   Attr_OwnerExec("OwnerExec", BoolData.class),
   Attr_GroupRead("GroupRead", BoolData.class),
   Attr_GroupWrite("GroupWrite", BoolData.class),
   Attr_GroupExec("GroupExec", BoolData.class),
   Attr_WorldRead("WorldRead", BoolData.class),
   Attr_WorldWrite("WorldWrite", BoolData.class),
   Attr_WorldExec("WorldExec", BoolData.class),
   Attr_SetUid("SetUid", BoolData.class),
   Attr_SetGid("SetGid", BoolData.class),
   Attr_StickyBit("StickyBit", BoolData.class),
   Attr_Owner_Name("Owner_Name", StringData.class),
   Attr_Group_Name("Group_Name", StringData.class),
   Attr_Owner_Id("Owner_Id", LongData.class),
   Attr_Group_Id("Group_Id", LongData.class),
   Attr_CharacterSpecialFile("CharacterSpecialFile", BoolData.class),
   Attr_BlockSpecialFile("BlockSpecialFile", BoolData.class),
   Attr_UnixFamilySocket("UnixFamilySocket", BoolData.class),
   Attr_NamedPipe("NamedPipe", BoolData.class),
   Attr_SymbolicLink("SymbolicLink", BoolData.class),
   Attr_Inode("Inode", LongData.class),
   Attr_HardLinks("HardLinks", LongData.class),
   Hash_Sha1("Sha1", StringData.class),
   Hash_Md5("Md5", StringData.class),
   Hash_Sha256("Sha256", StringData.class),
   Hash_Sha512("Sha512", StringData.class);

   private static final Map<String, FileObjectFields> nameMap = new HashMap();
   public static final FileObjectFields[] FileFields;
   public static final FileObjectFields[] DirFields;
   public static final FileObjectFields[] DriveFields;
   public static final FileObjectFields[] AttrFields;
   public static final FileObjectFields[] HashFields;
   private final String name;
   private final Class<? extends Data> clazz;

   public static final FileObjectFields getField(String var0) {
      return (FileObjectFields)nameMap.get(var0);
   }

   private FileObjectFields(String var3, Class<? extends Data> var4) {
      this.name = var3.toUpperCase();
      this.clazz = var4;
   }

   public final String getName() {
      return this.name;
   }

   public final Class<? extends Data> getClazz() {
      return this.clazz;
   }

   static {
      FileObjectFields[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         FileObjectFields var3 = var0[var2];
         nameMap.put(var3.name, var3);
      }

      FileFields = new FileObjectFields[]{File_Parent, File_Name, File_TranslatedName, File_AlternateName, File_Size, File_Created, File_Modified, File_Accessed};
      DirFields = new FileObjectFields[]{Dir_Id, Dir_LastPartial, Dir_LastFull, Dir_AccessDenied};
      DriveFields = new FileObjectFields[]{Drive_Id, Drive_Source, Drive_Type, Drive_Serial, Drive_FileSystem, Drive_Options};
      AttrFields = new FileObjectFields[]{Attr_Archive, Attr_Compressed, Attr_Encrypted, Attr_Hidden, Attr_Offline, Attr_ReadOnly, Attr_ReparsePoint, Attr_SparseFile, Attr_System, Attr_Temporary, Attr_NotContentIndexed, Attr_Device, Attr_OwnerRead, Attr_OwnerWrite, Attr_OwnerExec, Attr_GroupRead, Attr_GroupWrite, Attr_GroupExec, Attr_WorldRead, Attr_WorldWrite, Attr_WorldExec, Attr_SetUid, Attr_SetGid, Attr_StickyBit, Attr_Owner_Name, Attr_Group_Name, Attr_Inode, Attr_Owner_Id, Attr_Group_Id, Attr_HardLinks, Attr_CharacterSpecialFile, Attr_BlockSpecialFile, Attr_UnixFamilySocket, Attr_NamedPipe, Attr_SymbolicLink};
      HashFields = new FileObjectFields[]{Hash_Sha1, Hash_Md5, Hash_Sha256, Hash_Sha512};
   }
}
