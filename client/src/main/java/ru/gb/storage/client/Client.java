package ru.gb.storage.client;


import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import ru.gb.storage.commons.message.AuthMessage;
import ru.gb.storage.commons.message.FileContentMessage;
import ru.gb.storage.commons.message.FileRequestMessage;
import ru.gb.storage.commons.message.Message;

import java.io.*;

public class Client /*extends Application*/ {
    public static void main(String[] args) {
        //Application.launch(args);
        new Client().start();
    }

    //public void start(Stage stage) throws Exception {
        //stage.show();

    public void start() {
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
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {

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
                                            ctx.writeAndFlush(authMessage);

                                            final FileRequestMessage message = new FileRequestMessage();
                                            message.setPath("/home/inna/Загрузки/sampleb.mp3");
                                            ctx.writeAndFlush(message);
                                        }

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {

                                            System.out.println("receive msg " + msg);

                                            if (msg instanceof FileContentMessage) {
                                                FileContentMessage fcm = (FileContentMessage) msg;
                                                try (final RandomAccessFile accessFile = new RandomAccessFile("/home/inna/sampleb.mp3", "rw")) {
                                                    accessFile.seek(fcm.getStartPosition());
                                                    accessFile.write(fcm.getContent());
                                                    if(fcm.isLast()) {
                                                        ctx.close();
                                                    }
                                                }catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                           /*
                                                try (final FileOutputStream outputStream = new FileOutputStream("/home/inna/new2.txt")) {

                                                    outputStream.write(fcm.getContent());


                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }*/

                                            }
                                        }
                                    }
                            );
                        }
                    });

            System.out.println("Client started");

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    }

