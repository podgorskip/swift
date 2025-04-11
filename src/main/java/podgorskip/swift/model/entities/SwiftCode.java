package podgorskip.swift.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
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

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "superiorUnit", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference
    private List<SwiftCode> branches = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "superior_unit")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private SwiftCode superiorUnit;
}
