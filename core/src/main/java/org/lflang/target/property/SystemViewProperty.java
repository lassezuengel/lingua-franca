package org.lflang.target.property;

import org.lflang.MessageReporter;
import org.lflang.ast.ASTUtils;
import org.lflang.lf.Element;
import org.lflang.lf.LfPackage.Literals;
import org.lflang.target.Target;
import org.lflang.target.TargetConfig;
import org.lflang.target.property.type.PlatformType.Platform;
import org.lflang.target.property.type.SystemViewType;
import org.lflang.target.property.type.SystemViewType.SystemViewSetting;

/**
 * Directive to enable Segger SystemView technology.
 * By default, SystemView is disabled (NONE).
 *
 * The following settings are supported:
 * <ul>
 *   <li>NONE: Disable SystemView technology.</li>
 *   <li>ENABLE: Enable SystemView technology.</li>
 *   <li>ENABLE_AND_INSTRUMENT: Enable SystemView and automatically insert
 *       instrumentation code (e.g., to log task switches) where possible.</li>
 * </ul>
 *
 * @implNote Currently, this property only works for Zephyr targets.
 */
public final class SystemViewProperty extends TargetProperty<SystemViewSetting, SystemViewType> {

  /** Singleton target property instance. */
  public static final SystemViewProperty INSTANCE = new SystemViewProperty();

  private SystemViewProperty() {
    super(new SystemViewType());
  }

  @Override
  public SystemViewSetting initialValue() {
    return SystemViewSetting.getDefault();
  }

  @Override
  public Element toAstElement(SystemViewSetting value) {
    return ASTUtils.toElement(value.toString());
  }

  @Override
  protected SystemViewSetting fromAst(Element node, MessageReporter reporter) {
    return fromString(ASTUtils.elementToSingleString(node), reporter);
  }

  protected SystemViewSetting fromString(String string, MessageReporter reporter) {
    return this.type.forName(string);
  }

  @Override
  public String name() {
    return "systemview";
  }

  @Override
  public void validate(TargetConfig config, MessageReporter reporter) {
    var platform = config.getOrDefault(PlatformProperty.INSTANCE).platform();

    if (platform != Platform.ZEPHYR) {
      reporter
          .at(config.lookup(this), Literals.KEY_VALUE_PAIR__NAME)
          .error(
              String.format(
                  "Platform '%s' does not support the 'systemview' property.", platform.name()));
    }

    if (config.target != Target.C) {
      reporter
          .at(config.lookup(this), Literals.KEY_VALUE_PAIR__NAME)
          .error(
              String.format(
                  "Target '%s' does not support the 'systemview' property.", config.target.name()));
    }
  }
}
