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

package ai.konduit.serving.models.tensorflow.format;

import ai.konduit.serving.pipeline.api.data.NDArray;
import ai.konduit.serving.pipeline.api.format.NDArrayFactory;
import org.nd4j.common.base.Preconditions;
import org.tensorflow.Tensor;

import java.util.HashSet;
import java.util.Set;

public class TFNDArrayFactory implements NDArrayFactory {
    @Override
    public Set<Class<?>> supportedTypes() {
        Set<Class<?>> s = new HashSet<>();
        s.add(Tensor.class);
        return s;
    }

    @Override
    public boolean canCreateFrom(Object o) {
        return o instanceof Tensor;
    }

    @Override
    public NDArray create(Object o) {
        Preconditions.checkState(canCreateFrom(o), "Unable to create TensorFlow NDArray from object of %s", o.getClass());

        Tensor a;
        if(o instanceof Tensor){
            a = (Tensor) o;
        } else {
            throw new IllegalStateException("Format not supported: " + o.getClass());
        }

        //TODO add all the other java types!

        return new TFNDArray(a);
    }
}
