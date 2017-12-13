import org.opencv.core.Core;

public class Test {
    public static void main (String args[]) {
        System.out.println("Hello, OpenCV");
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.NATIVE_LIBRARY_NAME.toString());
    }
}

