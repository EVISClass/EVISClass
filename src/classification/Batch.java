package classification;

import java.util.ArrayList;
import java.util.Collections;
import organizestream.Classe;
import organizestream.Instance;


//Batch representation of the NCM algorithm
public class Batch {

    private ArrayList<Instance> training = new ArrayList<Instance>();
    private ArrayList<Instance> test = new ArrayList<Instance>();

    public ArrayList<Instance> getTest() {
        return test;
    }

    public ArrayList<Instance> getTraining() {
        return training;
    }

    public void addClasse(Classe classe, double splitTrainingTest) {
        int limit = (int) (classe.getInstances().size() * splitTrainingTest);

        ArrayList<Instance> instances = classe.getInstances();
        Collections.shuffle(instances);

        for (int i = 0; i < instances.size(); i++) {
            if (i <= limit) {
                training.add(instances.get(i));
            } else {
                test.add(instances.get(i));
            }

        }

    }

}
