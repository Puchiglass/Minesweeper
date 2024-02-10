package com.example.demo.service;

import com.example.demo.exception.*;
import com.example.demo.model.Game;
import com.example.demo.model.Turn;

public interface GameService {

    void create(Game game) throws InccorrectWidthOrHeight, IncorrectNumberOfMines;

    Game turn(Turn turn) throws IncorrectColOrRow, WrongCell, GameOver;
}
