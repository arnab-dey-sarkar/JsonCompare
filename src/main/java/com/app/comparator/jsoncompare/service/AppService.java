package com.app.comparator.jsoncompare.service;

import com.app.comparator.jsoncompare.model.AppModel;
import com.app.comparator.jsoncompare.repository.AppRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    @Autowired
    AppRepository appRepository;

    public AppModel compare(String jsonFile1, String jsonFile2, String dataFile) {
        AppModel appModel = new AppModel();
        String fields = new String();
        int c = 0;
        String query = "SELECT * FROM Sheet1";
        try {
            Fillo fillo = new Fillo();
            Connection connection = fillo.getConnection(dataFile);
            Recordset recordset = connection.executeQuery(query);
            while (recordset.next()) {
                String oldValue = recordset.getField("OldResponseJsonpath");
                String newValue = recordset.getField("NewResponseJsonpath");
                String id = recordset.getField("Id");

                JSONParser jsonParser = new JSONParser();

                String response1 = jsonParser.parse(jsonFile1).toString();
                String response2 = jsonParser.parse(jsonFile2).toString();

                Configuration configuration = Configuration.builder()
                        .jsonProvider(new JacksonJsonProvider())
                        .build();

                DocumentContext jsonContext1 = JsonPath.using(configuration).parse(response1);
                DocumentContext jsonContext2 = JsonPath.using(configuration).parse(response2);


                String expected = jsonContext1.read(oldValue).toString().trim();
                String actual = jsonContext2.read(newValue).toString().trim();



                if (!expected.equals(actual)) {
                    fields = fields + " Expected=" + expected + " Actual=" + actual + ";";
                    c++;
                }

            }
            appModel.setFields(fields);
            if (c > 0)
                appModel.setStatus("Fail");
            else
                appModel.setStatus("Pass");

            recordset.close();
            connection.close();
        } catch (FilloException | ParseException e) {
            e.printStackTrace();
        }
        appRepository.save(appModel);
        return appModel;
    }
}
