package com.cmpe281.restpackage;

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

import org.apache.http.client.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;

import java.util.*;
import java.io.*;

public class SimpleRestService extends ServerResource {
	protected static final String EXT_PORT  = ":8081";
	protected static final String INT_PORT  = ":8080";
	
	protected static final String FILLER_URL = "/restlet/service";
	protected static final String POST_INTERNAL_URL  = "/postInternal";
	protected static final String PUT_INTERNAL_URL  = "/putInternal";
	protected static final String DELETE_INTERNAL_URL  = "/deleteInternal";
	
	protected static final String MASTER_URL = "http://cmpe-281-shubhamvadhera.c9users.io";
	protected static final String SLAVE_1_URL = "http://cmpe-281-clone-1-shubhamvadhera.c9users.io";
	
	protected static boolean isSlave = false;
	private static final Logger logger = Logger.getLogger(SimpleRestService.class);
	 
	KeyValueStorage kvstorage = new KeyValueStorage() ;
/*      @Get
	public String represent() {
        return "hello, world";
    }*/
	@Get ("json")
	public String getSomething() throws JSONException {
		
		JSONObject json = new JSONObject();
		String request = getQuery().getValues("key");
		String result = null;
		String value = kvstorage.fetch(request);
		result = "Response from Restlet Webservice : " + value;
		System.out.println("Value for the key "+request+ " is "+value);
		json.put(request,value);
		json.put("IP",getCurrIp());
		JSONArray ja = new JSONArray();
		ja.put(json);
		System.out.println("JARRAY"+ja);
		String returnString = ja.toString();
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
	public String postSomething(Representation entity) throws ResourceException, IOException {
		String s = null;
		
	    if (entity.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) s = entity.getText();
	    else return "Invalid JSON format. Try again!";
	    
	    String[] data = s.split(":");
	    data[0] = data[0].replaceAll("[^a-zA-Z]","");
	    
	    if(isSlave) return postRedirect(data[0],data[1]);
	    
	    System.out.println("returning...." + s);
		kvstorage.store(data[0], data[1]);
		System.out.println("Saved the value in database: " + s);
		return s;
	}
	
	private String postRedirect (String key, String value) {
		String url = MASTER_URL + INT_PORT + FILLER_URL + POST_INTERNAL_URL;
		System.out.println ("Slave can't process post, redirecting to master: " + url);
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			
			StringEntity input = new StringEntity(key + ":" + value);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(postRequest);
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			String ret = "";
			System.out.println("Response from master:\n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				ret+=output;
			}

			httpClient.getConnectionManager().shutdown();
			ret = "Response from master: " + ret;
			
			return ret;
		} catch (Exception e) {
			System.out.println("EXCEPTION OCCURRED !");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return "EXCEPTION OCCURRED: " + e.getMessage();
		}
	}
	
	@Put
	public String putSomething(Representation entity) throws IOException {
		
		String value = entity.getText();
		System.out.println("Value "+value);
		String key = getQuery().getValues("newValue");
		System.out.println("key "+key);
		
		if(isSlave) return putRedirect(key,value);
		
		boolean status;
		status = kvstorage.update(key, value);
		
		if(status) return value+" successfully saved against the key "+key;
		
		else return "The value was not updated";		
	}
	
	
	private String putRedirect (String key, String value) {
		
		String url = MASTER_URL + INT_PORT + FILLER_URL + PUT_INTERNAL_URL;
		System.out.println ("Slave can't process post, redirecting to master: " + url);
		
			try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost putRequest = new HttpPost(url);
			
			StringEntity input = new StringEntity(key + ":" + value);
			input.setContentType("application/json");
			putRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(putRequest);
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			String ret = "";
			System.out.println("Response from master:\n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				ret+=output;
			}

			httpClient.getConnectionManager().shutdown();
			ret = "Response from master: " + ret;
			
			return ret;
		} catch (Exception e) {
			System.out.println("EXCEPTION OCCURRED !");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return "EXCEPTION OCCURRED: " + e.getMessage();
		}
	}

	@Delete
	public String deleteSomething(Representation entity) {
		String request;
		Form form = new Form(entity);
		request = getQuery().getValues("key");
        System.out.println("The request is "+request);
        if(isSlave) return deleteRedirect(request);
		kvstorage.delete(request);
		return "Succesfully deleted";
	}
	
	private String deleteRedirect (String key) {
		String url = MASTER_URL + INT_PORT + FILLER_URL + DELETE_INTERNAL_URL;
		System.out.println ("Slave can't process post, redirecting to master: " + url);
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost deleteRequest = new HttpPost(url);
			
			StringEntity input = new StringEntity(key);
			input.setContentType("application/json");
			deleteRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(deleteRequest);
			
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			String ret = "";
			System.out.println("Response from master:\n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				ret+=output;
			}

			httpClient.getConnectionManager().shutdown();
			ret = "Response from master: " + ret;
			
			return ret;
		} catch (Exception e) {
			System.out.println("EXCEPTION OCCURRED !");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			return "EXCEPTION OCCURRED: " + e.getMessage();
		}
		
	}
}
