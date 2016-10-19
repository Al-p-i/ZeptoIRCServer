package ircserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.IRCChannel;
import model.IRCUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

/**
 * Handles a server-side channel.
 */
@Sharable
public class IRCServerConnectionHandler extends SimpleChannelInboundHandler<String> {
  @Nullable
  private volatile ThreadLocal<IRCUser> ircUser;
  @NotNull
  private final Object lock = new Object();

  @Override
  public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
    // Send greeting for a new connection.
    ctx.write("Welcome to ZeptoIRCServer!\r\n");
    ctx.flush();
  }

  @Override
  public void channelRead0(@NotNull ChannelHandlerContext ctx, @NotNull String request) throws Exception {
    // Generate and write a response.
    String response;
    String trimmedRequest = request.trim();
    boolean close = false;
    if (request.isEmpty()) {
      return;
    } else if (trimmedRequest.startsWith("/")) {
      if (trimmedRequest.startsWith("/leave")) {
        if (Commands.LEAVE_PATTERN.matcher(request).matches()) {
          response = "*** Bye! ***\r\n";
          close = true;
        } else response = "*** Usage ***\n>> /leave\n";
      } else if (trimmedRequest.startsWith("/users")) {
        if (Commands.USERS_PATTERN.matcher(request).matches()) {
          response = listUsers(ircUser.get().getIrcChannel());
        } else response = "*** Usage ***\n>> /users\n";
      } else if (trimmedRequest.startsWith("/login")) {
        Matcher loginMatcher = Commands.LOGIN_PATTERN.matcher(request);
        if (loginMatcher.find()) {
          response = login(loginMatcher.group(1), loginMatcher.group(2));
        } else response = "*** Usage *** \n>> /login login password\n";
      } else if (trimmedRequest.startsWith("/join")) {
        Matcher joinMatcher = Commands.JOIN_PATTERN.matcher(request);
        if (joinMatcher.find()) {
          response = joinChannel(ircUser.get(), joinMatcher.group(1));
        } else response = "*** Usage ***\n>> /join channel\n";
      } else {
        response = "*** Unknown command \"" + request + "\" ***\n";
      }
    } else {
      sendMsg(request);
      response = "";
    }

    // We do not need to write a ChannelBuffer here.
    // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
    ChannelFuture future = ctx.write(response);

    // Close the connection if ircUser send /quit
    if (close) {
      future.addListener(ChannelFutureListener.CLOSE);
    }

  }

  private void sendMsg(@NotNull String request) {
    //TODO
  }

  @NotNull
  private String joinChannel(@Nullable IRCUser ircUser, @NotNull String channel) {
    synchronized (lock) {
      if (ircUser == null) {
        return "*** login first ***\n";
      }
      this.ircUser.get().setIrcChannel(new IRCChannel(channel));
      return "*** joined channel " + channel + "***\n";//TODO
    }
  }

  @NotNull
  private String listUsers(@Nullable IRCChannel ircChannel) {
    if (this.ircUser.get().getIrcChannel() == null) {
      return "*** join channel first ***\n";
    }
    return "sasha\npetya\n";//TODO
  }

  @NotNull
  private String login(@NotNull String login, @NotNull String password) {
    synchronized (lock) {
      this.ircUser.set(new IRCUser(login));
      return "*** Hello " + ircUser.get().getName() + " ***\n";
    }
  }

  @Override
  public void channelReadComplete(@NotNull ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(@NotNull ChannelHandlerContext ctx, @NotNull Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}