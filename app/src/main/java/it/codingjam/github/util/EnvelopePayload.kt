package it.codingjam.github.util

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME

/**
 * An annotation for identifying the payload that we want to extract from an API response wrapped in
 * an envelope object.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RUNTIME)
annotation class EnvelopePayload(val value: String = "")