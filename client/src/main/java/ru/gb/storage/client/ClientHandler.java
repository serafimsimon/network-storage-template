package ru.gb.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.message.AuthMessage;
import ru.gb.storage.commons.message.FileContentMessage;
import ru.gb.storage.commons.message.FileRequestMessage;
import ru.gb.storage.commons.message.Message;

import java.io.*;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    String filename;

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
        System.out.println("Enter loading file name");
        String path = bufferedReader.readLine();//Читаем имя файла, который будем скачивать
        filename = path;
        message.setPath("/home/inna/IdeaProjects/My_project/Server/" + path);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("receive msg " + msg);

        if (msg instanceof FileContentMessage) {
            FileContentMessage fcm = (FileContentMessage) msg;

            try (final RandomAccessFile accessFile = new RandomAccessFile("/home/inna/IdeaProjects/My_project/Client/" + filename, "rw")) {
                accessFile.seek(fcm.getStartPosition());
                accessFile.write(fcm.getContent());
                if(fcm.isLast()) {
                    ctx.close();
                }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
