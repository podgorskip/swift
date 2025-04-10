package podgorskip.swift.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import podgorskip.swift.model.entities.SwiftCode;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, UUID> {
    Optional<SwiftCode> findBySwiftCode(String swiftCode);
}
