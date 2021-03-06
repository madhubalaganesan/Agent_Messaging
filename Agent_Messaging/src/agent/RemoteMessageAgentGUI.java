package agent;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class RemoteMessageAgentGUI  extends JFrame {
	
	private RemoteMessageAgent remoteMessageAgent;
	String messageType = "";
	String title = "";

	JTextField messageContent; // text field to send messagesTA to the receiving agents
	JTextArea messageViewer; // text area that shows the chat messagesTA > (format: ReceiverAgentName (time stamp) : message from receiver)
	JComboBox messageTypes, receivers; // message type sets the kind of message, receiver is the drop down list of all the agents

	JFrame mainFrame;
	JLabel headerLabel;
	JLabel statusLabel;
	JPanel controlPanel;
	JLabel msglabel;
	JButton sendMessageBtn;

	JLabel contentTFLabel , messagesTALabel , messageTypeLable, receiverLabel;

	ArrayList<String> messageTypesList;
	ArrayList<String> receiversList;
	
	public RemoteMessageAgentGUI(RemoteMessageAgent a) {
		super(a.getLocalName());

		remoteMessageAgent = a;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				remoteMessageAgent.doDelete();
			}
		} );

		// all the GUI is instantiated here, so that it can be passed
		// as a parameter to the Agent class grid layout is used

		messageTypesList = new ArrayList();
		receiversList = new ArrayList();
		
		// Initializing message type
		messageTypesList.add("Request");
		messageTypesList.add("Propose");
		
		receiversList.add("Remote Platform");
		
		messageContent = new JTextField();
		messageContent.setPreferredSize(new Dimension(400, 30));

		messageViewer = new JTextArea(15, 30);
//		messageViewer.setPreferredSize(new Dimension(200, 200));
		messageViewer.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(messageViewer);

		messageTypes = new JComboBox(messageTypesList.toArray());
		messageTypes.setPreferredSize(new Dimension(400,20));

		//add actionListener to comboBox when any message type is selected
		messageTypes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Object selected = messageTypes.getSelectedItem();
				if(selected.toString().equals("Request"))
					messageType = "Request";
				else if(selected.toString().equals("Propose"))
					messageType = "Propose";
			}
		});
		
		updateReceiverCombo();
		
		messageTypeLable  = new JLabel("Message Type: ");
		messageTypeLable.setPreferredSize(new Dimension(400, 20));

		receiverLabel = new JLabel("Receivers: ");
		receiverLabel.setPreferredSize(new Dimension(400, 20));

		contentTFLabel = new JLabel("Content: ");
		contentTFLabel.setPreferredSize(new Dimension(400, 20));

		messagesTALabel = new JLabel("Full Conversation: ");
		messagesTALabel.setPreferredSize(new Dimension(400, 20));

		sendMessageBtn = new JButton("Send");
		sendMessageBtn.setPreferredSize(new Dimension(200, 50));

		headerLabel = new JLabel("",JLabel.CENTER );
		statusLabel = new JLabel("",JLabel.CENTER);
//		statusLabel.setSize(350,100);

		controlPanel = new JPanel();

//		GridLayout grid = new GridLayout(10,20);
//
//		controlPanel.setLayout(grid);

		controlPanel.add(messageTypeLable);
		controlPanel.add(messageTypes);
		controlPanel.add(messageContent);
		controlPanel.add(receiverLabel);
		controlPanel.add(receivers);
		controlPanel.add(contentTFLabel);
		controlPanel.add(messageContent);
		controlPanel.add(messagesTALabel);
//		controlPanel.add(messageViewer);
		controlPanel.add(scrollPane);
		
		sendMessageBtn.addActionListener( new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent ae) {
				try {
					String content = messageContent.getText().trim();
					remoteMessageAgent.getFromGui(
							messageType, receivers.getSelectedItem().toString(), content
					);

					messageContent.setText("");

					GuiEvent guiEvent = new GuiEvent(this, 1);
					remoteMessageAgent.postGuiEvent(guiEvent); // this postGuiEvent triggers onGuiEvent method in MessageAgent which in turn calls the sendMessage
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(RemoteMessageAgentGUI.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} );
		
		controlPanel.add(sendMessageBtn);

		Container contentPane = getContentPane();
		contentPane.setPreferredSize(new Dimension(400, 500));
		getContentPane().add(controlPanel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				remoteMessageAgent.doDelete();
			}
		} );
		
	}
	
	public void setMessageTextArea(String text){
		messageViewer.setText(text);
	}
	public void displayGUI() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}

	public void updateReceiverCombo(){

		receivers = new JComboBox(receiversList.toArray());
		receivers.setPreferredSize(new Dimension(400, 30));
	}

}
