package com.cmpe281.restpackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PartitionDetectionRequestHandler {

	ArrayList<RequestPojo> rpList;
	String slaveNodeURL;

	public final String SUCCESSFUL_QUEUE_EXECUTION = "Successfully executed the queue.";

	// String url = SLAVE_URL + INT_PORT + FILLER_URL + POST_INTERNAL_URL;
	public PartitionDetectionRequestHandler(String slaveUrl, String intPort, String fillerURL, String internalURL) {

		// initializing url for slave.

		this.slaveNodeURL = slaveUrl + intPort + fillerURL + internalURL;
	}

	public boolean isInPartition() {
		boolean partitionFlag = false;

		// Logic which will check for node's partition.

		return partitionFlag;
	}

	public void generateQueue(RequestPojo requestPojo) {
		if (rpList == null)
			rpList = new ArrayList<RequestPojo>();

		rpList.add(requestPojo);
	}

	public String executeRequestQueue() {

		if (!rpList.isEmpty()) {

			for (RequestPojo requestPojo : rpList) {
				// execute requests.
				if (requestPojo.getRequestType().equalsIgnoreCase("post")) {
					executePost(requestPojo);
				} else if (requestPojo.getRequestType().equalsIgnoreCase("get")) {
					executeGet(requestPojo);
				} else if (requestPojo.getRequestType().equalsIgnoreCase("put")) {
					executePut(requestPojo);
				} else if (requestPojo.getRequestType().equalsIgnoreCase("delete")) {
					executeDelete(requestPojo);
				} else {
					//return "Bad Request";
				}
				rpList.remove(requestPojo);
			}
			System.out.println("Queue exection has been finished.");
		}else{
			System.out.println("Empty Queue");
			return "Queue is Empty.";
		}

		return SUCCESSFUL_QUEUE_EXECUTION;

	}

	public void executePost(RequestPojo request) {

		BufferedReader br;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(slaveNodeURL); // creates post
																// object for
																// slave using
																// slaveNodeURL

			StringEntity input = new StringEntity(request.getRequestKey() + ":" + request.getRequestValue());
			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			String ret = "";
			System.out.println("Response from master:\n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				ret += output;
			}

			httpClient.getConnectionManager().shutdown();
			ret = "Response from master: " + ret;

			return ret;
		} catch (Exception e) {
			System.out.println("EXCEPTION OCCURRED !");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			//return "EXCEPTION OCCURRED: " + e.getMessage();
		} finally {
			br.close();
		}

	}

	public void executeGet(RequestPojo request) {

	}

	public void executePut(RequestPojo request) {

	}

	public void executeDelete(RequestPojo request) {

	}

}
