import java.util.ArrayDeque;

public class 주식가격{
     public int[] solution(int[] prices) {
        int n = prices.length;
        int[] answer = new int[n];
        
        ArrayDeque<int[]> q = new ArrayDeque<>(); //price, time
        
        for(int i=0; i < n ; i++){
            
            while(!q.isEmpty()){
                if(q.peek()[0] <= prices[i]){
                    break;
                }
                
                int[] info = q.pop();
                answer[info[1]] = i - info[1];
            }
            
            q.push(new int[]{prices[i], i});
        }
        
        while(!q.isEmpty()){
            int[] info = q.pop();
            answer[info[1]] = n - 1 - info[1];
        }
        
        return answer;
    }
}