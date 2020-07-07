package pers.adlered.voter;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class VoterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoterApplication.class, args);
    }

}
