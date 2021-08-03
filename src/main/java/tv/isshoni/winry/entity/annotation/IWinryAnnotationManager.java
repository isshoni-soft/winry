package tv.isshoni.winry.entity.annotation;

import tv.isshoni.araragi.annotation.model.IAnnotationManager;
import tv.isshoni.winry.annotation.Bootstrap;

public interface IWinryAnnotationManager extends IAnnotationManager {

    void initialize(Bootstrap bootstrap);
}
