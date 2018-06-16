package org.revault.moneytransfer;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.revault.moneytransfer.configure.ApplicationBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.net.URI;

/**
 * MoneyTransferApp class.
 *
 */
public class MoneyTransferApp {
    private final static Logger LOGGER = LoggerFactory.getLogger(MoneyTransferApp.class);
    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI = "http://localhost:8080/moneytransfer/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    private static HttpServer startServer() {
        // init logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        // create a resource config that scans for JAX-RS resources and providers
        // in org.revault.moneytransfer package
        final ResourceConfig rc = new ResourceConfig().packages("org.revault.moneytransfer");
        rc.register(new ApplicationBinder());


        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * MoneyTransferApp method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args){
        final HttpServer server = startServer();
        try {
            server.start();
            LOGGER.info("Jersey app started with WADL available at {}application.wadl.\n" +
                    "Hit enter to stop it...", BASE_URI);
            System.in.read();
        } catch (IOException e) {
            LOGGER.error("Error starting server: {}", e.getMessage(), e);
        }

        if (server.isStarted()) {
            server.shutdownNow();
        }
    }
}

