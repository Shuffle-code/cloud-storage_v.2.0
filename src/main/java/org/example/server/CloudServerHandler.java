package org.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.command.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private static Path pathServer;
    private final Path pathRoot = Paths.get("data");
    private String login;
    AuthServer authServer = new AuthServer();




    public void setPathServer(Path pathServer) {
        this.pathServer = pathServer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        pathServer = Paths.get(pathRoot + "\\" + login);
        System.out.println(pathServer + " - Path*");
        sendList(ctx);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getType()){
            case FILE:
                processFileMessage((FileMessage) cloudMessage);
                sendList(ctx);
                break;
            case FILE_REQUEST:
                processFileRequest((FileRequest) cloudMessage, ctx);
                break;
            case PATH_REQUEST:
                processPathRequest((ChangePath) cloudMessage, ctx);
                break;
            case AUTH:
                processAuthorization((AuthMessage) cloudMessage, ctx);
                break;
            case REGISTRATION:
                processRegistration((RegistrationMessage) cloudMessage, ctx);


        }
    }

    private void processPathRequest(ChangePath cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path serverPath = pathServer.resolve(cloudMessage.getDirName()).normalize();
        System.out.println(serverPath);
        if (Files.isDirectory(serverPath) && !serverPath.toAbsolutePath().toString().equals(pathRoot.toAbsolutePath().toString())) {
            pathServer = serverPath;
            sendList(ctx);
        }
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(pathServer));
    }

    private void processFileMessage(FileMessage cloudMessage) throws IOException {
        Files.write(pathServer.resolve(cloudMessage.getFileName()), cloudMessage.getBytes()); // Записали байты в память
    }

    private void processFileRequest(FileRequest cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = pathServer.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }
    private void processAuthorization(AuthMessage authMessage, ChannelHandlerContext ctx) throws IOException {
        AuthServer authServer = new AuthServer();
        System.out.println(authServer.authorization(authMessage.getLogin(),
                authMessage.getPassword()));
        if(authServer.authorization(authMessage.getLogin(),
                authMessage.getPassword())) {
            login = authMessage.getLogin();
            System.out.println(login);
            pathServer = Paths.get(pathRoot + "\\" + login);
            setPathServer(Paths.get(pathRoot + "\\" + login));
            ctx.writeAndFlush(new AuthResponse(true));
//            sendList(ctx);
            System.out.println(new ListMessage(pathServer)  + "-Path");
            ctx.writeAndFlush(new ListMessage(pathServer));
        }
    }
    private void processRegistration(RegistrationMessage registrationMessage, ChannelHandlerContext ctx) throws IOException {
        AuthServer authServer = new AuthServer();
        login = registrationMessage.getLogin();
        pathServer = Paths.get(pathRoot + "\\" + login);
        System.out.println(pathServer);
        Files.createDirectory(pathServer);
        authServer.registration(registrationMessage.getName(), registrationMessage.getLogin(),
                registrationMessage.getPassword());
        ctx.writeAndFlush(new RegistrationResponse(true));

    }




}
