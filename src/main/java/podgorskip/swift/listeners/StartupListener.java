package podgorskip.swift.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import podgorskip.swift.services.SwiftCodeService;
import java.io.File;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class StartupListener {
    private final SwiftCodeService swiftCodeService;
    private final ResourceLoader resourceLoader;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            transferSwiftCodes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void transferSwiftCodes() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:spreadsheets/swift_codes.xlsx");
        File file = resource.getFile();

        swiftCodeService.transferSwiftCodes(file.getAbsolutePath());
    }
}