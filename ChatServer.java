import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private static ServerSocket s;

	public static void main(String[] args) {
		ArrayList<ChatHandler> AllHandlers = new ArrayList<ChatHandler>();
		try {
			
         ServerSocket s = new ServerSocket(4000);
			for (;;) {
				Socket incoming = s.accept();
				new ChatHandler(incoming, AllHandlers).start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}


class ChatHandler extends Thread {
	public ChatHandler(Socket i, ArrayList<ChatHandler> h) {
		incoming = i;
		handlers = h;
		handlers.add(this);
		try {
			in = new ObjectInputStream(incoming.getInputStream());
			out = new ObjectOutputStream(incoming.getOutputStream());
		} catch (IOException ioe) {
			System.out.println("Could not create streams.");
		}
	}

	public synchronized void broadcast_msg() {

		ChatHandler left = null;
		for (ChatHandler handler : handlers) {
			ChatMessage cm = new ChatMessage();
			cm.setMessage(name + ": " + myObject.getMessage());
			cm.setType("msg");
			try {
				handler.out.writeObject(cm);
				System.out.println("Writing to handler outputstream: " + name + cm.getMessage());
			} catch (IOException ioe) {
				// one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);

		if (myObject.getMessage().equals("bye")) { // my client wants to leave
			done = true;
			handlers.remove(this);
			System.out.println("Removed handler. Number of handlers: " + handlers.size());
		}
		System.out.println("Number of handlers: " + handlers.size());
	}

	public synchronized void broadcast_user() {

		if (myObject.getName().equals("Chat Room")) {
			for (ChatHandler handler : handlers) {

				ChatMessage cm = new ChatMessage();
				cm.setType("msg");
				cm.setName(this.name);
				cm.setMessage(myObject.getMessage());
				try {
					handler.out.writeObject(cm);
				} catch (IOException e) {
					
					e.printStackTrace();
				}

			}
			return;
		}

		for (ChatHandler handler : handlers) {

			if (myObject.getName().equals(handler.name)) {
				ChatMessage cm = new ChatMessage();
				cm.setType("msg");
				cm.setName(this.name);
				cm.setMessage(myObject.getMessage());
				try {
					handler.out.writeObject(cm);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}

		}
	}

	public synchronized void broadcast_draw() {

		for (ChatHandler handler : handlers) {

			ChatMessage cm = myObject;
			try {
				handler.out.writeObject(cm);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public synchronized void broadcast_command() {
		if (myObject.getMessage().equals("add")) {
			ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
			ChatMessage clear = new ChatMessage("command", "list", "clear");
			ChatMessage addmsg = new ChatMessage("msg", this.name, " has entered");
			for (ChatHandler handler : handlers) {
				ChatMessage cm = new ChatMessage();
				cm.setType("command");
				cm.setName(handler.name);
				cm.setMessage("This is name");
				messages.add(cm);
				try {
					handler.out.writeObject(addmsg);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			for (ChatHandler handler : handlers) {
				try {
					handler.out.writeObject(clear);
					for (ChatMessage msg : messages) {
						handler.out.writeObject(msg);
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		} else if (myObject.getMessage().equals("remove")) {
			done = true;
			handlers.remove(this);
			System.out.println("Removed handler. Number of handlers: " + handlers.size());

			ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
			ChatMessage clear = new ChatMessage("command", "list", "clear");
			ChatMessage addmsg = new ChatMessage("msg", this.name, "has exited");
			for (ChatHandler handler : handlers) {
				ChatMessage cm = new ChatMessage();
				cm.setType("command");
				cm.setName(handler.name);
				cm.setMessage("This is name");
				messages.add(cm);
				try {
					handler.out.writeObject(addmsg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (ChatHandler handler : handlers) {
				try {
					handler.out.writeObject(clear);
					for (ChatMessage msg : messages) {
						handler.out.writeObject(msg);
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
	}

	public void run() {
		try {
			while (!done) {
				myObject = (ChatMessage) in.readObject();
				System.out.println("Receive Msg");
				if (myObject.getType().equals("command")) {

					this.name = myObject.getName();
					broadcast_command();
					System.out.println(this.name);
				}
				if (myObject.getType().equals("msg")) {

					broadcast_user();
					System.out.println(this.name);
				}
				if (myObject.getType().equals("draw")) {

					broadcast_draw();
					System.out.println(this.name);
				}
				System.out.println("Message read: " + myObject.getMessage());
			}
		} catch (IOException e) {
			if (e.getMessage().equals("Connection reset")) {
				System.out.println("A client terminated its connection.");
			} else {
				System.out.println("Problem receiving: " + e.getMessage());
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe.getMessage());
		} finally {
			handlers.remove(this);
		}
	}

	ChatMessage myObject = null;
	private Socket incoming;

	boolean done = false;
	ArrayList<ChatHandler> handlers;
	String name;
   
	ObjectOutputStream out;
	ObjectInputStream in;
}
