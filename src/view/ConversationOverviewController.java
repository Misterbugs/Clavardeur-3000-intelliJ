package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.Message;
import message.MsgAskFile;
import message.MsgFactory;
import message.MsgText;
import model.*;

import javax.swing.*;
import java.io.File;
import java.util.Observable;

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
	private VBox theVBox = new VBox();
	@FXML
	private TextArea textToSend;

	private Conversation conversation;

	private boolean shiftPressed = false; //TODO make shift work

	private Model model;

	//reference to the main application
	private MainApp mainApp;


	/**
	 * Initializer called after the fxml file has been loaded
	 */
	@FXML
	private void initialize() {


		theVBox.setStyle("-fx-background-color:#f9ffe5;");

		model = Model.getInstance();

		fileChooser = new FileChooser();

		fileButton.setOnAction((event) -> {
				File file = fileChooser.showOpenDialog(new Stage());
				if(file != null){


					Controller.getInstance().getNetworkHandler().sendAskFile(((SimpleConversation) conversation).getReceiver(), file, (callbackCode)->{
						if(callbackCode==1){

						}else {
							Platform.runLater(() -> {
								Alert alert = new Alert(Alert.AlertType.ERROR);
								alert.setTitle("Couldn't send the file");
								alert.setHeaderText("Another file waits to be sent and the user hasn't responded yet. ");
								alert.showAndWait().ifPresent(rs -> {
									if (rs == ButtonType.OK) {
										//System.out.println("Pressed OK.");
									}
								});
							});

						}

					});
				}
			}
		);


		//Lambda to make the enter key send message
		textToSend.setOnKeyPressed((event) -> {
			if ((event.getCode() == KeyCode.ENTER) && !shiftPressed) {
				event.consume();
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
		for(MsgText m : conversation.getMessageList()){
			appendMessage(m);
		}
		conversation.register(this);
		labelDestName.setText("Conversation with " + ((SimpleConversation) conversation).getReceiver().getUserNameString());

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
			Controller.getInstance().getNetworkHandler().sendMessageACK(msg, result -> {
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

			});

			textToSend.clear();

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

						// generating the popup that ask if the user wants to receive the file.
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

						// Managing the outcomes depending on the users' choice
						alert.showAndWait().ifPresent(type -> {

							if (type == okButton) {
								//
								System.out.println("Accepting file");
								Controller.getInstance().getNetworkHandler().acceptFile(UserList.getInstance().getSourceUser(filemesg),filemesg.getFilename(),filemesg.getSize());
							} else{

								Controller.getInstance().getNetworkHandler().declineFile(UserList.getInstance().getSourceUser(filemesg),filemesg.getFilename());
								//mainApp.getNet().sendMessage(MsgFactory.createReplyFileMessage())
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
				Text theText = new Text(((MsgText) mesg).getTextMessage() + System.lineSeparator());
				TextFlow newText = new TextFlow(theText);
				newText.setTextAlignment(TextAlignment.RIGHT);
				newText.setMaxWidth((theVBox.getWidth() / 2) - 1 );
				newText.setPrefWidth(theVBox.getWidth()/4);
				newText.setMinWidth(theVBox.getWidth()/5);
				newText.setTranslateX(boxSize);
				newText.setStyle("-fx-background-color: #2076ff; -fx-text-fill: white; -fx-padding: 20px; -fx-fill-width: false;" );;
				//previousMessages.appendText("You : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
				//previousMessages.appendText(newText);
				theVBox.getChildren().add(newText);






			}else{
				//previousMessages.appendText(mesg.getSourceUserName() + " : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
				Text theText = new Text(((MsgText) mesg).getTextMessage() + System.lineSeparator());
				TextFlow newText = new TextFlow(theText);
				newText.setTextAlignment(TextAlignment.LEFT);
				newText.setMaxWidth((theVBox.getWidth() / 2) - 1 );
				newText.setPrefWidth(theVBox.getWidth()/4);
				newText.setMinWidth(theVBox.getWidth()/5);
				newText.setTranslateX(boxSize);
				newText.setStyle("-fx-background-color: #e1ebff; -fx-text-fill: #2f2f2f; -fx-padding: 20px; -fx-fill-width: false;" );;
				//previousMessages.appendText("You : " + ((MsgText) mesg).getTextMessage() + System.lineSeparator());
				//previousMessages.appendText(newText);
				theVBox.getChildren().add(newText);

				Thread t = new Thread(() -> {
					Media sound = new Media(this.getClass().getResource("/ah.wav").toString());
					MediaPlayer mediaPlayer = new MediaPlayer(sound);
					mediaPlayer.play();
				});

				t.start();

			}
		});
	}
}



