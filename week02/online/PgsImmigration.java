// 프로그래머스 43238 · 입국심사
// https://school.programmers.co.kr/learn/courses/30/lessons/43238
import java.util.Arrays;

public class PgsImmigration {

  long cal(long n, int[] times){
    long res = 0;
    for(int time: times){
      res += n/time;
    }
    
    return res;
  }

  public long solution(int n, int[] times) {
    long answer = 0;
    Arrays.sort(times);      
    
    long min = 1;
    long max = times[times.length - 1] * n;
    long mid = (min + max) / 2;
    answer = cal(mid, times);
    
    long prev = -1;
    
    while(min < max && answer != n){
        //System.out.println(min + "," + max);
        if(answer > n){
            max = mid;
        }else{
            min = mid;
        }
        
        mid = (min + max)/2;
        answer = cal(mid, times);
        
        
    }
    return mid;
      
  }
}
