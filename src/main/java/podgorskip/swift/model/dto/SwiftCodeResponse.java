package podgorskip.swift.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SwiftCodeResponse {
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private Boolean isHeadquarter;
    private String swiftCode;
    private List<SwiftCodeResponse> branches;
}
