package com.commercetools.sdk.contrib;

import io.sphere.sdk.client.*;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public final class SphereClientRule extends ExternalResource implements BlockingSphereClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SphereClientRule.class);
    private BlockingSphereClient client;

    @Override
    public void close() {
        LOGGER.warn("it is not recommended to close the client directly in " + getClass().getName());
        client.close();
    }

    @Override
    public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
        return client.execute(sphereRequest);
    }

    @Override
    public <T> T executeBlocking(final SphereRequest<T> sphereRequest) {
        return client.executeBlocking(sphereRequest);
    }

    @Override
    public <T> T executeBlocking(final SphereRequest<T> sphereRequest, final long l, final TimeUnit timeUnit) {
        return client.executeBlocking(sphereRequest, l, timeUnit);
    }

    @Override
    protected void after() {
        client.close();
    }

    @Override
    protected void before() throws Throwable {
        try (final FileInputStream fileInputStream = new FileInputStream(new File("integrationtest.properties"))) {
            final Properties properties = new Properties();
            properties.load(fileInputStream);
            final SphereClientConfig config =  SphereClientConfig.ofProperties(properties, "");
            final SphereClient underlying = SphereClientFactory.of().createClient(config);
            client = BlockingSphereClient.of(underlying, 20, TimeUnit.SECONDS);
        }
    }
}
