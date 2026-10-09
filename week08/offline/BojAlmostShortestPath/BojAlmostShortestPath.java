// 백준 5719 · 거의 최단 경로
import java.io.*;
import java.util.*;

public class BojAlmostShortestPath {
    static class Edge{
        int s;
        int e;

        @Override
        public boolean equals(Object obj){
            if(obj instanceof Edge){
                Edge edge = (Edge)obj;

                return this.s == edge.s && this.e == edge.e;
            }

            return false;
        }

        @Override
        public int hashCode(){
            return Objects.hash(s,e);
        }

        Edge(int s, int e){
            this.s = s;
            this.e = e;
        } 
    }
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = null;

        int N;
        int M;
        int S;
        int D;
        int U;
        int V;
        int P;

        while(true){
            st = new StringTokenizer(br.readLine());
            N = Integer.parseInt(st.nextToken());
            M = Integer.parseInt(st.nextToken());

            if(N == 0 && M == 0){
                break;
            }

            st = new StringTokenizer(br.readLine());
            S = Integer.parseInt(st.nextToken());
            D = Integer.parseInt(st.nextToken());

            ArrayList<int[]>[] edges = new ArrayList[N];
            int[] degrees = new int[N];

            for(int i=0; i<N; i++){
                edges[i] = new ArrayList<>();
            }

            for(int i=0; i<M; i++){
                st = new StringTokenizer(br.readLine());
                U = Integer.parseInt(st.nextToken());
                V = Integer.parseInt(st.nextToken());
                P = Integer.parseInt(st.nextToken());

                edges[U].add(new int[]{V,P});
                degrees[V]++;
            }


            ArrayDeque<Integer> q = new ArrayDeque<>();
            int[] minW = new int[N];
            HashSet<Integer>[] uses = new HashSet[N];
            for(int i=0; i<N; i++){
                if(degrees[i] == 0){
                    q.offer(i);
                    minW[i] = -1;
                }

                uses[i] = new HashSet<>();
            }

            minW[S] = 0;

            while(!q.isEmpty()){
                int cur = q.poll();

                if(cur == D) continue;

                for(int[] edge : edges[cur]){
                    int next = edge[0];
                    int w = edge[1] + minW[cur];
                    
                    if(minW[next] == -1){

                    }else if(minW[next] == w){
                        uses[next].add(cur);
                    }else if(minW[next] > w){
                        uses[next].clear();
                        uses[next].add(cur);
                    }

                    if(--degrees[next] == 0){
                        q.offer(next);
                    }
                }    
            }

            HashSet<Edge> useEdges = new HashSet<>();
            boolean[] visit = new boolean[N];
            
            q.offer(D);
            visit[D] = true;
            while(!q.isEmpty()){
                int cur = q.poll();

                for(int next: uses[cur]){
                    useEdges.add(new Edge(next, cur));
                    if(visit[next]) continue;
                    q.offer(next);
                    visit[next] = true;
                }
            }

            PriorityQueue<int[]> pq = new PriorityQueue<>((o1,o2) -> Integer.compare(o1[1],o2[1])); //node,w
            pq.offer(new int[]{S,0});
            int[] pqMinW = new int[N];
            for(int i=0; i<N; i++){
                pqMinW[i] = Integer.MAX_VALUE;
            }

            pqMinW[S] = 0;
            int ans = -1;
            while(!pq.isEmpty()){
                int[] info = pq.poll();
                int cur = info[0];
                int w = info[1];

                if(cur == D && (ans > w || ans == -1)){
                    ans = w;
                } 

                for(int[] edge : edges[cur]){
                    int next = edge[0];
                    int nw = w + edge[1];
                    if(useEdges.contains(new Edge(cur,next))){
                        continue;
                    }

                    if(pqMinW[next] > nw){
                        pqMinW[next] = nw;
                        pq.offer(new int[]{next, nw});
                    }
                }
            }

            sb.append(ans).append("\n");
        }   


        

        System.out.print(sb);
    }
}
