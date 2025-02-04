/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  *  See the NOTICE file distributed with this work for additional
 *  *  information regarding copyright ownership.
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package ai.konduit.serving.documentparser;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.nd4j.common.base.Preconditions;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@Data
@Accessors(fluent = true)
@JsonName("DOCUMENTPARSER")
@Schema(description = "A pipeline step that configures a tika parser")
public class DocumentParserStep implements PipelineStep {

    @Schema(description = "A list of names of the input placeholders ( computation graph, with multiple inputs. Where values from the input data keys are mapped to " +
            "the computation graph inputs).")
    private List<String> inputNames;

    @Schema(description = "A list of names of the output placeholders (computation graph, with multiple outputs. Where the values of these output keys are mapped " +
            "from the computation graph output - INDArray[] to data keys).")
    private List<String> outputNames;
    @Schema(description = "A list of parser types: tika,pdfbox")
    private List<String> parserTypes;
    @Schema(description = "A list of parser types: tika,pdfbox")
    private List<String> tableRowExtractorTypes;
    @Schema(description = "A list of selectors for table extraction from html")
    private List<String> selectors;
    @Schema(description = "A list of field names per input/output for resolving rows where the column name is on its own with the value being in another row.")
    private List<List<String>> fieldNames;

    @Schema(description = "A list of field names per input/output for resolving rows within a table " +
            "where the column name is on its own with the value being in another row. This is used in situations where a global " +
            " field name might otherwise clash with values in other tables.")
    private Map<String,List<String>> tableSpecificFieldNames;
    @Schema(description = "A list of field names per input/output for resolving rows where the column name is on the same line as the value")
    private List<List<String>> partialFieldNames;
    @Schema(description = "A list of table titles to look for")
    private List<String> tableKeys;
    @Schema(description = "Table extraction types per field. Defaults to using html.text()")
    private Map<String,TextExtractionType> textExtractionTypes;

    public DocumentParserStep() {
    }

    public DocumentParserStep(
            @JsonProperty("inputNames") List<String> inputNames,
            @JsonProperty("outputNames") List<String> outputNames,
            @JsonProperty("parserTypes") List<String> parserTypes,
            @JsonProperty("tableRowExtractorTypes") List<String> tableRowExtractorTypes,
            @JsonProperty("selectors") List<String> selectors,
            @JsonProperty("fieldNames") List<List<String>> fieldNames,
            @JsonProperty("tableSpecificFieldNames") Map<String,List<String>> tableSpecificFieldNames,
            @JsonProperty("tableKeys") List<String> tableKeys,
            @JsonProperty("partialFieldNames") List<List<String>> partialFieldNames,
            @JsonProperty("textExtractionTypes") Map<String,TextExtractionType> textExtractionTypes) {
        if(inputNames != null)
            this.inputNames = inputNames;
        if(outputNames != null)
            this.outputNames = outputNames;
        if(parserTypes != null)
            this.parserTypes = parserTypes;
        if(tableRowExtractorTypes != null)
            this.tableRowExtractorTypes = tableRowExtractorTypes;
        if(selectors != null)
            this.selectors = selectors;
        if(fieldNames != null)
            this.fieldNames = fieldNames;
        if(tableKeys != null)
            this.tableKeys = tableKeys;
        if(partialFieldNames != null)
            this.partialFieldNames = partialFieldNames;
        if(tableSpecificFieldNames != null)
            this.tableSpecificFieldNames = tableSpecificFieldNames;
        if(textExtractionTypes != null)
            this.textExtractionTypes = textExtractionTypes;

    }
}
