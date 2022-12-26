package smart.environment.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
public class SortingReport {
    private int numberOfRows;
    private long initTime;
    private long averageSortingTime;
    ArrayList<SortingReport.SortingResult> sortingResultArrayList;

    public SortingReport () {
        this.initTime = 0;
        this.averageSortingTime = 0;
        sortingResultArrayList = new ArrayList<SortingReport.SortingResult>();
    }

    @Data
    @AllArgsConstructor
    public static class SortingResult {
        private String description;
        private long sortingTime;
    }
}
