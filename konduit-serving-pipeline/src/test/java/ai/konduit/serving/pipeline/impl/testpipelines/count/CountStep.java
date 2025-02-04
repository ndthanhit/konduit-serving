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

package ai.konduit.serving.pipeline.impl.testpipelines.count;

import ai.konduit.serving.pipeline.api.serde.JsonSubType;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import ai.konduit.serving.pipeline.registry.PipelineRegistry;
import ai.konduit.serving.pipeline.util.ObjectMappers;
import lombok.NoArgsConstructor;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.Collections;

@lombok.Data
@NoArgsConstructor
public class CountStep implements PipelineStep {

    public CountStep(@JsonProperty("count") int count) {
        this.count = count;
    }

    static {
        PipelineRegistry.registerStepRunnerFactory(new CountPipelineFactory());
        ObjectMappers.registerSubtypes(Collections.singletonList(new JsonSubType("COUNT_STEP", CountStep.class, PipelineStep.class)));
    }

    public int count;



}
