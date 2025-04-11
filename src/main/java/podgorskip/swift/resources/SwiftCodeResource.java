package podgorskip.swift.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import podgorskip.swift.endpoints.SwiftCodeEndpoints;
import podgorskip.swift.model.dto.CountrySwiftCodeResponse;
import podgorskip.swift.model.dto.SwiftCodeResponse;
import podgorskip.swift.model.entities.SwiftCode;
import podgorskip.swift.model.mappers.SwiftCodeMapper;
import podgorskip.swift.services.SwiftCodeService;
import java.util.List;
import java.util.Optional;

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
                .countryName(Optional.ofNullable(swiftCodes.get(0)).map(SwiftCode::getCountryName).orElse(null))
                .swiftCodes(swiftCodes.stream().map(SwiftCodeMapper.INSTANCE::toSwiftCodeResponse).toList())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
