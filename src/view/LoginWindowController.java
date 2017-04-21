package view;

import javax.xml.soap.MessageFactory;

import controller.MainApp;
import controller.NetworkHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import message.MsgFactory;
import model.Model;
import model.User;
import network.Network;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LoginWindowController {

	
	@FXML
	private TextField userNameTextField;
	
	@FXML
	private Button logInButton;
	
	private MainApp mainApp;
	
	
	public LoginWindowController() {
		// TODO Auto-generated constructor stub
	}
	
	@FXML
	public void initialize(){
		
	}
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
		
	}
	@FXML
	public void handleLoginButtonAction(ActionEvent event){
		tryLogIn();
	}

	@FXML
	public void onEnter(ActionEvent ae){
		tryLogIn();
	}

	private void tryLogIn(){
		if(userNameTextField.getText().trim().isEmpty()){

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Username Error");
			alert.setHeaderText("Please enter a valid Username");
			alert.showAndWait().ifPresent(rs -> {
				if (rs == ButtonType.OK) {
					System.out.println("Pressed OK.");
				}
			});
		}else{
			logInButton.setText("yoyo");
			System.out.println("Connected as : " + userNameTextField.getText());

			Model.getInstance().logIn(userNameTextField.getText());

			mainApp.getRootLayout().setCenter(null);//TODO Who needs to do this ? (same problem for everytime you want to update the main window...)
			mainApp.showUsersOverview();
		}
	}
}
