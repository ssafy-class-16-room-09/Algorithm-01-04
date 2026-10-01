// 백준 1450 · 냅색문제
import java.io.*;
import java.util.*;

public class BojKnapsackCount {

    static int[] items; 
    static int N;
    static long C;
    static long[] left;
    static long[] right;
    static int lSize;
    static int rSize;

    static void getComb(int flag, int cur, long sum, int s, int e, int rl){
        if(cur == e){
            if(rl == 0){
               left[flag] = sum;
            }else{
                right[flag] = sum;
            }
            return;
        }

        int bit = cur-s;
        getComb(flag | (1 << bit) , cur+1, sum+items[cur], s, e, rl);
        getComb(flag , cur+1, sum, s, e, rl);
    }

    static int binarySearch(long v){
        int s = 0;
        int e = rSize;
        int m = (s+e)/2;
        
        while(s < e){
            m = (s+e)/2;
            long c = v+right[m];
            if(c <= C){
                s = m+1;
            }else{
                e = m;
            }
        }

        return s;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        C = Long.parseLong(st.nextToken());
        
        items = new int[N];
        
        int lCnt = N/2;
        int rCnt = N - lCnt;

        lSize = (int)Math.pow(2, lCnt);
        rSize = (int)Math.pow(2, rCnt);
        left = new long[lSize];
        right = new long[rSize];
        

        
        st = new StringTokenizer(br.readLine());

        for(int i=0; i<N; i++){
            items[i] = Integer.parseInt(st.nextToken());
        }

        // System.out.println(lCnt);
        // System.out.println(rCnt);
        getComb(0,0,0,0,lCnt, 0);
        getComb(0,lCnt,0,lCnt,N,1);


        Arrays.sort(right);

        int ans = 0;

        // System.out.println(Arrays.toString(left));
        // System.out.println(Arrays.toString(right));
        
        for(int i=0; i<lSize; i++){
            int v = binarySearch(left[i]); 
            // System.out.println(v);
            ans += v;
        }

        System.out.print(ans);
    }
}
