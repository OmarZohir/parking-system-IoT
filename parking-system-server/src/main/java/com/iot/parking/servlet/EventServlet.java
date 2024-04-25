/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package com.iot.parking.servlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iot.parking.ParkingLot;
import com.iot.parking.utils.ParkingLotAction;

//Responsible for forwarding all updated to the web application
public class EventServlet extends EventSourceServlet implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(EventServlet.class);

	private final Gson gson;
	
	private Set<ParkinLotEventSource> eventSources = Collections
			.newSetFromMap(new ConcurrentHashMap<ParkinLotEventSource, Boolean>());


	public EventServlet(ParkingLot parkingLot) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		this.gson = gsonBuilder.create();
		
		parkingLot.addActionListener(this);  
	}
	
	public void actionPerformed(ActionEvent e){  
		LOG.info(e.toString());
		ParkingLotAction event = this.gson.fromJson(e.getActionCommand(), ParkingLotAction.class);
		this.sendEvent(event.getAction(), event.jsonData());
		LOG.info("Send data");
	}  

	private synchronized void sendEvent(String event, String data) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Dispatching {} event.", event);
		}

		for (ParkinLotEventSource eventSource : eventSources) {
			eventSource.sentEvent(event, data);
		}
	}


	@Override
	protected EventSource newEventSource(HttpServletRequest req) {
		return new ParkinLotEventSource();
	}

	private class ParkinLotEventSource implements EventSource {

		private Emitter emitter;

		@Override
		public void onOpen(Emitter emitter) throws IOException {
			this.emitter = emitter;
			eventSources.add(this);
		}

		@Override
		public void onClose() {
			eventSources.remove(this);
		}

		public void sentEvent(String event, String data) {
			try {
				emitter.event(event, data);
			} catch (IOException e) {
				e.printStackTrace();
				onClose();
			}
		}
	}
}
