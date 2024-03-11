package com.calculator.My_Project;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
//import io.vertx.core.http.HttpRequest;
public class InputVerticle extends AbstractVerticle {

  public static final Logger logger = LogManager.getLogger(InputVerticle.class);



  //private PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) {

    logger.info("Input Verticle deployed");
    Router router = Router.router(vertx);
    router.get("/calculate").handler(this::handleCalculateRequest);

    vertx.createHttpServer().requestHandler(router).listen(8080).onComplete(ok ->{
        startPromise.complete();
      })
      .onFailure( cause -> startPromise.fail("failed to establish connection to the request handlers"));
}
    private void handleCalculateRequest(RoutingContext routingContext) {
      String xValue = routingContext.request().getParam("x");
      String yValue = routingContext.request().getParam("y");
      logger.info("routing context: "+xValue);
      logger.info("Input parameters extracted from the request");
      // Send the input parameters to the calculation verticle
      JsonObject message = new JsonObject();

      message.put("xvalue", xValue);
      message.put("yvalue", yValue);
      message.put("messsage", "Values have been extracted and sent");
      EventBus eventBus = vertx.eventBus();

      eventBus.request("calculate",message, ok -> {
        if (ok.succeeded()) {
          routingContext.response().putHeader("content-type", "text/plain").end("Result: " + ok.result().body());
        } else {
          routingContext.response().setStatusCode(500).end(" request did not fall through");

        }
      });
    }
  }
