package com.example.demo.controller;

import com.example.demo.exception.*;
import com.example.demo.model.Game;
import com.example.demo.model.Turn;
import com.example.demo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody Game game) {
        try {
            gameService.create(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (InccorrectWidthOrHeight | IncorrectNumberOfMines e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FATAL ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/turn")
    public ResponseEntity<?> turn(@RequestBody Turn turn) {
        try {
            final Game game = gameService.turn(turn);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (IncorrectColOrRow | WrongCell | GameOver e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("FATAL ERROR: " + e.getMessage());
        }
    }
}
