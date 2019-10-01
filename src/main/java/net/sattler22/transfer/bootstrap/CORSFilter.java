package net.sattler22.transfer.bootstrap;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Money Transfer Cross-Origin Resource Sharing (CORS) Filter
 *
 * @author Pete Sattler
 * @version September 2019
 */
public final class CORSFilter implements ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (isCrossOriginRequest(request)) {
            if (isOptionsRequest(request)) {
                LOGGER.info("CORS preflighted request detected");
                response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                response.getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, If-Match");
                response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            }
            else {
                LOGGER.info("CORS simple request detected");
            }
            final String origin = request.getHeaderString("Origin");
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
