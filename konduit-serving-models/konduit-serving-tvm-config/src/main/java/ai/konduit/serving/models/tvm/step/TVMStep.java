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

package ai.konduit.serving.models.tvm.step;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;


import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonName("TVM")
@Schema(description = "A pipeline step that configures a TVM model that is to be executed.")
public class TVMStep implements PipelineStep {



    @Schema(description = "A list of names of the input placeholders.")
    private List<String> inputNames;

    @Schema(description = "A list of names of the output arrays - i.e., what should be predicted.")
    private List<String> outputNames;

    @Schema(description = "Uniform Resource Identifier of model")
    private String modelUri;

    @Schema(description = "Lazy initialization of the step. Useful when model is being created within the pipeline.")
    private boolean lazyInit;

    @Tolerate
    public TVMStep inputNames(String... inputNames) {
        return this.inputNames(Arrays.asList(inputNames));
    }

    @Tolerate
    public TVMStep outputNames(String... outputNames) {
        return this.outputNames(Arrays.asList(outputNames));
    }
}

