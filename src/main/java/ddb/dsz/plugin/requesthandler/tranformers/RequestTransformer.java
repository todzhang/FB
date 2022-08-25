package ddb.dsz.plugin.requesthandler.tranformers;

import ddb.dsz.plugin.requesthandler.jaxb.requesthandlercomms.ObjectFactory;
import org.apache.commons.collections.Transformer;

public abstract class RequestTransformer implements Transformer {
   protected static final ObjectFactory objFact = new ObjectFactory();
}
