package smart.environment.controllers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smart.environment.entities.dto.SelectionReport;
import smart.environment.entities.RandomIntSeq;
import smart.environment.entities.dto.SortingReport;

import java.util.*;

import static org.apache.spark.sql.functions.col;

@PropertySource("classpath:application.properties")
@RequestMapping("/localDefault")
@RestController
public class LocalDefaultController {
    final static List<String> searchKeys = Arrays.asList("fx", "java", "span", "class", "href");
    private long selectionDataLoadingTime, sortingDataLoadingTime;
    @Autowired
    SparkSession sparkSession;

    @GetMapping("/selectionReport")
    public SelectionReport getSelectionReport() {
        String logFile = "./datasrc/JavaFXdocs.html";
        SelectionReport selectionReport = new SelectionReport();
        long sparkMethodsExecTime = 0, deltaTime, timePoint_1a, timePoint_1b, timePoint_2a, timePoint_2b;
        long numAs;
//      ------Init------
        timePoint_1a = System.nanoTime();
        Dataset<String> logData = sparkSession.read().textFile(logFile).cache();
        timePoint_1b = System.nanoTime();
        if (selectionDataLoadingTime == 0) selectionDataLoadingTime = (timePoint_1b - timePoint_1a) / 1000000;
//      ----End Init----
        for (int i = 0; i < 5; i++) {
            String searchKey = searchKeys.get(i);
//      ----Selection----
            timePoint_2a = System.nanoTime();
            numAs = logData.filter((String s) -> s.contains(searchKey)).count();
            timePoint_2b = System.nanoTime();
//      --End Selection--
            deltaTime = (timePoint_2b - timePoint_2a) / 1000000;
            selectionReport.getSelectionResultArrayList().add(new SelectionReport
                    .SelectionResult(searchKey, numAs, deltaTime));
            sparkMethodsExecTime = sparkMethodsExecTime + deltaTime;
        }
        selectionReport.setInitTime(selectionDataLoadingTime);
        selectionReport.setAverageSelectionTime(sparkMethodsExecTime / 5);
        String result = "Selection data init time: " + selectionReport.getInitTime()
                + " ms; Average selection time: " + selectionReport.getAverageSelectionTime() + " ms.";
        System.out.println("=============================================================================");
        System.out.println(result);
        System.out.println("=============================================================================");
//        sparkSession.stop();
        return selectionReport;
    }

    @GetMapping("/sortingReport")
    public SortingReport getSortingReport() {
        SortingReport sortingReport = new SortingReport();
        long sparkMethodsExecTime = 0, deltaTime, timePoint_1a, timePoint_1b, timePoint_2a, timePoint_2b;
        RandomIntSeq[] randomIntTable = new RandomIntSeq[20000];
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < randomIntTable.length; i++) {
            randomIntTable[i] = new RandomIntSeq(i,
                    random.nextInt(1000000000),
                    random.nextInt(1000000000),
                    random.nextInt(1000000000),
                    random.nextInt(1000000000),
                    random.nextInt(1000000000));
        }
//      ------Init------
        timePoint_1a = System.nanoTime();
        Dataset<org.apache.spark.sql.Row> randomIntSet =
                sparkSession.createDataFrame(List.of(randomIntTable), RandomIntSeq.class);
        randomIntSet.show(10);
        timePoint_1b = System.nanoTime();
//      ----End Init----
        sortingDataLoadingTime = (timePoint_1b - timePoint_1a) / 1000000;
        for (int i = 0; i < 5; i++) {
//      ----Selection----
            timePoint_2a = System.nanoTime();
            Dataset<org.apache.spark.sql.Row> sortedIntSet =
                    randomIntSet.orderBy(col("int" + (i + 1)).desc());
            sortedIntSet.show(10);
            timePoint_2b = System.nanoTime();
//      --End Selection--
            System.out.println("------------------------------------");
            System.out.println("Table above sorted by column: " + "'int" + (i + 1) + "'");
            System.out.println("------------------------------------");
            System.out.println();
            deltaTime = (timePoint_2b - timePoint_2a) / 1000000;
            sortingReport.getSortingResultArrayList().add(new SortingReport
                    .SortingResult("Sorted by column: "
                    + "'int" + (i + 1) + "'", deltaTime));
            sparkMethodsExecTime = sparkMethodsExecTime + deltaTime;
        }
        sortingReport.setNumberOfRows(randomIntTable.length);
        sortingReport.setInitTime(sortingDataLoadingTime);
        sortingReport.setAverageSortingTime(sparkMethodsExecTime / 5);
        String result = "Sorting data init time: " + sortingReport.getInitTime()
                + " ms; Average selection time: " + sortingReport.getAverageSortingTime() + " ms.";
        System.out.println("=============================================================================");
        System.out.println(result);
        System.out.println("=============================================================================");
//        sparkSession.stop();
        return sortingReport;
    }
}
