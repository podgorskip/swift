package podgorskip.swift.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import podgorskip.swift.exceptions.ParsingException;
import podgorskip.swift.exceptions.SwiftCodeException;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import podgorskip.swift.model.entities.SwiftCode;
import podgorskip.swift.respositories.SwiftCodeRepository;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class SwiftCodeServiceTest {
    @Mock private SwiftCodeRepository swiftCodeRepository;
    @Mock private RedisCacheService redisCacheService;

    @InjectMocks private SwiftCodeService swiftCodeService;

    private SwiftCode headquarter;
    private SwiftCode branch;
    private UUID headquarterId;
    private UUID branchId;

    private final String BRANCH_SWIFT = "ABCDEFGHABC";
    private final String HEADQUARTER_SWIFT = "ABCDEFGHXXX";

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        headquarterId = UUID.randomUUID();
        branchId = UUID.randomUUID();

        headquarter = SwiftCode.builder()
                .id(headquarterId)
                .isHeadquarter(true)
                .swiftCode(HEADQUARTER_SWIFT)
                .countryISO2("US")
                .build();

        branch = SwiftCode.builder()
                .id(branchId)
                .isHeadquarter(false)
                .swiftCode(BRANCH_SWIFT)
                .countryISO2("US")
                .superiorUnit(headquarter)
                .build();

        headquarter.getBranches().add(branch);
    }

    @Test
    void testCreateSwiftCode_NewBranch_WithHeadquarterFound() {
        String code = "ABCDEFGH123";
        String expectedHQCode = code.substring(0, 8) + "XXX";

        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode(code)
                .bankName("Test Bank")
                .address("123 Test St")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.findBySwiftCode(expectedHQCode)).thenReturn(Optional.of(headquarter));

        UUID newId = UUID.randomUUID();
        SwiftCode newBranch = SwiftCode.builder()
                .id(newId)
                .swiftCode(code)
                .bankName("Test Bank")
                .address("123 Test St")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(false)
                .superiorUnit(headquarter)
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(newBranch);

        UUID resultId = swiftCodeService.createSwiftCode(request);

        assertEquals(newId, resultId);
        verify(swiftCodeRepository).findBySwiftCode(expectedHQCode);
        verify(swiftCodeRepository).save(any(SwiftCode.class));
        verify(redisCacheService).save(eq(expectedHQCode), any(SwiftCode.class));
        verify(redisCacheService).addToSet(eq("US"), eq(expectedHQCode));
        verify(redisCacheService).save(eq(code), any(SwiftCode.class));
        verify(redisCacheService).addToSet(eq("US"), eq(code));
    }

    @Test
    void testCreateSwiftCode_NewBranch_NoHeadquarterFound() {
        String code = "ZYXWV123";
        String expectedHQCode = code.substring(0, 8) + "XXX";

        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode(code)
                .bankName("New Bank")
                .address("456 Test Ave")
                .countryISO2("CA")
                .countryName("Canada")
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.findBySwiftCode(expectedHQCode)).thenReturn(Optional.empty());

        UUID newId = UUID.randomUUID();
        SwiftCode newSwiftCode = SwiftCode.builder()
                .id(newId)
                .swiftCode(code)
                .bankName("New Bank")
                .address("456 Test Ave")
                .countryISO2("CA")
                .countryName("Canada")
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(newSwiftCode);

        UUID resultId = swiftCodeService.createSwiftCode(request);

        assertEquals(newId, resultId);
        verify(swiftCodeRepository).findBySwiftCode(expectedHQCode);
        verify(swiftCodeRepository).save(any(SwiftCode.class));
        verify(redisCacheService).save(eq(code), any(SwiftCode.class));
        verify(redisCacheService).addToSet(eq("CA"), eq(code));
        verify(redisCacheService, never()).save(eq(expectedHQCode), any(SwiftCode.class));
    }

    @Test
    void testCreateSwiftCode_NewHeadquarter() {
        String code = "NEWHQXXX";

        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode(code)
                .bankName("HQ Bank")
                .address("789 HQ Blvd")
                .countryISO2("FR")
                .countryName("France")
                .isHeadquarter(true)
                .build();

        UUID newId = UUID.randomUUID();
        SwiftCode newHQ = SwiftCode.builder()
                .id(newId)
                .swiftCode(code)
                .bankName("HQ Bank")
                .address("789 HQ Blvd")
                .countryISO2("FR")
                .countryName("France")
                .isHeadquarter(true)
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(newHQ);

        UUID resultId = swiftCodeService.createSwiftCode(request);

        assertEquals(newId, resultId);
        verify(swiftCodeRepository).save(any(SwiftCode.class));
        verify(redisCacheService).save(eq(code), any(SwiftCode.class));
        verify(redisCacheService).addToSet(eq("FR"), eq(code));
    }

    @Test
    void testCreateSwiftCode_NullIsHeadquarter_DefaultsToFalse() {
        String code = "NULLHQ45";

        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .swiftCode(code)
                .bankName("Default Bank")
                .address("101 Default St")
                .countryISO2("DE")
                .countryName("Germany")
                .isHeadquarter(null)
                .build();

        when(swiftCodeRepository.findBySwiftCode("NULLHQXXX")).thenReturn(Optional.empty());

        UUID newId = UUID.randomUUID();
        SwiftCode newSwiftCode = SwiftCode.builder()
                .id(newId)
                .swiftCode(code)
                .bankName("Default Bank")
                .address("101 Default St")
                .countryISO2("DE")
                .countryName("Germany")
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(newSwiftCode);

        UUID resultId = swiftCodeService.createSwiftCode(request);

        assertEquals(newId, resultId);

        verify(swiftCodeRepository).save(argThat(swiftCode ->
                !swiftCode.getIsHeadquarter() && swiftCode.getSwiftCode().equals(code)
        ));
    }

    @Test
    void testGetSwiftCode_FoundInCache() {
        when(redisCacheService.get(BRANCH_SWIFT)).thenReturn(Optional.of(branch));

        SwiftCode result = swiftCodeService.getSwiftCode(BRANCH_SWIFT);

        assertEquals(branch, result);
        verify(redisCacheService).get(BRANCH_SWIFT);
        verify(swiftCodeRepository, never()).findBySwiftCode(any());
    }

    @Test
    void testGetSwiftCode_FallbackToDB() {
        when(redisCacheService.get(BRANCH_SWIFT)).thenReturn(Optional.empty());
        when(swiftCodeRepository.findBySwiftCode(BRANCH_SWIFT)).thenReturn(Optional.of(branch));

        SwiftCode result = swiftCodeService.getSwiftCode(BRANCH_SWIFT);

        assertEquals(branch, result);
        verify(redisCacheService).get(BRANCH_SWIFT);
        verify(swiftCodeRepository).findBySwiftCode(BRANCH_SWIFT);
    }

    @Test
    void testGetSwiftCode_NotFoundAnywhere() {
        String unknown = "UNKNOWN123";
        when(redisCacheService.get(unknown)).thenReturn(Optional.empty());
        when(swiftCodeRepository.findBySwiftCode(unknown)).thenReturn(Optional.empty());

        SwiftCodeException exception = assertThrows(SwiftCodeException.class,
                () -> swiftCodeService.getSwiftCode(unknown));

        assertTrue(exception.getMessage().contains("Swift code not found"));
        verify(redisCacheService).get(unknown);
        verify(swiftCodeRepository).findBySwiftCode(unknown);
    }

    @Test
    void testGetSwiftCodes_FromCache() {
        Set<String> codes = Set.of(HEADQUARTER_SWIFT, BRANCH_SWIFT);

        when(redisCacheService.getSet("US")).thenReturn(codes);
        when(redisCacheService.get(HEADQUARTER_SWIFT)).thenReturn(Optional.of(headquarter));
        when(redisCacheService.get(BRANCH_SWIFT)).thenReturn(Optional.of(branch));

        List<SwiftCode> result = swiftCodeService.getSwiftCodes("US");

        assertEquals(2, result.size());
        assertTrue(result.contains(headquarter));
        assertTrue(result.contains(branch));
        verify(swiftCodeRepository, never()).findByCountryISO2(anyString());
    }

    @Test
    void testGetSwiftCodes_FromDB_WhenCacheEmpty() {
        headquarter.setSwiftCode(HEADQUARTER_SWIFT);
        branch.setSwiftCode(BRANCH_SWIFT);

        when(redisCacheService.getSet("US")).thenReturn(Collections.emptySet());
        when(swiftCodeRepository.findByCountryISO2("US")).thenReturn(List.of(headquarter, branch));

        List<SwiftCode> result = swiftCodeService.getSwiftCodes("US");

        assertEquals(2, result.size());
        assertTrue(result.contains(headquarter));
        assertTrue(result.contains(branch));

        verify(swiftCodeRepository).findByCountryISO2("US");
        verify(redisCacheService).save(HEADQUARTER_SWIFT, headquarter);
        verify(redisCacheService).save(BRANCH_SWIFT, branch);
        verify(redisCacheService).addToSet("US", HEADQUARTER_SWIFT);
        verify(redisCacheService).addToSet("US", BRANCH_SWIFT);
    }

    @Test
    void testRemoveAllSwiftCodes() {
        swiftCodeService.removeAllSwiftCodes();
        verify(swiftCodeRepository).deleteAll();
    }
    @Test
    void testRemoveSwiftCode_WithHeadquarter() {
        String branchCode = branch.getSwiftCode();
        String hqCode = headquarter.getSwiftCode();
        String countryCode = branch.getCountryISO2();

        when(swiftCodeRepository.findBySwiftCode(branchCode)).thenReturn(Optional.of(branch));

        UUID resultId = swiftCodeService.removeSwiftCode(branchCode);

        assertEquals(branchId, resultId);
        verify(swiftCodeRepository).delete(branch);
        verify(redisCacheService).delete(branchCode);
        verify(redisCacheService).save(hqCode, headquarter);
        verify(redisCacheService).deleteFromSet(countryCode, branchCode);
    }

    @Test
    void testRemoveSwiftCode_NoHeadquarter() {
        SwiftCode orphan = SwiftCode.builder()
                .id(UUID.randomUUID())
                .swiftCode("ORPHAN123")
                .countryISO2("PL")
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.findBySwiftCode("ORPHAN123")).thenReturn(Optional.of(orphan));

        UUID resultId = swiftCodeService.removeSwiftCode("ORPHAN123");

        assertEquals(orphan.getId(), resultId);
        verify(swiftCodeRepository).delete(orphan);
        verify(redisCacheService).delete("ORPHAN123");
        verify(redisCacheService).deleteFromSet("PL", "ORPHAN123");
        verify(redisCacheService, never()).save(any(), any());
    }

    @Test
    void testRemoveSwiftCode_NotFound() {
        when(swiftCodeRepository.findBySwiftCode("MISSING")).thenReturn(Optional.empty());

        SwiftCodeException exception = assertThrows(SwiftCodeException.class,
                () -> swiftCodeService.removeSwiftCode("MISSING"));

        assertTrue(exception.getMessage().contains("Swift code not found"));
        verify(swiftCodeRepository).findBySwiftCode("MISSING");
        verifyNoMoreInteractions(swiftCodeRepository);
        verifyNoInteractions(redisCacheService);
    }

    @Test
    void testTransferSwiftCodes_UnsupportedExtension() {
        String filePath = "test_swiftcodes.txt";

        ParsingException exception = assertThrows(
                ParsingException.class,
                () -> swiftCodeService.transferSwiftCodes(filePath)
        );

        assertEquals("Unsupported extension: txt", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }
}