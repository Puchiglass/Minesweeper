package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Turn {
    @JsonProperty("col")
    private final int col;
    @JsonProperty("row")
    private final int row;
    @JsonProperty("game_id")
    private String gameId;

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public String getGameId() {
        return gameId;
    }

    public Turn(int col, int row, String gameId) {
        this.col = col;
        this.row = row;
        this.gameId = gameId;
    }
}
