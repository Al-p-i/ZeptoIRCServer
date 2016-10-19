package model;

import org.jetbrains.annotations.NotNull;

/**
 * @author apomosov
 */
public class IRCUser {
  @NotNull
  private final String name;
  @NotNull
  private volatile IRCChannel ircChannel;

  public IRCUser(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public IRCChannel getIrcChannel() {
    return ircChannel;
  }

  public void setIrcChannel(@NotNull IRCChannel ircChannel) {
    this.ircChannel = ircChannel;
  }
}
