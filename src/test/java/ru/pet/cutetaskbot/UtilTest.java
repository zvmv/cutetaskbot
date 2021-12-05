package ru.pet.cutetaskbot;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
class UtilTest {



    @Test
    void now() {
        Assertions.assertEquals("Сегодня", Util.stripDate(LocalDate.now()));
    }

    void this_weak(){
    }
}