package server;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Kristofer Svensson & Amar Sadikovic on 2017-10-06.
 */
public class Clients {

    private HashMap<String, ClientInstance> clientStorage = new HashMap<>();

    public void addClient(ClientInstance client){
        clientStorage.put(client.getClientName(), client);
    }

    public void removeClient(String clientName){
        clientStorage.remove(clientName);
    }

    public int getSize(){
        return clientStorage.size();
    }

    public ClientInstance get(String clientName){
        return clientStorage.get(clientName);
    }

    public Set<String> getKeys(){
        return clientStorage.keySet();
    }
}
