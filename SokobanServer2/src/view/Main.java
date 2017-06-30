package view;

import javafx.application.Application;
import javafx.stage.Stage;
import server.MyServer;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			MyServer server = new MyServer(7171);

			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					server.start();
				}
			}).start();
			
			FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));				 
			AnchorPane root = (AnchorPane) mainLoader.load();
			MainWindowController mainWindow = mainLoader.getController();
			
			Scene scene = new Scene(root, 700, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
