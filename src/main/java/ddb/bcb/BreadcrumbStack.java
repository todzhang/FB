package ddb.bcb;

import java.util.Stack;

public class BreadcrumbStack extends Stack<Object> {
   public Object push(Object var1) {
      assert var1 instanceof BreadcrumbItemChoices && (super.empty() || super.peek() instanceof BreadcrumbItem) || var1 instanceof BreadcrumbItem && !super.empty() && super.peek() instanceof BreadcrumbItemChoices;

      return super.push(var1);
   }
}
