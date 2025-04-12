package podgorskip.swift.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull @NotBlank @Size(min = 2, max = 2, message = "ISO2 country code must be 2 characters long")
    private String countryISO2;

    @NotNull @NotBlank private String countryName;

    @NotNull private Boolean isHeadquarter;

    @NotNull @NotBlank @Size(min = 11, max = 11, message = "Swift code must be 11 characters long")
    private String swiftCode;
}
