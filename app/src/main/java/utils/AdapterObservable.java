package utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class AdapterObservable extends Observable {
    private static final String TAG = "ADAPTEROBSERVABLE";
    private static AdapterObservable instance = new AdapterObservable();
    private List<Observer> observerList;

    private AdapterObservable(){
        observerList = new ArrayList<>();
    }

    public synchronized static AdapterObservable getInstance() { return instance; }

    public synchronized void updateValue() {
            setChanged();
            notifyObservers();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        observerList.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        observerList.remove(o);
    }

    @Override
    public synchronized int countObservers() {
        return observerList.size();
    }

    @Override
    public synchronized void deleteObservers() {
        observerList.clear();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observerList) {
            observer.update(this, observer);
        }
        clearChanged();
    }

    public String listObservers() {
        StringBuilder str = new StringBuilder();
        for (Observer ob : observerList) {
            str.append(ob.toString()).append(" ");
        }
        return str.toString();
    }

    public int getValue() {
        return 100;
    }
}
