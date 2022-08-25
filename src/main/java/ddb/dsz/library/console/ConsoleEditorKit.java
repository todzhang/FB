package ddb.dsz.library.console;

import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.ZoneView;

public class ConsoleEditorKit extends StyledEditorKit {
   @Override
   public ViewFactory getViewFactory() {
      return new ViewFactory() {
         @Override
         public View create(Element element) {
            String name = element.getName();
            if (name != null) {
               if (name.equals("content")) {
                  return new LabelView(element);
               }

               if (name.equals("paragraph")) {
                  return new ParagraphView(element);
               }

               if (name.equals("section")) {
                  return new ZoneView(element, 1);
               }

               if (name.equals("component")) {
                  return new ComponentView(element);
               }

               if (name.equals("icon")) {
                  return new IconView(element);
               }
            }

            return new LabelView(element);
         }
      };
   }
}
