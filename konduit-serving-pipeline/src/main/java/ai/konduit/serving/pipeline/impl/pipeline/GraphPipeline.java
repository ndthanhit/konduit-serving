/* ******************************************************************************
 * Copyright (c) 2022 Konduit K.K.
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
 ******************************************************************************/
package ai.konduit.serving.pipeline.impl.pipeline;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.pipeline.api.pipeline.Pipeline;
import ai.konduit.serving.pipeline.api.pipeline.PipelineExecutor;
import ai.konduit.serving.pipeline.impl.pipeline.graph.GraphStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.nd4j.shade.jackson.annotation.JsonProperty;
import org.nd4j.shade.jackson.annotation.JsonPropertyOrder;

import java.util.Map;
import java.util.UUID;

/**
 * A pipeline with a graph structure - possibly including conditional operations, etc.
 * Use {@link ai.konduit.serving.pipeline.impl.pipeline.graph.GraphBuilder} to construct new instances:
 * Usage:
 * <pre>
 * {@code
 * GraphBuilder b = new GraphBuilder();
 * GraphStep input = b.input();
 * GraphStep output = input.then("myStep", ...);
 * Pipeline p = b.build(output);
 * }</pre>
 *
 * @author Alex Black
 * @see SequencePipeline
 */
@Data
@Accessors(fluent = true)
@JsonPropertyOrder({"outputStep", "steps"})
@Schema(description = "A type of pipeline that defines the execution flow in a directed acyclic graph (DAG) of configurable steps. " +
        "The execution flow can also contain optional steps.")
@JsonName("GRAPH_PIPELINE")
public class GraphPipeline implements Pipeline {
    public static final String INPUT_KEY = "input";

    @Schema(description = "A map of configurable graph steps that defines a directed acyclic graph (DAG) of a pipeline execution.")
    private final Map<String, GraphStep> steps;

    @Schema(description = "Name of the output step in the graph pipeline map.")
    private final String outputStep;

    @Schema(description = "A unique identifier that's used to differentiate among different executing pipelines. Used " +
            "for identifying a pipeline while reporting metrics.")
    @EqualsAndHashCode.Exclude
    private String id;

    public GraphPipeline(@JsonProperty("steps") Map<String, GraphStep> steps,
                         @JsonProperty("outputStep") String outputStep,
                         @JsonProperty("id") String id){
        this.steps = steps;
        this.outputStep = outputStep;
        this.id = id;
    }

    @Override
    public PipelineExecutor executor() {
        return new GraphPipelineExecutor(this);
    }

    @Override
    public int size() {
        return steps != null ? steps.size() : 0;
    }

    @Override
    public String id() {
        if(id == null)
            id = UUID.randomUUID().toString().substring(0, 8);
        return id;
    }
}
