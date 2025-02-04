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

package ai.konduit.serving.vertx.protocols.http.api;

/**
 * This class represents the exceptions which occurs when the inference HTTP API fails at some point
 */
public class KonduitServingHttpException extends IllegalStateException {

    /**
     * The error response object associated with the excpetion.
     */
    private final ErrorResponse errorResponse;

    public KonduitServingHttpException(HttpApiErrorCode errorCode, Throwable throwable) {
        super(throwable);
        errorResponse = ErrorResponse.builder().errorCode(errorCode)
                .errorMessage(throwable.getMessage() != null ? throwable.getMessage() : throwable.toString())
                .build();
    }

    public KonduitServingHttpException(HttpApiErrorCode errorCode, String errorMessage) {
        super(String.format("Error Code: %s%n Error Message: %s", errorCode.name(), errorMessage));
        errorResponse = ErrorResponse.builder().errorCode(errorCode).errorMessage(errorMessage).build();
    }

    /**
     * Get the error response object.
     */
    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }
}
