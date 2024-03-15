package diningphilosophers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {
    private final Lock verrou = new ReentrantLock();
    private final Condition condVerrou = verrou.newCondition();
    private static int stickCount = 0;
    private boolean iAmFree = true;
    private final int myNumber;

    public ChopStick() {
        myNumber = ++stickCount;
    }

    /*synchronized public boolean tryTake(int delay) throws InterruptedException {
        if (!iAmFree) {
            wait(delay);
            if (!iAmFree) // Toujours pas libre, on abandonne
            {
                return false; // Echec
            }
        }
        iAmFree = false;
        // Pas utile de faire notifyAll ici, personne n'attend qu'elle soit occupée
        return true; // Succès
    }*/

    public boolean tryTake(int timeout) throws InterruptedException {
        verrou.lock();
        try {
            while (!iAmFree) {
                condVerrou.await();
                return false; // Echec
            }
            iAmFree = false;

            return true;

        } finally {
            verrou.unlock();
        }
    }

    public void release() {
        verrou.lock();
        try {
            iAmFree = true;
            condVerrou.signalAll();
            System.out.println("Stick " + myNumber + " Released");
        } finally {
            verrou.unlock();
        }
    }

    @Override
    public String toString() {
        return "Stick#" + this.myNumber;
    }
}
