package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Application;
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
	
	/**
	 * An easy way t
	private ObservableMap<String, User> userData;o store and find users
	 */
	//private ObservableMap<String, User> userData = FXCollections.observableHashMap();
	private ObservableList<User> userData;

	//private ObservableMap<String, SimpleConversation> simpleConversations=FXCollections.observableHashMap();
	//private ArrayList<Conversation> serializableConversations = new ArrayList<Conversation>();
	
	public NetworkHandler net; //TODO c'est dégueu
	
	public MainApp(){
		
		//Adding some conversations
		
		net = new NetworkHandler( Network.getInstance());
		
		userData = Model.getInstance().getKnownUsers();
		
		//Test
		
		Address broadcastAddr = Network.getInstance().getBroadcastAddress();
		InetAddress inet = null;
		try {
			inet = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		User localUsr = new User("John Doe", new Address( inet, 2048), true) ;
		Message m = MsgFactory.createHelloMessage(localUsr, broadcastAddr);
		net.sendMessage(m);
		
		//Fin test
		
		//InetAddress iAdd;
		/*
		try {
			iAdd = InetAddress.getLocalHost();
			Address add1 = new Address(iAdd, 8080);
			Address add2 = new Address(iAdd, 8080);
			Address add3 = new Address(iAdd, 8080);
			Address add4 = new Address(iAdd, 8080);
			//Some sample data
			userData.put("Flo", new User("Flo", add1, true));
			userData.put("Romain", new User("Romain", add2, true));
			userData.put("John", new User("John", add3, true));
			userData.put("Jane", new User("Jane", add4, true));
			
			//A sample SimpleConversation with Romain
			SimpleConversation conv1 = new SimpleConversation(new User("Romain", add2, true));
			MsgText mess1 = new MsgText(iAdd, 8080, "Flo", iAdd, 8080, 1, "Eh coucou !");
			//System.out.println(mess1.toString());
			conv1.addMessage(mess1);
			//System.out.println(conv1.toString());
			//serializableConversations.add(conv1);
			//conversations =FXCollections.observableArrayList(serializableConversations);
			
			simpleConversations.put(conv1.getId(), conv1);
			

			System.out.println(conv1.getId());
			
			
		
		} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		*/
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
	
	
	

	
	//En commentaires pour la gloire !
//	public ObservableList<User> getUserDataList(){
//		ObservableList<User> users = FXCollections.observableArrayList();
//		for(User usr : userData.values()){
//			users.add(usr);
//		}
//		
//		return users;
//	}
	
	
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
			controller.getPreviousMessages().appendText(controller.getConversation().toString());
			
		
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
			controller.getPreviousMessages().appendText(controller.getConversation().toString());
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ObservableList<User> getUserData(){
		return userData;
	}

	public static void main(String[] args) {
		launch(args);
		
	}
}
