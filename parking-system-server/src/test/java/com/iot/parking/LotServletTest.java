package com.iot.parking;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hamcrest.CoreMatchers;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class LotServletTest {

	@Test
	public void testLotRequest() throws ClientProtocolException, IOException {
		String url = "http://localhost:8082/api/lots";
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		
		HttpResponse response;
		response = client.execute(request);
		assertThat("Lots request", response.getStatusLine().getStatusCode(), CoreMatchers.is(200));
		
		
	}

}
