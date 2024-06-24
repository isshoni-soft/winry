package model;

import tv.isshoni.araragi.logging.AraragiLogger;
import tv.isshoni.winry.api.annotation.logging.Logger;

public class InjectedObject {

    @Logger("InjectedObject") private AraragiLogger logger;

    public AraragiLogger getLogger() {
        return this.logger;
    }
}
