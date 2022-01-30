package com.cloud.proxy.weatherapigateway.exception;

public class RouteNotFoundException extends RuntimeException{

    public RouteNotFoundException(final String path, final Throwable throwable){
        super(String.format("Route having the \"%s\" path has not been created or registered, hence cannot route there", path), throwable);
    }
}
