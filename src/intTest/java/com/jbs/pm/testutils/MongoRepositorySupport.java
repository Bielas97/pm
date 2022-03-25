package com.jbs.pm.testutils;

import com.github.cloudyrock.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import com.github.cloudyrock.standalone.MongockStandalone;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

public class MongoRepositorySupport
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String DATABASE = "test-db";
  private static final String DATABASE_USERNAME = "admin";
  private static final String DATABASE_PASSWORD = "pass";

  static GenericContainer<?> MONGO_DB_CONTAINER =
      new GenericContainer(DockerImageName.parse("mongo:5.0.6"))
          .withExposedPorts(27017)
          .withEnv("MONGO_INITDB_ROOT_USERNAME", DATABASE_USERNAME)
          .withEnv("MONGO_INITDB_ROOT_PASSWORD", DATABASE_PASSWORD)
          .withEnv("MONGO_INITDB_DATABASE", DATABASE);

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    MONGO_DB_CONTAINER.start();
    Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
    MONGO_DB_CONTAINER.followOutput(logConsumer);
    injectDbConfig(applicationContext);
    runMigrations();
  }

  private void injectDbConfig(ConfigurableApplicationContext applicationContext) {
    ConfigurableEnvironment environment = applicationContext.getEnvironment();
    MapPropertySource intDbConfig = new MapPropertySource("int-db-config", dbConfig());
    environment.getPropertySources().addFirst(intDbConfig);
  }

  private Map<String, Object> dbConfig() {
    return Map.of("spring.data.mongodb.uri", getConnectionUrl());
  }

  private String getConnectionUrl() {
    final int mongoPort = 27017;
    return String.format(
        "mongodb://admin:pass@%s:%d/%s?authSource=admin",
        MONGO_DB_CONTAINER.getContainerIpAddress(),
        MONGO_DB_CONTAINER.getMappedPort(mongoPort),
        "test-db");
  }

  private void runMigrations() {
    final ConnectionString connectionString = new ConnectionString(getConnectionUrl());

    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      MongockStandalone.builder()
          .setDriver(MongoSync4Driver.withDefaultLock(mongoClient, "test-db"))
          .addChangeLogsScanPackage("com.jbs.pm.adapter.output.db.changelogs")
          .buildRunner()
          .execute();
    }
  }
}
