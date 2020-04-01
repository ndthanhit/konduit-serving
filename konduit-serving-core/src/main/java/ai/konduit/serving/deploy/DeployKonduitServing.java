/*
 * *****************************************************************************
 * Copyright (c) 2020 Konduit K.K.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */

package ai.konduit.serving.deploy;

import ai.konduit.serving.InferenceConfiguration;
import ai.konduit.serving.util.ObjectMappers;
import ai.konduit.serving.verticles.inference.InferenceVerticle;
import io.vertx.core.*;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import lombok.extern.slf4j.Slf4j;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static io.vertx.core.file.FileSystemOptions.DEFAULT_FILE_CACHING_DIR;
import static io.vertx.core.file.impl.FileResolver.CACHE_DIR_BASE_PROP_NAME;
import static io.vertx.core.file.impl.FileResolver.DISABLE_CP_RESOLVING_PROP_NAME;
import static io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME;
import static java.lang.System.setProperty;

@Slf4j
public class DeployKonduitServing {

    static {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
        LoggerFactory.getLogger(LoggerFactory.class); // Required for Logback to work in Vertx

        setProperty("vertx.cwd", new File(".").getAbsolutePath());
        setProperty(CACHE_DIR_BASE_PROP_NAME, DEFAULT_FILE_CACHING_DIR);
        setProperty(DISABLE_CP_RESOLVING_PROP_NAME, Boolean.TRUE.toString());
    }

    public static void deployInference(DeploymentOptions deploymentOptions, Handler<AsyncResult<InferenceConfiguration>> eventHandler) {
        deployInference(new VertxOptions().setMaxEventLoopExecuteTime(120).setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS),
                deploymentOptions, eventHandler);
    }

    public static void deployInference(InferenceConfiguration inferenceConfiguration, Handler<AsyncResult<InferenceConfiguration>> eventHandler) {
        deployInference(new VertxOptions().setMaxEventLoopExecuteTime(120).setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS),
                new DeploymentOptions().setConfig(new JsonObject(inferenceConfiguration.toJson())), eventHandler);
    }

    public static void deployInference(VertxOptions vertxOptions, InferenceConfiguration inferenceConfiguration, Handler<AsyncResult<InferenceConfiguration>> eventHandler) {
        deployInference(vertxOptions, new DeploymentOptions().setConfig(new JsonObject(inferenceConfiguration.toJson())), eventHandler);
    }

    public static void deployInference(VertxOptions vertxOptions, DeploymentOptions deploymentOptions, Handler<AsyncResult<InferenceConfiguration>> eventHandler) {
        Vertx vertx = Vertx.vertx(vertxOptions);

        vertx.deployVerticle(clazz(), deploymentOptions, handler -> {
            if (handler.failed()) {
                log.error(String.format("Unable to deploy verticle %s", className()), handler.cause());

                if (eventHandler != null) {
                    eventHandler.handle(Future.failedFuture(handler.cause()));
                }

                vertx.close();
            } else {
                log.info(String.format("Deployed verticle %s", className()));
                if (eventHandler != null) {
                    VertxImpl vertxImpl = (VertxImpl) vertx;
                    DeploymentOptions inferenceDeploymentOptions = vertxImpl.getDeployment(handler.result()).deploymentOptions();

                    try {
                        InferenceConfiguration inferenceConfiguration = ObjectMappers.fromJson(inferenceDeploymentOptions.getConfig().encode(), InferenceConfiguration.class);
                        eventHandler.handle(Future.succeededFuture(inferenceConfiguration));
                    } catch (Exception exception){
                        log.debug("Unable to parse json configuration into an InferenceConfiguration object. " +
                                "This can be ignored if the verticle isn't an InferenceVerticle.", exception);
                        eventHandler.handle(Future.succeededFuture());
                    }
                }
            }
        });
    }

    private static Class<? extends Verticle> clazz() {
        return InferenceVerticle.class;
    }

    private static String className() {
        return clazz().getCanonicalName();
    }
}