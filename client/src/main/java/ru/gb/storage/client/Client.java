package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.application.Application;
import javafx.stage.Stage;
import ru.gb.storage.commons.AuthMessage;
import ru.gb.storage.commons.Message;
import ru.gb.storage.commons.TextMessage;

import java.io.*;
import java.time.LocalDateTime;

public class Client extends Application {
    public static void main(String[] args) {
        //Application.launch(args);
        new Client().start();
    }

    public void start(Stage stage) throws Exception {
        //stage.show();
        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    //максимальный размер сообщения равен 1024*1024 байт, в начале сообщения пдля хранения длины зарезервировано 3 байта,
                                    //которые отбросятся после получения всего сообщения и передачи его дальше по цепочке
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    //Перед отправкой добавляет в начало сообщение 3 байта с длиной сообщения
                                    new LengthFieldPrepender(3),
                                    //new JsonDecoder(),
                                    //new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                            //System.out.println("receive msg " + msg);
                                        }
                                    }
                            );
                        }
                    });

            System.out.println("Client started");

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();

            InputStream inputStream = System.in;
            Reader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println("Enter login");
            String logg = bufferedReader.readLine(); //читаем логин с клавиатуры;
            System.out.println("Enter password");
            String pass = bufferedReader.readLine(); //читаем пароль с клавиатуры;

            AuthMessage authMessage = new AuthMessage();
            authMessage.setLogin(String.format(logg));
            authMessage.setPassword(String.format(pass));
            channel.writeAndFlush(authMessage);

            /*while (channel.isActive()) {

                TextMessage textMessage = new TextMessage();
                textMessage.setText(String.format("[%s] %s", LocalDateTime.now(), Thread.currentThread().getName()));
                System.out.println("Try to send message: " + textMessage);
                channel.writeAndFlush(textMessage);

                DateMessage dateMessage = new DateMessage();
                dateMessage.setDate(new Date());
                channel.write(dateMessage);
                System.out.println("Try to send message: " + dateMessage);
                channel.flush();
                Thread.sleep(3000);
            }*/

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
    }
}
