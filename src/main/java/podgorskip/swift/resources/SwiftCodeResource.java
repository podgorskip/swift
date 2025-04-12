package podgorskip.swift.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import podgorskip.swift.endpoints.SwiftCodeEndpoints;
import podgorskip.swift.model.dto.CountrySwiftCodeResponse;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.dto.SwiftCodeResponse;
import podgorskip.swift.model.entities.SwiftCode;
import podgorskip.swift.model.mappers.SwiftCodeMapper;
import podgorskip.swift.services.SwiftCodeService;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class SwiftCodeResource implements SwiftCodeEndpoints {
    private final SwiftCodeService swiftCodeService;

    @Override
    public ResponseEntity<SwiftCodeResponse> getSwiftCode(String code) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(SwiftCodeMapper.INSTANCE.toSwiftCodeResponse(swiftCodeService.getSwiftCode(code)));
    }

    @Override
    public ResponseEntity<CountrySwiftCodeResponse> getCountrySwiftCodes(String countryISO2) {
        List<SwiftCode> swiftCodes = swiftCodeService.getSwiftCodes(countryISO2);

        CountrySwiftCodeResponse response = CountrySwiftCodeResponse.builder()
                .countryISO2(countryISO2)
                .countryName(swiftCodes.stream()
                        .findFirst()
                        .map(SwiftCode::getCountryName)
                        .orElse(null))
                .swiftCodes(swiftCodes.stream().map(SwiftCodeMapper.INSTANCE::toSwiftCodeResponse).toList())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<String> addSwiftCode(SwiftCodeRequest request) {
        UUID id = swiftCodeService.createSwiftCode(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(String.format("Successfully created swift code, id=%s", id));
    }

    @Override
    public ResponseEntity<String> deleteSwiftCode(String code) {
        UUID id = swiftCodeService.removeSwiftCode(code);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(String.format("Successfully removed swift code, id=%s", id));
    }
}
