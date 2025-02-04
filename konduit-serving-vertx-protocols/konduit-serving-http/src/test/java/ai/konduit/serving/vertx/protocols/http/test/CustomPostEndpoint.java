/*
 *  ******************************************************************************
 *  * Copyright (c) 2022 Konduit K.K.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package ai.konduit.serving.vertx.protocols.http.test;

import ai.konduit.serving.endpoint.Endpoint;
import ai.konduit.serving.pipeline.api.data.Data;
import ai.konduit.serving.vertx.protocols.http.api.InferenceHttpApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class CustomPostEndpoint implements Endpoint {
    public static final String PATH = "custom_post_endpoint";
    public static Data input;
    public static String output;

    @Override
    public HttpMethod type() {
        return HttpMethod.POST;
    }

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public List<String> consumes() {
        return Collections.singletonList(APPLICATION_JSON.toString());
    }

    @Override
    public List<String> produces() {
        return Collections.singletonList(HttpHeaderValues.TEXT_PLAIN.toString());
    }

    @Override
    public Handler<RoutingContext> handler() {
        return ctx -> {
            input = InferenceHttpApi.extractData(APPLICATION_JSON.toString(), ctx);
            output = "Input as JSON: " + input.toJson();
            ctx.response()
                    .setStatusCode(200)
                    .putHeader(CONTENT_TYPE, TEXT_PLAIN.toString())
                    .end(output, StandardCharsets.UTF_8.name());
        };
    }
}
