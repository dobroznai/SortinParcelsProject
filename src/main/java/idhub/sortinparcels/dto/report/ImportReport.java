package idhub.sortinparcels.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportReport {

    private int totalRows;            // скільки рядків було у файлі
    private int imported;             // реально збережено
    private int duplicatesInFile;     // дублікати усередині файлу
    private int duplicatesInDb;       // дублікати в БД
    private int invalidRows;          // не пройшли валідацію
}