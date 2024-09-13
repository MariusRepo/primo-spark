package org.primo.response;

import com.google.gson.Gson;
import org.primo.utils.StatusEnum;
import spark.Response;

import static org.primo.utils.StatusEnum.FAILURE;

public class ResponseBuilder {

    private static final Gson gson = new Gson();

    public static String successResponse(Response res, StatusEnum status, Object body) {
        res.status(200);
        res.type("application/json");
        return gson.toJson(new PrimoApiResponse<>(status.toString(), body, null));
    }

    public static String successResponse(Response res, StatusEnum status, Object body, String message) {
        res.status(200);
        res.type("application/json");
        return gson.toJson(new PrimoApiResponse<>(status.toString(), body, message));
    }

    public static String errorResponse(Response res, String message, String type, int statusCode) {
        res.status(statusCode);
        res.type("application/json");
        return gson.toJson(new PrimoApiResponse<>(FAILURE.toString(), new ApiErrorResponse(type, message)));
    }
}

