package org.primo.response;

import com.google.gson.Gson;
import spark.Response;

public class ResponseBuilder {

    private static final Gson gson = new Gson();

    public static String buildSuccessResponse(Response res, Object body) {
        res.status(200);
        res.type("application/json");
        return gson.toJson(body);
    }

    public static String buildResponse(Response res, Object body, int statusCode) {
        res.status(statusCode);
        res.type("application/json");
        return gson.toJson(body);
    }

    // Global bad catch all
    public static String buildErrorResponse(Response res, String message, int statusCode) {
        res.status(statusCode);
        res.type("application/json");
        ErrorResponse errorResponse = new ErrorResponse(message);
        return gson.toJson(errorResponse);
    }
}

