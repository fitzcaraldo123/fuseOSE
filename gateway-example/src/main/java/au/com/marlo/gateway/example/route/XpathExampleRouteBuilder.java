package au.com.marlo.gateway.example.route;

import au.com.marlo.gateway.camel.processor.BodyToString;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route builder with xpath filtering on body
 * before sending to the exchange header.
 *
 * Created by isilva on 01/11/16.
 */
public class XpathExampleRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(XpathExampleRouteBuilder.class);

    public static final String BEAN_NAME = "headerHandler" ;

    private BodyToString bodyToString = new BodyToString();

    /**
     * Configures the route.
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        try {

            from("{{route5.from}}")
                    .transacted()
                    .log("Entering route 5...")
                    .to("{{route5.external.endpoint}}")
                    .process(bodyToString)
                    // The namespaces must defined with prefix|uri;prefix|uri
                    .setHeader("xpath_namespaces", constant("" +
                            "xt|http://www.marlo.com.au//trainning/xmltrainning;" +
                            "ord|http://www.marlo.com.au/trainning/xmltrainning/order;" +
                            "user|http://www.marlo.com.au/trainning/xmltrainning/user;" +
                            "prod|http://www.marlo.com.au/trainning/xmltrainning/product"))
                    .setHeader("xpath_query", constant("//ord:product"))
                    .log("body on Route5 : ${body} ")
                    .bean(BEAN_NAME, "bodyToHeader")
                    .to("{{route5.to}}");
        }
        catch(Exception exception){
            logger.error("error trying to create route");
            logger.error(exception.getMessage());
        }

    }
}
