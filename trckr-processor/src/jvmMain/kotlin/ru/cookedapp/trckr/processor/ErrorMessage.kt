package ru.cookedapp.trckr.processor

internal object ErrorMessage {

    const val INCORRECT_TRACKER_ANNOTATION_TARGET = "@Tracker annotation can be applied only to interface."
    const val INCORRECT_TRACKER_CLASS_DECLARATION = "Tracker can't contain classes (only companion objects allowed)."

    fun incorrectTrackerInnerDeclaration(trackerName: String) =
        "Tracker \"$trackerName\" can contain only event methods or companion object."

    fun eventMethodMissingAnnotation(methodName: String) =
        "Event method \"$methodName\" should be annotated with @Event annotation."

    fun suspendableEventMethod(methodName: String) =
        "Event method \"$methodName\" should not be suspendable."

    fun incorrectEventMethodReturnType(methodName: String) =
        "Event method \"$methodName\" should return Unit."

    fun incorrectParameterDeclaration(methodName: String, parameterName: String) =
        "Event method \"$methodName\" has parameter \"$parameterName\" without @Param annotation."
}
