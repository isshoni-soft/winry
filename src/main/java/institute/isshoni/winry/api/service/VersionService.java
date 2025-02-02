package institute.isshoni.winry.api.service;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.araragi.util.FileUtil;
import institute.isshoni.winry.api.annotation.Injected;
import institute.isshoni.winry.api.annotation.parameter.Context;
import institute.isshoni.winry.api.context.IWinryContext;

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

        loadVersionFor("winry");

        this.logger.info("Detected Winry version: " + getWinryVersion());

        loadVersionFor(this.context.getFileName());
    }

    public String loadVersionFor(String ctxName) {
        Properties properties = new Properties();
        ctxName = ctxName.toLowerCase();
        try {
            properties.load(FileUtil.getResource(ctxName + "_version.properties"));
        } catch (IOException | NullPointerException e) {
            this.logger.warn("Could not find version file for context: " + ctxName + "!");
            return null;
        }

        String version = properties.getProperty("version");
        this.logger.debug("Detected " + ctxName + " version: " + version);

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
