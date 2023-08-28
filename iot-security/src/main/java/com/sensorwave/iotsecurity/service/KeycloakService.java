package com.sensorwave.iotsecurity.service;

import com.sensorwave.iotsecurity.service.exceptions.KeycloakAdminTokenGenerationException;
import com.sensorwave.iotsecurity.service.exceptions.KeycloakUserCreationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.sensorwave.iotsecurity.config.KeycloakConfig;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.openapi.quarkus.keycloak_api_yaml.api.KeycloakClientAccessTokenApi;
import org.openapi.quarkus.keycloak_api_yaml.api.KeycloakUsersApi;
import org.openapi.quarkus.keycloak_api_yaml.model.ClientAccessToken;
import org.openapi.quarkus.keycloak_api_yaml.model.CredentialRepresentation;
import org.openapi.quarkus.keycloak_api_yaml.model.UserRepresentation;

import java.util.List;

import static org.openapi.quarkus.keycloak_api_yaml.api.KeycloakClientAccessTokenApi.GenerateClientAccessTokenMultipartForm;

@ApplicationScoped
class KeycloakService {

    @Inject
    KeycloakConfig keycloakConfig;

    @RestClient
    @Inject
    KeycloakUsersApi keycloakUsersApi;

    @RestClient
    @Inject
    KeycloakClientAccessTokenApi keycloakClientAccessTokenApi;

    public String createUser(final String realm, final String username, final String password) {
        final UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);
        userRepresentation.setEnabled(true);
        final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(password);
        credentialRepresentation.setType(KeycloakConfig.CREDENTIAL_REPRESENTATION_TYPE_PASSWORD);
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        return createUser(realm, userRepresentation);
    }

    public String createUser(final String realm, final UserRepresentation userRepresentation) {
        final String adminAccessToken = generateAdminClientAccessToken();
        try (final Response response = keycloakUsersApi.createUser(realm, "Bearer " + adminAccessToken, userRepresentation)) {
            final String userLocation = response.getHeaderString("Location");
            final String[] splittedURL = userLocation.split("/");
            final String createdUserId = splittedURL[splittedURL.length - 1];
            return createdUserId;
        } catch (ClientWebApplicationException e) {
            throw new KeycloakUserCreationException(e, userRepresentation);
        }
    }

    public boolean deleteUser(final String realm, final String userId) {
        final String adminAccessToken = generateAdminClientAccessToken();
        try (final Response response = keycloakUsersApi.deleteUserById(keycloakConfig.realm(), userId, "Bearer " + adminAccessToken)) {
            return response.getStatus() == HttpResponseStatus.OK.code();
        }
    }

    private String generateAdminClientAccessToken() {
        final KeycloakConfig.AdminCli adminCliConfig = keycloakConfig.adminCliConfig();
        final var clientCredentials = new GenerateClientAccessTokenMultipartForm();
        clientCredentials.clientId = adminCliConfig.id();
        clientCredentials.clientSecret = adminCliConfig.secret();
        clientCredentials.grantType = KeycloakConfig.DEFAULT_GRANT_TYPE;
        try (final Response response = keycloakClientAccessTokenApi.generateClientAccessToken(clientCredentials, keycloakConfig.realm())) {
            final ClientAccessToken clientAccessToken = response.readEntity(ClientAccessToken.class);
            return clientAccessToken.getAccessToken();
        } catch (ClientWebApplicationException e) {
            throw new KeycloakAdminTokenGenerationException(e);
        }
    }
}
