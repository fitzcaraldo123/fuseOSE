package au.com.marlo.gateway.camel.bean;


import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.builder.xml.XPathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeaderHandlerBean {

    private static final Logger logger = LoggerFactory.getLogger(HeaderHandlerBean.class);

    private  String headerPrefix;
    private String xpathHeader="xpath_query";
    private String xpathNamespaces = "xpath_namespaces";


    public HeaderHandlerBean(){

    }

    public HeaderHandlerBean(String headerPrefix){
        this.headerPrefix = headerPrefix;
    }


    /** puts the message in a header so we could propagate all the route steps through queues
     *
     * @param exchange
     */
    public void bodyToHeader(Exchange exchange){
        logger.info("entered bodyToHeader");
        Map<String, Object> headers = exchange.getIn().getHeaders();

        int specialHeaderIndex = 0;
        String xpathQuery = "";

        for (String key: headers.keySet() ) {
            if(key.contains(headerPrefix)){
                specialHeaderIndex = Integer.valueOf(key.substring( headerPrefix.length() ));
            }
            else if(key.equals(xpathHeader))
            {
                xpathQuery = (String) headers.get(xpathHeader);
            }
        }

        ++ specialHeaderIndex;
        logger.info("setting header :" + headerPrefix + specialHeaderIndex );
        //converting every body to string so it can be in the header

        String body =  exchange.getIn().getBody(String.class);
        String nsString = (String) exchange.getIn().getHeader(xpathNamespaces);
        logger.info("Namespace string : " + nsString);

        exchange.getIn().removeHeader(xpathHeader);
        exchange.getIn().removeHeader(xpathNamespaces);

        if( (xpathQuery != null) && (!xpathQuery.equals("")))
        {
            body = applyXpathQuery(exchange, xpathQuery, body, nsString);
        }

        logger.info("received body " + body);

        exchange.getIn().setHeader(headerPrefix + specialHeaderIndex, body );
        exchange.getIn().setBody("");
    }

    /**
     * Filters body message according to xpath query.
     * @param exchange
     * @param xpathQuery
     * @param body
     * @param nsString
     * @return
     */
    private String applyXpathQuery(Exchange exchange, String xpathQuery, String body, String nsString) {
        logger.info("Xpath query: " + xpathQuery);
        Namespaces ns = new Namespaces("","");
        if( (nsString != null) && (!nsString.equals(""))) {
            String[] nsElements = nsString.split(";");
            logger.info("Namespaces specified : ");
            for ( String element : nsElements )
            {
                String[] nsConfig = element.split("\\|");
                ns.add(nsConfig[0], nsConfig[1]);
                logger.info("Prefix : " + nsConfig[0] + " URL: " + nsConfig[1]);
            }
        }
        body = XPathBuilder.xpath(xpathQuery).namespaces(ns)
                .evaluate(exchange.getContext(), body, String.class);
        return body;
    }


    /**
     * The special header are organized by input order. This method will take the last header, put its content in the body
     * but still maintaining the header content
     */
    public void lastHeaderToBody(Exchange exchange) throws Exception {
        logger.info("entered last header");

        int lastHeaderIndex =0 ;

        Map<String, Object> headers = exchange.getIn().getHeaders();

        for (String key: headers.keySet() ) {
            if(key.contains(headerPrefix)){
                int headerIndex = Integer.valueOf(key.substring( headerPrefix.length() ));
                if(headerIndex > lastHeaderIndex) lastHeaderIndex = headerIndex;
            }
        }

        if (lastHeaderIndex == 0){
            throw new Exception("NO HEADER FOUND");
        }else {
            exchange.getIn().setBody(exchange.getIn().getHeader(headerPrefix + lastHeaderIndex));
        }


    }


    /**
     * when you want to avoid header propagation but still want to save the previous message, just put then in the exchange property.
     * all header that match the prefix will me put in the exchange properties and be cleaned from the header.
     * @param exchange
     */
    public void headersToProperties(Exchange exchange){
        logger.info("entered headersToProperties");

        Map<String, Object> headers = exchange.getIn().getHeaders();
        Set<String> keysToRemove = new HashSet<>();

        for (String key: headers.keySet() ) {
            if(key.contains(headerPrefix)){
                keysToRemove.add(key);
                exchange.setProperty(key, headers.get(key));
            }
        }

        for (String key: keysToRemove) {
            exchange.getIn().removeHeader(key);
        }
    }


    /**
     * after sending a call to an external system you might want to put your exchange in it previous state. this method will take all the properties that match
     * the header prefix and put them in the header.
     * @param exchange
     */
    public void propertiesToHeaders(Exchange exchange){
        logger.info("entered propertiesToHeaders");

        Map<String, Object> properties = exchange.getProperties();
        Set<String> keysToRemove = new HashSet<>();

        for (String key: properties.keySet() ) {
            if(key.contains(headerPrefix)){
                keysToRemove.add(key);
                exchange.getIn().setHeader(key, properties.get(key));
            }
        }

        for (String key: keysToRemove ) {
            exchange.removeProperties(key);
        }

    }

    /**
     * utility method used to log all properties in the exchange, since we don't have a way to do it using camel EL
     * @param exchange
     */
    public void loggAllExchangeProperties(Exchange exchange){
        Map<String, Object> properties = exchange.getProperties();
        logger.info("logging properties");
        for (String key: properties.keySet() ) {
            logger.info("property :  " + key + ", value : "+ properties.get(key));
        }
    }


    /**
     * Concatenates all steps stored in headers in only one message.
     * @param exchange
     */
    public void concatAllStepsToBody(Exchange exchange)
    {
        logger.info("entered concatAllStepsToBody");

        Map<String, Object> headers = exchange.getIn().getHeaders();
        StringBuilder builder = new StringBuilder();

        for (String key: headers.keySet() ) {
            if(key.contains(headerPrefix)){
                builder.append(headers.get(key)+":");
            }
        }
        builder.replace(builder.length()-1, builder.length(), "");
        exchange.getIn().setBody(builder.toString());
        logger.info("Body after concatenate : " + exchange.getIn().getBody());
    }


    public String getHeaderPrefix() {
        return headerPrefix;
    }

    public void setHeaderPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
    }
}
