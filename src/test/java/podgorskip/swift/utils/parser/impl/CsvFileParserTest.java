package podgorskip.swift.utils.parser.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class CsvFileParserTest {

    private CsvFileParser csvFileParser;
    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        csvFileParser = new CsvFileParser();

        tempFile = File.createTempFile("swift-test", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("COUNTRY ISO2 CODE,SWIFT CODE,CODE TYPE,NAME,ADDRESS,TOWN NAME,COUNTRY NAME,TIME ZONE\n");
            writer.write("US,ABCDEFGHXXX,HEADQUARTER,Test HQ Bank,123 HQ Street,New York,United States,EST\n");
            writer.write("US,ABCDEFGH123,BRANCH,Test Branch,456 Branch Ave,New York,United States,EST\n");
        }
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testParse_ValidCSV_ReturnsCorrectMaps() {
        Pair<Map<String, SwiftCodeRequest>, Map<String, String>> result = csvFileParser.parse(tempFile.getAbsolutePath());

        Map<String, SwiftCodeRequest> swiftMap = result.getLeft();
        Map<String, String> relationMap = result.getRight();

        assertEquals(2, swiftMap.size());
        assertEquals(1, relationMap.size());

        SwiftCodeRequest hq = swiftMap.get("ABCDEFGHXXX");
        assertNotNull(hq);
        assertTrue(hq.getIsHeadquarter());
        assertEquals("Test HQ Bank", hq.getBankName());

        SwiftCodeRequest branch = swiftMap.get("ABCDEFGH123");
        assertNotNull(branch);
        assertFalse(branch.getIsHeadquarter());
        assertEquals("Test Branch", branch.getBankName());

        assertEquals("ABCDEFGHXXX", relationMap.get("ABCDEFGH123"));
    }
}
