package org.bhn.auth.permissions;

import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.models.ClientModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resources.admin.permissions.IdentityProviderPermissionManagement;

import java.util.Map;


public class IdentityProviderPermissions implements IdentityProviderPermissionManagement {

    protected final KeycloakSession session;
    protected final RealmModel realm;
    protected final AuthorizationProvider authz;
    protected final MgmtPermissions root;

    public IdentityProviderPermissions(KeycloakSession session, RealmModel realm, AuthorizationProvider authz, MgmtPermissions root) {
        this.session = session;
        this.realm = realm;
        this.authz = authz;
        this.root = root;
    }


    @Override
    public boolean isPermissionsEnabled(IdentityProviderModel identityProviderModel) {
        return false;
    }

    @Override
    public void setPermissionsEnabled(IdentityProviderModel identityProviderModel, boolean b) {

    }

    @Override
    public Resource resource(IdentityProviderModel identityProviderModel) {
        return null;
    }

    @Override
    public Map<String, String> getPermissions(IdentityProviderModel identityProviderModel) {
        return Map.of();
    }

    @Override
    public boolean canExchangeTo(ClientModel clientModel, IdentityProviderModel identityProviderModel) {
        return false;
    }

    @Override
    public Policy exchangeToPermission(IdentityProviderModel identityProviderModel) {
        return null;
    }
}
