package org.example;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.example.command.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j

public class Client implements Initializable {

    private Path clientDir;
    private Path serverDir = Paths.get("data").toAbsolutePath();
    private static final int SIZE = 256;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField textFieldServer;
    public TextField textFieldClient;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;
    private byte[] buf;
    private ChannelHandlerContext ctx;

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}",message);
                processor.processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateClientView() {
        try {
            clientView.getItems().clear();
            Files.list(clientDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
            textFieldClient.clear();
            textFieldClient.appendText(String.valueOf(clientDir));
            textFieldServer.clear();
            textFieldServer.appendText(String.valueOf(serverDir));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buf = new byte[SIZE];
            clientDir = Paths.get(System.getProperty("user.home"));
            System.out.println(clientDir);
            System.out.println(serverDir);
            updateClientView();
            processor = new CloudMessageProcessor(clientDir,clientView, serverView, serverDir);
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            initMouseLinkedClient();
            initMouseLinkedServer();
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(clientDir.resolve(fileName)));

    }

    public void download(ActionEvent actionEvent) throws IOException { // загрузить
        String fileName = serverView.getSelectionModel().getSelectedItem(); // создали(нашли) имя файла из serverView
        os.writeObject(new FileRequest(fileName));
    }

    public void pathDownClient()  {
        Path current = clientDir;
        if (current == clientDir.subpath(0, 2)) { // path.resolve("..").toAbsolutePath().
            clientDir = current;
            updateClientView();
        } else {
            clientDir = clientDir.getParent();
            updateClientView();
        }
    }

    public void pathDownServer() throws IOException {
        if(serverDir.toString().equals(Paths.get("data").toAbsolutePath().toString())){
            textFieldServer.clear();
            textFieldServer.setText(serverDir.toString());
        } else {
            os.writeObject(new ChangePath(serverDir.resolve("..").normalize().toString()));
            serverDir = serverDir.resolve("..").normalize();
            textFieldServer.clear();
            textFieldServer.setText(serverDir.toString());
//            System.out.println(serverDir.resolve("..").normalize().toString());
        }

    }

    private void initMouseLinkedClient() {
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Path current = clientDir.resolve(getItem());
              if (Files.isDirectory(current)) {
                    clientDir = current;
                    Platform.runLater(this::updateClientView);
                }
            }
        });
    }

    private void initMouseLinkedServer () {
        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                try {
                    os.writeObject(new ChangePath(getItemServer()));
                    serverDir = serverDir.resolve(getItemServer());
                    textFieldServer.clear();
                    textFieldServer.setText(serverDir.toString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    private String getItem() {
        System.out.println(clientView.getSelectionModel().getSelectedItem());
        return clientView.getSelectionModel().getSelectedItem();
    }

    private String getItemServer() {
        System.out.println(serverView.getSelectionModel().getSelectedItem());
        return serverView.getSelectionModel().getSelectedItem();
    }
    public void updateTextFieldServer(String path) {
        Platform.runLater(() -> {
            textFieldServer.clear();
            textFieldServer.setText(path);
        });
    }
}

