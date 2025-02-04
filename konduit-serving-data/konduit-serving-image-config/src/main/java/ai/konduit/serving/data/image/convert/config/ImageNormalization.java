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

package ai.konduit.serving.data.image.convert.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * <ul>
 *     <li><b>NONE</b>: No image normalization will be applied</li>
 *     <li><b>SCALE</b>: Divide images by maxValue/2, or divide by 255/2 if maxValue is not specified, then subtract 1.0,
 *       to give output in range [-1,1]. This is the default.</li>
 *     <li><b>SCALE_01</b>: Divide images by maxValue, or divide by 255 if maxValue is not specified, to give output in range [0,1].</li>
 *     <li><b>SUBTRACT_MEAN</b>: Subtract the channels by the provided meanRgb array, with values [meanRed, meanGreen, meanBlue].
 *         out = (in - mean) for each channel. Note that if the output format is in BGR format, the meanRgb value should
 *         still be provided in RGB order</li>
 *     <li><b>STANDARDIZE</b>: Subtract the channels by the provided meanRgb array, and then divide by stdRgb, where meanRgb
 *         is [meanRed, meanGreen, meanBlue], and stdRgb is [standardDeviationRed, standardDeviationGreen, standardDeviationBlue].
 *         out = (in - mean)/std for each channel. Note that if the output format is in BGR format, the meanRgb and stdRgb
 *         values should still be provided in RGB order.</li>
 *     <li><b>INCEPTION</b>: Applies inception preprocessing for inference/evaluation as described here: <a href="https://github.com/tensorflow/models/blob/master/research/slim/preprocessing/inception_preprocessing.py">inception_preprocessing.py</a>
 *         Specifically: preprocess_for_eval method: scale to [-1, 1] range.
 *         In practice this is done by dividing by 255 (assuming pixels are in range 0 to 255) to give [0, 1] then subtracting
 *         0.5 and multiplying by 2 to give [-1, 1]. Note uses maxValue (like SCALE) if provided.
 *         </li>
 *     <li><b>VGG_SUBTRACT_MEAN</b>: As per <i>SUBTRACT_MEAN</i> but the fixed values [meanRed, meanGreen, meanBlue] =
 *         [123.68, 116.779, 103.939]. Note the meanRgb array with these values need not be provided explicitly. If the
 *         output format is BGR, these are appropriately reordered before applying to the channels.</li>
 * </ul>
 *
 */
@AllArgsConstructor
@Data
@Accessors(fluent = true)
@Builder
@Schema(description = "Configuration that specifies the normalization type of the image array values.")
public class ImageNormalization {

    protected static final double[] VGG_MEAN_RGB = {123.68, 116.779, 103.939};
    protected static final double[] IMAGE_NET_MEAN_RGB = {0.485, 0.456, 0.406};
    protected static final double[] IMAGE_NET_STD_RGB = {0.229, 0.224, 0.225};

    public static double[] getVggMeanRgb(){
        return VGG_MEAN_RGB.clone();
    }

    public static double[] getImagenetMeanRgb(){
        return IMAGE_NET_MEAN_RGB.clone();
    }

    public static double[] getImageNetStdRgb(){
        return IMAGE_NET_STD_RGB.clone();
    }


    @Schema(description = "An enum that specifies the normalization type of an image array values. <br><br>" +
            "NONE -> No image normalization will be applied, <br>" +
            "SCALE -> Divide images by maxValue/2, or divide by 255/2 if maxValue is not specified, then subtract 1.0, to give output in range [-1,1], <br>" +
            "SCALE_01 -> Divide images by maxValue, or divide by 255 if maxValue is not specified, to give output in range [0,1], <br>" +
            "SUBTRACT_MEAN -> Subtract the channels by the provided meanRgb array, with values [meanRed, meanGreen, meanBlue], " +
            "out = (in - mean) for each channel. Note that if the output format is in BGR format, the meanRgb value should " +
            "still be provided in RGB order. <br>" +
            "STANDARDIZE -> Subtract the channels by the provided meanRgb array, and then divide by stdRgb, where meanRgb " +
            "is [meanRed, meanGreen, meanBlue], and stdRgb is [standardDeviationRed, standardDeviationGreen, standardDeviationBlue]. " +
            "out = (in - mean)/std for each channel. Note that if the output format is in BGR format, the meanRgb and stdRgb " +
            "values should still be provided in RGB order. <br>" +
            "INCEPTION -> Applies inception preprocessing for inference/evaluation as described here: https://github.com/tensorflow/models/blob/master/research/slim/preprocessing/inception_preprocessing.py " +
            "Specifically, preprocess_for_eval method: scale to [-1, 1] range. " +
            "In practice this is done by dividing by 255 (assuming pixels are in range 0 to 255) to give [0, 1] then subtracting " +
            "0.5 and multiplying by 2 to give [-1, 1]. Note uses maxValue (like SCALE) if provided. <br>" +
            "VGG_SUBTRACT_MEAN -> As per SUBTRACT_MEAN but the fixed values [meanRed, meanGreen, meanBlue] = " +
            " 123.68, 116.779, 103.939]. Note the meanRgb array with these values need not be provided explicitly. If the " +
            " output format is BGR, these are appropriately reordered before applying to the channels.")
    public enum Type {
        NONE,
        SCALE,
        SCALE_01,
        SUBTRACT_MEAN,
        STANDARDIZE,
        INCEPTION,
        VGG_SUBTRACT_MEAN,
        IMAGE_NET
    }

    
    @Schema(description = "An enum that specifies the type of normalization applied.",
            defaultValue = "SCALE")
    public Type type = Type.SCALE;

    @Schema(description = "Max value to divided each value in the image with. Used with SCALE and INCEPTION " +
            "normalization type.")
    private Double maxValue;

    @Schema(description = "An array of mean rgb values. Used with SUBTRACT_MEAN, STANDARDIZE and VGG_SUBTRACT_MEAN " +
            "normalization type.")
    private double[] meanRgb;

    @Schema(description = "The standard deviation of each color channel in the sequence of rgb. Used with " +
            "STANDARDIZE normalization type.")
    private double[] stdRgb;

    public ImageNormalization(){
        this(Type.SCALE);
    }

    public ImageNormalization(Type type){
        this.type = type;
    }


}
