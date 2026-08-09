package week1;

import java.util.*;
public class 다리를지나는트럭 {

    public int solution(int bridge_length, int weight, int[] truck_weights) {
        int time = 0;

        Queue<Integer> go_truck = new ArrayDeque<>();
        int[] go_time = new int[truck_weights.length];

        int cur_weight = 0;

        for (int i = 0; i < truck_weights.length; i++) {
            while (cur_weight + truck_weights[i] > weight || go_truck.size() >= bridge_length) {
                time++;
                for (int g : go_truck) {
                    go_time[g]++;
                }
                if (!go_truck.isEmpty() && go_time[go_truck.peek()] >= bridge_length) {

                    int truckIdx = go_truck.poll();
                    cur_weight -= truck_weights[truckIdx];
                }
            }

            go_truck.offer(i);
            cur_weight += truck_weights[i];

            time++;
            for (int g : go_truck) {
                go_time[g]++;
            }

            if (!go_truck.isEmpty() && go_time[go_truck.peek()] >= bridge_length) {
                int truckIdx = go_truck.poll();
                cur_weight -= truck_weights[truckIdx];
            }
        }

        while (!go_truck.isEmpty()) { // 마지막 트럭들 처리
            time++;

            for (int g : go_truck) {
                go_time[g]++;
            }

            if (go_time[go_truck.peek()] >= bridge_length) {
                int truckIdx = go_truck.poll();
                cur_weight -= truck_weights[truckIdx];
            }
        }

        return time;
    }

}
