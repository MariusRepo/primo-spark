package org.primo.exceptions;

import org.primo.response.ResponseBuilder;

import static spark.Spark.exception;

public class PrimoExceptionHandler {

    public PrimoExceptionHandler() {
        setupExceptionHandlers();
    }

    private void setupExceptionHandlers() {
        exception(PlayerException.class, (exception, req, res) -> res.body(ResponseBuilder.errorResponse(res, exception.getMessage(), "PLAYER_NOT_FOUND", 404)));
        exception(ParameterException.class, (exception, req, res) -> res.body(ResponseBuilder.errorResponse(res, exception.getMessage(), "MISSING_PARAMETER", 400)));
        exception(SpinException.class, (exception, req, res) -> res.body(ResponseBuilder.errorResponse(res, exception.getMessage(), "PROCESSING", 102)));
        exception(Exception.class, (exception, req, res) -> res.body(ResponseBuilder.errorResponse(res, "An unexpected error occurred.", "SERVER_ERROR", 500)));
    }
}
