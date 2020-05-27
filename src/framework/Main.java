package framework;

import classification.NCM;
import classification.NCMInterleaved;
import java.io.File;
import organizestream.DataSet;

public class Main {

    public static void main(String[] args) {

        String path = null;

        try {
            if (System.getProperty("os.name").equals("Linux")) {
                path = new File(".").getCanonicalPath() + "/dataset/";
            } else {
                path = new File(".").getCanonicalPath() + "\\dataset\\";
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        //################################################################
        //Executing NCM with batches approach
        //number classes initial batch, number classes incremental batch, training rate, runs
        NCM ncm = new NCM(2,2,0.7,10);
        /*NCM ncm = new NCM(4,2,0.7,10);
        NCM ncm = new NCM(6,2,0.7,10);
        NCM ncm = new NCM(8,2,0.7,10);
        NCM ncm = new NCM(10,2,0.7,10);
        NCM ncm = new NCM(12,2,0.7,10);
        NCM ncm = new NCM(14,2,0.7,10);
        NCM ncm = new NCM(16,2,0.7,10);
        NCM ncm = new NCM(18,2,0.7,10);
        NCM ncm = new NCM(20,0,0.7,10);*/
        ncm.execute(path + "CIFAR20classes10000instances.data");
        //################################################################
        
        
        //****************************************************************
        //Executing NCM in EVISClass Framework
        
        //Data stream simulator
        DataSet dataset = new DataSet();
        dataset.loadDataSet(path + "CIFAR20classes10000instances.data");
        //percentage classes initial batch, percentage instances initial batch, instances organized by centroid distance, True: Random  approach  without restrictions | False: Random approach with  fixed window
        String newFilePath = dataset.generateNewDataSet(20, 25, true, true);

        //Evaluator  of  image  data  stream 
        //instances initial batch, latency, label method, runs
        NCMInterleaved ncmInterleaved = new NCMInterleaved(500, 0, 1, 10);
        ncmInterleaved.execute(newFilePath);
        //****************************************************************
    }

}
