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

package ai.konduit.serving.pipeline.impl.data.image;

import ai.konduit.serving.pipeline.impl.data.image.base.BaseImageFile;

import java.io.File;
import java.nio.ByteBuffer;

public class Jpeg extends BaseImageFile {

    public Jpeg(File file) {
        this(file, null, null,null);
    }

    public Jpeg(File file, Integer height, Integer width,Integer channels){
        super(file, height, width,channels);
    }

    public Jpeg(byte[] bytes){
        this(bytes, null, null,null);
    }

    public Jpeg(byte[] bytes, Integer height, Integer width,Integer channels){
        super(bytes, height, width,channels);
    }

    public Jpeg(ByteBuffer byteBuffer){
        super(byteBuffer);
    }

    public Jpeg(ByteBuffer byteBuffer, Integer height, Integer width,Integer channels){
        super(byteBuffer, height, width,channels);
    }

    @Override
    public String formatName() {
        return "JPEG";
    }
}
