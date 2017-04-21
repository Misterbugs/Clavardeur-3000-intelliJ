package view;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import controller.MainApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import message.Message;
import message.MsgFactory;
import message.MsgText;
import model.*;

public class ConversationOverviewController implements IConversationObserver {

	@FXML
	private Button button;

	@FXML
	private TextArea previousMessages;

	@FXML
	private TextArea textToSend;

	private Conversation conversation;

	private boolean shiftPressed = false;

	private Model model;

	//reference to the main application
	private MainApp mainApp;


//	public ConversationOverviewController(Conversation conversation) {
//		this.conversation = conversation;
//	}
//	


	/**
	 * Initializer called after the fxml file has been loaded
	 */
	@FXML
	private void initialize() {

		model = Model.getInstance();


		//Lambda to make the enter key send message
		textToSend.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.SHIFT) {
				System.out.println("Shift Pressed");
				shiftPressed = true;
			}

			if ((event.getCode() == KeyCode.ENTER) && !shiftPressed) {
				event.consume();
				sendText();
			}
		});



		textToSend.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.SHIFT) {
				shiftPressed = false;
				System.out.println("Shift released");
			}

		});



	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public TextArea getTextToSend() {
		return textToSend;
	}

	public void setTextToSend(TextArea textToSend) {
		this.textToSend = textToSend;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
		conversation.register(this);
	}

	public Conversation getConversation() {
		return this.conversation;
	}

	public TextArea getPreviousMessages() {
		return previousMessages;
	}

	public void setPreviousMessages(TextArea previousMessages) {
		this.previousMessages = previousMessages;
	}

	/**
	 * Event function when the button "Send" is pressed
	 *
	 * @param event
	 */
	public void handleSendButtonAction(ActionEvent event) {
		sendText();

	}


	private void sendText() {
		//Send text if the text area is not empty
		if (!(textToSend.getText().equals(""))) {
			System.out.println("Sending TextMessage");

			Message msg = MsgFactory.createMessage(model.getLocalUser(), ((SimpleConversation) conversation).getReceiver(), textToSend.getText()); //TODO c'est limite
			mainApp.net.sendMessage(msg);
			conversation.addMessage((MsgText) msg); //TODO à changer
			//previousMessages.appendText("\n" + textToSend.getText());

			System.out.println("Conv : ");
			System.out.println(conversation.toString());
			textToSend.clear();
			//textToSend.end();
		}
	}

	@Override
	public void update(Message mesg) {
		//synchronized (this) {
			System.out.println("New message update !");
			appendMessage(mesg);

	}


	/**
	 * Prints a message in the conversation textArea
	 * @param mesg
	 */
	public void appendMessage(Message mesg){
		Platform.runLater(()->{
		String fullUserName = User.fullUserName(mesg.getSourceUserName(), new Address(mesg.getSourceAddress(), mesg.getSourcePort()));
			if(fullUserName.equals(model.getLocalUser().getFullUserName())){
				previousMessages.appendText("You : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
			}else{

			}

			//previousMessages.appendText(mesg.getSourceUserName() + " : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
		});
	}
}



