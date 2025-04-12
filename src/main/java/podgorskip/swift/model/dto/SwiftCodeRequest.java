package podgorskip.swift.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCodeRequest {
    @NotNull @NotBlank private String address;
    @NotNull @NotBlank private String bankName;
    @NotNull @NotBlank private String countryISO2;
    @NotNull @NotBlank private String countryName;
    @NotNull private Boolean isHeadquarter;
    @NotNull @NotBlank private String swiftCode;
}
