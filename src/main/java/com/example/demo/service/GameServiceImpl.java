package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.model.Game;
import com.example.demo.model.Turn;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class GameServiceImpl implements GameService {
    private static final Pattern PATTERN_OPEN = Pattern.compile("[0-8]");
    private static final Pattern PATTERN_BOUND = Pattern.compile("[1-8]");
    private static final Map<String, Game> GAME_MAP = new HashMap<>();

    @Override
    public void create(Game game) throws InccorrectWidthOrHeight, IncorrectNumberOfMines {
        final String gameId = String.valueOf(java.time.LocalDateTime.now());
//        final String gameId = "gameId";
        game.setGameId(gameId);

        int h = game.getHeight();
        if (h < 2 || h > game.getHeight())
            throw new InccorrectWidthOrHeight("высота поля должна быть не менее 2 и не более 30");

        int w = game.getWidth();
        if (w < 2 || w > game.getWidth())
            throw new InccorrectWidthOrHeight("ширина поля должна быть не менее 2 и не более 30");

        int mc = game.getMinesCount();
        if (w * h - 1 < mc) {
            int n = w * h - 1;
            throw new IncorrectNumberOfMines("количество мин должно быть не менее 1 и не более " + n);
        }

        String[][] field = new String[h][w];
        String[][] bombField = new String[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                field[i][j] = " ";
                bombField[i][j] = " ";
            }
        }
        game.setField(field);
        game.setBombField(bombField);

        GAME_MAP.put(gameId, game);
    }

    @Override
    public Game turn(Turn turn) throws IncorrectColOrRow, WrongCell, GameOver {
        String gameId = turn.getGameId();
        Game game = GAME_MAP.get(gameId);
        if (game.isCompleted())
            throw new GameOver("игра завершена");

        int x = turn.getRow();
        int y = turn.getCol();

        // Начала игры
        if (!game.isStarted()) {
            game.setStarted(true);
            startGame(game, x, y);
        }
        // Попадание в бомбу
        if (game.getBombField()[x][y].equals("B"))
            return loseGame(game);

        String cell = game.getField()[x][y];
        // Попадание в уже открытую
        Matcher matcher = PATTERN_OPEN.matcher(cell);
        if (matcher.find())
            throw new WrongCell("уже открытая ячейка");

        boolean[][] touched = new boolean[game.getHeight()][game.getWidth()];
        gameOpenCell(game, x, y, touched);

        game.setCompleted(isCompleted(game));

        return game.isCompleted()
                ? winGame(game)
                : game;
    }

    private void startGame(Game game, int x, int y) {
        String[][] bombField = game.getBombField();
        int xMax = game.getHeight();
        int yMax = game.getWidth();
        int minesCount = game.getMinesCount();
        // Потенциально узкое место из-за рандомного поиска места для мины
        Random random = new Random();
        while (minesCount > 0) {
            int xBomb = random.nextInt(yMax);
            int yBomb = random.nextInt(xMax);
            if (bombField[xBomb][yBomb].equals("B") || xBomb == x && yBomb == y)
                continue;
            bombField[xBomb][yBomb] = "B";
            minesCount--;
        }
        boolean[][] touched = new boolean[game.getHeight()][game.getWidth()];
        for (int i = 0; i < xMax; i++) {
            for (int j = 0; j < yMax; j++) {
                if (touched[i][j] || bombField[i][j].equals("B"))
                    continue;
                bombField[i][j] = startOpenCell(game, i, j, touched);
            }
        }
    }

    private String startOpenCell(Game game, int x, int y, boolean[][] touched) {
        String[][] bombField = game.getBombField();
        String cell = bombField[x][y];
        if (PATTERN_BOUND.matcher(cell).find())
            return cell; // если это 1-8
        else if (touched[x][y])
            return cell; // если уже заходили сюда
        touched[x][y] = true;

        int countMineAround = 0;
        for (int i = Math.max(0, x - 1); i <= Math.min(game.getHeight() - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(game.getWidth() - 1, y + 1); j++) {
                if (bombField[i][j].equals("B"))
                    countMineAround++;
                else
                    bombField[i][j] = startOpenCell(game, i, j, touched);
            }
        }
        return String.valueOf(countMineAround);
    }

    private void gameOpenCell(Game game, int x, int y, boolean[][] touched) {
        String[][] field = game.getField();
        String[][] bombField = game.getBombField();
        String cellOnBombField = bombField[x][y];
        touched[x][y] = true;

        if (PATTERN_BOUND.matcher(cellOnBombField).find()) {
            field[x][y] = cellOnBombField;
        } else if (cellOnBombField.equals("0")) {
            field[x][y] = cellOnBombField;
            int xMax = game.getHeight();
            int yMax = game.getWidth();
            if (x - 1 >= 0 && !touched[x - 1][y])
                gameOpenCell(game, x - 1, y, touched);
            if (y + 1 < yMax && !touched[x][y + 1])
                gameOpenCell(game, x, y + 1, touched);
            if (x + 1 < xMax && !touched[x + 1][y])
                gameOpenCell(game, x + 1, y, touched);
            if (y - 1 >= 0 && !touched[x][y - 1])
                gameOpenCell(game, x, y - 1, touched);
        }
    }

    private Game loseGame(Game game) {
        game.setCompleted(true);
        game.setField(game.getBombField());
        String[][] field = game.getField();
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (field[i][j].equals("B")) {
                    field[i][j] = "X";
                }
            }
        }
        return game;
    }

    private Game winGame(Game game) {
        game.setField(game.getBombField());
        String[][] field = game.getField();
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (field[i][j].equals("B")) {
                    field[i][j] = "M";
                }
            }
        }
        return game;
    }

    private boolean isCompleted(Game game) {
        String[][] field = game.getField();
        String[][] bombField = game.getBombField();
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {
                if (field[i][j].equals(" ") && !bombField[i][j].equals("B"))
                    return false;
            }
        }
        return true;
    }

}
