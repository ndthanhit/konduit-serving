/*
 *
 *  * ******************************************************************************
 *  *  * Copyright (c) 2015-2019 Skymind Inc.
 *  *  * Copyright (c) 2022 Konduit K.K.
 *  *  *
 *  *  * This program and the accompanying materials are made available under the
 *  *  * terms of the Apache License, Version 2.0 which is available at
 *  *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  *  * License for the specific language governing permissions and limitations
 *  *  * under the License.
 *  *  *
 *  *  * SPDX-License-Identifier: Apache-2.0
 *  *  *****************************************************************************
 *
 *
 */

package ai.konduit.serving.output.types;

import java.io.Serializable;

/**
 * The Batch Output represents
 * the output from an output adapter.
 * This includes any common fields that the output
 * needs when returning data to the end user.
 *
 * @author Adam Gibson
 */
public interface BatchOutput extends Serializable {

    /**
     * Set the batch id for the batch output.
     * This batch id is used during retraining.
     *
     * @param batchId the batch id for the batch output
     */
    void setBatchId(String batchId);

    /**
     * Return the batch id for this batch output.
     * The batch id used for retraining.
     *
     * @return batch id
     */
    String batchId();

}
