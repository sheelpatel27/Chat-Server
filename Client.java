import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class Client extends Thread implements ActionListener {

	ChatMessage myObject;
	boolean sendingdone = false, receivingdone = false;
	Scanner scan;
	Socket socketToServer;  
	ObjectOutputStream myOutputStream;
	ObjectInputStream myInputStream;
	Frame f;
	static TextField tf;
	static TextArea ta;
   boolean startflag = false;
	
	String login;
	Button connect;
	Button disconnect;
	static java.awt.List list;

	ReceiverThread receive;
	static boolean threadflag = false;

	public Client() {
		f = new Frame();
		f.setSize(1000, 500);
		f.setLocationRelativeTo(null);
		f.setTitle("My Chat/Whiteboard Frame");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});


		Panel button = new Panel();
      Panel chatPanel = new Panel();
      Panel whiteboard = new Panel();
      Panel buttonPanel = new Panel();
			
		Panel users = new Panel();
		users.setLayout(new BorderLayout());
		list = new java.awt.List();
		users.add(list, BorderLayout.WEST);
  
		chatPanel.setLayout(new BorderLayout());
		tf = new TextField();
		tf.addActionListener(this);
		ta = new TextArea();
		chatPanel.add(tf, BorderLayout.NORTH);
		chatPanel.add(ta, BorderLayout.CENTER);
		users.add(chatPanel, BorderLayout.CENTER);


		connect = new Button("Connect");
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		disconnect = new Button("Disconnect");
		disconnect.setActionCommand("disconnect");
		disconnect.addActionListener(this);
		disconnect.setEnabled(false);
		button.add(connect);
		button.add(disconnect);
		f.add(button, BorderLayout.SOUTH);

		
		whiteboard.setLayout(new BorderLayout());
		board = new Panel();
		board.setMinimumSize(new Dimension(200,200));
		board.setPreferredSize(new Dimension(500,500));
		board.setEnabled(false);
           
      whiteboard.add(board, BorderLayout.CENTER);
		Button red = new Button("Red");
		red.setActionCommand("red");
		red.addActionListener(this);

		Button green = new Button("Green");
		green.setActionCommand("green");
		green.addActionListener(this);

		Button blue = new Button("Blue");
		blue.setActionCommand("blue");
		blue.addActionListener(this);

		Button cyan = new Button("Cyan");
		cyan.setActionCommand("cyan");
		cyan.addActionListener(this);

		Button magenta = new Button("Magenta");
		magenta.setActionCommand("magenta");
		magenta.addActionListener(this);

		buttonPanel.add(red);
		buttonPanel.add(green);
		buttonPanel.add(blue);
		buttonPanel.add(cyan);
		buttonPanel.add(magenta);
		whiteboard.add(buttonPanel, BorderLayout.SOUTH);
		f.add(whiteboard, BorderLayout.EAST);

		f.add(users);
		f.setVisible(true);
      
		board.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent me) {	
				Point p = new Point();
				p.x = me.getX();
				p.y = me.getY();
				draw.add(p);
				drawtmp();	
			}

			public void mouseMoved(MouseEvent me) {
			}
		});

		board.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent me) {}
			public void mouseEntered(MouseEvent me) {}
			public void mouseExited(MouseEvent me) {}	
			public void mousePressed(MouseEvent me) {
				draw = new ArrayList<Point>();
				Point p = new Point();
				p.x = me.getX();
				p.y = me.getY();
				draw.add(p);
			}
	
			public void mouseReleased(MouseEvent me) {
				Point p = new Point();
				p.x = me.getX();
				p.y = me.getY();
				draw.add(p);
				drawcolor.add(drawColor);
				drawobject.add(draw);
				senddraw(drawobject, drawcolor);
			}
		});	
	}

	public void actionPerformed(ActionEvent ae) {
   if (ae.getActionCommand().equals("red")) {
			drawColor = Color.red;
		}

		if (ae.getActionCommand().equals("green")) {
			drawColor = Color.green;
		}

		if (ae.getActionCommand().equals("blue")) {
			drawColor = Color.blue;
		}

		if (ae.getActionCommand().equals("cyan")) {
			drawColor = Color.cyan;
		}

		if (ae.getActionCommand().equals("magenta")) {
			drawColor = Color.magenta;
		}


		if (ae.getSource() == connect) {
			if (tf.getText() == null) {}
			try {
				socketToServer = new Socket("127.0.0.1", 4000);
				myOutputStream = new ObjectOutputStream(socketToServer.getOutputStream());
				myInputStream = new ObjectInputStream(socketToServer.getInputStream());

				if (!startflag) {
					receive = new ReceiverThread(myInputStream, this);
					receive.start();
					startflag = true;
				}
				login = tf.getText();
				ChatMessage msg = new ChatMessage("command", login, "add");
				myOutputStream.writeObject(msg);

				disconnect.setEnabled(true);
				connect.setEnabled(false);
				board.setEnabled(true);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		if (ae.getSource() == disconnect) {
			try {
				ChatMessage msg = new ChatMessage("command", login, "remove");
				myOutputStream.writeObject(msg);
				try {
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
						
				startflag = false;
				connect.setEnabled(true);
				disconnect.setEnabled(false);
				board.setEnabled(false);

				list.removeAll();
				ta.setText(null);
				ta.repaint();
				f.repaint();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		if (ae.getSource().equals(tf)) {
			String str = list.getSelectedItem();
			if (str != null) {
				myObject = new ChatMessage();
				myObject.setType("msg");
				myObject.setName(str);
				myObject.setMessage(tf.getText());
            if (list.getSelectedItem() .equals(null)) {
					ta.append("");
				} else {
					ta.append(list.getSelectedItem() + ":" + tf.getText() + "\n");
				}
				

				tf.setText("");
				try {
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			} else {
				myObject = new ChatMessage();
				myObject.setType("msg");
				myObject.setName("Chat Room");
				myObject.setMessage(tf.getText());
				
				tf.setText("");
				try {
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		}
}

	Graphics g;
	Panel board;
	ArrayList<ArrayList<Point>> drawobject = new ArrayList<ArrayList<Point>>();
	ArrayList<Point> draw = new ArrayList<Point>();
	ArrayList<Color> drawcolor = new ArrayList<Color>();
	Color drawColor = Color.black;
   
	public void drawPanel() {
		g = board.getGraphics();
		g.clearRect(0, 0, board.getWidth(), board.getHeight());
		int indexcolor = 0;
		for (ArrayList<Point> draw : drawobject) {
			g.setColor(drawcolor.get(indexcolor));
			int i = 0;
			Point prev = new Point();
			for (Point p : draw) {
				i++;
				if (i > 1) {
					g.drawLine(prev.x, prev.y, p.x, p.y);
				}
				prev.x = p.x;
				prev.y = p.y;
			}
			indexcolor++;
		}
	}

	public void drawtmp() {
		g = board.getGraphics();
		g.setColor(drawColor);
		int i = 0;
		Point prev = new Point();
		for (Point p : draw) {
			i++;
			if (i > 1) {
				g.drawLine(prev.x, prev.y, p.x, p.y);
			}
			prev.x = p.x;
			prev.y = p.y;
		}

	}

	public void senddraw(ArrayList<ArrayList<Point>> obj, ArrayList<Color> color) {
		ChatMessage msg = new ChatMessage("draw", "", "");
		msg.setDrawobject(obj);
		msg.setDrawcolor(color);
		try {
			myOutputStream.writeObject(msg);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Listening for messages from server . . . ");
		try {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			while (!receivingdone) {

				myObject = (ChatMessage) myInputStream.readObject();
				ta.append(myObject.getMessage() + "\n");

			}
		} catch (IOException ioe) {
			System.out.println("IOE: " + ioe.getMessage());
		} catch (ClassNotFoundException cnf) {
			System.out.println(cnf.getMessage());
		}
	}

	public static void main(String[] arg) {
		new Client();
	}
}

class ReceiverThread extends Thread {

	private ObjectInputStream in;
	Client client;

	public ReceiverThread(ObjectInputStream in, Client client) {
		this.in = in;
		this.client = client;
	}
	
	public void run() {
		while (!Client.threadflag) {
			Object tmp = null;
			try {
				tmp = in.readObject();

			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

			if (tmp == null) {
				try {
					Thread.sleep(1);
					continue;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			ChatMessage msg = new ChatMessage();
			msg = (ChatMessage) tmp;
			if (msg.getType().equals("command")) {
				if (msg.getName().equals("list") && msg.getMessage().equals("clear")){
					Client.list.removeAll();
					Client.list.add("Chat Room");
				}
					
				if (msg.getMessage().equals("This is name"))
					Client.list.add(msg.getName());
			} else if (msg.getType().equals("msg")) {
				Client.ta.append(msg.getName() + ": " + msg.getMessage() + "\n");
			} else if (msg.getType().equals("draw")) {
				System.out.println(msg.getDrawobject().size());
				client.drawobject = new ArrayList<ArrayList<Point>>(msg.getDrawobject());
				client.drawcolor = new ArrayList<Color>(msg.getColor());
				client.drawPanel();
			}

		}
		Client.threadflag = false;
	}
}
