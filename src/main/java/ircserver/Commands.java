package ircserver;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author apomosov
 */
public interface Commands {
  @NotNull
  Pattern LOGIN_PATTERN = Pattern.compile("\\s*?/login\\s+(\\w+)\\s+(\\w+)\\s*$");
  @NotNull
  Pattern JOIN_PATTERN = Pattern.compile("\\s*?/join\\s+(\\w+)\\s*$");
  @NotNull
  Pattern LEAVE_PATTERN = Pattern.compile("\\s*?/leave\\s*$");
  @NotNull
  Pattern USERS_PATTERN = Pattern.compile("\\s*?/users\\s*$");
}
