package view;

import controller.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import message.Message;
import message.MsgFactory;
import message.MsgText;
import model.Conversation;
import model.Model;

public class ConversationOverviewController {

	@FXML
	private Button button;

	@FXML
	private TextArea previousMessages;

	@FXML
	private TextArea textToSend;

	private Conversation conversation;


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

		//Lambda to make the enter key send message
		textToSend.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				sendText();
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

	private void sendText(){
		//Send text if the text area is not empty
		if (!(textToSend.getText().equals(""))) {
			System.out.println("Sending TextMessage");

			Message msg = MsgFactory.createMessage(Model.getInstance().getLocalUser(), Model.getInstance().getLocalUser(), textToSend.getText());
			mainApp.net.sendMessage(msg);
			conversation.addMessage((MsgText)msg); //TODO à changer
			previousMessages.appendText("\n" + textToSend.getText());

			System.out.println("Conv : ");
			System.out.println(conversation.toString());
			textToSend.clear();
		}
	}

}



