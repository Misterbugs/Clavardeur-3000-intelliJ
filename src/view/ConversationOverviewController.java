package view;

import controller.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
	private void initialize(){
		
		
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

	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
		
	}
	
	public void setConversation(Conversation conversation){
		this.conversation = conversation;
	}
	
	public Conversation getConversation(){
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
	 * @param event
	 */
	public void handleSendButtonAction(ActionEvent event){
		
		//Send text if the text area is not empty
		if(!(textToSend.getText().equals(""))){
			System.out.println("Sending TextMessage");
			mainApp.net.sendMessage(MsgFactory.createMessage(Model.getInstance().getLocalUser(), Model.getInstance().getLocalUser(), textToSend.getText()));
			conversation.addMessage((MsgText)(MsgFactory.createMessage(Model.getInstance().getLocalUser(), Model.getInstance().getLocalUser(), textToSend.getText())));
			previousMessages.appendText("\n" + textToSend.getText());
			textToSend.clear();
		}
	}
}
