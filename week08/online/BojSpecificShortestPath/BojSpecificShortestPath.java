// 백준 1504 · 특정한 최단 경로
import java.io.*;
import java.util.*;

public class BojSpecificShortestPath {
    static ArrayList<int[]>[] edges;
    static int N;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        int E = Integer.parseInt(st.nextToken());

        edges = new ArrayList[N];

        for(int i=0; i<N; i++){
            edges[i] = new ArrayList<>();
        }

        
        for(int i=0; i<E; i++){
            st = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st.nextToken()) - 1;
            int b = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken());

            edges[a].add(new int[]{b,c});
            edges[b].add(new int[]{a,c});
        }

        st = new StringTokenizer(br.readLine());
        int v1 = Integer.parseInt(st.nextToken())-1;
        int v2 = Integer.parseInt(st.nextToken())-1;

        int ans = -1;

        int d1;
        int a1 = bfs(0,v1);
        int a2 = bfs(v1,v2);
        int a3 = bfs(v2,N-1);

        if(a1 == -1 || a2 == -1 || a3 == -1){
            d1 = -1;
        }else{
            d1 = a1 + a2 + a3;
        }

        int d2;
        int b1 = bfs(0,v2);
        int b2 = bfs(v2,v1);
        int b3 = bfs(v1,N-1);

      

        if(b1 == -1 || b2 == -1 || b3 == -1){
            d2 = -1;
        }else{
            d2 = b1 + b2 + b3;
        }

        if(d1 == -1){
            ans = d2;
        }else if(d2 == -1){
            ans = d1;
        }else if(d1 != -1 && d2 != -1){
            ans = Math.min(d1,d2);
        }
        
        System.out.print(ans);
    }


    static int bfs(int s, int e){
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1,o2) -> Integer.compare(o1[1],o2[1]));
        int[] visit = new int[N];
        
        for(int i=0; i<N; i++){
            visit[i] = -1;
        }

        pq.offer(new int[]{s,0});
        visit[s] = 0;

        while(!pq.isEmpty()){
            int[] info = pq.poll();
            int n = info[0];
            int w = info[1];

            for(int i=0; i<edges[n].size(); i++){
                int[] edge = edges[n].get(i);
                int next = edge[0];
                int nw = w + edge[1];

                if(nw < visit[next] || visit[next] == -1){
                    visit[next] = nw;
                    if(next == e){
                        continue;
                    }
                    pq.offer(new int[]{next, nw});
                }
            }
        }

        return visit[e];
    }


}
