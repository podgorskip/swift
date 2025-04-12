package podgorskip.swift.utils.parser.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import podgorskip.swift.exceptions.ParsingException;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.utils.parser.FileParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class XlsxFileParser implements FileParser {

    @Getter
    @RequiredArgsConstructor
    private enum Column {
        COUNTRY_ISO2("COUNTRY ISO2 CODE"),
        SWIFT_CODE("SWIFT CODE"),
        CODE_TYPE("CODE TYPE"),
        NAME("NAME"),
        ADDRESS("ADDRESS"),
        TOWN_NAME("TOWN NAME"),
        COUNTRY_NAME("COUNTRY NAME"),
        TIME_ZONE("TIME ZONE");

        private final String header;
    }

    @Override
    public Pair<Map<String, SwiftCodeRequest>, Map<String, String>> parse(String filePath) {
        Map<String, SwiftCodeRequest> swiftCodeMap = new HashMap<>();
        Map<String, String> relationMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnMap = new HashMap<>();
            for (Cell cell : headerRow) {
                columnMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String address = getCellValue(row, columnMap.get(Column.ADDRESS.getHeader()));
                String bankName = getCellValue(row, columnMap.get(Column.NAME.getHeader()));
                String countryISO2 = getCellValue(row, columnMap.get(Column.COUNTRY_ISO2.getHeader())).toUpperCase();
                String countryName = getCellValue(row, columnMap.get(Column.COUNTRY_NAME.getHeader())).toUpperCase();
                String swiftCode = getCellValue(row, columnMap.get(Column.SWIFT_CODE.getHeader()));
                boolean isHeadquarter = swiftCode.endsWith("XXX");

                SwiftCodeRequest swiftCodeRequest = SwiftCodeRequest.builder()
                        .address(address)
                        .bankName(bankName)
                        .countryISO2(countryISO2)
                        .countryName(countryName)
                        .isHeadquarter(isHeadquarter)
                        .swiftCode(swiftCode)
                        .build();

                swiftCodeMap.put(swiftCode, swiftCodeRequest);

                if (!isHeadquarter) {
                    String bankPrefix = swiftCode.substring(0, 8);
                    relationMap.put(swiftCode, bankPrefix + "XXX");
                }
            }
        } catch (IOException e) {
            throw new ParsingException("Failed to read XLSX file, err=" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return Pair.of(swiftCodeMap, relationMap);
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            default -> "";
        };
    }
}