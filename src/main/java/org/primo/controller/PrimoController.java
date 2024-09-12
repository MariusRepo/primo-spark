package org.primo.controller;

import org.primo.entities.Play;
import org.primo.exceptions.PlayerException;
import org.primo.response.PrimoResponse;
import org.primo.response.ResponseBuilder;
import org.primo.service.PrimoService;

import java.util.List;

import static spark.Spark.get;

public class PrimoController {

    private PrimoService primoService;

    public PrimoController(PrimoService primoService) {
        this.primoService = primoService;
        setupRoutes();
    }

    private void setupRoutes() {
        setupSpinRoute();
        setupResultsRoute();
        setupPlayerResultRoute();
    }

    private void setupSpinRoute() {
        get("/spin/:username", (req, res) -> {
            String username = req.params(":username");
            if (username == null || username.trim().isEmpty()) {
                throw new PlayerException("Player name is missing or empty!");
            }
            Play playResult = primoService.spin(username);
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(List.of(playResult), "Please play again!"));
        });
    }

    private void setupResultsRoute() {
        get("/result", (req, res) -> {
            List<Play> plays = primoService.results();
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(plays, "All spins!"));
        });
    }

    private void setupPlayerResultRoute() {
        get("/result/:playerName", (req, res) -> {
            String playerName = req.params(":playerName");
            if (playerName == null || playerName.trim().isEmpty()) {
                throw new PlayerException("Player name is missing or empty!");
            }
            List<Play> plays = primoService.result(playerName);
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(plays, "List of spins for: " + playerName));
        });
    }

}
