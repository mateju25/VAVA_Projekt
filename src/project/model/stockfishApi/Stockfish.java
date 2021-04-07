package project.model.stockfishApi;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Stockfish {
    //region Private
    private Process myProcess =  null;
    private BufferedReader output = null;
    private BufferedWriter input = null;
    private ArrayList<String> moves = null;

    private Stockfish() {
        try {
            ProcessBuilder pb = new ProcessBuilder(System.getProperty("user.dir").replace('\\','/') + "/src/" + "project/model/stockfishApi/resources/stockfish_13_win_x64.exe");
            myProcess = pb.start();
            output = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
            input = new BufferedWriter(new OutputStreamWriter(myProcess.getOutputStream()));
            moves = new ArrayList<>();
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
            input.write("go depth 1" + "\n");
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
    public static Stockfish getInstance() {
        return new Stockfish();
    }

    public ArrayList<String> getMoves() {
        return this.moves;
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
            return lastLine.contains("(None)");
    }

    public String getBestMove(String paMove) {
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

    //endregion

    public static void main(String[] args) {
        Stockfish stockfish = Stockfish.getInstance();
        Scanner in = new Scanner(System.in);
        for (int i = 0; i < 100; i++) {
            System.out.println(stockfish.getBestMove(in.nextLine()));
        }
    }
}
