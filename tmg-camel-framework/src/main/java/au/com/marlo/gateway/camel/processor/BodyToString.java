package au.com.marlo.gateway.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * In case of stream body is necessary string conversion.
 */
public class BodyToString implements Processor{
    @Override
    public void process(Exchange exchange) throws Exception {
        String body =  exchange.getIn().getBody(String.class);
        exchange.getIn().setBody(body);
    }
}
