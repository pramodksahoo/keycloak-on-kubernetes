FROM quay.io/keycloak/keycloak:21.1.1

COPY service/target/org.bhn-custom-service.jar /opt/keycloak/providers
COPY themes/bhn-iam /opt/keycloak/themes/ 
COPY themes/bhn /opt/keycloak/themes/bhn
COPY themes/gc.com /opt/keycloak/themes/gc.com
COPY themes/mpc /opt/keycloak/themes/mpc
COPY certs/ /opt/certs/
# New Relic
COPY bin/newrelic.jar /opt/newrelic/newrelic.jar

# Configurations
COPY conf/* /opt/keycloak/conf/

ENV KC_HOSTNAME_STRICT=false
ENV KC_HTTPS_CERTIFICATE_FILE=/opt/certs/server.crt.pem
ENV KC_HTTPS_CERTIFICATE_KEY_FILE=/opt/certs/server.key.pem
# CMD ["-b", "0.0.0.0", "--start-mode normal"]

# Enable health
ENV KC_HEALTH_ENABLED=true



WORKDIR /opt
COPY startApp.sh /opt/startApp.sh

ENTRYPOINT ["bash", "-c", "/opt/startApp.sh"]