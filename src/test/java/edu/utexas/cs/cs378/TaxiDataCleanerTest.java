package edu.utexas.cs.cs378;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TaxiDataCleanerTest {

    @Test
    public void cleansValidAndInvalidLines() {
        String validLine =
                "d41d8cd98f00b204e9800998ecf8427e,0cc175b9c0f1b6a831c399e269772661,2013-01-01 00:00:00,2013-01-01 00:10:00,600,1.20,-73.991242,40.750562,-73.985001,40.758244,CASH,3.50,0.50,0.50,0.00,0.00,4.50";

        String invalidTotal =
                "d41d8cd98f00b204e9800998ecf8427e,92eb5ffee6ae2fec3ad71c777531578f,2013-01-01 00:00:00,2013-01-01 00:10:00,600,1.20,-73.991242,40.750562,-73.985001,40.758244,CASH,3.50,0.50,0.50,0.00,0.00,10.00";

        String tooLarge =
                "d41d8cd98f00b204e9800998ecf8427e,8277e0910d750195b448797616e091ad,2013-01-01 00:00:00,2013-01-01 00:10:00,600,1.20,-73.991242,40.750562,-73.985001,40.758244,CARD,540.00,5.00,0.00,5.00,0.00,550.00";

        List<String> lines = Arrays.asList(validLine, invalidTotal, tooLarge);

        TaxiDataCleaner.CleanResult result = TaxiDataCleaner.cleanLines(lines.stream());

        assertEquals(3, result.getTotalLines());
        assertEquals(2, result.getInvalidLines());
        assertEquals(1, result.getValidItems().size());
        assertEquals(validLine, result.getValidItems().get(0).getLine());
        assertEquals(2, result.getErrorSamples().size());
        assertTrue(result.getErrorSamples().contains(invalidTotal));
        assertTrue(result.getErrorSamples().contains(tooLarge));
    }
}
