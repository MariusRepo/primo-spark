package org.primo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrimoApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private ApiErrorResponse error;

    public PrimoApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public PrimoApiResponse(String status, T data, String message) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
