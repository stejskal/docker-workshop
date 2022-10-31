package workshop.mr;

public class Launcher {
    public static void main(String[] args) throws Exception {
        if(args.length < 1){
            throw new Exception("Worker urls required");
        }

        Server server = new Server();
        server.start(args);
        System.out.println("server started");
    }
}
