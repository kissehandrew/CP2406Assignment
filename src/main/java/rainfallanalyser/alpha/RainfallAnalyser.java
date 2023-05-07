package rainfallanalyser.alpha;

import org.apache.commons.csv.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CP2406 Assignment - Andrew Kisseh
 * Rainfall Analyser - Alpha Version
 * This Java Class analyses raw csv files from the user-specified path containing rainfall data and returns an analysed
 * csv file
 */
public class RainfallAnalyser {

    public static void main(String[] args) {

        System.out.println("Welcome to the Rainfall Analyser");
        System.out.println("This program will take raw rainfall data from BOM in csv format");
        System.out.println("It will then return the extracted Total, Min & Max Monthly rainfall");
        System.out.println("Please enter 0 to exit the program");

        String filename;
        while (true) {
            try {
                filename = getFileName();
                if (filename == null) {
                    System.out.println("Goodbye!");
                    break;
                }
                ArrayList<String> analysedRainRecords = analyseRainRecords(filename);
                saveRainfallData(analysedRainRecords, filename);
                System.out.println(filename + " was successfully analysed!");

            } catch (Exception e) {
                System.out.println("Error:");
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Save analysed file
     */
    private static void saveRainfallData(ArrayList<String> analysedRainRecords, String filename) {
        TextIO.writeFile(getSavePath(filename));
        TextIO.putln("year,month,total,minimum,maximum");

        for (String rainRecords: analysedRainRecords) {
            TextIO.putln(rainRecords);
        }
    }

    /**
     * Load file to be analysed and create ArrayList representing rainfall data.
     */
    private static ArrayList<String> analyseRainRecords(String fileName) throws Exception {

        // Checking if the file is empty
        File f = new File("src/main/resources/raw_rainfalldata/" + fileName);
        if (f.length() == 0)
            throw new Exception("This is an empty file!");

        // Create FileReader and CSV reader
        Reader reader = new FileReader("src/main/resources/raw_rainfalldata/" + fileName);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);

        int year, month, day;
        double rainfall;
        int currentYear = 0;
        int currentMonth = 1;
        double monthlyTotalRainfall = 0.0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = 0.0;
        ArrayList<String> rainfallData = new ArrayList<>();

        for (CSVRecord record : records) {
            // Get data from particular columns of rainfalldata csv file
            String yearString = record.get("Year");
            String monthString = record.get("Month");
            String dayString = record.get("Day");
            String rainfallString = record.get("Rainfall amount (millimetres)");

            // covert data in appropriate data types
            year = Integer.parseInt(yearString);
            month = Integer.parseInt(monthString);
            day = Integer.parseInt(dayString);

            // validate recorded date
            if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
                System.out.println("Error: Invalid format (Date)");
                throw new NumberFormatException ("Date out of expected range");
            }

            // check rainfall for rain record, if empty - assume 0
            rainfall = Objects.equals(rainfallString, "") ? 0 : Double.parseDouble(rainfallString);

            // save data and reset total/min/max values for next month
            if (month != currentMonth) {
                // Check if it is the first year before saving data
                rainfallData.add(writeCurrentData(monthlyTotalRainfall, minRainfall, maxRainfall, currentMonth, currentYear == 0? year : currentYear));
                currentYear = year;
                currentMonth = month;
                monthlyTotalRainfall = 0;
                maxRainfall = 0.0;
                minRainfall = Double.POSITIVE_INFINITY;
            }

            monthlyTotalRainfall += rainfall;
            if (rainfall > maxRainfall) maxRainfall = rainfall;
            if (rainfall < minRainfall) minRainfall = rainfall;
        }

        rainfallData.add(writeCurrentData(monthlyTotalRainfall, minRainfall, maxRainfall, currentMonth, currentYear));
        return rainfallData;
    }

    /**
     * return monthly data as string
     */
    private static String writeCurrentData(double monthlyTotal, double minRainfall, double maxRainfall, int month, int year) {
        return String.format("%d,%d,%1.2f,%1.2f,%1.2f", year, month, monthlyTotal, minRainfall, maxRainfall);
    }

    /**
     * Get list of available rainfall data sets to be analysed.
     * Let the user select the file to be analysed
     */
    private static String getFileName() {
        System.out.println("\nThe files available to be analysed are:");
        File f = new File("src/main/resources/raw_rainfalldata");
        String[] pathNames = f.list();

        assert pathNames != null;
        for (int i = 0; i < pathNames.length; i++) {
            System.out.println((i+1) + ": " + pathNames[i]);
        }

        System.out.println("Enter the number of the file to be analysed: ");

        int fileNumber;
        String filename;
        while (true) {
            // Check if selected file is valid
            try {
                fileNumber = TextIO.getInt();
                if (fileNumber == 0) {
                    return null;
                }
                filename = pathNames[fileNumber - 1];
                break;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("That is outside of the range of available data files to analyse.");
                System.out.println("Please select another file");
            }
        }
        return filename;
    } //

    /**
     * Return analysed rainfall data file path
     */
    private static String getSavePath(String filename) {
        String[] filenameElements = filename.trim().split("\\.");
        return "src/main/resources/analysed_rainfalldata/" + filenameElements[0] + "_analysed.csv";
    }
}

