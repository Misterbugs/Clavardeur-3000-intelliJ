package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import message.Message;
import message.MsgFactory;
import message.MsgText;
import model.Address;
import model.Model;
import model.SimpleConversation;
import model.User;
import network.Network;
import view.ConversationOverviewController;
import view.LoginWindowController;
import view.UserOverviewController;


public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	public NetworkHandler getNet() {
		return net;
	}

	/**
	 * An easy way t
	private ObservableMap<String, User> userData;o store and find users
	 */
	//private ObservableMap<String, User> userData = FXCollections.observableHashMap();
	//private ObservableList<User> userData;

	//private ObservableMap<String, SimpleConversation> simpleConversations=FXCollections.observableHashMap();
	//private ArrayList<Conversation> serializableConversations = new ArrayList<Conversation>();
	
	private NetworkHandler net; //TODO c'est dégueu
	
	public MainApp(){
		
		net = new NetworkHandler( Network.getInstance());


		//Serialization xml
		/**
		 * Here, we just serialize the text from MsgText objects... Is it sufficient ? ... Maybe not...
		 * May be good to serialize the Conversation object...
		 */
//		XMLEncoder encoder = null;
//		try{
//			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("Conversations.clv")));
//			encoder.writeObject(serializableConversations);
//			/*for(Conversation conv : conversations){
//				for(MsgText msg : conv.getMessageList()){
//					encoder.writeObject(msg.getTextMessage());
//				}
//			}
//			*/
//			encoder.flush();
//
//		}catch(IOException e){
//			e.printStackTrace();
//		}finally{
//			if(encoder != null){
//				encoder.close();
//			}
//		}
		//Binary serialization
		/*ObjectOutputStream oos = null;
		try{
			final FileOutputStream file = new FileOutputStream("Conversations.clv");
			oos = new ObjectOutputStream(file);
			oos.writeObject(serializableConversations);
			oos.flush();
		}catch(final IOException e){
			e.printStackTrace();
		}*/



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
                    Model.getInstance().logOut();
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
					// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//public ObservableList<User> getUserData(){
	//	return userData;
	//}

	public static void main(String[] args) {
		launch(args);
		
	}
}
