package podgorskip.swift.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import podgorskip.swift.exceptions.ParsingException;
import podgorskip.swift.exceptions.SwiftCodeException;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.entities.SwiftCode;
import podgorskip.swift.model.mappers.SwiftCodeMapper;
import podgorskip.swift.respositories.SwiftCodeRepository;
import podgorskip.swift.utils.parser.FileParser;
import podgorskip.swift.utils.parser.impl.CsvFileParser;
import podgorskip.swift.utils.parser.impl.XlsxFileParser;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public UUID createSwiftCode(SwiftCodeRequest request) {

        SwiftCode swiftCode = SwiftCode.builder()
                .address(request.getAddress())
                .bankName(request.getBankName())
                .countryISO2(request.getCountryISO2())
                .countryName(request.getCountryName())
                .isHeadquarter(request.getIsHeadquarter() != null ? request.getIsHeadquarter() : false)
                .swiftCode(request.getSwiftCode())
                .build();

        String headquarterSwiftCode = swiftCode.getSwiftCode().substring(0, 8) + "XXX";
        swiftCodeRepository.findBySwiftCode(headquarterSwiftCode).ifPresent(swiftCode::setSuperiorUnit);

        SwiftCode persistedSwiftCode = swiftCodeRepository.save(swiftCode);
        redisCacheService.save(persistedSwiftCode.getSwiftCode(), persistedSwiftCode);
        redisCacheService.addToSet(persistedSwiftCode.getCountryISO2(), persistedSwiftCode.getSwiftCode());
        return persistedSwiftCode.getId();
    }

    public SwiftCode getSwiftCode(String code) {
        return redisCacheService.get(code)
                .orElseGet(() -> swiftCodeRepository.findBySwiftCode(code)
                        .orElseThrow(() -> new SwiftCodeException(
                                "Swift code not found, code=" + code,
                                HttpStatus.NOT_FOUND)
                        ));
    }

    public List<SwiftCode> getSwiftCodes(String countryISO2) {
        Set<String> swiftCodesCache = redisCacheService.getSet(countryISO2);

        if (swiftCodesCache != null && !swiftCodesCache.isEmpty()) {
            return swiftCodesCache.stream()
                    .map(redisCacheService::get)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        }

        List<SwiftCode> swiftCodes = swiftCodeRepository.findByCountryISO2(countryISO2);

        swiftCodes.forEach(swiftCode -> {
            redisCacheService.save(swiftCode.getSwiftCode(), swiftCode);
            redisCacheService.addToSet(countryISO2, swiftCode.getSwiftCode());
        });

        return swiftCodes;
    }

    public void transferSwiftCodes(String filePath) {
        FileParser fileParser;

        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        switch (extension) {
            case "csv" -> fileParser = new CsvFileParser();
            case "xlsx" -> fileParser = new XlsxFileParser();
            default -> throw new ParsingException("Unsupported extension: " + extension, HttpStatus.BAD_REQUEST);
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
                SwiftCode superiorUnit = persistentSwiftCodes.get(potentialHqCode);

                if (Objects.nonNull(superiorUnit)) {
                    branch.setSuperiorUnit(superiorUnit);
                    superiorUnit.getBranches().add(branch);
                }
            }

            swiftCodeRepository.saveAll(persistentSwiftCodes.values());

            persistentSwiftCodes.values().forEach(persistentSwiftCode -> {
                redisCacheService.save(persistentSwiftCode.getSwiftCode(), persistentSwiftCode);
                redisCacheService.addToSet(persistentSwiftCode.getCountryISO2(), persistentSwiftCode.getSwiftCode());
            });

        } catch (ParsingException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ParsingException(
                    String.format("Error parsing file, filename=%s, error=%s", filePath, exception.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public void removeAllSwiftCodes() {
        swiftCodeRepository.deleteAll();
    }

    public UUID removeSwiftCode(String code) {
        return swiftCodeRepository.findBySwiftCode(code)
                .map(swiftCode -> {
                    swiftCodeRepository.delete(swiftCode);
                    return swiftCode.getId();
                })
                .orElseThrow(() -> new SwiftCodeException(
                        String.format("Swift code not found, code=%s", code),
                        HttpStatus.NOT_FOUND
                ));
    }
}
