package org.lflang.federated.generator;

import org.lflang.generator.LFGeneratorContext;
import org.lflang.generator.SubContext;

/**
 * Subcontext carrying the federate instance for which code generation runs.
 */
public class FederatedSubContext extends SubContext {

  private final FederateInstance federate;

  public FederatedSubContext(
      LFGeneratorContext containingContext,
      int startPercentProgress,
      int endPercentProgress,
      FederateInstance federate) {
    super(containingContext, startPercentProgress, endPercentProgress);
    this.federate = federate;
  }

  public FederateInstance getFederateInstance() {
    return federate;
  }
}
