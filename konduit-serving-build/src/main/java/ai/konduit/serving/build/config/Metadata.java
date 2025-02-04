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

package ai.konduit.serving.build.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.nd4j.shade.jackson.annotation.JsonProperty;

@Data
@Accessors(fluent = true)
@NoArgsConstructor
public class Metadata {

    private String author;
    private String timestamp;
    private String buildVersion;

    public Metadata(@JsonProperty("author") String author, @JsonProperty("timestamp") String timestamp,
                    @JsonProperty("buildVersion") String buildVersion){
        this.author = author;
        this.timestamp = timestamp;
        this.buildVersion = buildVersion;
    }

}
