package com.zkconfigserver.verticle;

import com.zkconfigserver.config.Configuration;
import com.zkconfigserver.controller.AuthenController;
import com.zkconfigserver.controller.ExceptionController;
import com.zkconfigserver.controller.ZkNodeController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * Created by madali on 2016/5/23.
 */
public class MainVerticle extends AbstractVerticle {
    private static final AuthenController authenController = new AuthenController();
    private static final ZkNodeController zkNodeController = new ZkNodeController();
    private static final ExceptionController exceptionController = new ExceptionController();

    private static final int SERVER_PORT = Configuration.SERVER_PORT;
    private static final String BASE_PATH = "/v1";

    private static final Vertx vertx = Vertx.vertx();
    private static final HttpServer httpServer = vertx.createHttpServer();
    private static final Router router = Router.router(vertx);

    public MainVerticle() {
    }

    @Override
    public void start() {
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("X-PINGARUNER")
                .allowedHeader("Content-Type")
                //设置Authorization可跨域
                .allowedHeader("Authorization")
        );
        router.route().handler(BodyHandler.create());

        router.route().failureHandler(exceptionController::error);

        router.route(HttpMethod.POST, BASE_PATH + "/login").blockingHandler(authenController::login, false);
        router.route(HttpMethod.DELETE, BASE_PATH + "/logout").blockingHandler(authenController::logout, false);

        // /serviceConfig /infrastructure
        router.route(HttpMethod.GET, BASE_PATH + "/configuration").blockingHandler(zkNodeController::selectConfiguration, false);
        //connected
        router.route(HttpMethod.GET, BASE_PATH + "/connection").blockingHandler(zkNodeController::selectConnection, false);

        //修改  serviceConfig infrastructure
        router.route(HttpMethod.PUT, BASE_PATH + "/configuration").blockingHandler(zkNodeController::updateConfiguration, false);

        httpServer.requestHandler(router::accept).listen(SERVER_PORT);
    }
}
