package podgorskip.swift.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodeRequest {
    private String bankName;
    private String countryISO2;
    private String countryName;
    private Boolean isHeadquarter;
    private String swiftCode;
}
