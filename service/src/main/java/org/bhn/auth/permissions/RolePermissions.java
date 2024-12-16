package org.bhn.auth.permissions;

import org.jboss.logging.Logger;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.permissions.RolePermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.RolePermissionManagement;

import java.util.Map;

public class RolePermissions implements RolePermissionEvaluator, RolePermissionManagement {

    private static final Logger logger = Logger.getLogger(RolePermissions.class);

    protected final KeycloakSession session;
    protected final RealmModel realm;
    protected final AccessToken accessToken;
    protected final AuthorizationProvider authz;
    protected final MgmtPermissions root;

    public RolePermissions(MgmtPermissions root, AccessToken accessToken, AuthorizationProvider authz, RealmModel realmModel) {
        this.session = authz.getKeycloakSession();
        this.accessToken = accessToken;
        this.authz = authz;
        this.root = root;
        this.realm = realmModel;
    }


    @Override
    public boolean canList(RoleContainerModel roleContainerModel) {
        return false;
    }

    @Override
    public void requireList(RoleContainerModel roleContainerModel) {

    }

    @Override
    public boolean canMapRole(RoleModel roleModel) {
        return true;//TODO: just put true for now
    }

    @Override
    public void requireMapRole(RoleModel roleModel) {

    }

    @Override
    public boolean canManage(RoleModel roleModel) {
        return false;
    }

    @Override
    public void requireManage(RoleModel roleModel) {

    }

    @Override
    public boolean canView(RoleModel roleModel) {
        return false;
    }

    @Override
    public void requireView(RoleModel roleModel) {

    }

    @Override
    public boolean canMapClientScope(RoleModel roleModel) {
        return false;
    }

    @Override
    public void requireMapClientScope(RoleModel roleModel) {

    }

    @Override
    public boolean canMapComposite(RoleModel roleModel) {
        return false;
    }

    @Override
    public void requireMapComposite(RoleModel roleModel) {

    }

    @Override
    public boolean canManage(RoleContainerModel roleContainerModel) {
        return false;
    }

    @Override
    public void requireManage(RoleContainerModel roleContainerModel) {

    }

    @Override
    public boolean canView(RoleContainerModel roleContainerModel) {
        return false;
    }

    @Override
    public void requireView(RoleContainerModel roleContainerModel) {

    }

    @Override
    public boolean isPermissionsEnabled(RoleModel roleModel) {
        return false;
    }

    @Override
    public void setPermissionsEnabled(RoleModel roleModel, boolean b) {

    }

    @Override
    public Map<String, String> getPermissions(RoleModel roleModel) {
        return Map.of();
    }

    @Override
    public Policy mapRolePermission(RoleModel roleModel) {
        return null;
    }

    @Override
    public Policy mapCompositePermission(RoleModel roleModel) {
        return null;
    }

    @Override
    public Policy mapClientScopePermission(RoleModel roleModel) {
        return null;
    }

    @Override
    public Resource resource(RoleModel roleModel) {
        return null;
    }

    @Override
    public ResourceServer resourceServer(RoleModel roleModel) {
        return null;
    }

    @Override
    public Policy manageUsersPolicy(ResourceServer resourceServer) {
        return null;
    }

    @Override
    public Policy viewUsersPolicy(ResourceServer resourceServer) {
        return null;
    }

    @Override
    public Policy rolePolicy(ResourceServer resourceServer, RoleModel roleModel) {
        return null;
    }
}
