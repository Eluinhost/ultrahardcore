package uk.co.eluinhost.ultrahardcore.features;

public interface IUHCFeature {

    /**
    * Attempt to enable the feature
    * @return bool true if the feature was enabled, false if already enabled or event cancelled
    */
    boolean enableFeature();

    /**
     * Attempt to disable the feature
     * @return bool true if the feature was disabled, false if already disabled or event cancelled
     */
    boolean disableFeature();

    /**
     * @return name of the feature
     */
    String getFeatureID();

    /**
     * Is the feature enabled?
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Get the description of the feature
     * @return String
     */
    String getDescription();
}
