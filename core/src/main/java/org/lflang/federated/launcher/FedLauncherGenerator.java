package org.lflang.federated.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.lflang.MessageReporter;
import org.lflang.federated.generator.FederateInstance;
import org.lflang.federated.generator.FederationFileConfig;
import org.lflang.target.TargetConfig;

/**
 * Abstract utility class that can be used to create a launcher for federated LF programs.
 *
 * @ingroup federated
 */
public abstract class FedLauncherGenerator {
  protected TargetConfig targetConfig;
  protected FederationFileConfig fileConfig;
  protected MessageReporter messageReporter;

  /**
   * @param targetConfig The current target configuration.
   * @param fileConfig The current file configuration.
   * @param messageReporter A error reporter for reporting any errors or warnings during the code
   *     generation
   */
  public FedLauncherGenerator(
      TargetConfig targetConfig, FederationFileConfig fileConfig, MessageReporter messageReporter) {
    this.targetConfig = targetConfig;
    this.fileConfig = fileConfig;
    this.messageReporter = messageReporter;
  }

  public abstract void doGenerate(List<FederateInstance> federates, RtiConfig rtiConfig);

  /**
   * Write the shell script to a file.
   * @param shCode The shell script code
   * @param scriptName The name of the script file (without extension)
   */
  protected void writeShellScript(StringBuilder shCode, String scriptName) {
    // Create bin directory for the script.
    if (!Files.exists(fileConfig.binPath)) {
      try {
        Files.createDirectories(fileConfig.binPath);
      } catch (IOException e) {
        messageReporter.nowhere().error("Unable to create directory: " + fileConfig.binPath);
      }
    }

    // Write the launcher file.
    File file = fileConfig.binPath.resolve(scriptName).toFile();
    messageReporter.nowhere().info("Script for launching the federation: " + file);

    // Delete file previously produced, if any.
    if (file.exists()) {
      if (!file.delete()) {
        messageReporter
            .nowhere()
            .error("Failed to delete existing federated launch script \"" + file + "\"");
      }
    }

    try (FileOutputStream fOut = new FileOutputStream(file)) {
      fOut.write(shCode.toString().getBytes());
    } catch (FileNotFoundException e) {
      messageReporter.nowhere().error("Unable to find file: " + file);
    } catch (IOException e) {
      messageReporter.nowhere().error("Unable to write to file: " + file);
    }

    if (!file.setExecutable(true, false)) {
      messageReporter.nowhere().warning("Unable to make launcher script executable.");
    }
  }
}
