package com.calculator.My_Project;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.pgclient.PgPool;

import io.vertx.pgclient.PgConnectOptions;

import static javax.swing.DropMode.INSERT;


public class DatabaseVerticle extends AbstractVerticle {

  private PgPool client;
  private static final Logger logger = LoggerFactory.getLogger(DatabaseVerticle.class);

  @Override
  public void start() {
    logger.info(" inside database");
    // Configure connection options
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("Kranthi@517");

    // Configure pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(1);
    logger.info("before pool..");
    // Create client pool
    client = PgPool
      .pool(vertx, connectOptions, poolOptions);


    vertx.eventBus().consumer("databaselinkaddress", databasextractor ->
    {
      JsonObject dataremove = (JsonObject) databasextractor.body();
      System.out.println("dataremove.." + dataremove);
      String expression = dataremove.getString("expression");
      double result = dataremove.getDouble("result");
      String messageString = dataremove.getString("message");
      String result2 = Double.toString(result);
      // Log pool creation status

      // Listen for events from other verticles
//      vertx.eventBus().consumer("computatiod, message -> {
//        String result = (String) message.body();
      System.out.println("hey: ");
      String sql = "INSERT INTO user_expressions (result, expression) VALUES ($1, $2)";
      Tuple params = Tuple.of(result, expression);
      logger.info(sql);

      // Execute the query with the result

      client.withConnection(connection -> {
        logger.info("Hey, am inside pool..");
        connection.preparedQuery(sql).execute(params)
          .compose(res1 -> {
            logger.info(res1);
            return connection.query("select * from user_expressions").execute();
          })
          .compose(res2 -> {

            RowSet<Row> rowSet = res2;
            for (Row row : rowSet) {
              logger.info(row.toJson());
            }
            return connection.query("select result from user_expressions").execute();
          })
          .onComplete(ar -> {
            if (ar.succeeded()) {
              RowSet<Row> rowSet = ar.result();
              for (Row row : rowSet) {
                logger.info(row.toJson());
              }
//                logger.info(ar.result().next());
              System.out.println("Data inserted successfully!");
            } else {
              System.err.println("Failed to insert data: " + ar.cause().getMessage());
            }
          });

        return null;
      });

//      logger.info("completed query");

    });
  }


  @Override
  public void stop () {
    try {
      client.close();
      logger.info("Database connection pool closed successfully.");
    } catch (Exception e) {
      logger.error("Failed to close database connection pool: " + e.getMessage());
    }
  }
}
