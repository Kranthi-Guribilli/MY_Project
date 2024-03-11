package com.calculator.My_Project;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

public class CalculatorVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(CalculatorVerticle.class);

  public void start(Promise<Void> startPromise) {
    logger.info("Calculator verticle deployed");
    // vertx.eventBus().consumer()
    vertx.eventBus().consumer("calculate", extractor ->
    {
      JsonObject data = (JsonObject) extractor.body();

      String x = data.getString("xvalue");
      String y = data.getString("yvalue");

      double xValue = Double.parseDouble(x);
      double yValue = Double.parseDouble(y);
      String messageString = data.getString("message");
      System.out.println("x: "+ xValue);
      System.out.println("y: "+ yValue);
      double result = xValue + yValue; // Perform calculation (you can modify this to perform any operation)
      // Reply back with the result
      extractor.reply(result);
      String expression = String.format("%.2f + %.2f", xValue, yValue);

      JsonObject databasepayload = new JsonObject();
      databasepayload.put("expression", expression);
      databasepayload.put("result", result);

      logger.info("payload.."+databasepayload);
      vertx.eventBus().request("databaselinkaddress", databasepayload );
    });
  }
}

