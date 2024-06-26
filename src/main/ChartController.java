package main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.units.ChillerState;
import main.utilits.DAO;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartController implements Initializable, DAO {
    @FXML
    public LineChart<Number, Number> mainChart;
    @FXML
    public NumberAxis xAxis, yAxis;

    HBox titleBox;
    int x;
    int y;
    Stage stage;

    @FXML
    public void Close(MouseEvent event){
        Main.stage.show();
        Main.stage.setX(stage.getX());
        Main.stage.setY(stage.getY());
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleBox.setOnMousePressed(mouseEvent -> {
            stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            x = (int) (stage.getX() - mouseEvent.getScreenX());
            y = (int) (stage.getY() - mouseEvent.getScreenY());
        });
        titleBox.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + x);
            stage.setY(mouseEvent.getScreenY() + y);
        });

        Platform.runLater(() -> {
            stage = (Stage) titleBox.getScene().getWindow();
            stage.setX(Main.stage.getX());
            stage.setY(Main.stage.getY());
        });

        xAxis = new NumberAxis();
        xAxis.setLabel("Age");
        yAxis = new NumberAxis();
        yAxis.setLabel("Salary (INR)");

        XYChart.Series<Number, Number> data = new XYChart.Series<>();
        int i = 0;
        for(ChillerState butChillerState:getAllRecords()){
            data.getData().add(new XYChart.Data<>(i, butChillerState.getTpo()));
            i++;
        }
        mainChart.setData(FXCollections.observableArrayList(data));
    }
}
