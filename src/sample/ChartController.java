package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sample.units.ChillerState;
import sample.utilits.DAO;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartController implements Initializable, DAO {
    @FXML
    public LineChart mainChart;
    @FXML
    public NumberAxis xAxis, yAxis;

    public HBox titleBox;
    public int x;
    public int y;
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
        //stage = (Stage) titleBox.getScene().getWindow();
        titleBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
                x = (int) (stage.getX() - mouseEvent.getScreenX());
                y = (int) (stage.getY() - mouseEvent.getScreenY());
            }
        });
        titleBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + x);
                stage.setY(mouseEvent.getScreenY() + y);
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) titleBox.getScene().getWindow();
                stage.setX(Main.stage.getX());
                stage.setY(Main.stage.getY());
            }
        });

        xAxis = new NumberAxis();
        xAxis.setLabel("Age");
        yAxis = new NumberAxis();
        yAxis.setLabel("Salary (INR)");

        XYChart.Series data = new XYChart.Series<Number, Number>();
        int i = 0;
        for(ChillerState butChillerState:getAllRecors()){
            data.getData().add(new XYChart.Data<>(i, butChillerState.getTpo()));
            i++;
        }
        mainChart.setData(FXCollections.observableArrayList(data));
    }
}
