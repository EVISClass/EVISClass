package classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import organizestream.Instance;

//NCM algorithm in the Test/Train approach
public class NCMInterleaved {

    private ArrayList<String> differentClasses = new ArrayList<String>();
    private ArrayList<Instance> completeDataSet = new ArrayList<Instance>();
    private HashMap<String, ClasseNCM> trainingModel = new HashMap<String, ClasseNCM>();

    private int instancesInitialBatch;
    private int latency;

    private double maxDistance = Double.MIN_VALUE;
    private double instanceDistance = Double.MIN_VALUE;

    /*
    1 - All
    2 - Aleatory
    3 - Confidence Labels Strategy
    4 - Uncertainty Strategy With Randomization
     */
    private int labelMethod;

    private Random random = new Random();
    private int runs;

    public NCMInterleaved(int instancesInitialBatch, int latency, int labelMethod, int runs) {
        this.instancesInitialBatch = instancesInitialBatch;
        this.latency = latency;
        this.labelMethod = labelMethod;
        this.runs = runs;
    }

    //check nearest centroid class
    public String checkClass(Instance instance) {

        double distance = Double.MAX_VALUE;
        String classe = "";
        for (Map.Entry<String, ClasseNCM> entry : trainingModel.entrySet()) {
            Instance centroid = entry.getValue().getCentroid();

            double currentDistance = Utils.distance(centroid, instance);
            if (currentDistance < distance) {
                distance = currentDistance;
                classe = entry.getKey();
                instanceDistance = distance;
            }

        }

        return classe;
    }

    //execute NCM
    public void execute(String path) {

        if (!loadDataSet(path)) {
            System.out.println("error load dataset");
            return;
        }
        ArrayList<Float> arrayAccuracys = new ArrayList<Float>();
        ArrayList<Integer> arrayLabels = new ArrayList<Integer>();
        double analysed = 0;
        int rights = 0;
        int labels = 0;

        for (int i = 0; i < runs; i++) {

            int tempAnalysed = 0;
            int tempRight = 0;
            int tempLabel = instancesInitialBatch;

            labels += instancesInitialBatch;
            maxDistance = Double.MIN_VALUE;

            System.out.println("Run " + (i + 1) + " of " + runs);

            ArrayList<Instance> buffer = new ArrayList<Instance>();

            trainingModel = new HashMap<String, ClasseNCM>();

            for (int j = 0; j < completeDataSet.size(); j++) {

                //training with initial batch
                if (j < instancesInitialBatch) {

                    ClasseNCM classe = trainingModel.get(completeDataSet.get(j).getClasse());

                    if (classe == null) {

                        classe = new ClasseNCM(completeDataSet.get(j).getClasse());
                        classe.addInstance(completeDataSet.get(j));
                        trainingModel.put(completeDataSet.get(j).getClasse(), classe);
                    } else {

                        classe.addInstance(completeDataSet.get(j));
                        trainingModel.replace(completeDataSet.get(j).getClasse(), classe);
                    }
                } else {
                    //finished the initial training
                    if (j == instancesInitialBatch) {
                        for (Map.Entry<String, ClasseNCM> entry : trainingModel.entrySet()) {
                            entry.getValue().calculateCentroidDistance();

                            double distance = entry.getValue().getMaxDistance();

                            if (distance > maxDistance) {
                                maxDistance = distance;
                            }
                        }
                    }

                    //start streaming
                    
                    tempAnalysed++;
                    analysed++;

                    String classe = checkClass(completeDataSet.get(j));

                    if (classe.equals(completeDataSet.get(j).getClasse())) {
                        rights++;
                        tempRight++;
                    }

                     // All labels
                    if (labelMethod == 1) {
                        buffer.add(completeDataSet.get(j));
                    //Aleatory labels
                    } else if (labelMethod == 2) {
                        if (random.nextInt(2) == 1) {
                            buffer.add(completeDataSet.get(j));
                        }
                    //Confidence Labels Strategy
                    } else if (labelMethod == 3) {
                        if (instanceDistance > maxDistance) {
                            buffer.add(completeDataSet.get(j));
                        }
                    //Uncertainty Strategy With Randomization
                    } else if (labelMethod == 4) {
                        if (instanceDistance > maxDistance) {
                            buffer.add(completeDataSet.get(j));

                            maxDistance = maxDistance / (1 + random.nextDouble() * (2 - 1));
                        }

                    }

                    //checks if latency is done
                    if (buffer.size() >= latency && !buffer.isEmpty()) {

                        ClasseNCM classeNCM = trainingModel.get(buffer.get(0).getClasse());

                        //incremental NCM training by instances in the buffer
                        if (classeNCM == null) {
                            classeNCM = new ClasseNCM(buffer.get(0).getClasse());
                            trainingModel.put(buffer.get(0).getClasse(), classeNCM);

                            classeNCM.incrementalTrainingInstance(buffer.get(0));

                        }

                        double tempDistance = trainingModel.get(buffer.get(0).getClasse()).incrementalTrainingInstance(buffer.get(0));

                        if (tempDistance > maxDistance) {
                            maxDistance = tempDistance;
                        }

                        buffer.remove(0);
                        labels++;
                        tempLabel++;

                    }

                }

            }

            arrayAccuracys.add((float) tempRight / (float) tempAnalysed);
            arrayLabels.add(tempLabel);
        }

        double std = 0;
        double avg = 0;

        for (Float f : arrayAccuracys) {
            avg += f;
        }

        avg = avg / arrayAccuracys.size();

        System.out.println("AVG Accuracy: " + avg);

        for (Float f : arrayAccuracys) {
            std += Math.pow(f - avg, 2);
        }

        std = std / arrayAccuracys.size();
        System.out.println("STD accuracy: " + Math.sqrt(std));

        std = 0;
        avg = 0;

        for (Integer i : arrayLabels) {
            avg += i;
        }

        avg = avg / arrayLabels.size();

        System.out.println("AVG labels: " + avg);

        for (Integer i : arrayLabels) {
            std += Math.pow(i - avg, 2);
        }

        std = std / arrayLabels.size();
        System.out.println("STD label: " + Math.sqrt(std));

    }

    // load dataset and create instances
    public boolean loadDataSet(String dataSetFile) {

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(dataSetFile));

            String line;

            int countInstance = 0;

            while ((line = in.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                System.out.println("Loading instance " + (++countInstance));

                Instance instance = new Instance(line);

                if (!differentClasses.contains(instance.getClasse())) {
                    differentClasses.add(instance.getClasse());
                }

                completeDataSet.add(instance);
            }

            in.close();

        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;

    }

}
