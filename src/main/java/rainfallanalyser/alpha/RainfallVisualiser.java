package rainfallanalyser.alpha;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * CP2406 Assignment - Andrew Kisseh
 * Alpha Version
 * Based on of the assignment's starting code that was provided.
 * Only drawPicture() class was modified for this project.
 * For this version - user is required to supply path of the analysed file to visualized
 */
public class RainfallVisualiser extends Application {

    public void drawPicture(GraphicsContext g, int width, int height) {

        // Create x and y-axis
        int border_width = 20;
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeLine(border_width, border_width, border_width, height - border_width);
        g.strokeLine(border_width, height - border_width, width - border_width, height - border_width);

        TextIO.getln(); // disregard first line as it is header row

        ArrayList<Double> allMonthlyTotals = new ArrayList<>();

        double maxMonthlyTotal = Double.NEGATIVE_INFINITY;
        int firstYear = 2100;
        int lastYear = 0;
        while (!TextIO.eof()) {
            String[] line = TextIO.getln().trim().strip().split(",");
            double monthlyTotal = Double.parseDouble(line[2]);
            allMonthlyTotals.add(monthlyTotal);
            if (monthlyTotal > maxMonthlyTotal)
                maxMonthlyTotal = monthlyTotal;

            int year = Integer.parseInt(line[0]);
            if (year < firstYear)
                firstYear = year;
            else if (year > lastYear)
                lastYear = year;
        }

        double xAxisLength = width - 2 * border_width;
        double yAxisLength = height - 2 * border_width;
        double currentXPos = border_width;
        double barWidth = xAxisLength / allMonthlyTotals.size();

        g.setFill(Color.DARKGREEN);
        g.setLineWidth(0.5);

        for (Double monthlyTotal : allMonthlyTotals) {
            double columnHeight = (monthlyTotal / maxMonthlyTotal) * yAxisLength;

            g.fillRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);
            g.strokeRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);
            currentXPos += barWidth;
        }

        // Adding title and axis names
        g.setFill(Color.BLACK);
        g.setFont(Font.font(24));
        g.fillText("Rainfall: " + firstYear + " to " + lastYear, width/2.5, border_width);

        g.setFont(Font.font(15));
        g.fillText("Months", width/2.0, height-5);

        g.rotate(-90);
        g.fillText("Rainfall (millimeters)",-height/1.6, border_width-5);
    }


    //------ Implementation details: DO NOT EDIT THIS ------
    public void start(Stage stage) {
        int width = 200 * 6 + 40;
        int height = 500;
        Canvas canvas = new Canvas(width, height);
        drawPicture(canvas.getGraphicsContext2D(), width, height);
        BorderPane root = new BorderPane(canvas);
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualiser");
        stage.show();
        stage.setResizable(false);
    }
    //------ End of Implementation details ------


    public static void main(String[] args) {
        System.out.print("Enter path: ");
        var path = TextIO.getln();

        TextIO.readFile(path);
        launch();
    }

}
