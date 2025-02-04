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

package ai.konduit.serving.pipeline.impl.pipeline.graph.switchfn;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.pipeline.api.data.Data;
import ai.konduit.serving.pipeline.api.data.ValueType;
import ai.konduit.serving.pipeline.impl.pipeline.graph.SwitchFn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import org.nd4j.common.base.Preconditions;
import org.nd4j.shade.jackson.annotation.JsonProperty;

/**
 * A {@link SwitchFn} that selects the output based on a integer values from the Data instance.
 * The specified field name must be an integer value between 0 and numOutputs inclusive
 *
 * @author Alex Black
 */
@lombok.Data
@Accessors(fluent = true)
@JsonName("INT_SWITCH")
@Schema(description = "A switch function that that selects the output based on a integer values from the data instance. " +
        "The specified field name must be an integer value between 0 and numOutputs-1 inclusive")
public class DataIntSwitchFn implements SwitchFn {

    @Schema(description = "The number of outputs from a switch step.")
    private final int numOutputs;

    @Schema(description = "Field name key from a data instance whose value will channel the graph flow to a specific switch branch. " +
            "In this case, it will be the nth index from a list of switch outputs.")
    private final String fieldName;

    public DataIntSwitchFn(@JsonProperty("numOutputs") int numOutputs, @JsonProperty("fieldName") String fieldName){
        Preconditions.checkState(numOutputs > 0, "Number of outputs must be positive, got %s", numOutputs);
        this.numOutputs = numOutputs;
        this.fieldName = fieldName;
    }

    @Override
    public int numOutputs() {
        return numOutputs;
    }

    @Override
    public int selectOutput(Data data) {
        Preconditions.checkState(data.has(fieldName), "Input data does not have an integer field of name \"%s\"", fieldName);
        Preconditions.checkState(data.type(fieldName) == ValueType.INT64, "Input data field \"%s\" has type \"%s\", " +
                "must be INT64 (long)", fieldName, data.type(fieldName));
        long l = data.getLong(fieldName);
        return (int)l;
    }
}
