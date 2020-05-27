package classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import organizestream.Classe;
import organizestream.Instance;

//Functions on the used dataset 
public class DataSetNCM {

    private ArrayList<Instance> completeDataSet = new ArrayList<Instance>();
    private ArrayList<Instance> newDataSet = new ArrayList<Instance>();
    private ArrayList<String> differentClasses = new ArrayList<String>();
    private HashMap<String, Classe> examplesByClasses = new HashMap<String, Classe>();

    // load dataset and create array instances
    public boolean loadDataSet(String dataSetFile) {

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

    public HashMap<String, Classe> getExamplesByClasses() {
        return examplesByClasses;
    }

    public ArrayList<String> getDifferentClasses() {
        return differentClasses;
    }

}
