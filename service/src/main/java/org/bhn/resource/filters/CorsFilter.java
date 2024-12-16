package org.bhn.resource.filters;

import org.jboss.resteasy.spi.CorsHeaders;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter {


    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        if (containerRequestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            Response.ResponseBuilder builder = Response.ok();
            String requestMethods = containerRequestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
            String allowHeaders = containerRequestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            String origin = containerRequestContext.getHeaderString(CorsHeaders.ORIGIN);
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            containerRequestContext.abortWith(builder.build());
        }
    }
}
