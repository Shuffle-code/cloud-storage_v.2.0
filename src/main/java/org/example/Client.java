package org.example;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    public Label enterNewDir;
    public TextField nameNewDir;
    private Path clientDir;
    private static final int SIZE = 256;
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField textFieldServer;
    public TextField textFieldClient;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private byte[] buf;


    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}", message);
                System.out.println(message);
                System.out.println(" *Hello World*");
                processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void processMessage(CloudMessage message) throws IOException {
        switch (message.getType()) {
            case LIST:
                processMessage((ListMessage) message);
                break;
            case FILE:
                processMessage((FileMessage) message);
                break;

        }
    }

    public void processMessage(FileMessage message) throws IOException {
        Files.write(clientDir.resolve(message.getFileName()), message.getBytes());
        Platform.runLater(this::updateClientView);
    }

    public void processMessage(ListMessage message) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(message.getFiles());
            updateTextFieldServer(message.getPath());
        });
    }

    private void updateClientView() {
        try {
            clientView.getItems().clear();
            Files.list(clientDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
            textFieldClient.clear();
            textFieldClient.appendText(String.valueOf(clientDir));
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
            updateClientView();
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
        if (current == clientDir.subpath(0, 2)) {
            clientDir = current;
            updateClientView();
        } else {
            clientDir = clientDir.getParent();
            updateClientView();
        }
    }

    public void pathDownServer() throws IOException {
        os.writeObject(new ChangePath(".."));
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
                if (getItemServer() != null) {
                    try {
                        os.writeObject(new ChangePath(getItemServer()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                }

                }else {
                    enterNewDir.setVisible(true);
                    nameNewDir.setVisible(true);
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

    public void create() throws IOException {
        os.writeObject(new CreateMassage(nameNewDir.getText()));
        nameNewDir.clear();
        enterNewDir.setVisible(false);
        nameNewDir.setVisible(false);
    }

    public void delete () throws IOException {
        String deleteFile = getItem();
        if (deleteFile == null) {
            String deleteFileServer = getItemServer();
            if (deleteFileServer != null){
                os.writeObject(new DeleteMassage(deleteFileServer));
            }

        } else {
            Files.delete(clientDir.resolve(deleteFile));
            updateClientView();
        }


    }
    private void initMouseLinkedOneServer () {
    }

    public void host(ActionEvent event) throws IOException {
    }
}

