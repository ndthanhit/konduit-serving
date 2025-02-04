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

package ai.konduit.serving.pipeline.impl.testpipelines.callback;

import ai.konduit.serving.pipeline.api.data.Data;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import ai.konduit.serving.pipeline.registry.PipelineRegistry;

import java.util.function.Consumer;

@lombok.Data
public class CallbackStep implements PipelineStep {

    static {
        PipelineRegistry.registerStepRunnerFactory(new CallbackPipelineFactory());
    }

    private final Consumer<Data> consumer;

    public CallbackStep(Consumer<Data> consumer){
        this.consumer = consumer;
    }

}
