package it.codingjam.github.util


/**
 * An annotation for identifying the payload that we want to extract from an API response wrapped in
 * an envelope object.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnvelopePayload(val value: String = "")