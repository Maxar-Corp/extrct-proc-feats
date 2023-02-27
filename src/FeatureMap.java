import java.util.List;
import java.util.Map;

public interface FeatureMap {

    public Feature getFeature(int id) throws Exception;

    public Feature findFeature(String featureName) throws Exception;

    public List<Feature> getGroupFeatures(FeatureGroups group);

    public void lock();

    public int length();

    public String printFeatureList();

    public Map<FeatureGroups, List<Feature>> getMapping();

    public void add(String[] csvLine, List<String> keys) throws IllegalAccessError;
}