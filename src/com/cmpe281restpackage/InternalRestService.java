package com.cmpe281restpackage;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;

import com.cmpe281.paulpackage.*;

//import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import org.restlet.representation.Representation;
import org.restlet.*;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternalRestService extends ServerResource {
	//protected static boolean isSlave = false;
	private static final Logger logger = Logger.getLogger(SimpleRestService.class);
	
	KeyValueStorage kvstorage = new KeyValueStorage() ;
	 
	@Get ("json")
	public String getInternal() throws JSONException {
		
		JSONObject json = new JSONObject();
		json.put("Internal Get: IP:",getCurrIp());
		String returnString = json.toString();
		System.out.println("returnString "+returnString);
		return returnString;
		
	}
	
	private String getCurrIp () {
		InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
                return e.getMessage();
        }
        return ip.toString();
	}
	
	@Post
	public String postInternal(Representation entity) throws ResourceException, IOException {
		String s = entity.getText();
	    System.out.println("returning...." + s);
	    
		String[] data = s.split(":");
		//data[0] = data[0].replaceAll("[^a-zA-Z]","");
		kvstorage.store(data[0], data[1]);
		return s;
	}
	
	@Put
	public String putInternal(Representation entity) throws ResourceException, IOException {
		String value = entity.getText();
		System.out.println("Value "+value);
		String key = getQuery().getValues("newValue");
		System.out.println("key "+key);
		boolean status;
		status = kvstorage.update(key, value);
		if(status) return value+" successfully saved against the key "+key;
		else return "The value was not updated";	
	}
	
	
	@Delete
	public String deleteInternal(Representation entity) throws ResourceException, IOException {
		String request;
		Form form = new Form(entity);
		request = getQuery().getValues("key");
        System.out.println("The request is "+request);
       	//boolean delStatus;
		kvstorage.delete(request);
		return "success";
		//else return "The delete operation was unsuccessful";
	}
}
