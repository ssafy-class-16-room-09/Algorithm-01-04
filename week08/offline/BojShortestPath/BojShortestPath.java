// 백준 1753 · 최단경로
import java.io.*;
import java.util.*;

public class BojShortestPath {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(br.readLine());

        int V = Integer.parseInt(st.nextToken());
        int E = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(br.readLine()) - 1;

        ArrayList<int[]>[] edges = new ArrayList[V];

        for(int i=0; i<V; i++){
            edges[i] = new ArrayList<>();
        }


        int[][] map = new int[V][V];
        for(int i=0; i<V; i++){
            for(int j=0; j<V; j++){
                map[i][j] = Integer.MAX_VALUE;

                if(i == j) map[i][j] = 0;
            }
        }

        
        for(int i=0; i<E; i++){
            st = new StringTokenizer(br.readLine());

            int s = Integer.parseInt(st.nextToken())-1;
            int e = Integer.parseInt(st.nextToken())-1;
            int w = Integer.parseInt(st.nextToken());

            edges[s].add(new int[]{e,w});
        }

        int[] minW = new int[V];

        for(int i=0; i<V; i++){
            minW[i] = Integer.MAX_VALUE;
        }
        PriorityQueue<int[]> q = new PriorityQueue<>((o1,o2) -> Integer.compare(o1[1], o2[1])); // node, weight
        q.offer(new int[]{K,0});
        minW[K] = 0;

        while(!q.isEmpty()){
            int[] info = q.poll();
            int cur = info[0];
            int w = info[1];

            for(int[] edge : edges[cur]){
                int next = edge[0];
                int nw = w + edge[1];

                if(minW[next] > nw){
                    minW[next] = nw;
                    q.offer(new int[]{next, nw});
                }
            }

        }

        for(int i=0; i<V; i++){
            System.out.println((minW[i] == Integer.MAX_VALUE) ? "INF" : minW[i]);
        }
    }
}
