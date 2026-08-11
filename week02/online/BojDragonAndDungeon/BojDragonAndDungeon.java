// 백준 · 드래곤 앤 던전
// https://www.notion.so/3b8216e328d180ffb42cc824ab0d5cd4?source=copy_link
import java.io.*;
import java.util.*;

public class BojDragonAndDungeon {

    static int[][] dungeons;
    static long max;
    static long min = 1;

    static boolean gotoDungeon(long hp, long atk){
        long cur = hp;
        for(int[] dungeon: dungeons ){
            if(dungeon[0] == 1){

                long need = dungeon[2] / atk + ((dungeon[2] % atk != 0) ? 1 : 0);
                
                cur -= dungeon[1] * (need - 1);

                if(cur <= 0){
                    return false;
                }
            }else{
                cur += dungeon[2];
                if(cur > hp){
                    cur = hp;
                }

                atk += dungeon[1];
            }
        }

        return true;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(br.readLine());

        ArrayDeque<Integer> s = new ArrayDeque<>();

        int n = Integer.parseInt(st.nextToken());
        long atk = Long.parseLong(st.nextToken());

        dungeons = new int[n][3];

        max = 0;

        long tmpAtk = atk;
        for(int i=0; i<n; i++){
            
            st = new StringTokenizer(br.readLine());
            dungeons[i][0] = Integer.parseInt(st.nextToken());
            dungeons[i][1] = Integer.parseInt(st.nextToken());
            dungeons[i][2] = Integer.parseInt(st.nextToken());
            
            if(dungeons[i][0] == 1){
                long need = dungeons[i][2] / tmpAtk + ((dungeons[i][2] % tmpAtk != 0) ? 1 : 0);
                max += need * dungeons[i][1];
            }else{
                tmpAtk += dungeons[i][1];
            }
        }

        long answer = 1;
        while(min <= max){
            long mid = (min + max) / 2;
            if(gotoDungeon(mid, atk)){
                answer = mid;
                max = mid - 1;

            }else{
                min = mid + 1;
            }
        }
        
        System.out.println(answer);
    }
}
