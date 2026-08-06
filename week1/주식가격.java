package codingtest;

import java.util.*;

class Solution {
    public int[] solution(int[] prices) {
        int[] answer = new int [prices.length];
        Deque <Integer> st = new ArrayDeque<>();
        
        
        for(int i =0; i<prices.length; i++) {
        	int cur_price = prices[i];
        	while(!st.isEmpty() && prices[st.peek()]>cur_price) { // 들어오는 가격보다 낮으면.
        		int idx = st.pop();
        		answer[idx] = i -idx;
        	}	
        	st.push(i);

        }
        
        while(!st.isEmpty()){
        	int idx = st.pop();
        	answer[idx] = prices.length - idx-1;
        }
        
        return answer;
    }
}