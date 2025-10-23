package idhub.sortinparcels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SortinParcelsApplication {

    public static void main(String[] args) {
        log.info("IDHub SortinPackage Application Started");
        SpringApplication.run(SortinParcelsApplication.class, args);

    }

}
