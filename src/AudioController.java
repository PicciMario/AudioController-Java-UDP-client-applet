import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * An applet/application to provide a User Interface to control a device
 * (had to be an audio device) via UDP packets.
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 * @version 0.1
 *
 */
public class AudioController extends JApplet implements Runnable, ActionListener{
	
	/** Class version (for Applet Serialization purposes) */
	private static final long serialVersionUID = 1L;
	
	/** Thread object */
	Thread clockThread;
	
	JLabel footer, header;
	
	JMenuBar menuBar;
	JMenu file;
	JMenuItem menuEsci, menuAbout;
	
	JTextField ipAddress;
	JTextField portNumber;
	
	ImageIcon bullet_red, bullet_black, bullet_green, bullet_yellow;
	
	
	/**
	 * Initialization of the Applet.
	 */
	public void init(){
		Console.log("Applet started - method init().");
		
		Console.log("Loading images...");
		Image image;
		try {
			image = getImage(new URL(getCodeBase(), "rect_red.png"));
			bullet_red = new ImageIcon(image);
			image = getImage(new URL(getCodeBase(), "rect_green.png"));
			bullet_green = new ImageIcon(image);
			image = getImage(new URL(getCodeBase(), "rect_yellow.png"));
			bullet_yellow = new ImageIcon(image);
			image = getImage(new URL(getCodeBase(), "rect_black.png"));
			bullet_black = new ImageIcon(image);		
		} catch (Exception e) {
			Console.log("Unable to load images..");
			Console.log(e.toString());
		}
		
		/* --------------------------------------------------
		 * Main container
		 * -------------------------------------------------- */
		BorderLayout frame = new BorderLayout();
		setLayout(frame);

		/* --------------------------------------------------
		 * Header (title area)
		 * -------------------------------------------------- */
		header = new JLabel("AudioController");
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(new Font("", Font.BOLD, 20));
		add(header, BorderLayout.NORTH);
		
		/* --------------------------------------------------
		 * Right vertical bar
		 * -------------------------------------------------- */
		JPanel barraLaterale = new JPanel(new GridBagLayout());
		GridBagConstraints constr = new GridBagConstraints();
		
		constr.gridx = 0;
		constr.gridy = 0;
		barraLaterale.add(new JLabel("Options"), constr);
		
		constr.gridx = 0;
		constr.gridy = 1;
		Pulsante pulsante1 = new Pulsante("pulsante1", (byte)1, this);
		barraLaterale.add(pulsante1, constr);
		
		constr.gridx = 0;
		constr.gridy = 2;
		Pulsante pulsante2 = new Pulsante("pulsante2", (byte)2, this);
		barraLaterale.add(pulsante2, constr);
		
		constr.gridx = 0;
		constr.gridy = 3;
		Pulsante pulsante3 = new Pulsante("pulsante3", (byte)3, this);
		barraLaterale.add(pulsante3, constr);
		
		this.add(barraLaterale, BorderLayout.EAST);
		
		/* --------------------------------------------------
		 * Tabbed pane (main area)
		 * -------------------------------------------------- */
		
		//------ PANE 1 -------------------------------------
		
		JPanel tab1 = new JPanel(new GridLayout(1,0));
		tab1.add(new Fader("con1", (byte)1, this));
		tab1.add(new Fader("con2", (byte)2, this));
		tab1.add(new Fader("con3", (byte)3, this));
		tab1.add(new Fader("con4", (byte)4, this));
		tab1.add(new Fader("con5", (byte)5, this));
		tab1.add(new Fader("con6", (byte)6, this));
		
		//------ PANE 2 -------------------------------------
		
		JPanel tab2 = new JPanel(new GridLayout(1,0));
		tab2.add(new Fader("con1", (byte)1, this));
		tab2.add(new GraphicLevel("mille1", 20, this));
		tab2.add(new GraphicLevel("mille2", 50, this));
		tab2.add(new GraphicLevel("mille3", 70, this));
		tab2.add(new GraphicLevel("mille4", 100, this));
		tab2.add(new Level("mille", 20, this));
		tab2.add(new Level("mille2", 50, this));
		tab2.add(new Level("mille3", 70, this));
		tab2.add(new Level("mille", 100, this));
		//------ PANE 4 -------------------------------------
		
		JPanel tab4 = new JPanel(new GridLayout());
		JProgressBar progr = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		progr.setValue(20);
		tab4.add(progr);
		
		//------ PANE 3 -------------------------------------
		
		GridBagLayout innerGridLayout = new GridBagLayout();
		JPanel tab3 = new JPanel(innerGridLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
	
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel hostLabel = new JLabel("Indirizzo Host ");
		tab3.add(hostLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		ipAddress = new JTextField(getCodeBase().getHost());
		ipAddress.setColumns(15);
		tab3.add(ipAddress, c);
		
		Console.log("Host: " + getCodeBase().getHost());
		
		c.gridx = 0;
		c.gridy = 1;
		tab3.add(new JLabel("Port number "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		portNumber = new JTextField(Integer.toString(port));
		tab3.add(portNumber,c);
		
		//------ PANE CONTAINER ------------------------------
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Controlli", tab1);
		tabs.addTab("Volumi", tab2);
		tabs.addTab("Network", tab3);
		tabs.addTab("Prove", tab4);
		add(tabs, BorderLayout.CENTER);
		
		footer = new JLabel("Under construction by PicciMario - ing@work");
		footer.setHorizontalAlignment(JLabel.CENTER);
		add(footer, BorderLayout.SOUTH);
		
		/* --------------------------------------------------
		 * Menu bar (right now a bit useless, maybe later...)
		 * -------------------------------------------------- */
		menuBar = new JMenuBar();
		file = new JMenu("File");
		menuBar.add(file);
		
		menuAbout = new JMenuItem("About...");
		menuAbout.addActionListener(this);
		file.add(menuAbout);
		
		menuEsci = new JMenuItem("Esci");
		menuEsci.addActionListener(this);
		file.add(menuEsci);
		
		this.setJMenuBar(menuBar);
       
	}
	
	/**
	 * Called when an action is performed on elements created directly in the AudioController class.
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		if (arg0.getSource() == menuEsci){
			System.exit(0);
		}
		if (arg0.getSource() == menuAbout){
			JOptionPane.showMessageDialog(null, "AudioController by Mario Piccinelli");
		}
		
	}
	
    /** Executed only when this program runs as an application. */
    public static void main(String[] args) {
    	
    	/*TODO Problema: caricamento immagine da sistemare 
    	 * quando si esegue come applicazione
    	 */
    	
        //Create a new window.
        MainFrame f = new MainFrame("Converter Applet/Application");

        //Create a Converter instance.
        AudioController audioController = new AudioController();

        //Initialize the Converter instance.
        audioController.init();
        audioController.start();

        //Add the Converter to the window and display the window.
        f.add("Center", audioController);
        f.pack();        //Resizes the window to its natural size.
        f.setVisible(true);
    }

	/**
	 * Specifies the margins of the main window.
	 */
	public Insets getInsets() {
		Insets insets = new Insets(6,6,6,6);
		return insets;
	}
	
	/**
	 * Called when the applet is started.
	 */
	public void start() {
		Console.log("Applet started - method start().");
	    if (clockThread == null) {
	        clockThread = new Thread(this, "Clock");
	        clockThread.start();
	    }
	} 
	
	/**
	 * Called when the applet is stopped.
	 */
	public void stop() { 
	    clockThread = null;
	}

	static int threadSleep = 100;
	
	/**
	 * The synchronous job of executing the actions from the queue.
	 */
	public void run() {
		
		Console.log("Thread started...");
		
		 Thread myThread = Thread.currentThread();
		    while (clockThread == myThread) {   
		    	try {
		            Thread.sleep(threadSleep);
		        } catch (InterruptedException e){}
		        
		        doAction();
		    }

	}
	
	//gestione delle Action
	
	static InetAddress address;
	static int port = 2000;
	static int timeout = 500;
	
	DatagramSocket socket;
	
	/**
	 * Opens the UDP socket.
	 * @return True if the operation was successful, false otherwise.
	 */
	private boolean openSocket() {
		try {
			address = InetAddress.getByName(getCodeBase().getHost());
			socket = new DatagramSocket();
			socket.setSoTimeout(timeout);
			return true;	
		} catch (Exception e) {
			Console.log("Unable to open the network socket..");
			Console.log(e.toString());
			return false;
		} 
		
	}
	
	/**
	 * Closes the UDP socket.
	 */
	private void closeSocket() {
		
		if (socket == null) return;
		if (socket.isClosed()) return;
		
		try {
			socket.close();
		} catch (Exception e) {
			Console.log("Unable to close the socket..");
			Console.log(e.toString());
		} 
		
	}
	
	/**
	 * Transmits the argument to the socket.
	 * @param buf The bytes to transmit.
	 */
	private void sendPacket(byte[] buf) {
		
		try {
			DatagramPacket query = new DatagramPacket( buf, buf.length, address, port );
			socket.send(query);
	
		} catch (Exception e) {
			Console.log("Unable to send the packet..");
			Console.log(e.toString());
		}
	}
	
	/**
	 * The actions queue.
	 */
	private Vector<Action> actionQueue = new Vector<Action>();
	
	/**
	 * Adds an action to the queue.
	 * @param command The command byte of the action.
	 * @param argument The arguments (byte array) of the action.
	 */
	public void addActionAndWait(byte command, byte[] argument){
		Action azione = new Action(command, argument);
		azione.waitForAck = true;
		actionQueue.add(azione);
	}
	
	public void addActionAndReturn(byte command, byte[] argument, AudioControllerIndicator caller){
		Action azione = new Action(command, argument, caller);
		azione.waitForAck = true;
		actionQueue.add(azione);
	}
	
	/**
	 * POPs an action from the queue (First IN, first OUT) and executes it.
	 * Called by the run() method.
	 */
	private void doAction(){
		if (actionQueue.size() == 0) return;
		Action azione = actionQueue.remove(0);
		
		//reduces transitory actions
		if (actionQueue.size() > 1)
			if (azione.command == ((Action)actionQueue.get(0)).command) {
				Console.log("Skip this action due to overlap...");
				azione = actionQueue.remove(0);
				return;
			}
		
		//logs the action about to be executed
		String testoAzione = Byte.toString(azione.command);
		if (azione.argument.length > 0){
			testoAzione += " - ";
			for (int i = 0; i < azione.argument.length; i++){
				testoAzione += Byte.toString(azione.argument[i]) + " ";
			}
		}
		else{
			testoAzione += " (no args)";
		}
		Console.log("Executing action: " + testoAzione);
		
		byte buffer[] = new byte[1 + azione.argument.length];
		buffer[0] = azione.command;
		for (int i=0; i < azione.argument.length; i++){
			buffer[i+1] = azione.argument[i];
		}
		
		boolean socketStatus = openSocket();
		
		if (socketStatus == false) {
			Console.log("Unable to complete the action, skipping..");
			return;
		}
		
		sendPacket(buffer);
		
		if (azione.waitForAck == true)
		{
			DatagramPacket risposta = new DatagramPacket(new byte[10], 10);
			try {
				socket.receive(risposta);
				Console.log("ACK received.");
				if (azione.caller != null) {
					azione.caller.setValueFromAck(risposta.getData());
				}
			} catch (SocketTimeoutException e) {
				Console.log("ACK not received due to timeout (" + timeout + " millisecs).");
			} catch (Exception e){
				Console.log("ACK failed:");
				Console.log(e.toString());
			}
		}
		
		closeSocket();
	}
	
}

/**
 * The prototype of an action.
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class Action{
	byte command;
	byte[] argument;
	boolean waitForAck = false;
	AudioControllerIndicator caller;
	
	public Action(byte command, byte[] argument){
		this.command = command;
		this.argument = argument;
	}
	
	public Action(byte command, byte[] argument, AudioControllerIndicator caller){
		this.command = command;
		this.argument = argument;
		this.caller = caller;
	}
}

/** Provides a window if this program runs as an application. */
@SuppressWarnings("serial")
class MainFrame extends Frame {

	MainFrame(String title) {
    	super(title);
    } 

    public boolean handleEvent(Event e) {
    	if (e.id == Event.WINDOW_DESTROY) {
    		System.exit(0);
    	}
    	//return super.handleEvent(e);
    	return true;
    }
}

/**
 * A slider input tied to the action queue.
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class Fader extends JPanel implements ChangeListener {
	
	private static final long serialVersionUID = 1L;
	AudioController main;
	
	JSlider slider;
	JLabel label;
	JTextField text;
	
	String id;
	byte command;
	
	Fader(String id, byte command, AudioController main) {
		
		this.id = id;
		this.main = main;
		this.command = command;
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		//labelConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		label = new JLabel(id);
		add(label, labelConstraints);		
		
		GridBagConstraints faderConstraints = new GridBagConstraints();
		faderConstraints.gridx = 0;
		faderConstraints.gridy = 1;
		faderConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		slider = new JSlider(JSlider.VERTICAL);
		slider.addChangeListener(this);
		slider.setMinimumSize(new Dimension(0,100));
		slider.setPreferredSize(new Dimension(0,180));
		add(slider,faderConstraints);
		
		GridBagConstraints textConstraints = new GridBagConstraints();
		textConstraints.gridx = 0;
		textConstraints.gridy = 2;
		//textConstraints.fill = GridBagConstraints.HORIZONTAL;
		
		text = new JTextField();
		text.setColumns(2);
		text.setText(""+slider.getValue());
		add(text,textConstraints);

	}

	public void stateChanged(ChangeEvent e) {
		Console.log("" + slider.getValue());
		text.setText("" + slider.getValue());
		
		byte[] buffer = new byte[1];
		buffer[0] = (byte)slider.getValue();
		
		main.addActionAndWait(command, buffer);
	}
	
}

/**
 * A button input tied to the action queue.
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class Pulsante extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	AudioController main;
	byte command;
	JButton pulsante;
	String nome;
	
	Pulsante(String nome, byte command, AudioController main){
		this.main = main;
		this.command = command;
		this.nome = nome;
		pulsante = new JButton(nome);
		add(pulsante);
		pulsante.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		byte buffer[] = new byte[0];
		main.addActionAndWait(command, buffer);
		Console.log("Pressed key " + nome);
	}
	
}

/**
 * A bar output.
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class Level extends JPanel implements AudioControllerIndicator{
	
	private static final long serialVersionUID = 1L;
	
	AudioController main;
	JProgressBar progressBar;
	String name;
	JLabel label;
	JTextField text;
	
	int value;
	
	Level(String name, int value, AudioController main){
		
		this.main = main;
		this.name = name;
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(3,3,3,3);
		label = new JLabel(name);
		add(label, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		progressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 100);

		progressBar.setMinimumSize(new Dimension(0,100));
		progressBar.setPreferredSize(new Dimension(0,180));
		add(progressBar, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		text = new JTextField(Integer.toString(value));
		text.setColumns(2);
		add(text, constraints);
		
		setValue(value);
		
	}

	public void setValue(int value) {	
		progressBar.setValue(value);
		this.value = value;
	}

	public void setValueFromAck(byte[] ack) {
		// TODO Auto-generated method stub
		
	}

}

/**
 * A bar output.
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class GraphicLevel extends JPanel implements AudioControllerIndicator{
	
	private static final long serialVersionUID = 1L;
	
	AudioController main;
	String name;
	JLabel label;
	
	int value;
	int max = 100;
	int min = 0;
	double valYellow = 0.5;
	double valRed = 0.8;
	
	final int numBullets = 12;
	JLabel bullets[];
	
	JTextField text;
	
	ImageIcon bullet_red, bullet_green, bullet_yellow, bullet_black;
	
	GraphicLevel(String name, int value, AudioController main){
		
		this.main = main;
		this.name = name;
		
		this.bullet_green = main.bullet_green;
		this.bullet_red = main.bullet_red;
		this.bullet_yellow = main.bullet_yellow;
		this.bullet_black = main.bullet_black;
		
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(3,3,3,3);
		label = new JLabel(name);
		add(label, constraints);
		
		bullets = new JLabel[numBullets];
		
		for (int i = 0; i < numBullets; i++)
		{
			constraints.gridx = 0;
			constraints.gridy = i+1;
			
			bullets[numBullets - 1 - i] = new JLabel(bullet_black); 
			add(bullets[numBullets - 1 - i], constraints);
			
		}
		
		constraints.gridx = 0;
		constraints.gridy = numBullets + 1;
		text = new JTextField(Integer.toString(value));
		text.setColumns(2);
		add(text, constraints);
		
		setValue(value);
		
	}
	
	public void setValue(int value) {
		
		if (value >= max) value = max;
		if (value <= min) value = min;
		
		double increment = (int)((max-min)/numBullets);
		
		for (int i = 0; i < numBullets; i++){
			if (value > (i*increment)) {
				if ((i+1)*increment >= min+(max-min)*valRed)
					bullets[i].setIcon(bullet_red);
				else if ((i+1)*increment >= min+(max-min)*valYellow)
					bullets[i].setIcon(bullet_yellow);
				else
					bullets[i].setIcon(bullet_green);
			}
			else {
				bullets[i].setIcon(bullet_black);
			}
		}
		
		text.setText(Integer.toString(value));
		this.value = value;
	}

	public void setValueFromAck(byte[] ack) {
		// TODO Auto-generated method stub
		
	}

}

/**
 * Static class for general purpose functions.
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
class Console{
	
	static boolean debug = true;
	
	/**
	 * Writes text to the standard output (if debug is enabled).
	 * @param text String to log.
	 */
	static void log(String text){
		if (debug)
			System.out.println(text);
	}
	
}

/**
 * 
 * @author Mario Piccinelli (mario.piccinelli@gmail.com)
 *
 */
interface AudioControllerIndicator {
	public void setValueFromAck(byte[] ack);
}
