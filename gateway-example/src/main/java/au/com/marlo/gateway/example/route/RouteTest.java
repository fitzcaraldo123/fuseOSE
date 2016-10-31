package au.com.marlo.gateway.example.route;

import org.apache.camel.builder.RouteBuilder;

public class RouteTest extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("activemq:TEST")
                .to("validator:Response.xsd");
    }
}