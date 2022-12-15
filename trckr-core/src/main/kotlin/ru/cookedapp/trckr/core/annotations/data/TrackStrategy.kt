package ru.cookedapp.trckr.core.annotations.data

/**
 * Enum class to set tracking strategy for specific event parameter.
 *
 * By default, strategy is set to `TrackStrategy.DEFAULT`, so you don't need to set strategy
 * explicitly for each parameter.
 */
enum class TrackStrategy {

    /**
     * Track strategy set in parameter by default.
     *
     * This mode tells **trckr** that during conversion phase it should check registered
     * converters and try to convert parameter value. If no converter were found —
     * [TrckrConversionException][ru.cookedapp.trckr.core.exceptions.TrckrConversionException] exception
     * will be thrown.
     */
    DEFAULT,

    /**
     * This strategy has effect for nullable parameters. It skips whole parameter tracking when
     * value of it is `null`. For not-null parameters behavior same as [DEFAULT].
     */
    SKIP_IF_NULL,

    /**
     * This strategy has effect for nullable parameters. By default, you can't track `null` value
     * itself. It would be converted to "null" by [PrimitivesConverter][ru.cookedapp.trckr.core.converter.PrimitivesConverter].
     * Using this strategy `null` will be explicitly added to parameters map.
     * For not-null parameters behavior same as [DEFAULT].
     */
    TRACK_NULL,
}
