package org.example.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.check.Check;
import org.example.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView implements Initializable {
    @FXML
    public TextField url;
    @FXML
    public Button checkAll;
    @FXML
    public Button checkUrl;
    @FXML
    public TextArea resultUrl;
    private Stage fileStage;

    public void CheckUrl(ActionEvent actionEvent) throws Exception {
        if (this.url.getText().isEmpty()) {
            Utils.alert("提示", "请输入URL地址！");
        }else {
                String  result2 = Check.exploiteUrl(url.getText());
                resultUrl.appendText(result2 + url.getText() + "\n");


        }
    }

    public void CheckAllUrl(ActionEvent actionEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("文本文件 (*.txt)", new String[]{"*.txt"});
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(this.fileStage);
        if (selectedFile != null) {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            CompletableFuture.runAsync(() -> {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String item;
                    while ((item = reader.readLine()) != null) {
                        Check.exploiteAll(item).thenAccept(result2 -> {
                            if (result2 != null) {
                                Platform.runLater(() -> resultUrl.appendText(result2 + "\n"));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService).thenRun(executorService::shutdown);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
