import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {

        String dataFile = "./src/main/resources/data.xlsx";
        String jsonFile1 = "./src/main/resources/1.json";
        String jsonFile2 = "./src/main/resources/2.json";
        compareJsonResponse(dataFile, jsonFile1, jsonFile2);

    }
    public static void compareJsonResponse(String dataFile, String jsonFile1, String jsonFile2) {
        String query = "SELECT * FROM Sheet1";
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(dataFile);
            Recordset recordset = connection.executeQuery(query);
            while (recordset.next()) {
                String oldValue = recordset.getField("OldResponse");
                String newValue = recordset.getField("NewResponse");
                String id = recordset.getField("ID");

                Reader reader1 = Files.newBufferedReader(Paths.get(jsonFile1));
                Reader reader2 = Files.newBufferedReader(Paths.get(jsonFile2));
                JSONParser jsonParser = new JSONParser();

                String response1 = jsonParser.parse(reader1).toString();
                String response2 = jsonParser.parse(reader2).toString();

                Configuration configuration = Configuration.builder()
                        .jsonProvider(new JacksonJsonProvider())
                        .build();

                DocumentContext jsonContext1 = JsonPath.using(configuration).parse(response1);
                DocumentContext jsonContext2 = JsonPath.using(configuration).parse(response2);


                String expected = jsonContext1.read(oldValue).toString().trim();
                String actual = jsonContext2.read(newValue).toString().trim();

                String query1 = "UPDATE sheet1 Set Expected='" + expected + "' where ID=" + id;
                connection.executeUpdate(query1);
                String query2 = "UPDATE sheet1 Set Actual='" + actual + "' where ID=" + id;
                connection.executeUpdate(query2);
                String query3;

                if (expected.equals(actual))
                    query3 = "UPDATE sheet1 Set Status='Pass' where ID=" + id;
                else
                    query3 = "UPDATE sheet1 Set Status='Fail' where ID=" + id;

                connection.executeUpdate(query3);
            }
            recordset.close();
            connection.close();
        } catch (FilloException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
