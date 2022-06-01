package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.message.*;
import ru.gb.storage.server.services.AuthService;
import ru.gb.storage.server.services.AuthServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {
    private int counter = -1;

    private AuthService authService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("New active channel");
        TextMessage answer1 = new TextMessage();
        answer1.setText("Successfully connection");
        ctx.writeAndFlush(answer1);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        if (msg instanceof AuthMessage) {
            authService = new AuthServiceImpl();
            authService.start();

            AuthMessage message = (AuthMessage) msg;

            if (authService.getLoginBD(message.getLogin()) !=null && authService.getPasswordBD(message.getPassword()) !=null) {

                System.out.println("Success authentication! User " + message.getLogin() + " is here!");
                authService.stop();
                TextMessage answer2 = new TextMessage();
                answer2.setText("Success authentication! Hello, " + message.getLogin() + "!");

                ctx.writeAndFlush(answer2);
            } else {
                System.out.println("!!!ATTENTION!!! Wrong authentication!!!");
                TextMessage answer3 = new TextMessage();
                answer3.setText("!!!ATTENTION!!! Wrong login or password!");
                ctx.writeAndFlush(answer3);
                ctx.close();
            }
        }

        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("incoming text message: " + message.getText());
            ctx.writeAndFlush(msg);
        }

        if (msg instanceof FileRequestMessage) {
            FileRequestMessage frm = (FileRequestMessage) msg;

            final File file = new File(frm.getPath());
            counter++;

            try (final RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
                while (accessFile.getFilePointer() != accessFile.length()) {
                    final byte[] fileContent;
                    if (accessFile.length() - accessFile.getFilePointer() > 128 * 1024) {
                        fileContent = new byte[128 * 1024];
                    }else {
                        final long l = accessFile.length() - accessFile.getFilePointer();
                        fileContent = new byte[(int) l];

                    }

                    final FileContentMessage message = new FileContentMessage();
                    message.setStartPosition(accessFile.getFilePointer());
                    accessFile.read(fileContent);
                    message.setContent(fileContent);
                    message.setLast(accessFile.getFilePointer() == accessFile.length());
                    ctx.writeAndFlush(message);
                    System.out.println("Sent " + ++counter);
                }

            } catch (IOException e) {
                System.out.println("Wrong file path");
                TextMessage answer4 = new TextMessage();
                answer4.setText("!!!ATTENTION!!! Wrong file name or file don't exist");
                ctx.writeAndFlush(answer4);
                ctx.close();
                };
            }
        }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("client disconnect");
    }

}