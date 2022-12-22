package smart.environment.controllers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("sparkContext")
@RestController
public class SparkController {

    private long sparkSessionInitTime;
    @Autowired
    private SparkSession sparkSession;

    @RequestMapping("")
    public String getRowCount(@RequestParam(name = "charSequence", required = false) String varChar) {
        final String finalVarChar;
        String logFile = "./datasrc/JavaFXdocs.html";
        if (varChar == null) finalVarChar = "FX";
            else finalVarChar = varChar;
        Long timePoint_1 =  System.nanoTime();
        Dataset<String> logData = sparkSession.read().textFile(logFile).cache();
        Long timePoint_2 =  System.nanoTime();
        long numAs = logData.filter((String s) -> s.contains(finalVarChar)).count();
        Long timePoint_3 =  System.nanoTime();
        if (sparkSessionInitTime == 0) sparkSessionInitTime = timePoint_2 - timePoint_1;
        long sparkMethodsExecTime = timePoint_3 - timePoint_2;
        String result = "Count lines in file with char sequence '" + finalVarChar + "': " + numAs;
        String resultResponse = "Spark session initialization time: " + sparkSessionInitTime/1000000
                + " ms; Spark methods execution time: " + sparkMethodsExecTime/1000000
                + " ms; Result with file (./datasrc/JavaFXdocs.html) >>> " + result + " rows (of 43493).";
        System.out.println("========================================================");
        System.out.println(result);
        System.out.println("========================================================");
//        sparkSession.stop();

        return resultResponse;
    }
}
