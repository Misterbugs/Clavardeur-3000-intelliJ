package view;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import controller.MainApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.Message;
import message.MsgAskFile;
import message.MsgFactory;
import message.MsgText;
import model.*;

import java.io.File;

public class ConversationOverviewController implements IConversationObserver {
	@FXML
	private FileChooser fileChooser;

	@FXML
	private Button fileButton;

	@FXML
	private Label labelDestName;

	@FXML
	private Button button;

	@FXML
	private TextArea previousMessages;

	@FXML
	private TextArea textToSend;

	private Conversation conversation;

	private boolean shiftPressed = false; //TODO make shift work

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

		fileChooser = new FileChooser();

		fileButton.setOnAction((event) -> {
				File file = fileChooser.showOpenDialog(new Stage());
				if(file != null){
					Message msg;
					msg = MsgFactory.createFileAskMessage(model.getLocalUser(), ((SimpleConversation) conversation).getReceiver(), file.getName(), file.length(), 987);
					mainApp.net.sendMessage(msg);
				}
			}
		);

		//Lambda to make the enter key send message
		textToSend.setOnKeyPressed((event) -> {
			/*if (event.getCode() == KeyCode.SHIFT) {
				System.out.println("Shift Pressed");
				shiftPressed = true;
			}*/

			if ((event.getCode() == KeyCode.ENTER) && !shiftPressed) {
				event.consume();
				sendText();
			}
		});



		/*textToSend.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.SHIFT) {
				shiftPressed = false;
				System.out.println("Shift released");
			}

		});
		*/


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
		for(MsgText m : conversation.getMessageList()){
			appendMessage(m);
		}
		conversation.register(this);
		//labelDestName.setText("Conversation with " + ((SimpleConversation) conversation).getReceiver().getUserNameString());

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
		textToSend.requestFocus();
		sendText();

	}


	public void handleSendFileButton(){

		System.out.println("sending file!");
	}

	private void sendText() {
		//Send text if the text area is not empty
		if (!(textToSend.getText().equals(""))) {
			//System.out.println("Sending TextMessage");

			Message msg = MsgFactory.createMessage(model.getLocalUser(), ((SimpleConversation) conversation).getReceiver(), textToSend.getText());
			//mainApp.net.sendMessage(msg);
			mainApp.net.sendMessageACK(msg, result -> {
				if(result == 1){
					conversation.addMessage((MsgText) msg);
				}
				else {
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Communication error");
						alert.setHeaderText("No ack :'(");
						alert.showAndWait().ifPresent(rs -> {
							if (rs == ButtonType.OK) {
								//System.out.println("Pressed OK.");
							}
						});
					});

					Platform.runLater(() -> {previousMessages.appendText("NOT DELIVERED : " + ((MsgText) msg).getTextMessage() + " (no ACK from " + ((SimpleConversation) conversation).getReceiver().getUserNameString()+ ")") ;});
				}
				return 1;});




			//previousMessages.appendText("\n" + textToSend.getText());

			//System.out.println("Conv : ");
			//System.out.println(conversation.toString());
			textToSend.clear();
			//textToSend.end();
		}
	}

	@Override
	public void update(Message mesg) {
		//synchronized (this) {
			//System.out.println("New message update !");

		if(mesg instanceof MsgText){
			appendMessage(mesg);
		}
		else if(mesg instanceof MsgAskFile){

			Platform.runLater(() -> {
						MsgAskFile filemesg = (MsgAskFile) mesg;
						Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle(filemesg.getSourceUserName() + " wants to send you a file");
						alert.setContentText("Filename : \""+ filemesg.getFilename()+"\"" + System.lineSeparator() +
								"Size : " + filemesg.getSize()/ 1024 + "Ko" + System.lineSeparator() +
								"Port : "+ filemesg.getSendingTCPPort()
								+ System.lineSeparator() + "Do you want to download it?"
						);
						ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
						ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

						alert.getButtonTypes().setAll(okButton, noButton);
						alert.showAndWait().ifPresent(type -> {
							if (type == ButtonType.OK) {

							} else{

							}
						});
					}
			);

		}
		else {
			System.out.println("ConversationController : Unhandled message type");
		}


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
				previousMessages.appendText(mesg.getSourceUserName() + " : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
			}
		});
	}
}



