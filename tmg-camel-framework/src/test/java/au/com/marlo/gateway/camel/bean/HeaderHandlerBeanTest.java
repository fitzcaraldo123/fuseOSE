package au.com.marlo.gateway.camel.bean;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Test set for HeaderHandlerBean.
 * Created by isilva on 25/10/16.
 */
public class HeaderHandlerBeanTest {
    private HeaderHandlerBean bean = new HeaderHandlerBean("x_step_");
    private CamelContext ctx = new DefaultCamelContext();
    private Exchange exchange;

    /**
     * Create exchange to be used in tests.
     */
    @Before
    public void setUp()
    {
        this.exchange = new DefaultExchange(ctx);
    }

    /**
     * Tests sending an existent body to a step header.
     */
    @Test
    public void test_body_to_header()
    {
        exchange.getIn().setBody("TEST");
        bean.bodyToHeader(exchange);
        Assert.assertEquals(exchange.getIn().getHeader(bean.getHeaderPrefix() + "1"), "TEST");
        Assert.assertEquals(exchange.getIn().getBody(), "");
    }

    /**
     * Tests recovering the most updated header to body.
     */
    @Test
    public void test_last_header_to_body() throws Exception {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.put(bean.getHeaderPrefix() + "1", "FIRST STATE");
        headers.put(bean.getHeaderPrefix() + "2", "UPDATED STATE");
        exchange.getIn().setHeaders(headers);

        bean.lastHeaderToBody(exchange);

        Assert.assertEquals(exchange.getIn().getBody(), "UPDATED STATE");
        Assert.assertNotNull(bean.getHeaderPrefix() + "1");
        Assert.assertNotNull(bean.getHeaderPrefix() + "2");
    }

    /**
     * Tests the steps header are not overwritten.
     */
    @Test
    public void test_step_header_counting()
    {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.put(bean.getHeaderPrefix() + "1", "FIRST STATE");
        exchange.getIn().setHeaders(headers);
        exchange.getIn().setBody("TEST");

        bean.bodyToHeader(exchange);

        Assert.assertEquals(exchange.getIn().getBody(), "");
        Assert.assertEquals("FIRST STATE", exchange.getIn().getHeader(bean.getHeaderPrefix() + "1") );
        Assert.assertEquals("TEST", exchange.getIn().getHeader(bean.getHeaderPrefix() + "2"));
    }

    /**
     * Tests the headers are copied to properties.
     */
    @Test
    public void test_headers_to_properties()
    {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.put(bean.getHeaderPrefix() + "1", "FIRST STATE");
        headers.put(bean.getHeaderPrefix() + "2", "UPDATED STATE");
        exchange.getIn().setHeaders(headers);

        bean.headersToProperties(exchange);

        Assert.assertEquals("FIRST STATE", exchange.getProperty(bean.getHeaderPrefix() + "1") );
        Assert.assertEquals("UPDATED STATE", exchange.getProperty(bean.getHeaderPrefix() + "2"));

        // The headers must be deleted if sent to properties
        Assert.assertNull(exchange.getIn().getHeader(bean.getHeaderPrefix() + "1") );
        Assert.assertNull(exchange.getIn().getHeader(bean.getHeaderPrefix() + "2"));
    }

    /**
     * Tests the properties are copied to headers.
     */
    @Test
    public void test_properties_to_headers()
    {
        exchange.setProperty(bean.getHeaderPrefix() + "1", "FIRST STATE");
        exchange.setProperty(bean.getHeaderPrefix() + "2", "UPDATED STATE");

        bean.propertiesToHeaders(exchange);

        Assert.assertEquals("FIRST STATE", exchange.getIn().getHeader(bean.getHeaderPrefix() + "1") );
        Assert.assertEquals("UPDATED STATE", exchange.getIn().getHeader(bean.getHeaderPrefix() + "2"));

        // The properties must be deleted if sent to headers
        Assert.assertNull(exchange.getProperty(bean.getHeaderPrefix() + "1") );
        Assert.assertNull(exchange.getProperty(bean.getHeaderPrefix() + "2"));
    }

    /**
     * Tests the step headers are concatenated properly.
     */
    @Test
    public void test_concatenate_all_steps()
    {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.put(bean.getHeaderPrefix() + "1", "FIRST STATE");
        headers.put(bean.getHeaderPrefix() + "2", "UPDATED STATE");
        exchange.getIn().setHeaders(headers);

        bean.concatAllStepsToBody(exchange);

        Assert.assertEquals(exchange.getIn().getBody(), "FIRST STATE"+":"+"UPDATED STATE");
    }


}
