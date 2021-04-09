package project.model.stockfishApi;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Stockfish implements Runnable{
    //region Private
    private Process myProcess =  null;
    private BufferedReader output = null;
    private BufferedWriter input = null;
    private LinkedList<String> moves = null;
    private int level = 1;

    private Stockfish(int level) {
        this.level = level;
        try {
            ProcessBuilder pb = new ProcessBuilder(System.getProperty("user.dir").replace('\\','/') + "/src/" + "project/model/stockfishApi/resources/stockfish_13_win_x64.exe");
            myProcess = pb.start();
            output = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
            input = new BufferedWriter(new OutputStreamWriter(myProcess.getOutputStream()));
            moves = new LinkedList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildMovesString() {
        StringBuilder result = new StringBuilder();
        for (String move: this.moves) {
            if (move != null)
                result.append(move).append(" ");
        }
        return result.toString();
    }

    private String getLastLine() {
        try {
            input.write("position startpos moves " + buildMovesString() + "\n");
            input.flush();
            input.write("go nodes " + this.level + "\n");
            input.flush();

            while(true) {
                String line = output.readLine();
                if (line.contains("bestmove")) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //endregion Private

    //region Public
    public static Stockfish getInstance(int level) {
        return new Stockfish(level);
    }

    public LinkedList<String> getMoves() {
        return this.moves;
    }

    public void setMoves(LinkedList<String> moves) {
        this.moves = moves;
    }

    public void shutdownStockfish() {
        try {
            input.close();
            output.close();
            myProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean isCheckmate() {
        String lastLine = getLastLine();
        if (lastLine == null)
            return null;
        else
            return lastLine.contains("(none)");
    }

    public String getBestMove(String paMove) {
        if (paMove != null)
            moves.add(paMove);
        String lastLine = getLastLine();
        String[] parts = null;
        if (lastLine == null)
            return null;
        else
            parts = lastLine.split(" ");
            moves.add(parts[1]);
            return parts[1];
    }

    @Override
    public void run() {

    }

    //endregion
}
