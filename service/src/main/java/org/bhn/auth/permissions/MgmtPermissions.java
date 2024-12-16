package org.bhn.auth.permissions;

import org.bhn.auth.AuthEvaluator;
import org.bhn.resource.model.KeycloakIdentity;
import org.keycloak.Config;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.AuthorizationProviderFactory;
import org.keycloak.authorization.common.DefaultEvaluationContext;
import org.keycloak.authorization.common.UserModelIdentity;
import org.keycloak.authorization.identity.Identity;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.model.Scope;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.policy.evaluation.EvaluationContext;
import org.keycloak.common.Profile;
import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.admin.AdminAuth;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.keycloak.services.resources.admin.permissions.AdminPermissionManagement;
import org.keycloak.services.resources.admin.permissions.RealmsPermissionEvaluator;

import javax.ws.rs.ForbiddenException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MgmtPermissions implements AdminPermissionEvaluator, AdminPermissionManagement, RealmsPermissionEvaluator {
    protected RealmModel realm;
    protected KeycloakSession session;
    protected AuthorizationProvider authz;
    protected AdminAuth auth;
    protected Identity identity;
    protected UserModel admin;
    protected RealmModel adminsRealm;
    protected ResourceServer realmResourceServer;
    protected UserPermissions users;
    protected GroupPermissions groups;

    public MgmtPermissions(KeycloakSession session, RealmModel realm, AdminAuth auth) {
        this(session, realm);
        this.auth = auth;
        this.admin = auth.getUser();
        this.adminsRealm = auth.getRealm();
        if (!auth.getRealm().equals(realm)
                && !RealmManager.isAdministrationRealm(auth.getRealm())) {
            throw new ForbiddenException();
        }
        initIdentity(session, auth);
    }

    public MgmtPermissions(KeycloakSession session, RealmModel realm) {
        this.session = session;
        this.realm = realm;
        this.adminsRealm = realm;
        KeycloakSessionFactory keycloakSessionFactory = session.getKeycloakSessionFactory();
        if (Profile.isFeatureEnabled(Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ)) {
            AuthorizationProviderFactory factory = (AuthorizationProviderFactory) keycloakSessionFactory.getProviderFactory(AuthorizationProvider.class);
            this.authz = factory.create(session, realm);
        }
    }

    private void initIdentity(KeycloakSession session, AdminAuth auth) {
        if (Constants.ADMIN_CLI_CLIENT_ID.equals(auth.getToken().getIssuedFor())
                || Constants.ADMIN_CONSOLE_CLIENT_ID.equals(auth.getToken().getIssuedFor())) {
            this.identity = new UserModelIdentity(auth.getRealm(), auth.getUser());

        } else {
            this.identity = new KeycloakIdentity(auth.getToken(), session);
        }
    }

    @Override
    public ClientModel getRealmManagementClient() {
        ClientModel client = null;
        if (realm.getName().equals(Config.getAdminRealm())) {
            client = realm.getClientByClientId(Config.getAdminRealm() + "-realm");
        } else {
            client = realm.getClientByClientId(Constants.REALM_MANAGEMENT_CLIENT_ID);

        }
        return client;
    }

    @Override
    public AuthorizationProvider authz() {
        return authz;
    }



    @Override
    public void requireAnyAdminRole() {
        if (!hasAnyAdminRole()) {
            throw new ForbiddenException();
        }
    }

    public boolean hasAnyAdminRole() {
        return hasOneAdminRole(AdminRoles.ALL_REALM_ROLES);
    }

    public boolean hasAnyAdminRole(RealmModel realm) {
        return hasOneAdminRole(realm, AdminRoles.ALL_REALM_ROLES);
    }

    public boolean hasOneAdminRole(String... adminRoles) {
        RealmModel realm = this.realm;
        return hasOneAdminRole(realm, adminRoles);
    }

    public boolean hasOneAdminRole(RealmModel realm, String... adminRoles) {
        String clientId;
        RealmManager realmManager = new RealmManager(session);
        if (RealmManager.isAdministrationRealm(adminsRealm)) {
            clientId = realm.getMasterAdminClient().getClientId();
        } else if (adminsRealm.equals(realm)) {
            clientId = realm.getClientByClientId(realmManager.getRealmAdminClientId(realm)).getClientId();
        } else {
            return false;
        }
        return identity.hasOneClientRole(clientId, adminRoles);
    }


    public boolean isAdminSameRealm() {
        return auth == null || realm.getId().equals(auth.getRealm().getId());
    }

    @Override
    public AdminAuth adminAuth() {
        return auth;
    }

    public Identity identity() {
        return identity;
    }

    public UserModel admin() {
        return admin;
    }

    public RealmModel adminsRealm() {
        return adminsRealm;
    }

    @Override
    public RolePermissions roles() {
        return new RolePermissions(this, session.getAttribute("token", AccessToken.class), session.getAttribute("auth", AuthEvaluator.class).getAuthorizationProvider(), realm);
    }

    @Override
    public UserPermissions users() {
        return new UserPermissions(this, session.getAttribute("token", AccessToken.class), session.getAttribute("auth", AuthEvaluator.class).getAuthorizationProvider(), realm);
    }

    @Override
    public RealmPermissions realm() {
        return new RealmPermissions(this, session.getAttribute("token", AccessToken.class), session.getAttribute("auth", AuthEvaluator.class).getAuthorizationProvider(), realm);
    }

    @Override
    public ClientPermissions clients() {
        return new ClientPermissions(this, session.getAttribute("token", AccessToken.class), session.getAttribute("auth", AuthEvaluator.class).getAuthorizationProvider(), realm);
    }

    @Override
    public IdentityProviderPermissions idps() {
        return null;
    }

    @Override
    public GroupPermissions groups() {
        return null;
    }

    public ResourceServer findOrCreateResourceServer(ClientModel client) {
         return initializeRealmResourceServer();
    }

    public ResourceServer resourceServer(ClientModel client) {
        return realmResourceServer();
    }

    @Override
    public ResourceServer realmResourceServer() {
        if (authz == null) return null;
        if (realmResourceServer != null) return realmResourceServer;
        ClientModel client = getRealmManagementClient();
        if (client == null) return null;
        realmResourceServer = authz.getStoreFactory().getResourceServerStore().findByClient(client);
        return realmResourceServer;

    }

    public ResourceServer initializeRealmResourceServer() {
        if (authz == null) return null;
        if (realmResourceServer != null) return realmResourceServer;
        ClientModel client = getRealmManagementClient();
        if (client == null) return null;
        realmResourceServer = authz.getStoreFactory().getResourceServerStore().findByClient(client);
        if (realmResourceServer == null) {
            realmResourceServer = authz.getStoreFactory().getResourceServerStore().create(client);
        }
        return realmResourceServer;
    }

    protected Scope manageScope;
    protected Scope viewScope;

    public void initializeRealmDefaultScopes() {
        ResourceServer server = initializeRealmResourceServer();
        if (server == null) return;
        manageScope = initializeRealmScope(MgmtPermissions.MANAGE_SCOPE);
        viewScope = initializeRealmScope(MgmtPermissions.VIEW_SCOPE);
    }

    public Scope initializeRealmScope(String name) {
        ResourceServer server = initializeRealmResourceServer();
        if (server == null) return null;
        Scope scope  = authz.getStoreFactory().getScopeStore().findByName(server, name);
        if (scope == null) {
            scope = authz.getStoreFactory().getScopeStore().create(server, name);
        }
        return scope;
    }

    public Scope initializeScope(String name, ResourceServer server) {
        if (authz == null) return null;
        Scope scope  = authz.getStoreFactory().getScopeStore().findByName(server, name);
        if (scope == null) {
            scope = authz.getStoreFactory().getScopeStore().create(server, name);
        }
        return scope;
    }



    public Scope realmManageScope() {
        if (manageScope != null) return manageScope;
        manageScope = realmScope(MgmtPermissions.MANAGE_SCOPE);
        return manageScope;
    }


    public Scope realmViewScope() {
        if (viewScope != null) return viewScope;
        viewScope = realmScope(MgmtPermissions.VIEW_SCOPE);
        return viewScope;
    }

    public Scope realmScope(String scope) {
        ResourceServer server = realmResourceServer();
        if (server == null) return null;
        return authz.getStoreFactory().getScopeStore().findByName(server, scope);
    }

    public boolean evaluatePermission(Resource resource, ResourceServer resourceServer, Scope... scope) {
        Identity identity = identity();
        if (identity == null) {
            throw new RuntimeException("Identity of admin is not set for permission query");
        }
        return evaluatePermission(resource, resourceServer, identity, scope);
    }

    public Collection<Permission> evaluatePermission(ResourcePermission permission, ResourceServer resourceServer) {
        return evaluatePermission(permission, resourceServer, new DefaultEvaluationContext(identity, session));
    }

    public Collection<Permission> evaluatePermission(ResourcePermission permission, ResourceServer resourceServer, EvaluationContext context) {
        return evaluatePermission(Arrays.asList(permission), resourceServer, context);
    }

    public boolean evaluatePermission(Resource resource, ResourceServer resourceServer, Identity identity, Scope... scope) {
        EvaluationContext context = new DefaultEvaluationContext(identity, session);
        return evaluatePermission(resource, resourceServer, context, scope);
    }

    public boolean evaluatePermission(Resource resource, ResourceServer resourceServer, EvaluationContext context, Scope... scope) {
        return !evaluatePermission(Arrays.asList(new ResourcePermission(resource, Arrays.asList(scope), resourceServer)), resourceServer, context).isEmpty();
    }

    public Collection<Permission> evaluatePermission(List<ResourcePermission> permissions, ResourceServer resourceServer, EvaluationContext context) {
        RealmModel oldRealm = session.getContext().getRealm();
        try {
            session.getContext().setRealm(realm);
            return authz.evaluators().from(permissions, context).evaluate(resourceServer, null);
        } finally {
            session.getContext().setRealm(oldRealm);
        }
    }

    @Override
    public boolean canView(RealmModel realm) {
        return hasOneAdminRole(realm, AdminRoles.VIEW_REALM, AdminRoles.MANAGE_REALM);
    }

    @Override
    public boolean isAdmin(RealmModel realm) {
        return hasAnyAdminRole(realm);
    }

    @Override
    public boolean isAdmin() {
        if (RealmManager.isAdministrationRealm(adminsRealm)) {
            if (identity.hasRealmRole(AdminRoles.ADMIN) || identity.hasRealmRole(AdminRoles.CREATE_REALM)) {
                return true;
            }
            return session.realms().getRealmsStream().anyMatch(this::isAdmin);
        } else {
            return isAdmin(adminsRealm);
        }
    }

    @Override
    public boolean canCreateRealm() {
        if (!RealmManager.isAdministrationRealm(auth.getRealm())) {
           return false;
        }
        return identity.hasRealmRole(AdminRoles.CREATE_REALM);
    }

    @Override
    public void requireCreateRealm() {
        if (!canCreateRealm()) {
            throw new ForbiddenException();
        }
    }




}
