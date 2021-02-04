package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new SimpleAuthService();
        try {
            server = new ServerSocket(PORT);
            System.out.println("server started");

            while (true) {
                socket = server.accept();
                System.out.println("client connected" + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg, String... senders){
        if(senders.length == 1) {
            String message = String.format("[ %s ] : %s", senders[0], msg);
            for (ClientHandler c : clients) {
                c.sendMsg(message);
            }
        }
        else{
            String message = String.format("[ %s ] %s : %s", senders[1], senders[0],msg);
            for (ClientHandler c : clients){
                for (String nick : senders){
                    if (c.getNickname().equals(nick)) {
                        c.sendMsg(message);

//                        if (++countCurrent == countAll) {
//                            return;
//                        }
                    }
                }
            }
        }


    }

    public void broadcast(String msg, String... nicks) {
        int countCurrent = 0;
        int countAll = nicks.length;

        for (ClientHandler c: clients) {
            for (String nick : nicks) {
                if (c.getNickname().equals(nick)) {

                    if (++countCurrent == countAll) {
                        return;
                    }
                }
            }
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
