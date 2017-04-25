package controller;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import summary.ProcessStringToSummary;

/**
 * Created by Z8 on 4/16/2017.
 */
@RestController
@EnableAutoConfiguration
public class StartServer {

    @RequestMapping(value = "/get-all", method = RequestMethod.GET)
    public String getAll(){
        return "It work";
    }

    @RequestMapping(value = "/test-post-json", method = RequestMethod.POST, consumes = "application/json")
    public String testPostJson(@RequestBody String jsondata){
        return jsondata;
    }

    @RequestMapping(value = "/get-summarize", method = RequestMethod.POST, consumes = "application/json")
    public String summarizeString(@RequestBody String jsondata){
        JSONObject dataObject= new JSONObject(jsondata);
        String originData= dataObject.getString("data");
        //System.out.println(originData);
        String res=  ProcessStringToSummary.summarizeString(originData);
        JSONObject result= new JSONObject();
        result.put("result", res);
        return result.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(StartServer.class, args);
    }
}
