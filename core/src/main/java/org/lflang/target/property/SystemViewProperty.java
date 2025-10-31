package org.lflang.target.property;

/** Directive to enable Segger SystemView technology.
 * 
 * TODO: Change to a more general property the user can use to not only
 * enable/disable SystemView, but also enable automatic SystemView
 * code generation for the generated code.
 */
public final class SystemViewProperty extends BooleanProperty {

  /** Singleton target property instance. */
  public static final SystemViewProperty INSTANCE = new SystemViewProperty();

  private SystemViewProperty() {
    super();
  }

  @Override
  public String name() {
    return "systemview";
  }
}
