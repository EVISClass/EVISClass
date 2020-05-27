package organizestream;

import classification.Utils;
import java.util.ArrayList;

//Class representation with data instances
public class Classe {

    private String id;
    private ArrayList<Instance> originalInstances = new ArrayList<Instance>();
    private ArrayList<Instance> organizedInstances = new ArrayList<Instance>();
    private Instance centroid;

    private boolean usedCentroid = false;

    public Classe(String id) {
        this.id = id;
    }

    public ArrayList<Instance> getOriginalInstances() {
        return originalInstances;
    }

    public ArrayList<Instance> getOrganizedInstances() {
        return organizedInstances;
    }

    public ArrayList<Instance> getInstances() {
        return usedCentroid ? organizedInstances : originalInstances;
    }

    public Instance getCentroid() {
        return centroid;
    }

    public void addInstance(Instance instance) {
        originalInstances.add(instance);
    }

    //calculates the centroid of the class
    public void calculateCentroid() {
        ArrayList<Double> attributes = new ArrayList<Double>();

        for (Instance instance : originalInstances) {
            for (int i = 0; i < instance.getAttributes().size(); i++) {
                if (attributes.size() <= i) {
                    attributes.add(instance.getAttributes().get(i));
                } else {
                    attributes.set(i, instance.getAttributes().get(i) + attributes.get(i));
                }
            }
        }

        for (int i = 0; i < attributes.size(); i++) {
            attributes.set(i, attributes.get(i) / originalInstances.size());
        }

        centroid = new Instance(attributes, id);

    }

    //calculate the distance of the instances in relation to the centroid class
    public void organizeByCentroid() {
        usedCentroid = true;
        calculateCentroid();

        double distance = 0;

        for (Instance instance : originalInstances) {
            distance = Utils.distance(instance, centroid);
            instance.setDistance(distance);

            organizedInstances.add(instance);
        }

        for (int i = 0; i < organizedInstances.size(); i++) {
            for (int j = i + 1; j < organizedInstances.size(); j++) {
                if (organizedInstances.get(i).getDistance() > organizedInstances.get(j).getDistance()) {
                    Instance temp = organizedInstances.get(i);
                    organizedInstances.set(i, organizedInstances.get(j));
                    organizedInstances.set(j, temp);
                }

            }
        }

    }

    public void removeInstance() {
        if (usedCentroid && !organizedInstances.isEmpty()) {
            organizedInstances.remove(0);
        }

        if (!usedCentroid && !originalInstances.isEmpty()) {
            originalInstances.remove(0);
        }
    }

    public Instance getInstance() {
        if (usedCentroid && !organizedInstances.isEmpty()) {
            return organizedInstances.get(0);
        }

        if (!usedCentroid && !originalInstances.isEmpty()) {
            return originalInstances.get(0);
        }

        return null;
    }

    public int getSize() {
        return usedCentroid ? organizedInstances.size() : originalInstances.size();
    }

}
