package org.lflang.target.property;

import org.lflang.MessageReporter;
import org.lflang.ast.ASTUtils;
import org.lflang.lf.Element;
import org.lflang.lf.LfPackage.Literals;
import org.lflang.target.TargetConfig;
import org.lflang.target.property.type.IPvType;
import org.lflang.target.property.type.PlatformType.Platform;

/**
 * Directive to specify the used IP version for federate as well as RTI communication.
 *
 * The property supports the following settings:
 * <ul>
 *   <li>AUTO: Automatically select the IP version. This is the default
 *       and behaves as follows:
 *      <ul>
 *         <li>If the target platform is Zephyr, use IPV6. This is enforced because
 *             federated Zephyr uses IPv6 (via 6LoWPAN).
 *         </li>
 *         <li>Otherwise, use IPV4.</li>
 *      </ul>
 *   </li>
 *   <li>IPV4: Use IPv4.</li>
 *   <li>IPV6: Use IPv6.</li>
 * </ul>
 */
public final class IPvProperty extends TargetProperty<IPvType.IPvSetting, IPvType> {

  /** Singleton target property instance. */
  public static final IPvProperty INSTANCE = new IPvProperty();

  private IPvProperty() {
    super(new IPvType());
  }

  @Override
  public IPvType.IPvSetting initialValue() {
    return IPvType.IPvSetting.getDefault();
  }

  @Override
  public Element toAstElement(IPvType.IPvSetting value) {
    return ASTUtils.toElement(value.toString());
  }

  @Override
  protected IPvType.IPvSetting fromAst(Element node, MessageReporter reporter) {
    return fromString(ASTUtils.elementToSingleString(node), reporter);
  }

  protected IPvType.IPvSetting fromString(String string, MessageReporter reporter) {
    return this.type.forName(string);
  }

  @Override
  public String name() {
    return "ipVersion";
  }

  @Override
  public void validate(TargetConfig config, MessageReporter reporter) {
    var platform = config.getOrDefault(PlatformProperty.INSTANCE).platform();

    if (platform == Platform.ZEPHYR) {
      // allow only ipv6
      var value = config.getOrDefault(this);
      if (value != IPvType.IPvSetting.IPV6 && value != IPvType.IPvSetting.AUTO) {
        reporter
            .at(config.lookup(this), Literals.KEY_VALUE_PAIR__NAME)
            .error(
                String.format(
                    "Platform '%s' requires 'ipVersion' property to be set to 'ipv6' or 'auto'.",
                    platform.name()));
      }
    }
  }

  /**
   * Determine whether IPv6 is used based on the given target configuration.
   * @param config The target configuration.
   * @return True if IPv6 is selected (or preferred in case of `auto`), false if IPv4 is used.
   */
  public static boolean useIPv6(TargetConfig config) {
    var setting = config.getOrDefault(INSTANCE);
    if (setting == IPvType.IPvSetting.IPV6) {
      return true;
    } else if (setting == IPvType.IPvSetting.IPV4) {
      return false;
    } else { // AUTO
      var platform = config.getOrDefault(PlatformProperty.INSTANCE).platform();
      return platform == Platform.ZEPHYR;
    }
  }
}
