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

package ai.konduit.serving.data.image.step.point.draw;

import ai.konduit.serving.annotation.json.JsonName;
import ai.konduit.serving.data.image.convert.ImageToNDArrayConfig;
import ai.konduit.serving.data.image.util.ColorConstants;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Draw 2D points<br>
 * Configuration:<br>
 * <ul>
 *     <li><b>classColors</b>: Optional: A list of colors to use for each class. Must be in one of the following formats: hex/HTML - "#788E87",
 *             RGB - "rgb(128,0,255)", or one of 16 HTML color name such as \"green\" (https://en.wikipedia.org/wiki/Web_colors#HTML_color_names).
 *             If no colors are specified, or not enough colors are specified, random colors are used instead (note consistent between runs).
 *             Colors are mapped to named classes in alphabetical order, i.e. first color to class A, second color to class B, etc...</li>
 *     <li><b>points</b>: Names of the points to be drawn. Accepts both single points and lists of points.
 *     <li><b>radius</b>: Point radius on drawn image. </li>
 *     <li><b>image</b>: Optional. Name of the image to be drawn on</li>
 *     <li><b>width</b>: Must be provided when <b>image</b> isn't set. Used to resolve position of points with relative addressing (dimensions between 0 and 1)</li>
 *     <li><b>height</b>: Must be provided when <b>image</b> isn't set. Used to resolve position of points with relative addressing (dimensions between 0 and 1)</li>
 *     <li><b>outputName</b>: Name of the output image</li>
 * </ul>
 *
 * @author Paul Dubs
 */
@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonName("DRAW_POINTS")
@Schema(description = "A pipeline step that configures how to draw 2D points on an image.")
public class DrawPointsStep implements PipelineStep {
    public static final String DEFAULT_OUTPUT_NAME = "image";
    public static final String DEFAULT_NO_POINT_COLOR = "lime";

    @Schema(description = "Color belongs to non-label data", defaultValue = DEFAULT_NO_POINT_COLOR)
    private String noClassColor;

    @Schema(description = "This is an optional field which specifies the mapping of colors to use for each class. " + ColorConstants.COLOR_DESCRIPTION)
    private Map<String, String> classColors;

    @Schema(description = "Name of the input data fields containing the points to be drawn. Accepts both single points and lists of points. Accepts both relative and absolute addressed points.")
    private List<String> points;

    @Schema(description = "Optional. Point radius on drawn image. Default = 5px")
    private Integer radius;

    @Schema(description = "An optional field, specifying the name of the image to be drawn on")
    private String image;

    @Schema(description = "Must be provided when \"image\" isn't set. Used to resolve position of points with relative addressing (dimensions between 0 and 1)")
    private Integer width;

    @Schema(description = "Must be provided when \"image\" isn't set. Used to resolve position of points with relative addressing (dimensions between 0 and 1)")
    private Integer height;

    @Schema(description = "Used to account for the fact that n-dimensional array from ImageToNDArrayConfig may be " +
            "used to crop images before passing to the network, when the image aspect ratio doesn't match the NDArray " +
            "aspect ratio. This allows the step to determine the subset of the image actually passed to the network.")
    private ImageToNDArrayConfig imageToNDArrayConfig;

    @Schema(description = "Name of the output image",
            defaultValue = DEFAULT_OUTPUT_NAME)
    private String outputName;

    @Tolerate
    public DrawPointsStep points(String... points) {
        return this.points(Arrays.asList(points));
    }
}
