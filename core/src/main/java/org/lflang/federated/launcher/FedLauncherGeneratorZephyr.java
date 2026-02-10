package org.lflang.federated.launcher;

import java.util.List;
import org.lflang.MessageReporter;
import org.lflang.federated.generator.FederateInstance;
import org.lflang.federated.generator.FederationFileConfig;
import org.lflang.target.TargetConfig;

/**
 * Utility class that can be used to create a launcher for federated LF programs
 * for Zephyr platform. This launcher only starts the RTI; federates must be
 * manually flashed to microcontrollers.
 *
 * @ingroup federated
 */
public class FedLauncherGeneratorZephyr extends FedLauncherGenerator {
  public FedLauncherGeneratorZephyr(
      TargetConfig targetConfig, FederationFileConfig fileConfig, MessageReporter messageReporter) {
    super(targetConfig, fileConfig, messageReporter);
  }

  /**
   * Create the launcher shell script for Zephyr. This will create a single file in the output path
   * (bin directory) with name equal to the filename of the source file without the ".lf"
   * extension. This script launches only the RTI with a fixed federation ID (42).
   * Federates must be manually flashed to microcontrollers.
   */
  @Override
  public void doGenerate(List<FederateInstance> federates, RtiConfig rtiConfig) {
    StringBuilder shCode = new StringBuilder();

    shCode.append(getSetupCode()).append("\n");

    String host = rtiConfig.getHost();
    shCode.append("#### Host is ").append(host).append("\n");

    // Launch the RTI
    if (host.equals("fd01::1")) {
      shCode
          .append(
              getLaunchCode(getRtiCommand(fileConfig.getRtiBinPath().toString(), federates, "42", false)))
          .append("\n");
    } else {
      messageReporter.nowhere().error("Remote RTI launch is not supported for Zephyr target.");
      return;
    }

    // Add deployment instructions
    shCode.append(getDeploymentInstructions(federates)).append("\n");

    // Wait for RTI to complete
    shCode
        .append(
            String.join(
                "\n",
                "echo \"Waiting for RTI to complete...\"",
                "wait ${RTI}",
                "echo \"RTI has exited.\"",
                "EXITED_SUCCESSFULLY=true"))
        .append("\n");

    writeShellScript(shCode, fileConfig.name);
  }

  private String getSetupCode() {
    return String.join(
        "\n",
        "#!/bin/bash -l",
        "# Launcher for federated Zephyr " + fileConfig.name + ".lf Lingua Franca program.",
        "# This script launches only the RTI. Federates must be flashed to microcontrollers"
            + " manually.",
        "",
        "# Set a trap to kill RTI on error or control-C",
        "cleanup() {",
        "    if [ \"$EXITED_SUCCESSFULLY\" != true ] ; then",
        "        printf \"\n#### Killing RTI %s.\\n\" ${RTI}",
        "        kill ${RTI} || true",
        "        exit 1",
        "    fi",
        "}",
        "",
        "trap 'cleanup; exit' EXIT",
        "",
        "echo \"Federation " + fileConfig.name + " with Federation ID '42'\"");
  }

  private String getLaunchCode(String rtiLaunchCode) {
    String launchCodeWithLogging = rtiLaunchCode + " >& RTI.log &";
    String launchCodeWithoutLogging = rtiLaunchCode + " &";
    return String.join(
        "\n",
        "echo \"#### Launching the runtime infrastructure (RTI) for Zephyr.\"",
        "echo \"#### RTI will use federation ID: 42\"",
        "if [ \"$1\" = \"-l\" ]; then",
        "    " + launchCodeWithLogging,
        "else",
        "    " + launchCodeWithoutLogging,
        "fi",
        "RTI=$!",
        "sleep 1");
  }

  private String getDeploymentInstructions(List<FederateInstance> federates) {
    StringBuilder instructions = new StringBuilder();
    instructions.append("\n");
    instructions.append(
        "echo \"###############################################################\"\n");
    instructions.append("echo \"RTI is now running and waiting for federates to connect.\"\n");
    instructions.append(
        "echo \"Please flash the following federates to your microcontrollers:\"\n");

    for (FederateInstance federate : federates) {
      instructions.append("echo \"  - ").append(federate.name).append("\"\n");
    }

    instructions.append(
        "echo \"###############################################################\"\n");

    return instructions.toString();
  }
}
