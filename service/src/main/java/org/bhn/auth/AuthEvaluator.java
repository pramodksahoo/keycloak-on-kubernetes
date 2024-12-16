package org.bhn.auth;

import lombok.Getter;
import org.bhn.auth.permissions.ClientPermissions;
import org.bhn.auth.permissions.GroupPermissions;
import org.bhn.auth.permissions.MgmtPermissions;
import org.bhn.auth.permissions.RolePermissions;
import org.bhn.auth.permissions.UserPermissions;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.policy.evaluation.DefaultPolicyEvaluator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.permissions.*;

public class AuthEvaluator extends AdminAuth implements AdminPermissionEvaluator {

    private static AccessToken accessToken;

    @Getter
    private final AuthorizationProvider authorizationProvider;
    private final MgmtPermissions root;

    public AuthEvaluator(KeycloakSession session, RealmModel realmModel) {
        super(realmModel,
                session.getAttribute("token", AccessToken.class),
                session.users().getUserByEmail(realmModel, session.getAttribute("token", AccessToken.class).getEmail()),
                session.getContext().getClient());
        this.root = new MgmtPermissions(session, realmModel, this);
        this.authorizationProvider = new AuthorizationProvider(session, this.getRealm(), new DefaultPolicyEvaluator());
    }

    @Override
    public RealmPermissionEvaluator realm() {
        return null;
    }

    @Override
    public void requireAnyAdminRole() {
    }

    @Override
    public AdminAuth adminAuth() {
        return null;
    }

    @Override
    public RolePermissionEvaluator roles() {
        return new RolePermissions(this.root, accessToken, this.authorizationProvider, this.getRealm());
    }

    @Override
    public UserPermissionEvaluator users() {
        return new UserPermissions(this.root, accessToken, this.authorizationProvider, this.getRealm());
    }

    @Override
    public ClientPermissionEvaluator clients() {
        return new ClientPermissions(this.root, accessToken, this.authorizationProvider, this.getRealm());
    }

    @Override
    public GroupPermissionEvaluator groups() {
        return new GroupPermissions(this.root, accessToken, this.authorizationProvider, this.getRealm());
    }

}
