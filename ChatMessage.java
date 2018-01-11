import java.io.*;

public class ChatMessage implements Serializable {
	
	public String name;
	public String message;
	public String type;
	public java.util.ArrayList<java.util.ArrayList<java.awt.Point>>
   drawobject = new java.util.ArrayList<java.util.ArrayList<java.awt.Point>>();
	public java.util.ArrayList<java.awt.Color> drawcolor;
	
	public ChatMessage() {}
	public ChatMessage(String type, String name, String message) {
		setType(type);
		setName(name);
		setMessage(message);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public void setDrawobject(java.util.ArrayList<java.util.ArrayList<java.awt.Point>> drawobject){
		this.drawobject=new java.util.ArrayList<java.util.ArrayList<java.awt.Point>>(drawobject);
	}
	
	public java.util.ArrayList<java.util.ArrayList<java.awt.Point>> getDrawobject(){
		return drawobject;
	}
	
	public void setDrawcolor(java.util.ArrayList<java.awt.Color> arr){
		this.drawcolor=new java.util.ArrayList<java.awt.Color>(arr);
	}
	
	public java.util.ArrayList<java.awt.Color> getColor(){
		return this.drawcolor;
	}
}
