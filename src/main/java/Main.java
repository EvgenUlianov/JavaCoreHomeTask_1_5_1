import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class Main {

    private static final String FILE_CSV = "data.csv";
    private static final String FILE_JSON = "data.gson";

    public static void main(String[] args) {
        System.out.println("Задача 1: CSV - JSON парсер");

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        //String fileName = FILE_CSV; // убрал, все равно это константа по сути
        makeDataCSV();
        List<Employee> list = parseCSV(columnMapping);//, fileName);
        // check the process
        list.forEach(System.out::println);

        String json = listToJson(list);
        System.out.println(json);

        saveTextFileJson(json);
    }

    private static String listToJson(List<Employee> list) {

        // function 'setPrettyPrinting' makes returned String pretty)))
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(list);
    }

    private static List<Employee> parseCSV(String[] columnMapping){//, String fileName) {
        try (CSVReader csvReader = new CSVReader(
                new FileReader(FILE_CSV))) {

            // initiate strategy
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBeanBuilder<Employee> myCsvBuilder = new CsvToBeanBuilder<>(csvReader);
            myCsvBuilder = myCsvBuilder.withMappingStrategy(strategy);
            CsvToBean<Employee> csvToBean = myCsvBuilder.build();

            //List<Employee> theListOfEmployee = csvToBean.parse();
            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
            //List<Employee> empty = new ArrayList<>();
            return new ArrayList<>();
        }
    }

    private static void makeDataCSV() {

        List<Employee> list = new ArrayList<>();
        list.add(new Employee(1,"John", "Smith", "USA", 25));
        list.add(new Employee(2,"Inav", "Petrov", "RU", 23));
        list.add(new Employee(3,"Irina", "Smelova", "UA", 21));

        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");

        try(Writer writer  = new FileWriter(FILE_CSV)) {
            StatefulBeanToCsv<Employee> sbc =new StatefulBeanToCsvBuilder<Employee>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            sbc.write(list);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
    }



    private static void saveTextFileJson(String extForSaving) {
        if (!extForSaving.isEmpty()) {
            try (FileWriter writer = new FileWriter(FILE_JSON, false)) {
                // запись всей строки
                writer.write(extForSaving);
                // дозаписываем и очищаем буфер
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

}
