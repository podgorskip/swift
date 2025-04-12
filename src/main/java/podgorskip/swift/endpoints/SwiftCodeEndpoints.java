package podgorskip.swift.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podgorskip.swift.model.dto.CountrySwiftCodeResponse;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.dto.SwiftCodeResponse;

@RequestMapping("/v1/swift-codes")
@Tag(name = "Swift code API", description = "Operations related to swift codes")
public interface SwiftCodeEndpoints {

    @Operation(
            summary = "Get swift code details by code",
            description = "Fetches the details of a swift code by its code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved swift code details"),
            @ApiResponse(responseCode = "404", description = "Swift code not found")
    })
    @GetMapping("/{code}")
    ResponseEntity<SwiftCodeResponse> getSwiftCode(
            @Parameter(description = "The swift code to be fetched", required = true, example = "SPMLPLP1XXX")
            @PathVariable("code") String code
    );

    @Operation(
            summary = "Get swift codes by country",
            description = "Fetches a list of swift codes registered in the given country"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved swift codes for the country"),
    })
    @GetMapping("/country/{country}")
    ResponseEntity<CountrySwiftCodeResponse> getCountrySwiftCodes(
            @Parameter(description = "2-letter country ISO code", required = true, example = "PL")
            @PathVariable("country") String country
    );

    @Operation(
            summary = "Create a new swift code",
            description = "Adds a new swift code for a given bank address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created swift code"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "409", description = "Swift code already exists"),
    })
    @PostMapping
    ResponseEntity<String> addSwiftCode(@Valid @RequestBody SwiftCodeRequest request);

    @Operation(
            summary = "Delete a swift code",
            description = "Deletes a swift code by its code if it exists"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Swift code successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Swift code not found")
    })
    @DeleteMapping("/{code}")
    ResponseEntity<String> deleteSwiftCode(
            @Parameter(description = "The swift code to be deleted", required = true, example = "SPMLPLP1XXX")
            @PathVariable("code") String code
    );
}
