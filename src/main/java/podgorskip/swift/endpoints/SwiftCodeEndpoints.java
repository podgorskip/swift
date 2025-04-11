package podgorskip.swift.endpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import podgorskip.swift.model.dto.CountrySwiftCodeResponse;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.dto.SwiftCodeResponse;

@RequestMapping("/swift-codes")
public interface SwiftCodeEndpoints {

    @GetMapping("/{code}")
    ResponseEntity<SwiftCodeResponse> getSwiftCode(@PathVariable("code") String code);

    @GetMapping("/country/{country}")
    ResponseEntity<CountrySwiftCodeResponse> getCountrySwiftCodes(@PathVariable("country") String country);

    @PostMapping
    ResponseEntity<String> addSwiftCode(@RequestBody SwiftCodeRequest request);

    @DeleteMapping("/{code}")
    ResponseEntity<String> deleteSwiftCode(@PathVariable("code") String code);
}
