package podgorskip.swift.listeners;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import podgorskip.swift.services.SwiftCodeService;

@Component
@RequiredArgsConstructor
public class ShutdownListener {
    private final SwiftCodeService swiftCodeService;

    @PreDestroy
    public void onShutdown() {
        swiftCodeService.removeAllSwiftCodes();
    }
}
