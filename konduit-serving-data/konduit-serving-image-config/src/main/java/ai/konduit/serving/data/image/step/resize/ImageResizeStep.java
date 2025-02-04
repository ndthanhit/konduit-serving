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

package ai.konduit.serving.data.image.step.resize;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.data.image.convert.config.AspectRatioHandling;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Resize an image - scaling up or down as needed to comply with the specified output height/width.
 * Usually, both {@code height} and {@code width} are specified. However, if only one is specified, the other value is
 * calculated based on the aspect ration of the input image.
 * When both {@code height} and {@code width} are specified, and the aspect ratios of the input doesn't match
 * the aspect ratio of the output, (for example, 100x200 in, 200x200 out) the {@code aspectRatioHandling} setting (see
 * {@link AspectRatioHandling}) is used to determine how to handle this situation.<br>
 * Note that the names of the inputs Data fields to resize may or may not be specified:
 * If no value is provided for {@code inputNames} configuration: All input {@code Image} and {@code List<Image>} fields
 * in the input Data instance will be resized, regardless of name.<br>
 * If {@code inputNames} is specified: Only those fields with those names will be resized.<br>
 * <br>
 * <br>
 * Example 1: Scaling to 300x300, center cropping if needed: {@code new ImageResizeStep().height(300).width(300).aspectRatioHandling(AspectRatioHandling.CENTER_CROP)}<br>
 * Example 2: Scaling to height of 256 (any output width, maintaining original aspect ratio): {@code new ImageResizeStep().height(256)}<br>
 *
 * @author Alex Black
 */
@Data
@Accessors(fluent = true)
@JsonName("IMAGE_RESIZE")
@NoArgsConstructor
@Schema(description = "A pipeline step that resizes an image, scaling up or down as needed to comply with the " +
        "specified output height/width. Usually, both <height> and <width> are specified. However, if only one is specified, " +
        "the other value is calculated based on the aspect ration of the input image. When both <height> and <width> are specified, " +
        "and the aspect ratios of the input doesn't match the aspect ratio of the output, (for example, 100x200 in, 200x200 out) " +
        "the <aspectRatioHandling> setting is used to determine how to handle this situation. Note that the names of the inputs data fields " +
        "to resize may or may not be specified. If no value is provided for <inputNames> configuration, all input images fields " +
        "in the input Data instance will be resized, regardless of name. If <inputNames> is specified, only those fields with those " +
        "names will be resized.")
public class ImageResizeStep implements PipelineStep {

    @Schema(description = "Name of the input keys whose values contain images from the previous step.")
    protected List<String> inputNames;

    @Schema(description = "Resize height.")
    protected Integer height;

    @Schema(description = "Resize width.")
    protected Integer width;

    @Schema(description = "An enum to define how to handle the aspect ratio when the aspect ratio doesn't match with " +
            "that of the input image.",
            defaultValue = "STRETCH")
    protected AspectRatioHandling aspectRatioHandling = AspectRatioHandling.STRETCH;

    /**
     * Set the input name - i.e., the name of the {@code Image} or {@code List<Image>} field to be processed.
     * If any names have been previously set, they will be discarded when calling this method
     *
     * @param name Input {@code Image} or {@code List<Image>} name
     * @return This instance
     */
    public ImageResizeStep inputName(String name) {
        this.inputNames = Collections.singletonList(name);
        return this;
    }

    public ImageResizeStep(@JsonProperty("inputNames") List<String> inputNames, @JsonProperty("height") Integer height, @JsonProperty("width") Integer width,
                           @JsonProperty("aspectRatioHandling") AspectRatioHandling aspectRatioHandling) {
        this.inputNames = inputNames;
        this.height = height;
        this.width = width;
        this.aspectRatioHandling = aspectRatioHandling;
    }

    @Tolerate
    public ImageResizeStep inputNames(String... inputNames) {
        return this.inputNames(Arrays.asList(inputNames));
    }
}
