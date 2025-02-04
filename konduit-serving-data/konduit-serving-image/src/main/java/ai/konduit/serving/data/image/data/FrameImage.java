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

package ai.konduit.serving.data.image.data;

import ai.konduit.serving.pipeline.impl.data.image.base.BaseImage;
import org.bytedeco.javacv.Frame;

public class FrameImage extends BaseImage<Frame> {
    public FrameImage(Frame image) {
        super(image);
    }

    @Override
    public int height() {
        return image.imageHeight;
    }

    @Override
    public int width() {
        return image.imageWidth;
    }

    @Override
    public int channels() {
        return image.imageChannels;
    }
}
