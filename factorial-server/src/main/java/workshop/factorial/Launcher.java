package workshop.factorial;

public class Launcher {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            throw new Exception("Port number required for server startup");
        }
        int port = Integer.parseInt(args[0]);

        Server server = new Server();
        server.start(port);
        System.out.println("server started");
    }
}
