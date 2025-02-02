package model;

import institute.isshoni.araragi.logging.AraragiLogger;
import institute.isshoni.winry.api.annotation.logging.Logger;

public class InjectedObject {

    @Logger("InjectedObject") private AraragiLogger logger;

    public AraragiLogger getLogger() {
        return this.logger;
    }
}
