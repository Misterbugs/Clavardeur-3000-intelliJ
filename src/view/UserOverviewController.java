package view;

import com.sun.org.apache.xpath.internal.operations.Mod;
import controller.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import model.Model;
import model.SimpleConversation;
import model.User;
 
public class UserOverviewController {

	@FXML
	ObservableList<User> data = FXCollections.observableArrayList();
	@FXML
	private ListView<User> userTable;
	
	

//	
//	@FXML
//	private TableColumn<User, User> userNameColumn;
//	@FXML
//	private TableColumn<User, String> userAddressColumn;
	
	
	//reference to the main application
	private MainApp mainApp;
	
	public UserOverviewController(){
		
	}
	
	/**
	 * Initializer called after the FXML file has been loaded
	 */
	@FXML
	private void initialize(){
		
		userTable.setCellFactory(tc -> {
			ListCell<User> cell = new ListCell<User>(){
				@Override
				protected void updateItem(User user, boolean empty){
					super.updateItem(user, empty);
					setText(empty ? null : user.getUserNameString() +" : " + (user.getIsConnected() ? "Connected" : "Not Connected"));
				}
			};
			
			//Here we try to deal with user interaction for opening conversations
			cell.setOnMouseClicked(e -> {
				if(!cell.isEmpty()){
					String userName = cell.getItem().getUserNameString();
					System.out.println("Youpi �a marche !" + userName);
					
					//gets the User from the mainApp userList
					User receiver = cell.getItem();
					//Prepares the conversation id
					String conversationId = new String(receiver.getFullUserName());
					System.out.println(conversationId);
					
					//Check for the conversation existence
					if(Model.getInstance().getSimpleConversations().containsKey(conversationId)){
						mainApp.showSimpleConversationOverview(conversationId);
					}else{
						//If it does not exist, let's create it !
						/*SimpleConversation newConversation = new SimpleConversation(receiver);
						Model.getInstance().getSimpleConversations().put(conversationId, newConversation);
						System.out.println("Creating conversation...");
						mainApp.showSimpleConversationOverview(conversationId);
						*/
						System.out.println("Conversation doesn't exist!");
					}
					
					
				}
			});
			return cell;
		});
		
		
	}
	
	
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
		//data = mainApp.getUserData();
		//userTable.setItems(this.mainApp.getUserData());
		userTable.setItems(Model.getInstance().getKnownUsers());
		
		
		
	}
	
	
	
}
