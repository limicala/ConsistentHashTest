package hash;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.whitfin.siphash.SipHasher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class MaglevHash<K, N> implements GenericHash<K, N> {

    public static final int SPARE = 20;

    private static final Charset KEY_ENCODING = StandardCharsets.US_ASCII;
    private static final Charset INPUT_ENCODING = StandardCharsets.UTF_8;

    private int nServers;
    private int mSizeLookup;

    private ArrayList<N> nodes;
    private ArrayList<NodeState> nodeStates;

    private int[] lookup;
    private static final HashFunction HASH_INPUT = Hashing.murmur3_32(0xaceaceac);
    private static final HashFunction HASH_OFFSET = Hashing.murmur3_32(0xdeadbabe);
    private static final HashFunction HASH_SKIP = Hashing.murmur3_32(0xdeadbeaf);

    private Funnel<N> nodeFunnel;
    private Funnel<K> keyFunnel;

    private boolean isServerChanged;

    private static class NodeState {

        //will not change
        private int offset;
        private int skip;
        private int[] row;

        //will change
        private int nextPos;


        private void reset() {
            this.nextPos = 0;
        }
    }

    public MaglevHash(int mSizeLookup, Funnel<K> keyFunnel, Funnel<N> nodeFunnel, Collection<N> nodes){
        this.keyFunnel = keyFunnel;
        this.nodeFunnel = nodeFunnel;
        this.mSizeLookup = mSizeLookup;
        this.nodes = new ArrayList<>();
        this.nodeStates = new ArrayList<>();
        this.lookup = new int[mSizeLookup];
        for (N node : nodes){
            add(node);
        }
        isServerChanged = true;
    }

    @Override
    public N get(K key) {
        if(isServerChanged){
            fillLookUp();
            isServerChanged = false;
        }

        return nodes.get(lookup[(int)(HASH_INPUT.hashObject(key, keyFunnel).padToLong() % mSizeLookup)]);
    }

    public boolean add(N node){
        int i = 0;
        while (i < nodes.size() && nodes.get(i) != null) i++;


        NodeState state = this.createState(node);

//        nodes.add(node);
//        nodeStates.add(state);

        if( i == nodes.size() ){
            nodes.add(node);
            nodeStates.add(state);
            nServers += 1;
        }else{
            nodes.set(i, node);
            nodeStates.set(i, state);
        }

        isServerChanged = true;

        return true;
    }

    @Override
    public boolean remove(N node) {
        int i = nodes.indexOf(node);

        nodes.set(i, null);
        nodeStates.set(i, null);

        isServerChanged = true;

        return true;
    }

    @Override
    public String getLabel() {
        return "MaglevHash " + mSizeLookup;
    }

    private NodeState createState(N node){
        //return createState1(node);

        return createState2(node);
    }
    private NodeState createState1(N node) {
        NodeState state = new NodeState();

        state.offset = (int) (HASH_OFFSET.hashObject(node, nodeFunnel).padToLong() % mSizeLookup);
        state.skip = (int) (HASH_SKIP.hashObject(node, nodeFunnel).padToLong() % (mSizeLookup - 1) + 1);

        int[] row = new int[mSizeLookup];

        int tmp = state.offset;
        row[0] = tmp;
        for (int i = 1; i < mSizeLookup; i++){
            tmp = (tmp + state.skip) % mSizeLookup;
            row[i] = tmp;
        }
        state.row = row;
        return state;
    }

    private NodeState createState2(N node) {
        NodeState state = new NodeState();
        /* Go sample code
            h := siphash.Hash(0xdeadbeefcafebabe, 0, b)
            offset, skip := (h>>32)%M, ((h&0xffffffff)%(M-1) + 1)
         */

        long h = SipHasher.hash("0123456789ABCDEF".getBytes(), node.toString().getBytes());
        state.offset = (int) ((h >>> 32) % mSizeLookup);
        state.skip = Math.abs( (int) (h & 0xffffffff) % (mSizeLookup - 1) + 1);

        int[] row = new int[mSizeLookup];

        int tmp = state.offset;
        row[0] = tmp;
        for (int i = 1; i < mSizeLookup; i++){
            tmp = (tmp + state.skip) % mSizeLookup;
            //System.out.print(tmp + " ");
            row[i] = tmp;
        }
        //System.out.println();
        state.row = row;
        return state;
    }

    private void fillLookUp(){
        int filled = 0;
        reset();
        while(true) {
            for (int i = 0; i < nServers; ++i) {

                NodeState state = nodeStates.get(i);
                if(state == null) continue;

                while(state.nextPos < mSizeLookup) {

                    int c = state.row[state.nextPos];
                    state.nextPos++;
                    if (lookup[c] == -1) { //found
                        lookup[c] = i;
                        filled++;

                        if(filled == mSizeLookup){
                            return;
                        } else{
                            break;
                        }
                    }
                }

            }

        }
    }

    private void reset(){
        for (int i = 0; i < mSizeLookup; i++){
            lookup[i] = -1;
        }
        for (NodeState nodeState : nodeStates){
            if (nodeState != null){
                nodeState.reset();
            }

        }
    }

    public void print(){
        for (int i = 0; i < nServers; i++){
            int[] row = nodeStates.get(i).row;
            for (int j = 0; j < mSizeLookup; j++){
                System.out.print(row[j] + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------");
        for (int i = 0; i < mSizeLookup; i++){
            System.out.print(lookup[i] + " ");
        }
        System.out.println("\n----------------------");
    }
}
