package cz.muni.fi.xpastirc.auth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matej on 23.3.2018.
 */
public class UserInfo {

    public UserInfo() throws IOException {
        this.group2users = new HashMap<String, Set<String>>();

        FileReader fileReader = new FileReader(groupsFilePath);
        BufferedReader groupsReader = new BufferedReader(fileReader);
        while(groupsReader.ready())
        {
            try
            {
                String line = groupsReader.readLine();
                String [] tokens = line.split(":");
                String groupName = tokens[0];
                Set<String> users = group2users.get(groupName);
                if(users == null)
                {
                    users = new HashSet<String>();
                    group2users.put(groupName, users);
                }
                if(tokens.length>3)
                {
                    for(String uStr: tokens[3].split(","))
                        users.add(uStr);
                }
            } catch (Exception e) { continue; }
        }
        groupsReader.close();
        fileReader.close();
    }

    public boolean belongs2group(String user, String group)
    {
        Set<String> groupRef = group2users.get(group);
        if(groupRef == null) return false;
        return groupRef.contains(user);
    }

    private String groupsFilePath = "/etc/group";
    //private String groupsFilePath = "C:/group";
    private Map<String, Set<String>> group2users;

}