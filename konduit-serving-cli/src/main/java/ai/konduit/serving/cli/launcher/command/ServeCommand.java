/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  *  See the NOTICE file distributed with this work for additional
 *  *  information regarding copyright ownership.
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package ai.konduit.serving.cli.launcher.command;

import ai.konduit.serving.cli.launcher.KonduitServingLauncher;
import ai.konduit.serving.cli.launcher.LauncherUtils;
import ai.konduit.serving.pipeline.settings.DirectoryFetcher;
import io.vertx.core.cli.annotations.*;
import io.vertx.core.impl.launcher.CommandLineUtils;
import io.vertx.core.impl.launcher.commands.ExecUtils;
import io.vertx.core.spi.launcher.DefaultCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.common.io.StringUtils;
import org.nd4j.shade.guava.base.Strings;

import java.io.*;
import java.util.*;

import static ai.konduit.serving.cli.launcher.command.KonduitRunCommand.DEFAULT_SERVICE;

@Name("serve")
@Summary("Start a konduit server application")
@Description("Start a konduit server application. " +
        "The application is identified with an id that can be set using the `--serving-id` or `-id` option. " +
        "The application can be stopped with the `stop` command. " +
        "This command takes the `run` command parameters. To see the " +
        "run command parameters, execute `run --help`\n\n" +
        "Example usages:\n" +
        "--------------\n" +
        "- Starts a server in the foreground with an id of 'inf_server' using 'config.json' as configuration file:\n" +
        "$ konduit serve -id inf_server -c config.json\n\n" +
        "- Starts a server in the background with an id of 'inf_server' using 'config.yaml' as configuration file:\n" +
        "$ konduit serve -id inf_server -c config.yaml -b\n" +
        "--------------")
@Slf4j
public class ServeCommand extends DefaultCommand {

    protected String host;
    protected int port;
    protected String id;
    protected String launcher;
    protected int instances = 1;
    protected String classpath;
    protected String service;
    protected String configuration;

    protected boolean redirect;
    protected String jvmOptions;

    /**
     * Sets the host name of the konduit server.
     *
     * @param host host name
     */
    @Option(shortName = "h", longName = "host", argName = "host")
    @DefaultValue("localhost")
    @Description("Specifies the host name of the konduit server when the configuration provided is " +
            "just a pipeline configuration instead of a whole inference configuration. Defaults to 'localhost'.")
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port of the konduit server.
     *
     * @param port port number
     */
    @Option(shortName = "p", longName = "port", argName = "port")
    @DefaultValue("0")
    @Description("Specifies the port number of the konduit server when the configuration provided is " +
            "just a pipeline configuration instead of a whole inference configuration. Defaults to '0'.")
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the number of instance of the verticle to create.
     *
     * @param instances the number of instances
     */
    @Option(shortName = "i", longName = "instances", argName = "instances")
    @DefaultValue("1")
    @Description("Specifies how many instances of the server will be deployed. Defaults to 1.")
    public void setInstances(int instances) {
        this.instances = instances;
    }

    /**
     * Sets the classpath.
     *
     * @param classpath the classpath
     */
    @Option(shortName = "cp", longName = "classpath", argName = "classpath")
    @Description("Provides an extra classpath to be used for the verticle deployment.")
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    @Option(longName = "service", shortName = "s", argName = "type")
    @DefaultValue(DEFAULT_SERVICE)
    @Description("Service type that needs to be deployed. Defaults to \"inference\"")
    public void setMainVerticle(String konduitServiceType) {
        this.service = konduitServiceType;
    }

    /**
     * The main verticle configuration, it can be a json file or a json string.
     *
     * @param configuration the configuration
     */
    @Option(shortName = "c", longName = "config", argName = "server-config", required = true)
    @Description("Specifies configuration that should be provided to the verticle. <config> should reference either a " +
            "text file containing a valid JSON object which represents the configuration OR be a JSON string.")
    public void setConfig(String configuration) {
        this.configuration = configuration;
    }

    /**
     * Sets the "application id" that would be to stop the application and be listed in the {@link ListCommand} command.
     *
     * @param id the application ID.
     */
    @Option(longName = "serving-id", shortName = "id")
    @Description("Id of the serving process. This will be visible in the 'list' command. This id can be used to call 'predict' and 'stop' commands on the running servers. " +
            "If not given then an 8 character UUID is created automatically.")
    public void setApplicationId(String id) {
        this.id = id;
    }

