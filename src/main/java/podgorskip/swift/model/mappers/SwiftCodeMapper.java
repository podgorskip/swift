package podgorskip.swift.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.entities.SwiftCode;

@Mapper
public interface SwiftCodeMapper {
    SwiftCodeMapper INSTANCE = Mappers.getMapper(SwiftCodeMapper.class);

    SwiftCode toSwiftCode(final SwiftCodeRequest swiftCodeRequest);
}
