package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.AdminModel;
import server.MyServer;

public class MainWindowController implements Initializable
{
	@FXML private ListView listView;
		
	//List of clients
	protected ListProperty<String> list = new SimpleListProperty<>();
	
	private AdminModel admin;
	
	private Stage primaryStage;
	
	private MyServer server;
	
	@FXML
	public void habdleButtonAction(ActionEvent event)
	{
		updateList();
	}
	
	public void updateList()
	{
		this.list.set(FXCollections.observableArrayList(admin.getClients()));
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		this.admin = AdminModel.getInstance();
		this.listView.itemsProperty().bind(list);
		this.list.set(FXCollections.observableArrayList(admin.getClients()));
	}

	public void setServer(MyServer server)
	{
		this.server = server;
	}

	public void setPrimaryStage(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		exitPrimaryStage(this.primaryStage);
	}
	
	public void exitPrimaryStage(Stage primaryStage)
	{
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
			@Override
			public void handle(WindowEvent event) 
			{
				server.stop();
				System.out.println("Stop the server...");
			}
		});
	}
	
	public void stopServer()
	{
		server.stop();
		Platform.exit();
	}
	  
}
