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
package ai.konduit.serving.pipeline.registry;

import ai.konduit.serving.pipeline.impl.metrics.MetricsProvider;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.common.io.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

@Slf4j
public class MicrometerRegistry {
    private static List<io.micrometer.core.instrument.MeterRegistry> registries;

    static {
        initRegistries();
    }

    public static io.micrometer.core.instrument.MeterRegistry getRegistry() {
        if (CollectionUtils.isEmpty(registries)) {
            initRegistries();
        }
        if (registries.size() > 1) {
            log.info("Loaded {} MeterRegistry instances. Loading the first one.", registries.size());
        }
        return registries.get(0);
    }

    public static synchronized void initRegistries() {
        if(registries == null) {
            registries = new ArrayList<>();
        } else {
            registries.clear();
        }
        ServiceLoader<MetricsProvider> sl = ServiceLoader.load(MetricsProvider.class);
        Iterator<MetricsProvider> iterator = sl.iterator();
        while(iterator.hasNext()){
            MetricsProvider r = iterator.next();
            MeterRegistry reg = r.getRegistry();
            registries.add(reg);
            io.micrometer.core.instrument.Metrics.globalRegistry.add(reg);
        }

        if(registries.isEmpty()){
            //Nothing found via ServiceLoader
            registries.add(new SimpleMeterRegistry());
        }
    }
}
