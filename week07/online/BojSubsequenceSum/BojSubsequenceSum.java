// 백준 14225 · 부분수열의 합
import java.io.*;
import java.util.*;

public class BojSubsequenceSum {

    static int N;
    static int S;
    static int[] nums;
    static Set<Integer> ans;


    static void getPermutation(int flag, int ord, int sum){
        if(flag != 0 && sum == S){
            ans.add(flag);
        }

        for(int i=ord; i<N; i++){
            getPermutation(flag | (1 << i), i+1, sum + nums[i]);
            getPermutation(flag, i+1, sum);
        }
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        S = Integer.parseInt(st.nextToken());
        ans = new HashSet<>();
        nums = new int[N];

        st = new StringTokenizer(br.readLine());
        for(int i=0; i<N; i++){
            nums[i] = Integer.parseInt(st.nextToken());
        }
        getPermutation(0,0,0);
        System.out.print(ans.size());
    }
}
