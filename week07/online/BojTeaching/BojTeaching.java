// 백준 1062 · 가르치기
import java.io.*;
import java.util.*;

public class BojTeaching {

    static HashMap<Character, Integer> charIndex;
    static int K;
    static int N;
    static int charCnt;
    static int max;
    static int[] wordBits;
    final static int basicN = 5;

    static void getComb(int flag, int use, int cnt){

        if(use > K) return;

        
        int ans = 0;
        for(int i=0; i<N; i++){
            if((wordBits[i] & flag) == wordBits[i]){
                ans++;
            }
        }

        max = Math.max(ans, max);

      
        
        if (use == charCnt) return;
        
        getComb(flag, use+1, cnt);
        getComb(flag | (1 << use), use+1, cnt+1);
        
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        charIndex = new HashMap<>();
        charCnt = 0;

        String[] words = new String[N];
        wordBits = new int[N];

        char[] basic = new char[]{'a','n','t','i','c'};
        int basicFlag = 0;

        for(int i=0; i<basicN; i++){
            basicFlag |= (1 << charCnt);
            charIndex.put(basic[i], charCnt++);
        }

        // System.out.println(Integer.toBinaryString(basicFlag));

        for(int i=0; i<N; i++){
            words[i] = br.readLine();
            wordBits[i] = basicFlag;
            
            for(int j=4; j<words[i].length()-4; j++){
                Integer idx = charIndex.get(words[i].charAt(j));
                
                if(idx == null){
                    idx = charCnt;
                    charIndex.put(words[i].charAt(j), charCnt++);
                }

                wordBits[i] |= 1 << idx;
            }         
        }

        max = 0;

        if(K < basicN){
            System.out.print(max);
            return;
        }

        getComb(basicFlag, 5, 5);

        System.out.print(max);
    }
}