    /**
     * Sets the Java Virtual Machine options to pass to the spawned process. If not set, the JAVA_OPTS environment
     * variable is used.
     *
     * @param options the jvm options
     */
    @Option(shortName = "jo", longName = "java-opts")
    @Description("Java Virtual Machine options to pass to the spawned process such as \"-Xmx1G -Xms256m " +
            "-XX:MaxPermSize=256m\". If not set the `JAVA_OPTS` environment variable is used.")
    public void setJavaOptions(String options) {
        this.jvmOptions = options;
    }

    /**
     * A hidden option to set the launcher class.
     *
     * @param clazz the class
     */
    @Option(longName = "launcher-class")
    @Hidden
    public void setLauncherClass(String clazz) {
        this.launcher = clazz;
    }

    @Option(shortName = "b", longName = "background", flag = true)
    @Description("Runs the process in the background, if set.")
    public void setRedirect(boolean background) {
        this.redirect = !background;
    }

    private void addCustomServeOptions(List<String> cliArguments) {
        if(classpath != null) {
            cliArguments.add("--classpath");
            cliArguments.add(classpath);
        }

        if(service != null) {
            cliArguments.add("-s");
            cliArguments.add(service);
        }

        if(configuration != null) {
            cliArguments.add("-c");
            cliArguments.add(configuration);
        }
    }

    /**
     * Starts the application in background.
     */
    @Override
    public void run() {
        out.println("Starting konduit server...");
        List<String> cmd = new ArrayList<>();
        ProcessBuilder builder = new ProcessBuilder();
        addJavaCommand(cmd);

        // Must be called only once !
        List<String> cliArguments = new ArrayList<>();

        cliArguments.add("--instances");
        cliArguments.add(String.valueOf(instances));

        addCustomServeOptions(cliArguments);

        cliArguments.addAll(getArguments());

        String finalClassPath = Strings.isNullOrEmpty(classpath) ? System.getProperty("java.class.path") : classpath + File.pathSeparator + System.getProperty("java.class.path");

        // Add the classpath to env.
        builder.environment().putAll(System.getenv());
        builder.environment().put("CLASSPATH", finalClassPath);

        out.format("Expected classpath: %s%n", finalClassPath);

        if (launcher != null) {
            ExecUtils.addArgument(cmd, launcher);
            // Do we have a valid command ?
            Optional<String> maybeCommand = cliArguments.stream()
                    .filter(arg -> executionContext.launcher().getCommandNames().contains(arg))
                    .findFirst();
            if (!maybeCommand.isPresent()) {
                // No command, add `run`
                ExecUtils.addArgument(cmd, "run");
            }
        } else if (isLaunchedAsFatJar()) {
            if(classpath != null && classpath.contains(id) && StringUtils.endsWithIgnoreCase(classpath, "manifest.jar")) {
                ExecUtils.addArgument(cmd, "-jar");
                cmd.add(classpath);
            } else {
                cmd.add("-cp");
                cmd.add(finalClassPath);
                cmd.add(KonduitServingLauncher.class.getCanonicalName());
            }
            ExecUtils.addArgument(cmd, "run");
        } else {
            // probably a `vertx` command line usage, or in IDE.
            ExecUtils.addArgument(cmd, CommandLineUtils.getFirstSegmentOfCommand());
            ExecUtils.addArgument(cmd, "run");
        }

        cliArguments.forEach(arg -> ExecUtils.addArgument(cmd, arg));

        try {
            out.format("INFO: Running command %s%n", String.join(" ", cmd));
            builder.command(cmd); // Setting the builder command
            if (redirect) {
                runAndTailOutput(builder);
            } else {
                String commandLinePrefix = ((KonduitServingLauncher) executionContext.launcher()).commandLinePrefix();
                builder.start();
                out.format("For server status, execute: '%s list'%nFor logs, execute: '%s logs %s'%n",
                        commandLinePrefix,
                        commandLinePrefix,
                        id);
            }
        } catch (Exception e) {
            out.println("Cannot create konduit server process");
            e.printStackTrace(out);
            ExecUtils.exitBecauseOfProcessIssue();
        }

        LauncherUtils.cleanServerDataFilesOnceADay();
    }

    private void runAndTailOutput(ProcessBuilder builder) throws IOException {
        Process process = builder.start();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            Thread.sleep(2000);
            while (LauncherUtils.isProcessExists(id)) {
                while(reader.ready()){
                    out.println(reader.readLine());
                }
                while(errReader.ready()) {
                    out.println(errReader.readLine());
                }
                Thread.sleep(100);
            }
            //Print any additional errors
            while(reader.ready()) {
                out.println(reader.readLine());
            }
            while(errReader.ready()) {
                out.println(errReader.readLine());
            }
        } catch (InterruptedException interruptedException) {
            out.format("Killing server (%s) logs%n", id);
        }

