package com.calculator.My_Project;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MainVerticle extends AbstractVerticle {
  final Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    System.out.println("Starting vertx application!!");
    //Vertx vertx = Vertx.vertx();

    Promise<String> inputVerticleDeployment = Promise.promise();
    vertx.deployVerticle(new InputVerticle(), inputVerticleDeployment);

    Promise<String> calculatorVerticleDeployment = Promise.promise();
    vertx.deployVerticle(new CalculatorVerticle(), calculatorVerticleDeployment);

    Promise<String> databaseVerticleDeployment = Promise.promise();
    vertx.deployVerticle(new DatabaseVerticle(), databaseVerticleDeployment);

    CompositeFuture.all(inputVerticleDeployment.future(), calculatorVerticleDeployment.future(), databaseVerticleDeployment.future())
      .onSuccess(ar -> {
        System.out.println("All verticles deployed successfully");
        startPromise.complete();
      })
      .onFailure(err -> {
        System.err.println("Verticle deployment failed: " + err.getMessage());
        startPromise.fail(err);
      });
  }
}
