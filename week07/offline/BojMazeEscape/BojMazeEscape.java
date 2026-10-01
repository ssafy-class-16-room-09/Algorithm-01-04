// 백준 1194 · 달이 차오른다, 가자
import java.io.*;
import java.util.*;

public class BojMazeEscape {

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int[] s = new int[2]; //i,j
        

        char[][] map = new char[N][M];

        for(int i=0; i<N; i++){
            map[i] = br.readLine().toCharArray();
            for(int j=0; j<M; j++){
                if(map[i][j] == '0'){
                    s[0] = i;
                    s[1] = j;
                } 
            }
        }

        ArrayDeque<int[]> q = new ArrayDeque<>();
        boolean[][][] visit = new boolean[N][M][1 << 6];
        visit[s[0]][s[1]][0] = true;
        q.offer(new int[]{s[0], s[1], 0, 0}); //i,j,이동횟수,획득 열쇠

        int[] di = {1,-1,0,0};
        int[] dj = {0,0,1,-1};
        int ans = -1;
       
       

        BFS: while(!q.isEmpty()){
            
            int[] info = q.poll();
            // System.out.println(Arrays.toString(info));
            int i = info[0];
            int j = info[1];
            int v = info[2];
            int k = info[3];

            for(int d=0; d<4; d++){
                int ni = i + di[d];
                int nj = j + dj[d];

                if(ni < 0 || nj < 0 || ni >= N || nj >=M) continue;

                if(map[ni][nj] == '#') continue;

                if(map[ni][nj] >= 'A' && map[ni][nj] <= 'F' && checkKey(k,map[ni][nj])) continue;
                
                if(map[ni][nj] >= 'a' && map[ni][nj] <= 'f' ){
                    int nk = k | (1 << (map[ni][nj] - 'a'));
                    if(visit[ni][nj][nk]) continue;

                    visit[ni][nj][nk] = true;
                    q.offer(new int[]{ni, nj, v+1, nk});
                    continue;
                }

                if(map[ni][nj] == '1'){
                    ans = v+1;
                    break BFS;
                } 

                if(visit[ni][nj][k]) continue;

                visit[ni][nj][k] = true;
                q.offer(new int[]{ni, nj, v+1, k});
            }
            

        }

        System.out.print(ans);
    }

    static boolean checkKey(int keys, char door){
        int dFlag = 1 << (door - 'A');

        return (keys & dFlag) == 0; //문에 해당하는 열쇠 x = true
    }
}
