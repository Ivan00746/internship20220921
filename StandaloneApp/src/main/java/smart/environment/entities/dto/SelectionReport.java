package smart.environment.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class SelectionReport {
    private long id;
    private Date timeOfCreation;
    private long rowsQuantity;
    private long initTime;
    private long averageSelectionTime;
    ArrayList<SelectionResult> selectionResultArrayList;

    public SelectionReport () {
        this.initTime = 0;
        this.averageSelectionTime = 0;
        selectionResultArrayList = new ArrayList<SelectionResult>();
    }

    @Data
    @AllArgsConstructor
    public static class SelectionResult {
        private String charSequence;
        private long count;
        private long selectionTime;
    }
}
