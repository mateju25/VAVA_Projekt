package project.model.stockfishApi;

import javafx.scene.image.Image;
import project.model.GameParticipant;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Matej Delincak
 *
 * Trieda ma na starosti komunikaciu s programom Stockfish (.exe), zaroven obsahuje doteraz zahrane tahy.
 * Z tychto tahov sa sklada argument, ktory sa zapisuje do konzoly programu.
 */
public class Stockfish implements GameParticipant {
    private BufferedReader output = null;
    private BufferedWriter input = null;
    private LinkedList<String> moves = null;
    private int level = 1;

    public int getLevel() {
        return level;
    }

    private Stockfish(int level) {
        this.level = level;
        try {
            String url = System.getProperty("user.dir") + "/Data/stockfish_13_win_x64.exe";
            url = url.replace("\\", FileSystems.getDefault().getSeparator()).replace("/", FileSystems.getDefault().getSeparator());
            ProcessBuilder pb = new ProcessBuilder(url);
            //region Private
            Process myProcess = pb.start();
            output = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));
            input = new BufferedWriter(new OutputStreamWriter(myProcess.getOutputStream()));
            moves = new LinkedList<>();
        } catch (IOException e) {
            this.level = -1;
        }
    }

    /**
     * Funkcia posklada z atributu moves jeden String, v ktorom su zasebou pospajane vsetky tahy.
     * @return retazec tahov
     */
    private String buildMovesString() {
        StringBuilder result = new StringBuilder();
        for (String move: this.moves) {
            if (move != null)
                result.append(move).append(" ");
        }
        return result.toString();
    }

    /**
     * Funkcia zapise retazec tahov do programu a precita odpoved stockfishu
     * @return retazec najlepsieho tahu podla stockfishu
     */
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

    /**
     * Vlozi tah do zoznamu tahov, ak nie je null
     * @param paMove
     */
    public void makeMove(String paMove) {
        if (paMove != null)
            moves.add(paMove);
    }

    /**
     * Funkcia vlozi do moves tah, ktory najskor ziska z funkcie getLastLine
     * @return
     */
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

    /**
     * Funkcia vrati posledny tah v zozname tahov
     * @return posledny tah
     */
    @Override
    public String getLastMove() {
        return moves.size() == 0 ? "" : moves.getLast();
    }

}
