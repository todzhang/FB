package ddb.dsz.plugin.taskmanager.processinformation.group;

import ddb.dsz.core.data.ObjectValue;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class WindowsGroup extends Group {
   String type;
   String name;
   List<Attribute> attributes = new Vector();
   Long mask;

   public WindowsGroup(ObjectValue group) {
      super(group);
      this.type = group.getString("type");
      this.name = group.getString("name");
      this.mask = group.getInteger("attributes::mask");
      ObjectValue attributeList = group.getObject("attributes");
      if (attributeList != null) {
         Iterator i$ = attributeList.getBooleanNames().iterator();

         while(i$.hasNext()) {
            String key = (String)i$.next();
            this.attributes.add(new Attribute(key, attributeList.getBoolean(key)));
         }
      }

   }

   public List<Attribute> getAttributes() {
      return Collections.unmodifiableList(this.attributes);
   }

   public Long getMask() {
      return this.mask;
   }

   public String getName() {
      return this.name;
   }

   public String getType() {
      return this.type;
   }
}
