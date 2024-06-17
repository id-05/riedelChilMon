package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ChartController implements Initializable, DAO {
    @FXML
    public static LineChart mainChart;
    @FXML
    public NumberAxis xAxis, yAxis;

    @FXML
    public void Close(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xAxis = new NumberAxis();
        xAxis.setLabel("Age");
        yAxis = new NumberAxis();
        yAxis.setLabel("Salary (INR)");
        mainChart.setTitle("Average salary per age");

        XYChart.Series data = new XYChart.Series<Number, Number>();
        int i = 0;
        for(ChillerState butChillerState:getAllRecors()){
            data.getData().add(new XYChart.Data<>(i, butChillerState.getTpo()));
            i++;
        }
        mainChart.setData(FXCollections.observableArrayList(data));
        System.out.println("tests");
    }
}
