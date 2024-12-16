package org.bhn.eventlistener;

import com.google.auto.service.AutoService;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

@AutoService(Spi.class)
public class SQSSpi implements Spi {

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return "sqs-processor-spi";
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return SQSSpiProvider.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return SQSSpiProviderFactory.class;
    }
}