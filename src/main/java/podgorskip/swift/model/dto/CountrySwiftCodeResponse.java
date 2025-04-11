package podgorskip.swift.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountrySwiftCodeResponse {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeResponse> swiftCodes;
}
