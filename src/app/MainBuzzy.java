package app;

import java.io.*;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class MainBuzzy extends Application
{
    @Override
    public void start(Stage primaryStage) 
    {
        BorderPane root = new BorderPane();
        
        try
        {			
            Scene scene = new Scene(root,1260,600); // Para editar o tamanho mínimo da tela inicial ao abrir o programa
            scene.getStylesheets().add(getClass().getResource("/userInterface/styles.css").toExternalForm());   // associa um arquivo de estilos .css ao prorama
            primaryStage.setScene(scene);
            primaryStage.setTitle("Buzzy - Modelagem de redes Fuzzy-Bayesianas"); //define o título da janela de execução do programa
            primaryStage.sizeToScene();            
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.show();    // habilita o programa a ser exibido
            
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        root.setCenter(new MainScreen(primaryStage));
    }

    public static void main(String[] args)
    {
        launch(args);
    }    
}
