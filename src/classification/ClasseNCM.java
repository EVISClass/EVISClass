package classification;

import java.util.ArrayList;
import organizestream.Instance;

//Class representation of the NCM algorithm
public class ClasseNCM {

    private String id;
    private ArrayList<Instance> instances = new ArrayList<Instance>();
    private Instance centroid;
    private double maxDistance = Double.MIN_VALUE;

    public ClasseNCM(String id) {
        this.id = id;
    }

    //calculates the centroid of the class
    private void calculateCentroid() {

        ArrayList<Double> attributes = new ArrayList<Double>();

        if (centroid == null) {

            for (Double d : instances.get(0).getAttributes()) {
                attributes.add(d);
            }

            centroid = new Instance(attributes, id);
            return;
        }

        for (Double d : centroid.getAttributes()) {
            attributes.add(d);
        }

        Instance tempInstance = instances.get(instances.size() - 1);

        for (int i = 0; i < attributes.size(); i++) {
            double temp = attributes.get(i) + ((tempInstance.getAttributes().get(i) - attributes.get(i)) / instances.size());
            attributes.set(i, temp);
        }

        centroid = new Instance(attributes, id);

    }

    //incremental training of the NCM algorithm
    public double incrementalTrainingInstance(Instance instance) {
        addInstance(instance);
        calculateCentroid();

        double distance = Utils.distance(instance, centroid);
        instance.setDistance(distance);

        if (distance > maxDistance || maxDistance == Double.MIN_VALUE) {
            maxDistance = distance;
        }

        return maxDistance;
    }

    //calculate the distance of the instances in relation to the centroid class
    public void calculateCentroidDistance() {
        calculateCentroid();

        double distance = 0;

        for (Instance instance : instances) {
            distance = Utils.distance(instance, centroid);
            instance.setDistance(distance);

            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public Instance getCentroid() {
        return centroid;
    }

    public void addInstance(Instance instance) {
        instances.add(instance);
    }

}
