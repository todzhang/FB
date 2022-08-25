package ddb.web.util;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTMLAttributes extends HashMap<Object, Object> implements HTMLConstants {
   public static final String classVersion = "2.1";
   public static final long serialVersionUID = -4041554105676575008L;
   protected ArrayList<Object> keyList = new ArrayList();

   public void clear() {
      super.clear();
      this.keyList.clear();
   }

   public Object clone() {
      HTMLAttributes copy = (HTMLAttributes)super.clone();
      copy.keyList = (ArrayList)this.keyList.clone();
      return copy;
   }

   public Object put(Object attribute) {
      return this.put(attribute, (Object)null);
   }

   public Object put(Object attribute, Object value) {
      this.keyList.remove(attribute);
      this.keyList.add(attribute);
      return value instanceof Color ? super.put(attribute, HTMLUtils.toHTML((Color)value)) : super.put(attribute, value);
   }

   public Object put(Object attribute, int value) {
      return this.put(attribute, String.valueOf(value));
   }

   public void putAll(Map<?, ?> map) {
      this.keyList.removeAll(map.keySet());
      this.keyList.addAll(map.keySet());
      super.putAll(map);
   }

   public HTMLAttributes putOnly(Object attribute, Object value) {
      this.clear();
      this.put(attribute, value);
      return this;
   }

   public HTMLAttributes putOnly(Object attribute, int value) {
      return this.putOnly(attribute, String.valueOf(value));
   }

   public Object remove(Object attribute) {
      this.keyList.remove(attribute);
      return super.remove(attribute);
   }

   public String toString() {
      HTMLWriter html = new HTMLWriter();

      try {
         this.write(html);
      } catch (IOException var3) {
         throw new InternalError("Unexpected IOException in HTMLAttributes.toString()");
      }

      return html.toString();
   }

   protected final void write(String attribute, String value, Writer html) throws IOException {
      html.write(32);
      html.write(attribute);
      if (value != null && value.length() != 0) {
         html.write("=\"");
         html.write(value);
         html.write(34);
      }

   }

   public final void write(Writer writer) throws IOException {
      Iterator it = this.keyList.iterator();

      while(it.hasNext()) {
         String attribute = (String)it.next();
         Object value = this.get(attribute);
         String valueString = value != null ? value.toString() : null;
         this.write(attribute, valueString, writer);
      }

   }
}
