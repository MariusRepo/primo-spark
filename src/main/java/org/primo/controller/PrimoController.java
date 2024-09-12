package org.primo.controller;

import org.primo.entities.Play;
import org.primo.exceptions.PlayerException;
import org.primo.response.PrimoResponse;
import org.primo.response.ResponseBuilder;
import org.primo.service.PrimoService;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class PrimoController {

    private static final String SPIN_ENDPOINT = "/api/v1/spin";
    private static final String STATUS_ENDPOINT = "/api/v1/status";
    private static final String ALL_SPINS_ENDPOINT = "/api/v1/result";
    private static final String SPINS_BY_PLAYER_ENDPOINT = "/api/v1/result/:playerName";

    private final PrimoService primoService;

    public PrimoController(PrimoService primoService) {
        this.primoService = primoService;
        setupRoutes();
    }

    private void setupRoutes() {
        post(SPIN_ENDPOINT, (req, res) -> {
            String username = validateParam(req.queryParams("username"), "Player name is missing or empty!");
            String clientSeed = validateParam(req.queryParams("clientSeed"), "Client seed is missing or empty!");

            String result = primoService.spin(username, clientSeed);
            return ResponseBuilder.buildSuccessResponse(res, "Check spin status: http://localhost:4567/status/" + result);
        });

        get(STATUS_ENDPOINT, (req, res) -> {
            String username = validateParam(req.queryParams("username"), "Player name is missing or empty!");
            String token = validateParam(req.queryParams("token"), "Token is missing or empty!");

            Play play = primoService.checkSpinStatus(username, token);
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(List.of(play), "All spins!"));
        });

        get(ALL_SPINS_ENDPOINT, (req, res) -> {
            List<Play> plays = primoService.allSpins();
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(plays, "All spins!"));
        });

        get(SPINS_BY_PLAYER_ENDPOINT, (req, res) -> {
            String playerName = validateParam(req.params(":playerName"), "Player name is missing or empty!");

            List<Play> plays = primoService.playerSpins(playerName);
            return ResponseBuilder.buildSuccessResponse(res, new PrimoResponse(plays, "List of spins for: " + playerName));
        });
    }

    private String validateParam(String paramValue, String errorMessage) {
        return Optional.ofNullable(paramValue)
                .filter(s -> !s.trim().isEmpty())
                .orElseThrow(() -> new PlayerException(errorMessage));
    }

}
