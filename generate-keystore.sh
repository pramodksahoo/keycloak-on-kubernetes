#!/bin/bash
keytool -genkey -alias server -keyalg RSA -keysize 2048 -validity 3650 -keystore application.keystore -dname "CN=localhost,OU=Technology,O=BHN,L=Pleasanton,S=CA,C=US" -storepass password -keypass password -noprompt -ext SAN=dns:iam.bhn.technology
keytool -export -alias server -file keycloak.crt -keystore application.keystore -storepass password -noprompt
kubectl create secret generic keycloak-keystore -n iam --from-file application.keystore=application.keystore --from-file=keycloak.crt=keycloak.crt
kubectl delete pod/keycloak-0 -n iam
#kubectl label secret keycloak-keystore -n iam cert-manager-tls=kubed
kubectl annotate secret keycloak-keystore -n iam kubed.appscode.com/sync="cert-manager-tls=kubed"

#### junk ### 
# $ $(cat <<EOF | kubectl exec  -n iam -it keycloak-0 -- bash
# cd /opt/jboss/keycloak/standalone/configuration/;
# keytool -export -alias server -file keycloak.crt -keystore application.keystore -storepass password -noprompt
# EOF
# )
# $ kubectl -n iam cp keycloak-0:/opt/jboss/keycloak/standalone/configuration/keycloak.crt ./keycloak.crt
# $ kubectl -n iam cp keycloak-0:/opt/jboss/keycloak/standalone/configuration/application.keystore ./application.keystore

##### https://wyssmann.com/blog/2021/09/how-to-add-encoded-key-and-truststore-to-k8s-secret/ ##
