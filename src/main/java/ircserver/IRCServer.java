package ircserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import model.IRCChannel;
import model.IRCUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author apomosov
 */
public final class IRCServer {
  private static final boolean SSL = System.getProperty("ssl") != null;
  private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8992" : "8023"));
  private static final AuthenticationService authService = new AuthenticationService();
  @NotNull
  private final ConcurrentHashMap<String, IRCChannel> channels = new ConcurrentHashMap<>();
  @NotNull
  private final ConcurrentHashMap<String, IRCUser> users = new ConcurrentHashMap<>();


  public boolean login(String user, String password) {
    return authService.login(user, password);
  }

  public void addUserToChannel(String user, String channel) {
    IRCChannel ircChannel = channels.get(channel);
    IRCUser ircUser = users.get(user);
    if(ircChannel != null && ircUser != null)
      ircChannel.addUser(ircUser);
  }

  public void removeUserFromChannel(String user, String channel) {
    IRCChannel ircChannel = channels.get(channel);
    IRCUser ircUser = users.get(user);
    if(ircChannel != null && ircUser != null)
      ircChannel.removeUser(ircUser);
  }

  public void userLeave(String user) {
    IRCUser ircUser = users.get(user);
    ircUser.getIrcChannel().removeUser(ircUser);
    if(ircUser != null)
      users.remove(user);
  }

  public List<IRCUser> getUsers(String channel) {
    IRCChannel ircChannel = channels.get(channel);
    if(ircChannel != null){
      return ircChannel.getUsers();
    } else return null;
  }

  public static void main(@NotNull String[] args) throws Exception {
    // Configure SSL.
    final SslContext sslCtx;
    if (SSL) {
      SelfSignedCertificate ssc = new SelfSignedCertificate();
      sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
    } else {
      sslCtx = null;
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new IRCServerInitializer(sslCtx));

      b.bind(PORT).sync().channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}