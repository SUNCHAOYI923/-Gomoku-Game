package org.gomoku_game;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.*;

public class File
{
    public static void save_game ()
    {
        try
        {
            New_Game.GameState gameState = new New_Game.GameState (Gomoku.getChess (), Gomoku.getCurrentSide ());
            ObjectOutputStream output = new ObjectOutputStream (new FileOutputStream ("game_state.dat"));
            output.writeObject (gameState);output.writeInt (Start.ty);
            output.writeInt (New_Game.Black_ti);output.writeInt (New_Game.White_ti);
            output.close ();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle ("Gomoku Game");alert.setHeaderText (null);alert.setContentText ("Game saved successfully.");
            alert.showAndWait ();
        }
        catch (IOException e)
        {
            e.printStackTrace ();
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.setTitle ("Gomoku Game");alert.setHeaderText (null);alert.setContentText ("Failed to save game.");
            alert.showAndWait ();
        }
    }
    public static New_Game.GameState loadGame (Stage stage)
    {
        try
        {
            ObjectInputStream input = new ObjectInputStream (new FileInputStream ("game_state.dat"));
            New_Game.GameState gameState = (New_Game.GameState) input.readObject ();
            Start.ty = input.readInt ();
            New_Game.Black_ti = input.readInt ();New_Game.White_ti = input.readInt ();
            input.close ();
            return gameState;
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace ();
            return null;
        }
    }
}
