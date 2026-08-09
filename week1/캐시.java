package week1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class 캐시 {

    public int solution(int cacheSize, String[] cities) {
        int answer = 0;

        Map <String, Integer> map = new HashMap<>();
        int [] recent_used = new int[3];

        for(int i =0; i<cities.length; i++){
            if(!map.containsKey(cities[i])){
                map.put(cities[i], 1);
                answer +=5;

                int min_idx=0;
            }

            else{
                map.replace(cities[i], map.get(cities[i])+1);
            }
        }


        return answer;
    }

}
