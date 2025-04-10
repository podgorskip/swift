package podgorskip.swift.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity(name = "swift_code")
@AllArgsConstructor
@NoArgsConstructor
public class SwiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "country_ISO2", nullable = false)
    private String countryISO2;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "is_headquarter", nullable = false)
    @Builder.Default
    private Boolean isHeadquarter = false;

    @Column(name = "swift_code", nullable = false, unique = true)
    private String swiftCode;

    @OneToMany
    @JoinColumn(name = "superior_unit")
    private List<SwiftCode> branches;
}
