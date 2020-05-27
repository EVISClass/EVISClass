package organizestream;

import java.util.ArrayList;

//Data instance representation
public class Instance {

    private ArrayList<Double> attributes = new ArrayList<Double>();
    private String classe;
    private double distance = 0;

    public Instance(ArrayList<Double> attributes, String classe) {
        this.classe = classe;
        this.attributes = attributes;
    }

    public Instance(String line) {

        String[] temp = line.split(",");

        for (int i = 0; i < temp.length - 1; i++) {
            attributes.add(Double.parseDouble(temp[i]));
        }

        classe = temp[temp.length - 1];
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public ArrayList<Double> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Double> attributes) {
        this.attributes = attributes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
