# Deploy Keycloak with Kustomize

1. deploy and start

```bash
# dry run
$ ./run-eks.sh --dry-run -p {aws_profile_name}
# real deal
$ ./run-eks -p {aws_profile_name}
```

2. shutdown cleanup
```bash
$ ./stop-eks.sh
namespace "iam" deleted
serviceaccount "keycloak" deleted
role.rbac.authorization.k8s.io "keycloak-viewer" deleted
rolebinding.rbac.authorization.k8s.io "keycloak-view" deleted
configmap "cache-owners-k7ctg9ht26" deleted
configmap "env-bindings-cm-92mt28dh2m" deleted
service "keycloak" deleted
service "keycloak-discovery" deleted
statefulset.apps "keycloak" deleted
ingress.networking.k8s.io "ingress-nginx" deleted
```

3. check 
```bash
# see all namespace assets
$ kubectl get all -n iam
NAME             READY   STATUS    RESTARTS   AGE
pod/keycloak-0   1/1     Running   0          2m44s

NAME                         TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)             AGE
service/keycloak             ClusterIP   10.100.81.50   <none>        8080/TCP,8443/TCP   2m45s
service/keycloak-discovery   ClusterIP   None           <none>        8080/TCP,8443/TCP   2m45s

NAME                        READY   AGE
statefulset.apps/keycloak   1/1     2m45s

# see primary stateful set
$ kubectl get statefulsets -n iam
NAME       READY   AGE
keycloak   1/1     7m31s
```

4. scale
```bash
# scale to 2 replicas 
./scale.sh [-d] -s 2
statefulset.apps/keycloak scaled
# statefulsets
NAME       READY   AGE
keycloak   1/2     20m
# pods
NAME         READY   STATUS    RESTARTS   AGE
keycloak-0   1/1     Running   0          21m
keycloak-1   0/1     Running   0          50s
```


5. keystore 
```bash  ## one time event ##
## rm -rf application.keystore
$ ./generate-keystore.sh
## it will automatically delete the pod
```

## Reference Links ##
```
1. https://www.keycloak.org/2019/05/keycloak-cluster-setup.html
2. https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html/red_hat_data_grid_for_openshift/os_configuring_clusters#os_cluster_discovery_kube
3. https://blog.sighup.io/keycloak-ha-on-kubernetes/
4. https://github.com/thomasdarimont/keycloak-infini-kube
5. https://kubernetes.io/docs/tasks/manage-kubernetes-objects/kustomization/
6. https://github.com/L-U-C-K-Y/kustomize-with-multiple-envs/blob/main/base/kustomization.yaml
7. https://stackoverflow.com/questions/54398272/override-env-values-defined-in-container-spec
8. https://kubernetes.io/docs/tutorials/stateful-application/basic-stateful-set/
9. https://kubernetes.io/docs/tasks/run-application/scale-stateful-set/
10. https://www.tothenew.com/blog/keycloak-high-availability-setup-in-kubernetes/
11. https://www.keycloak.org/docs/latest/server_installation/#_clustering
12. http://docs.wildfly.org/23/High_Availability_Guide.html#JGroups_Subsystem
13. tput init
14. https://wyssmann.com/blog/2021/09/how-to-add-encoded-key-and-truststore-to-k8s-secret/
```

## Themes
https://keycloakthemes.com/blog/how-to-turn-off-the-keycloak-theme-cache
cd themes/bhn-iam && cp * keycloak-0:/opt/jboss/keycloak/themes/bhn-iam/ -n iam
