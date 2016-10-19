package model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author apomosov
 */
public class IRCChannel {
  @NotNull
  private final String name;
  @NotNull
  private final ConcurrentLinkedDeque<IRCUser> users = new ConcurrentLinkedDeque<>();
  @NotNull
  private final CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<>();

  public IRCChannel(@NotNull String name) {
    this.name = name;
  }

  public void sendMsg(@NotNull String msg) {
    history.add(msg);
  }

  public void getHistory(int depth){
    //TODO
  }

  public boolean addUser(IRCUser user){
    if (users.size() >= 10) {
      return false;
    }
    users.add(user);
    return true;
  }

  public void removeUser(IRCUser user){
    users.remove(user);
  }

  public List<IRCUser> getUsers(){
    return new ArrayList<>(users);
  }
}
