package au.com.marlo.gateway.example.route;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderHandlerExampleRouteBuilder extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HeaderHandlerExampleRouteBuilder.class);

    public static final String BEAN_NAME = "headerHandler" ;

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
                    .to("validator:Response.xsd")
                        .bean(BEAN_NAME,"propertiesToHeaders")
                        .bean(BEAN_NAME,"bodyToHeader")
                        .log("headers Route2 : ${headers} \n body on Route2 : ${body}")
                        .bean(BEAN_NAME,"loggAllExchangeProperties")
                    .to("{{route2.last.endpoint");


            from("{{route3.from}}")
                    .transacted()
                        .bean(BEAN_NAME,"loggAllExchangeProperties")
                        .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                        .bean(BEAN_NAME,"lastHeaderToBody")
                        .log("body on Route3 : ${body} ")
                        .bean(BEAN_NAME,"headersToProperties")
                        .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                        .bean(BEAN_NAME,"loggAllExchangeProperties")
                    .to("{{route3.external.endpoint}}")
                    .to("validator:Response.xsd")
                        .bean(BEAN_NAME,"propertiesToHeaders")
                        .bean(BEAN_NAME,"bodyToHeader")
                        .log("headers Route3 : ${headers} \n body on Route3 : ${body}")
                        .bean(BEAN_NAME,"loggAllExchangeProperties")                    
                    .to("{{route3.to}}");

            from("{{route4.from}}")
                    .transacted()
                    .log("headers on route 4: ${headers} \n body on route 3 : ${body}")
                    .bean(BEAN_NAME, "concatAllStepsToBody")
                    .to("{{route4.to}}");

        }catch(Exception exception){
            logger.error("error trying to create route");
            logger.error(exception.getMessage());
        }


    }


}
