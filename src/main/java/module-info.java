module rainfallanalyser.alpha {

    requires java.desktop;
    requires org.apache.commons.csv;
    requires javafx.graphics;


    opens rainfallanalyser.alpha to javafx.fxml;
    exports rainfallanalyser.alpha;
}