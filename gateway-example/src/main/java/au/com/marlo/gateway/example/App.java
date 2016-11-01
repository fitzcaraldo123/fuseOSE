package au.com.marlo.gateway.example;


import au.com.marlo.gateway.camel.bean.HeaderHandlerBean;
import au.com.marlo.gateway.example.route.HeaderHandlerExampleRouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.main.Main;

public class App {

    private static Main main;

    public static void main(String[] args) throws Exception {
        // create a Main instance
        main = new Main();
        // add routes
//        main.addRouteBuilder(new HeaderHandlerExampleRouteBuilder());
        // add event listener
//        main.addMainListener(new Events());

        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("au.com.marlo.gateway.example.cfg");
        main.bind("properties", pc);

//        HeaderHandlerBean bean = new HeaderHandlerBean("x_step_");
//        main.bind(HeaderHandlerExampleRouteBuilder.BEAN_NAME, bean);
//        main.addRouteBuilder(new HeaderHandlerExampleRouteBuilder());

        // run until you terminate the JVM
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

}
