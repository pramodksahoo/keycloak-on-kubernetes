package org.bhn.resource.handler;

import org.bhn.resource.model.ResponseBodyBuilder;
import org.keycloak.http.HttpCookie;
import org.keycloak.http.HttpResponse;
import org.keycloak.models.KeycloakSession;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class ResponseBuilder {

    private final Response.Status status;

    private final String message;

    private final String code;

    private Object data;

    private KeycloakSession session;

    private ArrayList<HttpCookie> cookies;

    public ResponseBuilder(Response.Status status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;

    }

    public ResponseBuilder(Response.Status status, String message, String code, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;

    }

    public ResponseBuilder(Response.Status status, String message, String code, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.code = code;

    }

    public ResponseBuilder(Response.Status status, String message, String code, Object data, ArrayList<HttpCookie> cookies) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.cookies = cookies;
        this.code = code;

    }

    public ResponseBuilder(Response.Status status, String message, String code, String data, ArrayList<HttpCookie> cookies) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.cookies = cookies;
        this.code = code;

    }

    public Response build() {

        ResponseBodyBuilder responseBody = ResponseBodyBuilder.builder()
                .success(true)
                .status(this.status.getStatusCode())
                .data(this.data)
                .code(this.code)
                .message(this.message).build();

        Response.ResponseBuilder response = Response.status(status).entity(responseBody);

        return response.build();
    }

    public Response build(KeycloakSession session) {

        ResponseBodyBuilder responseBody = ResponseBodyBuilder.builder()
                .success(true)
                .status(this.status.getStatusCode())
                .data(this.data)
                .code(this.code)
                .message(this.message).build();

        Response.ResponseBuilder response = Response.status(status).entity(responseBody);


        if(cookies != null) {
            HttpResponse httpResponse = session.getContext().getHttpResponse();
            cookies.forEach(httpResponse::setCookieIfAbsent);
        }

        return response.build();
    }
}


