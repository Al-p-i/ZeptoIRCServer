package model;

import org.jetbrains.annotations.NotNull;

/**
 * @author apomosov
 */
public class IRCUser {
  private final long id;

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  private final String name;

  public IRCUser(int id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }
}
