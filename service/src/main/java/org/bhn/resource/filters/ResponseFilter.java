package org.bhn.resource.filters;


import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.CorsHeaders;
import org.keycloak.http.HttpRequest;
import org.keycloak.services.resources.Cors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;


@NoCache
@Provider
public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {

        UriInfo info = request.getUriInfo();
        String extName = info.getPathParameters().getFirst("extension");

        if (extName != null && extName.equals("user")) {

            String origin = request.getHeaderString(CorsHeaders.ORIGIN);
            String requestMethod = request.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
            String allowHeaders = request.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);

            CacheControl cacheControl = new CacheControl();
            cacheControl.setNoStore(true);
            cacheControl.setNoCache(true);
            cacheControl.setMustRevalidate(true);
            cacheControl.setProxyRevalidate(true);
            cacheControl.setMaxAge(0);
            cacheControl.setSMaxAge(0);
            response.getHeaders().add(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
            response.getHeaders().add(HttpHeaders.EXPIRES, 0);
            response.getHeaders().add(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
//            response.getHeaders().add(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD, requestMethod);
//            response.getHeaders().add(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
            response.getHeaders().add(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

//            Cors.add((HttpRequest) request, (Response.ResponseBuilder) response).allowAllOrigins().auth().build();
        }
    }
}