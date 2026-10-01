// 백준 1268 · 임시 반장 정하기
import java.io.*;
import java.util.*;

public class BojTemporaryClassLeader {
    final static int GRADE = 5;
    final static int CLASS = 9;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = null;
        int N = Integer.parseInt(br.readLine());


        HashSet<Integer>[][] classInfo =  new HashSet[GRADE][CLASS];
        int[][] students = new int[N][GRADE];
        HashSet<Integer>[] candidate = new HashSet[N];

        for(int i=0; i<GRADE; i++){

            for(int j=0; j<CLASS; j++){
                classInfo[i][j] = new HashSet<>();
            }
            
        }

        for(int i=0; i<N; i++){
            candidate[i] = new HashSet<>();
        }

        for(int i=0; i<N; i++){
            st = new StringTokenizer(br.readLine());

            for(int g=0; g<GRADE; g++){
                int c = Integer.parseInt(st.nextToken())-1;
                classInfo[g][c].add(i);
                students[i][g] = c;
            }
            
        }
        

        for(int i=0; i<N; i++){
            for(int g=0; g<GRADE; g++){
                if(classInfo[g][students[i][g]].contains(i))
                    candidate[i].addAll(classInfo[g][students[i][g]]);
            }
        }
        
        int max = 0;
        int idx = 0;

        for(int i=0; i<N; i++){
            
            if(candidate[i].size() > max){
                max = candidate[i].size();
                idx = i;
            }
            // System.out.println(Integer.toBinaryString(candidate[i]));
        }
        System.out.print(idx+1);
    }
}
