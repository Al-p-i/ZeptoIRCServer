package model;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author apomosov
 */
public class IRCChannel {
  private final long id;
  @NotNull
  private final String name;
  @NotNull
  private final ConcurrentSkipListSet<IRCUser> users = new ConcurrentSkipListSet();
  @NotNull
  private final CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<>();

  public IRCChannel(long id, @NotNull String name) {
    this.id = id;
    this.name = name;
  }

  public void sendMsg(@NotNull String msg) {
    history.add(msg);
  }

  public void getHistory(int depth){
    //TODO
  }
}
