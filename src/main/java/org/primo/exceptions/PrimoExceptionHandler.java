package org.primo.exceptions;

import org.primo.response.ResponseBuilder;

import static spark.Spark.exception;

public class PrimoExceptionHandler {

    public PrimoExceptionHandler() {
        setupExceptionHandlers();
    }

    private void setupExceptionHandlers() {
        exception(PlayerException.class, (exception, req, res) -> {
            res.body(ResponseBuilder.buildErrorResponse(res, exception.getMessage(), 400));
        });

        exception(Exception.class, (exception, req, res) -> {
            res.body(ResponseBuilder.buildErrorResponse(res, "An unexpected error occurred.", 500));
        });
    }
}
