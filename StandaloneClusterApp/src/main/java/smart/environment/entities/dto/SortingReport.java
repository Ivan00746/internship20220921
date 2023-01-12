package smart.environment.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class SortingReport {
    private long id;
    private Date timeOfCreation;
    private long rowsQuantity;
    private long initTime;
    private long averageSortingTime;
    ArrayList<SortingResult> sortingResultArrayList;

    public SortingReport () {
        this.initTime = 0;
        this.averageSortingTime = 0;
        sortingResultArrayList = new ArrayList<SortingResult>();
    }

    @Data
    @AllArgsConstructor
    public static class SortingResult {
        private String description;
        private long sortingTime;
    }
}
