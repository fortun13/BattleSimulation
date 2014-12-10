package main.java.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Fortun on 2014-12-08.
 */
public class JsonTest {

    public static void main(String[] args) {
        //System.out.println("aaa");
        try {
            String content = "";
            Scanner s = new Scanner(new FileInputStream(new File("res/jsonTest.json")));
            while (s.hasNext())
                content += s.nextLine();

            s.close();
            JSONObject obj = new JSONObject(content);
            JSONArray agents = obj.getJSONArray("agents");



            Map<String,ArrayList<JSONObject>> map = new HashMap<>();

            for (int i=0;i<agents.length();i++) {
                JSONObject agent = agents.getJSONObject(i);

                if (map.containsKey(agent.get("type").toString()))
                    map.get(agent.get("type")).add(agent);
                else {
                    ArrayList<JSONObject> lst = new ArrayList<>();
                    lst.add(agent);
                    map.put(agent.get("type").toString(),lst);
                }
            }

            System.out.println(map);

            System.out.println(obj.getInt("boardWidth"));

            /*JSONArray obstacles = obj.getJSONArray("obstacles");

            for (int i=0;i<obstacles.length();i++) {
                JSONObject obstacle = obstacles.getJSONObject(i);
                System.out.println("Obstacle");
                System.out.println("x: " + obstacle.getInt("x"));
                System.out.println("y: " + obstacle.getInt("y"));
                System.out.println("width: " + obstacle.getInt("width"));
                System.out.println("height: " + obstacle.getInt("width"));
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
