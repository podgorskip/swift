package podgorskip.swift.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.dto.SwiftCodeResponse;
import podgorskip.swift.model.entities.SwiftCode;

@Mapper
public interface SwiftCodeMapper {
    SwiftCodeMapper INSTANCE = Mappers.getMapper(SwiftCodeMapper.class);

    SwiftCode toSwiftCode(final SwiftCodeRequest swiftCodeRequest);

    default SwiftCodeResponse toSwiftCodeResponse(final SwiftCode swiftCode) {
        return SwiftCodeResponse.builder()
                .address(swiftCode.getAddress())
                .bankName(swiftCode.getBankName())
                .countryISO2(swiftCode.getCountryISO2())
                .countryName(swiftCode.getCountryName())
                .isHeadquarter(swiftCode.getIsHeadquarter())
                .swiftCode(swiftCode.getSwiftCode())
                .branches(swiftCode.getBranches().stream().map(SwiftCodeMapper.INSTANCE::toSwiftCodeResponse).toList())
                .build();
    }
}
