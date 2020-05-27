package organizestream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//Functions to simulate the data stream
public class DataSet {

    private ArrayList<Instance> completeDataSet = new ArrayList<Instance>();
    private ArrayList<Instance> newDataSet = new ArrayList<Instance>();
    private ArrayList<String> differentClasses = new ArrayList<String>();
    private HashMap<String, Classe> examplesByClasses = new HashMap<String, Classe>();

    private String initialPath;

    // load dataset and create array instances
    public boolean loadDataSet(String dataSetFile) {

        initialPath = dataSetFile;

        int countInstance = 0;

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(dataSetFile));

            String line;

            while ((line = in.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                System.out.println("Loading instance " + (++countInstance));

                Instance instance = new Instance(line);

                if (!differentClasses.contains(instance.getClasse())) {
                    differentClasses.add(instance.getClasse());

                    Classe classe = new Classe(instance.getClasse());
                    classe.addInstance(instance);
                    examplesByClasses.put(instance.getClasse(), classe);
                } else {
                    Classe classe = examplesByClasses.get(instance.getClasse());
                    classe.addInstance(instance);
                    examplesByClasses.replace(instance.getClasse(), classe);
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

    // organize dataset
    public String generateNewDataSet(double percentageClasses, double percentageExamples, boolean centroid, boolean randomNewClasses) {
        int initialBatch = 0;
        if (centroid) {
            for (Map.Entry<String, Classe> entry : examplesByClasses.entrySet()) {
                entry.getValue().organizeByCentroid();
            }
        }

        HashMap<String, Integer> examplesClassesCurrent = new HashMap<String, Integer>();

        for (Map.Entry<String, Classe> entry : examplesByClasses.entrySet()) {
            examplesClassesCurrent.put(entry.getKey(), 0);
        }

        int totalClasses = differentClasses.size();

        double tempTotal = totalClasses * ((float) percentageClasses / 100.0);

        int totalInitialClasses = (int) tempTotal;

        if (totalInitialClasses <= 0) {
            totalInitialClasses = 1;
        }

        if (totalInitialClasses > totalClasses) {
            totalInitialClasses = totalClasses;
        }

        ArrayList<String> initialClasses = new ArrayList<String>();

        Random random = new Random();
        random.setSeed(1);

        ArrayList<String> tempClasses = new ArrayList<String>();
        ArrayList<String> ordemClasses = new ArrayList<String>();

        for (String classe : differentClasses) {
            tempClasses.add(classe);
        }

        while (!tempClasses.isEmpty()) {
            ordemClasses.add(tempClasses.remove(random.nextInt(tempClasses.size())));
        }

        //initial batch
        for (int i = 0; i < totalInitialClasses; i++) {
            Classe classe = examplesByClasses.get(ordemClasses.get(i));

            int totalInstancesInsered = classe.getOriginalInstances().size();
            int totalInstances = totalInstancesInsered;
            totalInstances = (int) (totalInstances * (percentageExamples / 100.0));

            if (totalInstances <= 0) {
                totalInstances = 1;
            }

            if (totalInstances > totalInstancesInsered) {
                totalInstances = totalInstancesInsered;
            }

            initialBatch += totalInstances;

            for (int j = 0; j < totalInstances; j++) {

                newDataSet.add(classe.getInstance());
                classe.removeInstance();
            }

        }

        //organize stream
        
        //Random  approach  without restrictions
        if (randomNewClasses) {

            while (newDataSet.size() < completeDataSet.size()) {
                String randomClasse = differentClasses.get(random.nextInt(differentClasses.size()));

                Classe classe = examplesByClasses.get(randomClasse);

                if (classe.getSize() == 0) {
                    differentClasses.remove(randomClasse);
                    continue;
                }

                newDataSet.add(classe.getInstance());
                classe.removeInstance();
            }
        } 
        
        //Random  approach  with  fixed  window
        else {

            int restInstances = completeDataSet.size() - initialBatch;
            int restClasses = differentClasses.size() - totalInitialClasses;

            float tempFrequency = (float) restInstances / (float) restClasses;

            int frequencyNewClasses = (int) tempFrequency;

            int controlClasses = totalInitialClasses + 1;

            int count = 0;

            while (newDataSet.size() < completeDataSet.size()) {
                if (controlClasses >= ordemClasses.size()) {
                    controlClasses = ordemClasses.size();
                }

                String randomClasse = ordemClasses.get(random.nextInt(controlClasses));

                Classe classe = examplesByClasses.get(randomClasse);

                if (classe.getSize() == 0) {
                    ordemClasses.remove(randomClasse);
                    controlClasses--;
                    continue;
                }

                newDataSet.add(classe.getInstance());
                classe.removeInstance();
                count++;
                if (count >= frequencyNewClasses) {
                    controlClasses++;
                    count = 0;
                }
            }
        }

        return saveNewDataSet(initialPath, totalInitialClasses, initialBatch);
    }

    
    // save the organized dataset
    public String saveNewDataSet(String path, int classes, int examples) {
        BufferedWriter out = null;
        path = path.substring(0, path.lastIndexOf("\\"));
        path += "\\newDataSet" + classes + "classes" + examples + "examples.data";
        try {
            out = new BufferedWriter(new FileWriter(path));

            for (Instance instance : newDataSet) {
                for (Double value : instance.getAttributes()) {
                    out.write(value + ",");
                }
                out.write(instance.getClasse() + "\n");
            }

            out.close();

        } catch (Exception e) {
            return null;
        }

        return path;
    }

}
