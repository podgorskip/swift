package podgorskip.swift.listeners;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import podgorskip.swift.services.RedisCacheService;
import podgorskip.swift.services.SwiftCodeService;

@Component
@RequiredArgsConstructor
public class ShutdownListener {
    private final SwiftCodeService swiftCodeService;
    private final RedisCacheService redisCacheService;

    @PreDestroy
    public void onShutdown() {
        swiftCodeService.removeAllSwiftCodes();
        redisCacheService.clearCaches();
    }
}
