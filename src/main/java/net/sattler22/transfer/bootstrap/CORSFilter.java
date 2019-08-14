package net.sattler22.transfer.bootstrap;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Money Transfer Cross-Origin Resource Sharing (CORS) Filter
 *
 * @author Pete Sattler
 * @version August 2019
 */
public final class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (isPreflightRequest(request)) {
            LOGGER.info("Aborting CORS preflight request");
            request.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (isCrossOriginRequest(request)) {
            if (isOptionsRequest(request)) {
                LOGGER.info("CORS preflight request detected");
                response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
                response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            }
            else {
                LOGGER.info("CORS simple request detected");
            }
            response.getHeaders().add("Access-Control-Allow-Origin", "*");
        }
    }

    private static boolean isCrossOriginRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null;
    }

    private static boolean isOptionsRequest(ContainerRequestContext request) {
        return request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return isCrossOriginRequest(request) && isOptionsRequest(request);
    }
}
