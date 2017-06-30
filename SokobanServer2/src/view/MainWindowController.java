package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import model.AdminModel;
import server.MyServer;

public class MainWindowController implements Initializable
{
	@FXML private ListView listView;
		
	//List of clients
	protected ListProperty<String> list = new SimpleListProperty<>();
	
	private AdminModel admin;
	
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
	
	
	  
}
