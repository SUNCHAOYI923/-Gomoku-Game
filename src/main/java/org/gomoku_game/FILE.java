package org.gomoku_game;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.*;

public class FILE
{
    public static void save_game (Stage nw)
    {
        New_Game.stop = !New_Game.stop;
        FileChooser fileChooser = new FileChooser ();
        fileChooser.setTitle ("Save Game");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter ("GOMOKU files (*.gomoku)", "*.gomoku"); //强行要求后缀名为 .gomoku
        fileChooser.getExtensionFilters ().add (extFilter);
        File file = fileChooser.showSaveDialog (nw);
        if (file == null)
        {
            New_Game.stop = !New_Game.stop;
            return ;
        }
        if (!file.getPath ().endsWith (".gomoku")) file = new File (file.getPath () + ".gomoku");
        try (ObjectOutputStream output = new ObjectOutputStream (new FileOutputStream (file)))
        {
            New_Game.GameState gameState = new New_Game.GameState (Gomoku.getChess (), Gomoku.getCurrentSide ());
            output.writeObject (gameState);output.writeInt (Start.ty);
            output.writeInt (New_Game.Black_ti);output.writeInt (New_Game.White_ti);
            output.writeInt (New_Game.cur_ti);
            output.close ();
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setTitle ("Gomoku Game");alert.setHeaderText (null);alert.setContentText ("Game saved successfully.");
            alert.showAndWait ();
        }
        catch (IOException e) {e.printStackTrace ();}
        New_Game.stop = !New_Game.stop;
    }
    public static New_Game.GameState loadGame (Stage nw)
    {
        FileChooser fileChooser = new FileChooser ();
        fileChooser.setTitle ("Load Game");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter ("GOMOKU files (*.gomoku)", "*.gomoku"); //强行要求后缀名为 .gomoku
        fileChooser.getExtensionFilters ().add (extFilter);
        File file = fileChooser.showOpenDialog (nw);
        if (file == null) return null;
        try (ObjectInputStream input = new ObjectInputStream (new FileInputStream (file)))
        {
            New_Game.GameState gameState = (New_Game.GameState) input.readObject ();
            Start.ty = input.readInt ();
            New_Game.Black_ti = input.readInt ();New_Game.White_ti = input.readInt ();
            New_Game.cur_ti = input.readInt ();
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
