package project.model.stockfishApi;

import project.model.GameParticipant;

import java.io.*;
import java.util.LinkedList;

public class Stockfish implements GameParticipant {
    private BufferedReader output = null;
    private BufferedWriter input = null;
    private LinkedList<String> moves = null;
    private int level = 1;

    private Stockfish(int level) {
        this.level = level;
        try {
            ProcessBuilder pb = new ProcessBuilder(System.getProperty("user.dir").replace('\\','/') + "/src/" + "project/model/stockfishApi/resources/stockfish_13_win_x64.exe");
            //region Private
            Process myProcess = pb.start();
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
            input.write("go depth " + this.level + "\n");
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

    public static Stockfish getInstance(int level) {
        return new Stockfish(level);
    }

    public void makeMove(String paMove) {
        if (paMove != null)
            moves.add(paMove);
    }

    public String makeBestMove() {
        String lastLine = getLastLine();
        String[] parts;
        if (lastLine == null)
            return null;
        else
            parts = lastLine.split(" ");
            moves.add(parts[1]);
            return parts[1];
    }

    public void setMoves(LinkedList<String> moves) {
        this.moves = moves;
    }

    @Override
    public String getLastMove() {
        return moves.size() == 0 ? "" : moves.getLast();
    }

}
