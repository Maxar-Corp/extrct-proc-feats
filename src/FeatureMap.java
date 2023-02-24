
import java.util.List;
import java.util.Map;

/**
 * This a mapping of all available feature for the bit map encoding
 *
 * @author mstabile
 *
 */
public interface FeatureMap {

    /**
     * Returns the Feature based upon its bit map id
     *
     * @param  id
     * @return
     * @throws Exception
     */
    public Feature getFeature(int id) throws Exception;

    /**
     * Returns the Feature by searching for is feature name
     *
     * @param  featureName
     * @return
     * @throws Exception
     */
    public Feature findFeature(String featureName) throws Exception;


    /**
     * returns a list of features associated with a group
     *
     * @param  group
     * @return
     */
    public List<Feature> getGroupFeatures(FeatureGroups group);

    /**
     * Locks the class from any changes.
     */
    public void lock();

    /**
     * Count of the number of features
     *
     * @return
     */
    public int length();

    public String printFeatureList();

    public Map<FeatureGroups, List<Feature>> getMapping();

    public void add(String[] csvLine, List<String> keys) throws IllegalAccessError;
}
