package workshop.factorial;

public class Launcher {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
        System.out.println("Factorial server started");
    }
}
