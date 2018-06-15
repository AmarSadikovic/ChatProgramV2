package server;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Kristofer Svensson, Amar Sadikovic
 */
public class Clients {

    private HashMap<String, ClientInstance> clientStorage = new HashMap<>();

    public synchronized void addClient(ClientInstance client){
        clientStorage.put(client.getClientName(), client);
    }

    public synchronized void removeClient(String clientName){
        clientStorage.remove(clientName);
    }

    public synchronized int getSize(){
        return clientStorage.size();
    }

    public synchronized ClientInstance get(String clientName){
        return clientStorage.get(clientName);
    }

    public synchronized Set<String> getKeys(){
        return clientStorage.keySet();
    }
}
