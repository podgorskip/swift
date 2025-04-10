package podgorskip.swift.utils.parser.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import podgorskip.swift.exceptions.ParsingException;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.utils.parser.FileParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Setter
@Component
public class CsvFileParser implements FileParser {
    private String separator = ",";

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

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] headers = reader.readLine().split(separator);
            Map<String, Integer> columnMap = new HashMap<>();

            for (int i = 0; i < headers.length; i++) {
                columnMap.put(headers[i].trim(), i);
            }

            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(separator, -1);

                String bankName = fields[columnMap.get(Column.NAME.getHeader())].trim();
                String countryISO2 = fields[columnMap.get(Column.COUNTRY_ISO2.getHeader())].trim().toUpperCase();
                String countryName = fields[columnMap.get(Column.COUNTRY_NAME.getHeader())].trim().toUpperCase();
                String swiftCode = fields[columnMap.get(Column.SWIFT_CODE.getHeader())].trim();
                boolean isHeadquarter = swiftCode.endsWith("XXX");

                SwiftCodeRequest swiftCodeRequest = SwiftCodeRequest.builder()
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
            throw new ParsingException("Failed to read CSV file: " + e);
        }

        return Pair.of(swiftCodeMap, relationMap);
    }

}