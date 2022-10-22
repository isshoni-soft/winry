package tv.isshoni.winry.api.service;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.araragi.util.FileUtil;
import tv.isshoni.winry.api.annotation.Injected;
import tv.isshoni.winry.api.annotation.parameter.Context;
import tv.isshoni.winry.api.entity.context.IWinryContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Injected
public class VersionService {

    private final AraragiLogger logger;
    private final IWinryContext context;

    private final Map<String, String> versions;

    public VersionService(@Context IWinryContext context) {
        this.versions = new HashMap<>();
        this.context = context;
        this.logger = context.getLoggerFactory().createLogger("VersionService");

        Properties properties = new Properties();
        try {
            properties.load(FileUtil.getResource("winry_version.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.versions.put("winry", properties.getProperty("version"));
        this.logger.info("Detected Winry version: " + getWinryVersion());

        loadVersionFor(this.context.getFileName());
    }

    public String loadVersionFor(String ctxName) {
        Properties properties = new Properties();
        ctxName = ctxName.toLowerCase();
        try {
            properties.load(VersionService.class.getResourceAsStream("/" + ctxName + "_version.properties"));
        } catch (IOException | NullPointerException e) {
            this.logger.warn("Could not find version file for context: " + ctxName + "!");
            return null;
        }

        String version = properties.getProperty("version");
        this.logger.info("Detected " + ctxName + " version: " + version);

        return (this.versions.putIfAbsent(ctxName, version) == null ? version : null);
    }

    public Optional<String> getVersion(String ctxName) {
        String version = this.versions.get(ctxName.toLowerCase());

        if (version == null) {
            version = loadVersionFor(ctxName);
        }

        return Optional.ofNullable(version);
    }

    public Optional<String> getVersion() {
        return getVersion(this.context.getFileName());
    }

    public String getWinryVersion() {
        return getVersion("winry").get();
    }
}
