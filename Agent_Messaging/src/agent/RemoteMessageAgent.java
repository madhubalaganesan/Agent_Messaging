package agent;

import java.util.ArrayList;

import agent.MessageAgent.ReceiveMessage;
import agent.MessageAgent.SendMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class RemoteMessageAgent extends GuiAgent {

	private RemoteMessageAgentGUI remoteMessageGUI;
	private String receiver = "";
	private String content = "";
	private String messagePerformative = "";
	private String filename = "messageHistory.txt";
	private String fullConversationText = ""; // all the conversations will be appended here
	public ArrayList<String> agentList;
	public static int agentCounterInitial = 0;
	public static int agentCounterFinal = 0;

	protected void setup() {

		// Create and show the GUI
		remoteMessageGUI = new RemoteMessageAgentGUI(this);
		remoteMessageGUI.displayGUI();

		// Printout a welcome message
		System.out.println("Messenger agent " + getAID().getName() + " is ready.");

		/**
		 * This piece of code, to register services with the DF, is explained in the
		 * book in section 4.4.2.1, page 73
		 **/
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("remote-messenger-agent");
		sd.setName(getLocalName() + "-Messenger agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new ReceiveMessage());

	}

	protected void takedown() {

		// Dispose the GUI if it is there
		if (remoteMessageGUI != null) {
			remoteMessageGUI.dispose();
		}

		// Printout a dismissal message
		System.out.println("Agent " + getAID().getName() + " is terminating.");

		/**
		 * This piece of code, to de-register with the DF, is explained in the book in
		 * section 4.4.2.1, page 73
		 **/

		// De-register from the yellow pages
		try {
			DFService.deregister(this);
			System.out.println("Agent " + getAID().getName() + " has been signed off.");
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	// Sending message is an implementation of OneShotBehavior(Send once for one
	// time)
	public class SendMessage extends OneShotBehaviour {

		// Send message from to someone
		public void action() {
			ACLMessage msg;
			if (messagePerformative.equals("Propose")) {
				msg = new ACLMessage(ACLMessage.PROPOSE);
			} else {
				msg = new ACLMessage(ACLMessage.REQUEST);
			}

			// the receiver variable is set in getFromGUI() method
			AID r = new AID();
			r.setName("R@Platform2");
			r.addAddresses("http://ec2-13-58-206-137.us-east-2.compute.amazonaws.com:7778");
			msg.addReceiver(r);
			msg.setLanguage("English");
			msg.setContent(content);
			send(msg);

			// saveToFile(getAID().getLocalName() +":"+ content);
			fullConversationText += "\nMe: " + msg.getContent();
			remoteMessageGUI.setMessageTextArea(fullConversationText);

			System.out.println(getAID().getName() + " sent a message to " + receiver + "\n"
					+ "Content of the message is: " + msg.getContent());
		}
	}

	public class ReceiveMessage extends CyclicBehaviour {

		// Variable for the contents of the received Message
		private String messagePerformative;
		private String messageContent;
		private String SenderName;
		private String MyPlan;

		// Receive message and append it in the conversation textArea in the GUI
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {

				messagePerformative = msg.getPerformative(msg.getPerformative());
				messageContent = msg.getContent();
				SenderName = msg.getSender().getLocalName();

				// print the message details in console
				System.out.println("**** " + getAID().getLocalName() + " received a aessage" + "\n" + "Sender name: "
						+ SenderName + "\n" + "Content of the message: " + messageContent + "\n" + "Message type: "
						+ messagePerformative + "\n");
				System.out.println("**********************************");

				fullConversationText += "\n" + SenderName + ": " + messageContent;
				remoteMessageGUI.setMessageTextArea(fullConversationText);

			}

		}
	}

	// get all entered input from gui agent
	public void getFromGui(final String messageType, final String dest, final String messages) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				messagePerformative = messageType;
				receiver = dest;
				content = messages;
			}
		});
	}

	// predefined function of GuiAgent - see postGuiEvent() in MessageAgentGui.java
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		addBehaviour(new SendMessage());

	}

}
