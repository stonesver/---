import java.sql.Struct;

public class LoserTree {
    private int[] tree;

    public class Leave{
        //记录应划分的归并段，用于实验3
        int RunNum=1;
        //记录败者的索引，用于实验4
        int FromRun=0;
        int value;
    }
    private int size;

    public Leave[] leaves;

    public LoserTree(int[] values) {
        this.size = values.length;
        this.tree = new int[size];
        this.leaves = new Leave[size];

        for(int i=0;i<size;i++){
            this.leaves[i]=new Leave();
        }

        for (int i = 0; i < size; i++) {
            this.tree[i] = -1; // Initialize the tree with -1.
            this.leaves[i].value = values[i]; // Initialize the leaves with their indexes.
            this.leaves[i].FromRun=i;
        }

        // Initialize the tree with the values from the leaves.
        for (int i = 0; i <size ; i++) {
            adjust(i);
        }
    }

    // Adjust the loser tree for a given leaf.
    public void adjust(int index) {
        int parent =(index+this.size)/2;
        while (parent > 0) {
            //先比较RunNum，再比较value，RunNum小的胜出，RunNum相等时，value小的胜出
            //tree[parent]为父节点的索引，记录败者
            //如果父节点是-1，则默认为败者
            if (tree[parent] == -1) {
                tree[parent] = index;
                return;
            }
            if (leaves[index].RunNum > leaves[tree[parent]].RunNum) {
                int temp = index;
                index=tree[parent];
                tree[parent] = temp;
            } else if (leaves[index].RunNum == leaves[tree[parent]].RunNum) {
                if (leaves[index].value > leaves[tree[parent]].value) {
                    int temp = index;
                    index=tree[parent];
                    tree[parent] = temp;
                }
            }
            parent /= 2;

        }
        tree[0] = index;
    }


    // Get the index of the winning leaf.
    public Leave getWinner() {
        return leaves[tree[0]];
    }

    // Replace the winning leaf with a new leaf and adjust the tree.
    public void replaceWinner(int newLeaf) {
        //先比较胜者和新叶子节点，比胜者小，则RunNum+1，否则，RunNum不变
        if (leaves[tree[0]].value > newLeaf||newLeaf==Integer.MAX_VALUE) {
            leaves[tree[0]].RunNum++;
        }
        leaves[tree[0]].value=newLeaf;
        adjust(tree[0]);
    }

//    public static void main(String[] args) {
//        int[] values = { 4,3,6,8,1,5,7,3,2,6,9,4,5,2,5,8 };
//        int [] sup = {3,5,4,1,9,2,6,1,3};
//        LoserTree tree = new LoserTree(values);
//        System.out.println(tree.getWinner().value);
//        for (int j : sup) {
//            tree.replaceWinner(j);
//            System.out.println(tree.getWinner().value);
//        }
//    }
}


