package org.lflang.target.property.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.lflang.target.property.type.SystemViewType.SystemViewSetting;

/**
 * Type for Segger SystemView technology settings.
 */
public class SystemViewType extends OptionsType<SystemViewSetting> {

  @Override
  protected Class<SystemViewSetting> enumClass() {
    return SystemViewSetting.class;
  }

  /**
   * Settings for Segger SystemView technology.
   */
  public enum SystemViewSetting {
    NONE("none"),
    ENABLE("enable"),
    ENABLE_AND_INSTRUMENT("enableAndInstrument");

    /** Alias used in toString method. */
    private final String alias;

    SystemViewSetting(String alias) {
      this.alias = alias;
    }

    /** Return the name in lower case. */
    @Override
    public String toString() {
      return this.alias;
    }

    public static List<SystemViewSetting> optionsList() {
      return Arrays.stream(SystemViewSetting.values()).collect(Collectors.toList());
    }

    public static SystemViewSetting getDefault() {
      return SystemViewSetting.NONE;
    }
  }
}
