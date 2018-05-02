import java.io.IOException;
import java.nio.ByteBuffer;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/websocketserver")
public class WebSocketEndpoint {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Session with id: "+ session.getId() +" has started");
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("Session with id: "+ session.getId() +" has closed");
		try {
			for(Session oneSession : session.getOpenSessions()) {
				if(oneSession.isOpen() && !oneSession.getId().equals(session.getId())) {
					oneSession.getBasicRemote().sendBinary(ByteBuffer.allocate(5), true);
				}
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	@OnError
	public void onError(Throwable error) {
		System.out.println("Error: "+ error.getMessage());
	}
	
	@OnMessage
	public void onMessage(ByteBuffer message, Boolean isLast, Session session) {
		try {
			for(Session oneSession : session.getOpenSessions()) {
				if(oneSession.isOpen() && !oneSession.getId().equals(session.getId())) {
					oneSession.getBasicRemote().sendBinary(message, isLast);					
				}
			}
			System.out.println("Binary last: "+isLast+" from session with id: "+session.getId());
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			for (Session oneSession : session.getOpenSessions()) {
				if (oneSession.isOpen() && !oneSession.getId().equals(session.getId())) {
				oneSession.getBasicRemote().sendText(message);
				}
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}
}
