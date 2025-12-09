package idhub.sortinparcels.io.reader;

import idhub.sortinparcels.dto.ParcelReaderDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataReader {
    List<ParcelReaderDto> read(MultipartFile file);
}
