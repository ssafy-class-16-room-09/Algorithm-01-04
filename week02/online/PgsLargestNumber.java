// 프로그래머스 42746 · 가장 큰 수
// https://school.programmers.co.kr/learn/courses/30/lessons/42746
import java.util.*;

public class PgsLargestNumber {
  

  public String solution(int[] numbers) {
    int n = numbers.length;
    String[] nums = new String[n];
    
    for(int i=0; i<n; i++){
      nums[i] = String.valueOf(numbers[i]);
    }
    
    
    Arrays.sort(nums, (s1,s2) -> {
      return (s2+s1).compareTo(s1+s2);
    });
    
    if(nums[0].equals("0")) return "0";
    
    StringBuilder answer = new StringBuilder();
    
    for(String s: nums){
      answer.append(s);
    }
    
    return answer.toString();
  }

}
