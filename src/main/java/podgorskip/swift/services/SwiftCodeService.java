package podgorskip.swift.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import podgorskip.swift.exceptions.ParsingException;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.entities.SwiftCode;
import podgorskip.swift.model.mappers.SwiftCodeMapper;
import podgorskip.swift.respositories.SwiftCodeRepository;
import podgorskip.swift.utils.parser.FileParser;
import podgorskip.swift.utils.parser.impl.CsvFileParser;
import podgorskip.swift.utils.parser.impl.XlsxFileParser;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    @Transactional
    public SwiftCode createSwiftCode(SwiftCodeRequest request) {

        SwiftCode swiftCode = SwiftCode.builder()
                .bankName(request.getBankName())
                .countryISO2(request.getCountryISO2())
                .countryName(request.getCountryName())
                .isHeadquarter(request.getIsHeadquarter() != null ? request.getIsHeadquarter() : false)
                .swiftCode(request.getSwiftCode())
                .build();

        String headquarterSwiftCode = swiftCode.getSwiftCode().substring(0, 8) + "XXX";
        swiftCodeRepository.findBySwiftCode(headquarterSwiftCode).ifPresent(swiftCode::setSuperiorUnit);

        return swiftCodeRepository.save(swiftCode);
    }

    public void transferSwiftCodes(String filePath) {
        FileParser fileParser;

        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        switch (extension) {
            case "csv" -> fileParser = new CsvFileParser();
            case "xlsx" -> fileParser = new XlsxFileParser();
            default -> throw new ParsingException("Unsupported extension: " + extension);
        }

        try {
            Pair<Map<String, SwiftCodeRequest>, Map<String, String>> result = fileParser.parse(filePath);
            Map<String, SwiftCodeRequest> swiftCodes = result.getLeft();
            Map<String, String> relations = result.getRight();

            Map<String, SwiftCode> persistentSwiftCodes = swiftCodes.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> SwiftCodeMapper.INSTANCE.toSwiftCode(entry.getValue())
                    ));


            for (Map.Entry<String, String> entry : relations.entrySet()) {
                String branchCode = entry.getKey();
                String potentialHqCode = entry.getValue();
                SwiftCode branch = persistentSwiftCodes.get(branchCode);
                branch.setSuperiorUnit(persistentSwiftCodes.getOrDefault(potentialHqCode, null));
            }

            swiftCodeRepository.saveAll(persistentSwiftCodes.values());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
