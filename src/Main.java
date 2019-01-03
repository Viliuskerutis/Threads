import java.lang.Thread;

public class Main {

    public static void main(String[] args) {
        Counter counter = new Counter();
        Locker locker = new Locker();

        Reader readerOne = new Reader("Pirmas skaitytojas", counter, locker);
        Reader readerTwo = new Reader("Antras skaitytojas", counter, locker);
        Reader readerThree = new Reader("Trečias skaitytojas", counter, locker);

        Writer writerOne = new Writer("Pirmas rašytojas", counter, locker);
        Writer writerTwo = new Writer("Antras rašytojas", counter, locker);
        Writer writerThree = new Writer("Trečias rašytojas", counter, locker);

        Cleaner cleaner = new Cleaner(counter, locker);

        readerOne.start();
        readerTwo.start();
        readerThree.start();

        cleaner.start();

        writerOne.start();
        writerTwo.start();
        writerThree.start();


        try {
            readerOne.join();
            readerTwo.join();
            readerThree.join();

            cleaner.join();

            writerOne.join();
            writerTwo.join();
            writerThree.join();
        } catch (InterruptedException ex){
            System.out.println("Įsiterpta į giją: " + ex);
        }
    }

    public static class Cleaner extends Thread{

        Counter _counter;
        Locker lock;

        Cleaner(Counter counter, Locker lock){
            _counter = counter;
            this.lock = lock;
        }

        @Override
        public void run(){
            for(int i = 0; i < 3; i++) {
                while(lock.isLocked()){}
                lock.Lock();
                _counter.Clean();
                lock.Unlock();
            }
        }
    }

    public static class Reader extends Thread{

        Counter _counter;
        String _name;
        Locker lock;

        Reader(String name, Counter counter, Locker lock){
            _counter = counter;
            _name = name;
            this.lock = lock;
        }

        @Override
        public void run(){
            for(int i = 0; i < 3; i++){
                while(lock.isLocked()){}
                lock.Lock();
                _counter.Decrease(_name);
                lock.Unlock();
            }
        }
    }

    public static class Writer extends Thread{

        Counter _counter;
        String _name;
        Locker lock;

        Writer(String name, Counter counter, Locker lock){
            _counter = counter;
            _name = name;
            this.lock = lock;
        }

        @Override
        public void run(){
            for(int i = 0; i < 3; i++){
                while(lock.isLocked()){}
                lock.Lock();
                _counter.Increase(_name);
                lock.Unlock();
            }
        }
    }

    public static class Counter {
        int count;
        int lock = -1;

        Counter(){
            count = 0;
        }

        synchronized void Increase(String name){
            count++;
            System.out.println(name + ": " + count + "\n");
            notifyAll();
        }
        synchronized void Decrease(String name){
            count--;
            System.out.println(name + ": " + count + "\n");
            notifyAll();
        }
        synchronized void Clean(){
            count = 0;
            System.out.println("Išvlytas: " + count + "\n");
            notifyAll();
        }
    }

    public static class Locker{
        int number;

        Locker(){
            number = -1;
        }

        void Lock(){
            number = 1;
        }

        void Unlock(){
            number = -1;
        }

        boolean isLocked(){
            return number == 1;
        }
    }
}
