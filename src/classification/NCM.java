package classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import organizestream.Classe;
import organizestream.Instance;

//NCM algorithm in the batch approach
public class NCM {

    private ArrayList<Batch> batches = new ArrayList<Batch>();
    private HashMap<String, Classe> instancesByClasses = new HashMap<String, Classe>();

    private Random random = new Random();

    private int classesInitialBatch;
    private int classesIncrementalBatch;
    private double splitTrainingTest;
    private int runs;
    private DataSetNCM dataSet;

    public NCM(int classesInitialBatch, int classesIncrementalBatch, double splitTrainingTest, int runs) {
        this.classesInitialBatch = classesInitialBatch;
        this.classesIncrementalBatch = classesIncrementalBatch;
        this.splitTrainingTest = splitTrainingTest;
        this.runs = runs;
        random.setSeed(1);
    }

    //check nearest centroid class
    public String checkClass(Instance instance) {

        double distance = Double.MAX_VALUE;
        String classe = "";
        for (Map.Entry<String, Classe> entry : instancesByClasses.entrySet()) {
            Instance centroid = entry.getValue().getCentroid();

            double currentDistance = Utils.distance(centroid, instance);
            if (currentDistance < distance) {
                distance = currentDistance;
                classe = entry.getKey();
            }

        }

        return classe;
    }

    //execute NCM
    public void execute(String path) {

        ArrayList<Float> accuracys = new ArrayList<Float>();

        double accuracy = 0;
        int times = 0;

        dataSet = new DataSetNCM();
        dataSet.loadDataSet(path);

        for (int i = 0; i < runs; i++) {

            organizeBatches();

            System.out.println("Run " + (i + 1) + " of " + runs + " | Batches: " + batches.size());

            for (Batch batch : batches) {

                for (Instance instance : batch.getTraining()) {
                    Classe classe = instancesByClasses.get(instance.getClasse());
                    if (classe == null) {
                        classe = new Classe(instance.getClasse());
                        classe.addInstance(instance);
                        instancesByClasses.put(instance.getClasse(), classe);

                    } else {

                        classe.addInstance(instance);
                        instancesByClasses.replace(instance.getClasse(), classe);

                    }
                }

                for (Map.Entry<String, Classe> entry : instancesByClasses.entrySet()) {
                    entry.getValue().calculateCentroid();
                }

                int countInstances = 0;
                int rights = 0;
                for (Instance instance : batch.getTest()) {
                    countInstances++;
                    String classe = checkClass(instance);

                    if (instance.getClasse().equals(classe)) {
                        rights++;
                    }
                }
                accuracys.add((float) rights / (float) countInstances);
                accuracy += (float) rights / (float) countInstances;
                times++;

            }
        }

        System.out.println("AVG Accuracy: " + accuracy / times);

        double std = 0;

        for (Float f : accuracys) {
            std += Math.pow(f - (accuracy / times), 2);
        }

        std = std / accuracys.size();
        System.out.println("STD: " + Math.sqrt(std));

    }

    //split instances in batches
    public void organizeBatches() {
        batches = new ArrayList<Batch>();
        instancesByClasses = new HashMap<String, Classe>();

        ArrayList<String> classes = new ArrayList<String>();

        for (String c : dataSet.getDifferentClasses()) {
            classes.add(c);
        }

        ArrayList<String> orderClasses = new ArrayList<String>();

        while (!classes.isEmpty()) {
            orderClasses.add(classes.remove(random.nextInt(classes.size())));
        }

        Batch initialBatch = new Batch();

        for (int i = 0; i < classesInitialBatch; i++) {
            initialBatch.addClasse(dataSet.getExamplesByClasses().get(orderClasses.get(i)), splitTrainingTest);
        }

        batches.add(initialBatch);

        int index = classesInitialBatch;
        int numberBatches = classesIncrementalBatch == 0 ? 0 : (dataSet.getExamplesByClasses().size() - classesInitialBatch) / classesIncrementalBatch;

        for (int i = 0; i < numberBatches; i++) {

            Batch newBatch = new Batch();

            for (int j = 0; j < classesIncrementalBatch; j++) {
                newBatch.addClasse(dataSet.getExamplesByClasses().get(orderClasses.get(index)), splitTrainingTest);
                index++;
            }

            batches.add(newBatch);
        }

    }

}