        if (!process.isAlive()) {
            out.format("Server with id (%s) terminated with exit code %s...%n", id, process.exitValue());
        }
    }

    private void addJavaCommand(List<String> cmd) {
        if (ExecUtils.isWindows()) {
            ExecUtils.addArgument(cmd, "cmd.exe");
            ExecUtils.addArgument(cmd, "/C");
            ExecUtils.addArgument(cmd, "start");
            ExecUtils.addArgument(cmd, "serving-id - " + id);
            ExecUtils.addArgument(cmd, "/B");
        }
        ExecUtils.addArgument(cmd, getJava().getAbsolutePath());

        // Compute JVM Options
        if (jvmOptions == null) {
            String opts = System.getenv("JAVA_OPTS");
            if (opts != null) {
                Arrays.stream(opts.split(" ")).forEach(s -> ExecUtils.addArgument(cmd, s));
            }
        } else {
            Arrays.stream(jvmOptions.split(" ")).forEach(s -> ExecUtils.addArgument(cmd, s));
        }

        String konduitLogsFileProperty = "konduit.logs.file.path";
        String konduitRuntimeLogbackFileProperty = "logback.configurationFile.runCommand";
        String logbackFileProperty = "logback.configurationFile";
        String defaultLogbackFile = "logback-run_command.xml";
        if (!String.join(" ", cmd).contains(logbackFileProperty)) {
            String konduitLogsFileSystemProperty = System.getProperty(konduitLogsFileProperty);
            String konduitRuntimeLogbackFileSystemProperty = System.getProperty(konduitRuntimeLogbackFileProperty);
            ExecUtils.addArgument(cmd, String.format("-D%s=%s", konduitLogsFileProperty,
                    (konduitLogsFileSystemProperty != null ? 
                        new File(konduitLogsFileSystemProperty) : 
                        new File(DirectoryFetcher.getCommandLogsDir(), id + ".log"))
                            .getAbsolutePath()
                )
            );
            File logbackFile = extractLogbackFile(konduitRuntimeLogbackFileSystemProperty != null ? 
                konduitRuntimeLogbackFileSystemProperty : 
                defaultLogbackFile);
            ExecUtils.addArgument(cmd, String.format("-D%s=%s", logbackFileProperty, logbackFile.getAbsolutePath()));
        }
    }

    private File extractLogbackFile(String file) {
        String s = UUID.randomUUID().toString().replace("-","").substring(0, 16);
        int idx = file.lastIndexOf('.');
        String name = file.substring(0, idx) + "_" + s + file.substring(idx);
        File out = new File(FileUtils.getTempDirectory(), name);
        File inputFile = new File(file);
        if(inputFile.exists() && inputFile.isFile()) {
            return inputFile;
        }
        else {
            try(InputStream is = new ClassPathResource(file).getInputStream(); 
                OutputStream os = new BufferedOutputStream(new FileOutputStream(out))) {
                IOUtils.copy(is, os);
            } catch (IOException e){
                log.error("Error extracting logback file: file does not exist or temp directory cannot be written to?", e);
            }

            return out;
        }
    }

    private File getJava() {
        File java;
        File home = new File(System.getProperty("java.home"));
        if (ExecUtils.isWindows()) {
            java = new File(home, "bin/java.exe");
        } else {
            java = new File(home, "bin/java");
        }

        if (!java.isFile()) {
            out.println("Cannot find java executable - " + java.getAbsolutePath() + " does not exist");
            ExecUtils.exitBecauseOfSystemConfigurationIssue();
        }
        return java;
    }

    private boolean isLaunchedAsFatJar() {
        return CommandLineUtils.getJar() != null;
    }

    private List<String> getArguments() {
        List<String> args = executionContext.commandLine().allArguments();
        // Add system properties passed as parameter
        if (systemProperties != null) {
            systemProperties.stream().map(entry -> "-D" + entry).forEach(args::add);
        }

        // Add id - it's important as it's the application mark.
        args.add("-Dserving.id=" + getId());
        return args;
    }

    protected String getId() {
        if (id == null) {
            id = UUID.randomUUID().toString().substring(0, 8);
        }

        if (LauncherUtils.isProcessExists(id)) {
            out.println(String.format("A konduit server with an id: '%s' already exists.", id));
            System.exit(1);
        }

        return id;
    }

}
