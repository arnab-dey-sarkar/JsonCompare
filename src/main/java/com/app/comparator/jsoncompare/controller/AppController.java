package com.app.comparator.jsoncompare.controller;

import com.app.comparator.jsoncompare.model.AppModel;
import com.app.comparator.jsoncompare.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AppController {
    @Autowired
    AppService appService;

    @RequestMapping(method = RequestMethod.POST, value = "/compare", consumes = "multipart/form-data")
    public AppModel compareJson(@RequestParam("file1") MultipartFile jsonFile1, @RequestParam("file2") MultipartFile jsonFile2) {
        String content1, content2;
        try {
            content1 = new String(jsonFile1.getBytes());
            content2 = new String(jsonFile2.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return appService.compare(content1, content2, "./src/main/resources/jsonMapping.xlsx");
    }
}
