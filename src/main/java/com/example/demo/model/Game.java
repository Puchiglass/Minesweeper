package com.example.demo.model;

import com.example.demo.exception.InccorrectWidthOrHeight;
import com.example.demo.exception.IncorrectNumberOfMines;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {
    @JsonProperty("game_id")
    private String gameId;
    @JsonProperty("height")
    private final int height;
    @JsonProperty("width")
    private final int width;
    @JsonProperty("mines_count")
    private final int minesCount;
    @JsonProperty("completed")
    private boolean completed;
    @JsonProperty("field")
    private String[][] field;
    @JsonIgnore
    private String[][] bombField;
    @JsonIgnore
    private boolean started;

    public Game(int height, int width, int minesCount) throws IncorrectNumberOfMines, InccorrectWidthOrHeight {
        this.height = height;
        this.width = width;
        this.minesCount = minesCount;
        this.started = false;
    }

    public String getGameId() {
        return gameId;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getMinesCount() {
        return minesCount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String[][] getField() {
        return field;
    }

    public String[][] getBombField() {
        return bombField;
    }

    public boolean isStarted() {
        return started;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setField(String[][] field) {
        this.field = field;
    }

    public void setBombField(String[][] bombField) {
        this.bombField = bombField;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

}
