import java.util.ArrayList;

public class JavaMain {

    public static void main(String[] args) {
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("world");

        String str = (String) list1.get(0);
        System.out.println(str);

        Sample<String> sample = new Sample<String>();
        sample.set("Hello!");
        System.out.println(sample.get());
    }

    public static class Sample<T> {
        private T data;

        public T get() {
            return data;
        }

        public void set(T data) {
            this.data = data;
        }
    }
}
