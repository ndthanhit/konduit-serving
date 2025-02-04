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

package ai.konduit.serving.build;

import ai.konduit.serving.build.config.*;
import ai.konduit.serving.build.config.Arch;
import ai.konduit.serving.build.config.OS;
import ai.konduit.serving.build.config.Target;
import ai.konduit.serving.build.deployments.UberJarDeployment;
import ai.konduit.serving.build.config.Module;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestConfig {

    @Test
    public void testSimple(){

        Config c = new Config()
                .pipelinePath("/path/to/my/pipeline.yaml")
                .ksVersion("LATEST")
                .metadata(new Metadata()
                        .author("User Name")
                        .buildVersion("1.0.0")
                        .timestamp("2020/05/26 12:00:00"))
                .target(new Target(OS.LINUX, Arch.x86, null))
                .serving(Serving.HTTP, Serving.GRPC)
                .modules(Module.forShortName("pipeline"))
                .deployments(new UberJarDeployment().outputDir("/my/output/dir").jarName("my.jar"));

        String json = c.toJson();
        String yaml = c.toYaml();

        System.out.println(json);
        System.out.println(yaml);

        Config cJ = Config.fromJson(json);
        Config cY = Config.fromYaml(yaml);

        assertEquals(c, cJ);
        assertEquals(c, cY);

    }

}
