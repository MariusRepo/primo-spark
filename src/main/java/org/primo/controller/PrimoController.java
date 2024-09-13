package org.primo.controller;

import org.primo.entities.GameSpinDTO;
import org.primo.entities.PlayerDTO;
import org.primo.service.PrimoService;

import java.util.List;

import static org.primo.response.ResponseBuilder.successResponse;
import static org.primo.utils.StatusEnum.PROCESSING;
import static org.primo.utils.StatusEnum.SUCCESS;
import static spark.Spark.get;
import static spark.Spark.post;

public class PrimoController {

    private static final String SPIN = "/api/v1/spin";
    private static final String SPIN_STATUS = "/api/v1/status";
    private static final String ALL_SPINS = "/api/v1/spins";
    private static final String PLAYER = "/api/v1/player/:playerName";
    private static final String PLAYERS = "/api/v1/players";
    private static final String PLAYER_SPINS = "/api/v1/spins/:playerName";

    private final PrimoService primoService;

    public PrimoController(PrimoService primoService) {
        this.primoService = primoService;
        setupRoutes();
    }

    private void setupRoutes() {
        // NOTE: Create endpoints that allow the frontend to start spins.
        post(SPIN, (req, res) -> {
            String username = primoService.validateParam(req.queryParams("username"), "Player is missing");
            String clientSeed = primoService.validateParam(req.queryParams("clientSeed"), "Seed is missing");

            String responseToken = primoService.spin(username, clientSeed);
            return successResponse(res, PROCESSING, "Check spin status token: " + responseToken);
        });

        // NOTE: Create endpoints that allow the frontend to retrieve game results.
        get(SPIN_STATUS, (req, res) -> {
            String username = primoService.validateParam(req.queryParams("username"), "Player is missing!");
            String token = primoService.validateParam(req.queryParams("token"), "Token is missing!");

            GameSpinDTO gameSpinDTO = primoService.checkSpinStatus(username, token);
            return successResponse(res, SUCCESS, gameSpinDTO, "Spin status for " + token);
        });

        // NOTE: Create endpoints that allow the frontend to access spin history.
        get(ALL_SPINS, (req, res) -> {
            List<GameSpinDTO> gameSpinDTOS = primoService.allSpins();
            return successResponse(res, SUCCESS, gameSpinDTOS, "List of all spins");
        });

        get(PLAYER_SPINS, (req, res) -> {
            String playerName = primoService.validateParam(req.params(":playerName"), "Player is missing");

            List<GameSpinDTO> gameSpinDTOS = primoService.playerSpins(playerName);
            return successResponse(res, SUCCESS, gameSpinDTOS, "List of spins for: " + playerName);
        });

        get(PLAYER, (req, res) -> {
            String playerName = primoService.validateParam(req.params(":playerName"), "Player is missing");

            PlayerDTO playerDTO = primoService.playerDetails(playerName);
            return successResponse(res, SUCCESS, playerDTO, "Player details for: " + playerName);
        });

        get(PLAYERS, (req, res) -> {
            List<PlayerDTO> playerDTO = primoService.playersDetails();
            return successResponse(res, SUCCESS, playerDTO);
        });
    }

}
