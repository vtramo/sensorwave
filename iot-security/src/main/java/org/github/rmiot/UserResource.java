package org.github.rmiot;


import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.github.rmiot.service.UserService;
import org.openapi.quarkus.iot_security_api_yaml.model.User;

import static org.openapi.quarkus.iot_security_api_yaml.api.UsersApi.RegisterUserMultipartForm;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Path("register")
    public User register(final RegisterUserMultipartForm credentials) {
        return userService.registerUser(credentials.username, credentials.password);
    }
}
