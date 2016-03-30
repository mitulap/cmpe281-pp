package com.cmpe281restpackage;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class SimpleRestServiceApplication {
    
    static Router routerEx;
    static Router routerIn;

public static void main(String[] args) throws Exception {
    
    String type = args[0];
    System.out.println(type);
    if (type.equals("slave")) SimpleRestService.isSlave = true;
    else if (type.equals("master"));
    else {
        System.out.println("Invalid argument: " + type);
        return;
    }
        
    routerEx = new Router();
    routerEx.attach("/service", SimpleRestService.class);
    routerEx.attach("/service/{request}", SimpleRestService.class);
    
    routerIn = new Router();
    routerIn.attach("/service", InternalRestService.class);
    routerIn.attach("/service/{request}", InternalRestService.class);
    
    Application appEx = new Application() {
        @Override
        public synchronized Restlet createInboundRoot() {
            routerEx.setContext(getContext());
            return routerEx;
        };
    };
    
    Application appIn = new Application() {
        @Override
        public synchronized Restlet createInboundRoot() {
            routerIn.setContext(getContext());
            return routerIn;
        };
    };
    
    Component componentEx = new Component();
    componentEx.getDefaultHost().attach("/restlet", appEx);
    
    Component componentIn = new Component();
    componentIn.getDefaultHost().attach("/restlet", appIn);

    new Server(Protocol.HTTP, 8081, componentIn).start();
    new Server(Protocol.HTTP, 8082, componentEx).start();
    
    }

}

