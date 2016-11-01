package au.com.marlo.gateway.example.route;

import au.com.marlo.gateway.camel.processor.BodyToString;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderHandlerExampleRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HeaderHandlerExampleRouteBuilder.class);

    public static final String BEAN_NAME = "headerHandler" ;

    private BodyToString bodyToString = new BodyToString();

    @Override
    public void configure() throws Exception {

        logger.info("starting route");

        try {
            from("{{route1.from}}")
                    .transacted()
                    .log("body on Route1 : ${body} ")
                .bean(BEAN_NAME,"bodyToHeader")
                    .log("headers Route1 : ${headers}")
                .to("{{route1.to}}");

            from("{{route2.from}}")
                        .transacted()
                        .log("headers Route2 : ${headers} \n body on Route2 : ${body}")
                    .bean(BEAN_NAME,"lastHeaderToBody")
                        .log("body on Route2 : ${body} ")
                    .bean(BEAN_NAME,"headersToProperties")
                        .log("headers Route2 : ${headers} \n body on Route2 : ${body}")
                    .bean(BEAN_NAME,"loggAllExchangeProperties")
                    .to("{{route2.external.endpoint}}")
                    .process(bodyToString)
                    .doTry()
                        .to("validator:Response.xsd")
                            .bean(BEAN_NAME,"propertiesToHeaders")
                            .bean(BEAN_NAME,"bodyToHeader")
                            .log("headers Route2 : ${headers} \n body on Route2 : ${body}")
                            .bean(BEAN_NAME,"loggAllExchangeProperties")
                        .to("{{route2.last.endpoint")
                    .endDoTry()
                    .doCatch(ValidationException.class)
                        .log(LoggingLevel.ERROR, "${body}")
                        .to("{{route2.poison.queue}}")
                    .end();

            from("{{route3.from}}")
                    .transacted()
                        .bean(BEAN_NAME,"loggAllExchangeProperties")
                        .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                        .bean(BEAN_NAME,"lastHeaderToBody")
                        .log("body on Route3 : ${body} ")
                        .bean(BEAN_NAME,"headersToProperties")
                        .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                        .bean(BEAN_NAME,"loggAllExchangeProperties" )
                    .to("{{route3.external.endpoint}}")
                    .process(bodyToString)
                    .doTry()
                        .to("validator:Response.xsd")
                            .bean(BEAN_NAME,"propertiesToHeaders")
                            .bean(BEAN_NAME,"bodyToHeader")
                            .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                            .bean(BEAN_NAME,"loggAllExchangeProperties")
                        .to("{{route3.to}}")
                    .endDoTry()
                    .doCatch(ValidationException.class)
                        .log(LoggingLevel.ERROR, "${body}")
                        .to("{{route3.poison.queue}}")
                    .end();

            from("{{route4.from}}")
                    .transacted()
                    .log("headers on route 4: ${headers} \n body on route 3 : ${body}")
                    .bean(BEAN_NAME, "concatAllStepsToBody")
                    .to("{{route4.to}}");

            from("{{route5.from}}")
                    .to("{{route5.external.endpoint}}")
                    .process(bodyToString)
                    .setHeader("xpath_namespaces", constant("" +
                            "xt|http://www.marlo.com.au//trainning/xmltrainning;" +
                            "ord|http://www.marlo.com.au/trainning/xmltrainning/order;" +
                            "user|http://www.marlo.com.au/trainning/xmltrainning/user;" +
                            "prod|http://www.marlo.com.au/trainning/xmltrainning/product"))
                    .setHeader("xpath_query", constant("//ord:product"))
                    .log("body on Route5 : ${body} ")
                    .bean(BEAN_NAME,"bodyToHeader")
                    .to("{{route5.to}}");

        }catch(Exception exception){
            logger.error("error trying to create route");
            logger.error(exception.getMessage());
        }


    }


}
