package org.lflang.target.property.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Type for IP version settings.
 */
public class IPvType extends OptionsType<IPvType.IPvSetting> {

  @Override
  protected Class<IPvSetting> enumClass() {
    return IPvSetting.class;
  }

  /**
   * Settings for IP version.
   */
  public enum IPvSetting {
    AUTO("auto"),
    IPV4("ipv4"),
    IPV6("ipv6");

    /** Alias used in toString method. */
    private final String alias;

    IPvSetting(String alias) {
      this.alias = alias;
    }

    /** Return the name in lower case. */
    @Override
    public String toString() {
      return this.alias;
    }

    public static List<IPvSetting> optionsList() {
      return Arrays.stream(IPvSetting.values()).collect(Collectors.toList());
    }

    public static IPvSetting getDefault() {
      return IPvSetting.AUTO;
    }
  }
}
