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
package ai.konduit.serving.pipeline.impl.pipeline.context;

import ai.konduit.serving.pipeline.api.context.*;
import ai.konduit.serving.pipeline.api.pipeline.Pipeline;
import ai.konduit.serving.pipeline.api.step.PipelineStep;
import ai.konduit.serving.pipeline.impl.pipeline.SequencePipeline;
import ai.konduit.serving.pipeline.impl.step.logging.LoggingStep;
import ai.konduit.serving.pipeline.impl.util.CallbackStep;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.event.Level;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricsTest {

    private Metrics m;

    @Before
    public void setUp() {
        m = new PipelineMetrics("testPipeline");
    }

    @Test
    public void testCounter() {
        Counter counter = m.counter("counter.test");
        counter.increment();
        counter.increment(10.0);
    }

    @Test
    public void testGauge() {
        Gauge gauge = m.gauge("id", 10.0);
        assertEquals(10.0, gauge.value(), 1e-4);
    }

    @Test
    public void testTimer() {
        Timer timer = m.timer("timer.test");
        timer.record(30, TimeUnit.MILLISECONDS);
        Timer.Sample sample = timer.start();
        long ret = sample.stop(timer);
        assertTrue(ret > 0);
    }

    @Test
    public void testPipelineMetrics() {
        AtomicInteger count1 = new AtomicInteger();
        AtomicInteger count2 = new AtomicInteger();

        PipelineStep step1 = new CallbackStep(d -> count1.getAndIncrement());
        PipelineStep step2 = new LoggingStep().log(LoggingStep.Log.KEYS_AND_VALUES).logLevel(Level.INFO);
        PipelineStep step3 = new CallbackStep(d -> count2.getAndIncrement());

        Pipeline p = SequencePipeline.builder()
                .add(step1)
                .add(step2)
                .add(step3)
                .build();

        Timer timer = m.timer("test.timer");
        timer.record(30, TimeUnit.MILLISECONDS);
        long ret = timer.start().stop(timer);
        assertTrue(ret > 0);
    }
}
