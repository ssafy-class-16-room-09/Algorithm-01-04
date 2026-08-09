package week1;

import java.util.*;

public class 기능개발 {
    public List<Integer> solution(int[] progresses, int[] speeds) throws Exception {

        int n = progresses.length;
        int [] end_day = new int [n];

        Deque<Integer> dq = new ArrayDeque<>();
        List<Integer> list = new ArrayList<>();
        for(int i =0; i<n; i++){
            end_day[i] = (100-progresses[i])/speeds[i];
            if(((100-progresses[i])%speeds[i])>0) end_day[i]+=1;
        }

        int prev_max = end_day[0];

        for(int i =0; i<n; i++){
            if(prev_max>=end_day[i]){ // 들어오는 값이 더 작거나 같으면 쌓기
                dq.push(end_day[i]);
            }
            else{ // 들어오면 값이 더 크면 여태동안 있던거 다 배포
               list.add(dq.size());
               dq.clear();
               dq.push(end_day[i]);
               prev_max = end_day[i];
            }
        }

        if(!dq.isEmpty()){
            list.add(dq.size());
            dq.clear();
        }

        return list;
    }
}
