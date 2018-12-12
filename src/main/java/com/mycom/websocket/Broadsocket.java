package com.mycom.websocket;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/chatting")
public class Broadsocket {

	private final Logger logger = LoggerFactory.getLogger(Broadsocket.class);
	private static Set<Session> clients = Collections
			.synchronizedSet(new HashSet<Session>());

	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println("입력된 메시지"+message);
		synchronized (clients) {
			for (Session client : clients) {
				if (!client.equals(session)) {
					client.getBasicRemote().sendText(message);
					
					Thread thread = new Thread() {
						@Override
						public void run() {
								try {
									Thread.sleep(10000);
									sendMessage("welcome~~~");
								}catch(InterruptedException e) {
									e.printStackTrace();
								}
							}
					};
					thread.start();
					
				}
			}
		}
	}

	public void sendMessage(String message) {
		for (Session session : this.clients) {
			if(session.isOpen()) {
				try {
					session.getBasicRemote().sendText(message);
				}catch(Exception e) {
					this.logger.error("에러에요오오오오오옹",e);
				}
			}
		}
	}
	
	@OnOpen
	public void onOpen(Session session) {
		// Add session to the connected sessions set
		System.out.println(session);
		clients.add(session);
		sendMessage("입장함");
		//======
		Thread thread = new Thread() {
			@Override
			public void run() {
					try {
						Thread.sleep(10000);
						sendMessage("welcome~~~");
					}catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
		};
		thread.start();
		//======
	}

	@OnClose
	public void onClose(Session session) {
		// Remove session from the connected sessions set
		clients.remove(session);
	}
}