package org.revault.moneytransfer.configure;

import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
    	register(new ApplicationBinder());
    	
        packages(true, "ort.revault.moneytransfer.api");
    }
}