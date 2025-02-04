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

package ai.konduit.serving.pipeline.impl.data.point;

import ai.konduit.serving.pipeline.api.data.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.nd4j.common.base.Preconditions;
import org.nd4j.shade.jackson.annotation.JsonProperty;

@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class NDPoint implements Point {
    private  double[] coords = null;
    private  String label = "";
    private  Double probability = 0.0;

    public NDPoint(@JsonProperty("coords") double[] coords, @JsonProperty("label") String label, @JsonProperty("probability") Double probability){
        Preconditions.checkState(coords != null ,"Invalid coordinates. Coordinates must not be null!");
        this.coords = coords;
        this.label = label;
        this.probability = probability;
    }

    @Override
    public double get(int n) {
        if(n >= coords.length){
            throw new IllegalArgumentException("Can not access dimension " + n + " of " + coords.length +" dimensional point!");
        }
        return coords[n];
    }

    @Override
    public int dimensions() {
        return coords.length;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Point))
            return false;
        return Point.equals(this, (Point)o);
    }
}
