package view;

import java.io.IOException;

import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Model;


public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	
	public MainApp(){
	}

	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Clavardeur 3000");
		
		initRootLayout();
		
		
		//showUsersOverview();
		showSplashScreen();
		//showConversationOverview();
		
		
	}
	
	public void initRootLayout(){
		try{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			//Show the scene
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

			primaryStage.setOnCloseRequest(e->{
				Platform.exit();

				if(Model.getInstance().getLocalUser() != null) {
                    Controller.getInstance().logOut();
                }
				System.out.println("Terminating ...");
				System.exit(1);
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public BorderPane getRootLayout(){
		return rootLayout;
	}
	
	
	public void showSplashScreen(){
		//Load userOverview
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/LoginWindow.fxml"));
			BorderPane loginWindow = (BorderPane) loader.load();

			//Set user overview in the center of root layout
			rootLayout.setCenter(loginWindow);

			LoginWindowController controller = loader.getController();
			controller.setMainApp(this);




		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showUsersOverview(){
		//Load userOverview
		try {
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/UserOverview.fxml"));
			AnchorPane userOverview = (AnchorPane) loader.load();
		
			//Set user overview int the Left of root layout
			rootLayout.setLeft(userOverview);
		
			UserOverviewController controller = loader.getController();
			controller.setMainApp(this);
			
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showConversationOverview(){
		//Load userOverview
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/ConversationOverview.fxml"));
			AnchorPane conversationOverview = (AnchorPane) loader.load();
		
			//Set user overview int the center of root layout
			rootLayout.setCenter(conversationOverview);
		
			ConversationOverviewController controller = loader.getController();
			controller.setMainApp(this);
			//controller.setConversation(simpleConversations.get(0));
			//controller.getPreviousMessages().appendText(controller.getConversation().toString()); //C'est dégueu
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showSimpleConversationOverview(String userName_address){
		//Load conversationOverview
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("../view/ConversationOverview.fxml"));
			AnchorPane conversationOverview = (AnchorPane) loader.load();
		
			//Set user overview int the center of root layout
			rootLayout.setCenter(conversationOverview);
		
			ConversationOverviewController controller = loader.getController();
			controller.setMainApp(this);
			controller.setConversation(Model.getInstance().getSimpleConversations().get(userName_address));
			//controller.getPreviousMessages().appendText(controller.getConversation().toString());
			
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		launch(args);
		
	}
}
