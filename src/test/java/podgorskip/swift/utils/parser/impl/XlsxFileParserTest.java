package podgorskip.swift.utils.parser.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.springframework.test.context.ActiveProfiles;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class XlsxFileParserTest {

    private XlsxFileParser xlsxFileParser;
    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        xlsxFileParser = new XlsxFileParser();

        tempFile = File.createTempFile("swift-test", ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SWIFT");
            Row header = sheet.createRow(0);
            String[] headers = {
                    "COUNTRY ISO2 CODE", "SWIFT CODE", "CODE TYPE", "NAME",
                    "ADDRESS", "TOWN NAME", "COUNTRY NAME", "TIME ZONE"
            };

            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("US");
            row1.createCell(1).setCellValue("ABCDEFGHXXX");
            row1.createCell(2).setCellValue("HEADQUARTER");
            row1.createCell(3).setCellValue("Test HQ Bank");
            row1.createCell(4).setCellValue("123 HQ Street");
            row1.createCell(5).setCellValue("New York");
            row1.createCell(6).setCellValue("United States");
            row1.createCell(7).setCellValue("EST");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("US");
            row2.createCell(1).setCellValue("ABCDEFGH123");
            row2.createCell(2).setCellValue("BRANCH");
            row2.createCell(3).setCellValue("Test Branch");
            row2.createCell(4).setCellValue("456 Branch Ave");
            row2.createCell(5).setCellValue("New York");
            row2.createCell(6).setCellValue("United States");
            row2.createCell(7).setCellValue("EST");

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }
        }
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testParse_ValidXlsx_ReturnsCorrectMaps() {
        Pair<Map<String, SwiftCodeRequest>, Map<String, String>> result = xlsxFileParser.parse(tempFile.getAbsolutePath());

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
