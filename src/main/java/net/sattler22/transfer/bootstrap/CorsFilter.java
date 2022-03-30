package net.sattler22.transfer.bootstrap;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

/**
 * Money Transfer Cross-Origin Resource Sharing (CORS) Filter
 *
 * @author Pete Sattler
 * @version August 2019
 */
public final class CorsFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (isCrossOriginRequest(request)) {
            if (isOptionsRequest(request)) {
                logger.info("CORS preflighted request detected");
                response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                response.getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, If-Match");
                response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            }
            else
                logger.info("CORS simple request detected");
            final var origin = request.getHeaderString("Origin");
            response.getHeaders().add("Access-Control-Allow-Origin", origin);
        }
    }

    private static boolean isCrossOriginRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null;
    }

    private static boolean isOptionsRequest(ContainerRequestContext request) {
        return request.getMethod().equalsIgnoreCase("Options");
    }
}
